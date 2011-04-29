/**
 * Copyright (C) 2011 Darien Hager
 *
 * This code is part of the "PackBSP" project, and is licensed under
 * a Creative Commons Attribution-ShareAlike 3.0 Unported License. For
 * either a summary of conditions or the full legal text, please visit:
 *
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Permissions beyond the scope of this license may be available
 * at http://technofovea.com/ .
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technofovea.packbsp.assets;

import com.technofovea.packbsp.*;
import com.technofovea.hl2parse.vdf.GameInfoReader;
import com.technofovea.hl2parse.registry.BlobParseFailure;
import com.technofovea.hl2parse.registry.CdrParser;
import com.technofovea.hl2parse.registry.CdrParser.AppDependency;
import com.technofovea.hl2parse.registry.ClientRegistry;
import com.technofovea.hllib.methods.ManagedLibrary;
import com.technofovea.packbsp.assets.AssetSource.Type;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default implemention of an asset locator.
 * 
 * @author Darien Hager
 */
public class AssetLocatorImpl implements AssetLocator {

    private static final Logger logger = LoggerFactory.getLogger(AssetLocatorImpl.class);
    static final String VPK_EXTENSION = ".vpk";
    static final String GCF_EXTENSION = ".gcf";
    static final String NCF_EXTENSION = ".ncf";
    static final String PAK_EXTENSION = ".pak";

    Set<String> operatingSystems;
    CdrParser cdr;
    ManagedLibrary lib;
    GameInfoReader gameInfoData;
    File steamApps;
    int mainAppId;
    List<Integer> dependencyIds = new ArrayList<Integer>();
    // Someday allow this value to be swapped-out to enable batch work. For now, leave it fixed.
    MapAssetSource mapSource = null;
    List<AssetSearchLocation> searchPaths;
    Map<AssetSearchLocation, AssetSource> packages = new HashMap<AssetSearchLocation, AssetSource>();

    /**
     * Creates a new locator in the context of examining a particular map.
     *
     * @param gameInfoData The game-info data that specifies search locations, prefixes, and ordering.
     * @param steamapps The folder which contains Stream's archive files.
     * @param reg An object for accessing Steam's metadata for dependencies and GCF provisioning
     * @param lib An object for doing I/O against various compressed file formats
     * @param bspFile The map being used as another source of assets
     * @throws PackbspException If a problem occurred accessing files or interpreting data.
     */
    public AssetLocatorImpl(GameInfoReader gameInfoData, File steamapps, ClientRegistry reg, ManagedLibrary lib, File bspFile) throws PackbspException {

        this.operatingSystems = new HashSet<String>();
        this.operatingSystems.add("windows");     //TODO for mac-compatibility have a way of detecting the OS

        this.gameInfoData = gameInfoData;
        this.steamApps = steamapps;
        this.lib = lib;
        this.cdr = reg.getContentDescriptionRecord();

        try {
            mapSource = new MapAssetSource(lib, bspFile);
        } catch (ArchiveIOException ex) {
            throw new PackbspException("Unable to open source BSP", ex);
        }

        // Determine appid, and any orange-box AdditionalContentId items
        mainAppId = gameInfoData.getSteamAppId();
        dependencyIds = gameInfoData.getAdditionalIds();

        // Process out the search paths
        searchPaths = processGameinfoPaths(gameInfoData.getSearchPaths());


        // Remove dupes
        searchPaths = dedupe(searchPaths);

        // Populate data and removed invalid paths
        Iterator<AssetSearchLocation> iter = searchPaths.iterator();
        while (iter.hasNext()) {
            AssetSearchLocation asl = iter.next();
            AssetSource pkg = null;
            switch (asl.getType()) {
                case DIR:
                    logger.debug("Creating asset source for directory: " + asl.getBase());
                    pkg = new DirectoryAssetSource(asl.getBase());
                    break;
                case ARCHIVE:
                    try {
                        logger.debug(("Creating asset source for archive " + asl.getBase() + " using prefix '" + asl.getPrefix() + "'"));
                        pkg = new ArchiveAssetSource(lib, asl.getBase(), asl.getPrefix());
                    } catch (ArchivePathException aape) {
                        /* This will tend to happen during normal use, when an
                         * archive is used which doesn't have the subfolder in
                         * the search path
                         */
                        logger.debug("Prefix " + asl.getPrefix() + " not found in archive asset source, skipping");
                        pkg = null;
                    } catch (ArchiveIOException ex) {
                        logger.error("Could not open asset-archive", ex);
                        pkg = null;
                    }
                    break;
                default:
                    logger.error("Unrecognized asset archive type in switch statement: " + asl.getType());

            }
            if (pkg != null) {
                packages.put(asl, pkg);
            } else {
                iter.remove();
            }
        }

    }

    /**
     * Retrieves a list of search locations being used.
     * @return A list
     */
    public List<AssetSearchLocation> getSearchPaths() {
        return new ArrayList<AssetSearchLocation>(searchPaths);
    }

    public List<AssetHit> locate(String path) {
        List<AssetHit> hits = new ArrayList<AssetHit>();

        if(mapSource.hasAsset(path)){
            hits.add(new AssetHit(mapSource, path));
        }

        for (AssetSearchLocation asl : searchPaths) {
            AssetSource src = packages.get(asl);
            boolean found = src.hasAsset(path);
            if (found) {
                hits.add(new AssetHit(src, path));
            }
        }
        return hits;
    }

    

    List<AssetSearchLocation> processGameinfoPaths(List<String> paths) {
        List<AssetSearchLocation> ret = new ArrayList<AssetSearchLocation>();
        for (String path : paths) {
            // Look for the special placeholders and pull them out
            path = path.trim();
            String rel_allsrc = GameInfoReader.PATH_DELIM + GameInfoReader.PLACEHOLDER_ALLSOURCE + GameInfoReader.PATH_DELIM;
            String rel_gameinfo = GameInfoReader.PATH_DELIM + GameInfoReader.PLACEHOLDER_GAMEINFODIR + GameInfoReader.PATH_DELIM;
            logger.debug("Checking gameinfo path: " + path);
            if (path.startsWith(rel_allsrc)) {
                logger.debug("Path is relative to all dependencies");
                // Relative to each of our dependencies.
                path = path.substring(rel_allsrc.length());

                // There are two types of "dependencies" here. There are the app-ids which
                // the *gameinfo* specifies as extras, and then there are the ones which
                // are implicitly folded into |all_source_engine_paths|

                // Handle the former at this level, the latter are handled deeper in.
                ret.addAll(makeArchiveWithDeps(mainAppId, path));

                // Now do the extra IDs specified in gameinfo
                for (int id : dependencyIds) {
                    ret.addAll(makeArchiveWithDeps(id, path));
                }


            } else if (path.startsWith(rel_gameinfo)) {
                logger.debug("Path is relative to the gameinfo file's directory");
                path = path.substring(rel_gameinfo.length());
                // Relative to Gameinfo location
                File temp = gameInfoData.getSourceFile().getParentFile();
                List<AssetSearchLocation> relFolder = makeDiskSource(temp);
                ret.addAll(relFolder);

            } else {
                File temp = new File(path);
                if (temp.isAbsolute()) {
                    logger.debug("Path is absolute disk location");
                    // File system abs path
                    List<AssetSearchLocation> absFolder = makeDiskSource(temp);
                    ret.addAll(absFolder);

                } else {
                    logger.debug("Path is within main app-id only (" + mainAppId + ")");
                    // Within GCF(s) proceeding from the main ID only
                    ret.addAll(makeArchiveWithDeps(mainAppId, path));
                }
            }
        }
        return ret;
    }

    /**
     * Creates a list of ordered search locations for this game.
     *
     * @param mainId The main ID of the game
     * @param path The path to the gameinfo.txt file's directory
     * @return
     */
    List<AssetSearchLocation> makeArchiveWithDeps(int mainId, String path) {
        List<AppDependency> deps;
        try {
            deps = cdr.getAppDependencies(mainId);
        } catch (BlobParseFailure ex) {
            deps = new ArrayList<AppDependency>();
            logger.error("Could not find dependencies for appid (" + mainId + ") in client-registry blob's 'content-description-record'", ex);
        }
        List<AssetSearchLocation> ret = new ArrayList<AssetSearchLocation>();


        AssetSearchLocation main = makeArchive(mainId, path);
        if (main != null) {
            ret.add(main);
        }

        for (AppDependency ad : deps) {
            if (ad.optional) {
                // If it's optional, other people might not have it, so act as if
                // it doesn't exist.
                logger.debug("Skipping optional AppId '{}'",ad.appid);
                continue;
            }
            //TODO operating system checks
            if(ad.operatingSystem != null && operatingSystems != null){
                if(!operatingSystems.contains(ad.operatingSystem)){
                    // Don't make a dependency for this one
                    logger.debug("Skipping platform-specific appid '{}' for OS '{}'", new Object[]{ad.appid,ad.operatingSystem});
                    continue;
                }
            }

            AssetSearchLocation asl = makeArchive(ad.appid, path);
            if (asl != null) {
                ret.add(asl);
            }
        }
        return ret;
    }

    AssetSearchLocation makeArchive(int appid, String path) {
        String fname;
        try {
            fname = cdr.getAppFolderName(appid);
        } catch (BlobParseFailure ex) {
            logger.error("Could not find folder name for appid (" + appid + ") in client-registry blob's 'content-description-record'", ex);
            return null;
        }

        File finalTarget = null;
        Type type = null;

        List<File> possibleArchives = new ArrayList<File>();
        possibleArchives.add(new File(steamApps, fname + GCF_EXTENSION));
        possibleArchives.add(new File(steamApps, fname + VPK_EXTENSION));
        possibleArchives.add(new File(steamApps, fname + NCF_EXTENSION));
        possibleArchives.add(new File(steamApps, fname + PAK_EXTENSION));

        //TODO discover if more than one can ever be used at a time.
        for (File target : possibleArchives) {
            if (target.isFile()) {
                type = Type.ARCHIVE;
                finalTarget = target;
                break;
            }
        }

        if (finalTarget == null) {
            return null;
        } else {
            assert (type != null);
            return new AssetSearchLocation(type, finalTarget, path);
        }
    }

    List<AssetSearchLocation> makeDiskSource(File dir) {
        List<AssetSearchLocation> ret = new ArrayList<AssetSearchLocation>();

        if (!dir.isDirectory()) {
            logger.error("Cannot make an asset folder source for path: " + dir.getAbsolutePath());
            return ret;
        }
        //TODO in addition to making the source for directory X, look for PAK
        // of (pending verification) VPK entries in the same directory. Then add
        // them as well.

        //TODO include the _language variations on paths
        ret.add(new AssetSearchLocation(Type.DIR, dir, ""));
        return ret;
    }

    List<AssetSearchLocation> dedupe(List<AssetSearchLocation> original) {
        Set<AssetSearchLocation> seen = new HashSet<AssetSearchLocation>();
        List<AssetSearchLocation> ret = new ArrayList<AssetSearchLocation>();

        for (AssetSearchLocation asl : original) {
            if (seen.contains(asl)) {
                continue;
            }
            ret.add(asl);
            seen.add(asl);
        }
        return ret;
    }
}
