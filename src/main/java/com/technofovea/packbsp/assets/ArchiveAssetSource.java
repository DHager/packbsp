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
package com.technofovea.packbsp.assets;

import com.technofovea.hllib.DirectoryItem;
import com.technofovea.hllib.HlPackage;
import com.technofovea.hllib.enums.HlOption;
import com.technofovea.hllib.enums.PackageType;
import com.technofovea.hllib.masks.FileMode;
import com.technofovea.hllib.masks.FindType;
import com.technofovea.hllib.methods.ManagedLibrary;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to assets which are inside HLLib-compatible archives,
 * typically GCF files.
 *
 * @author Darien Hager
 */
public class ArchiveAssetSource implements AssetSource {

    private static final Logger log = LoggerFactory.getLogger(ArchiveAssetSource.class);
    static final String ROOT_PREFIX = "root\\";
    HlPackage pkg;
    DirectoryItem root;
    ManagedLibrary lib;
    String prefix;
    File baseFile;
    FindType findFiles;
    FindType findDirectories;

    /**
     *
     * @param lib The JHlLib library object to use for I/O
     * @param base The archive file to open
     * @param prefix The prefix to use within that file, not including the "ROOT\" folder.
     * @throws ArchiveIOException If there was a problem opening the archive.
     * @throws ArchivePathException If the archive was opened successfully, but the desired prefix path does not exist.
     */
    public ArchiveAssetSource(ManagedLibrary lib, File base, String prefix) throws ArchiveIOException, ArchivePathException {
        this.baseFile = base;
        this.prefix = prefix;
        this.lib = lib;
        pkg = openArchiveFile(this.lib, base);

        root = lib.packageGetRoot();

        findFiles = new FindType();
        findFiles.set(FindType.HL_FIND_CASE_SENSITIVE, false);
        findFiles.set(FindType.HL_FIND_FILES);

        findDirectories = new FindType();
        findFiles.set(FindType.HL_FIND_CASE_SENSITIVE, false);
        findDirectories.set(FindType.HL_FIND_FOLDERS);

        lib.bindPackage(pkg);
        DirectoryItem prefixFolder = lib.folderGetItemByPath(root, prefix, findDirectories);
        if (prefixFolder == null) {
            throw new ArchivePathException(prefix);
        }
    }

    public Type getType() {
        return Type.ARCHIVE;
    }

    public boolean hasAsset(String relativePath) {
        String convertedPath = convertPath(relativePath);
        DirectoryItem item = findItem(convertedPath);
        if (item == null) {
            return false;
        } else {
            return true;
        }

    }

    public File getData(String relativePath) {
        String convertedPath = convertPath(relativePath);
        DirectoryItem item = findItem(convertedPath);
        return extractTemporary(lib, item);
    }

    /**
     * Given a full path, search for an item.
     * @param path The full path, including the ROOT\ folder.
     * @return An item or null if not found.
     */
    DirectoryItem findItem(String path) {
        lib.bindPackage(pkg);
        DirectoryItem result = lib.folderGetItemByPath(root, path, findFiles);
        return result;

    }

    /**
     * Given a relative path from the outside world, convert it for direct use.
     * Back/forward slashes are corrected, and the necessary prefixes are added.
     * @param path A relative path (ex. "materials/test.vmt")
     * @return A corrected full path (ex. "ROOT\hl2\materials\test.vmt")
     */
    protected String convertPath(String path) {
        path = prefix + "/" + path;
        path = ROOT_PREFIX + path.trim().replace("/", "\\");
        return path;
    }

    /**
     * Utility method for opening an archive file.
     *
     * @param lib The JHlLib interface for I/O
     * @param archiveFile The file to open
     * @return A package reference
     * @throws ArchiveIOException If a problem occurs trying to open the archive for reading
     */
    protected static HlPackage openArchiveFile(ManagedLibrary lib, File archiveFile) throws ArchiveIOException {
        final String errorPath = archiveFile.getAbsolutePath();
        PackageType pt = lib.getPackageType(archiveFile);
        if (pt == null) {
            String errorDetail = lib.getString(HlOption.ERROR);
            log.error("Error determining package type. Archive: '" + errorPath + "' Error Detail: " + errorDetail);
        }
        HlPackage pkg = lib.createPackage(pt);
        if (pkg == null) {
            String errorDetail = lib.getString(HlOption.ERROR);
            throw new ArchiveIOException("Error creating package object. Archive: '" + errorPath + "' Error Detail: " + errorDetail);
        }

        boolean r_bind = lib.bindPackage(pkg);
        if (!r_bind) {
            String errorDetail = lib.getString(HlOption.ERROR);
            lib.packageRemove(pkg);
            throw new ArchiveIOException("Error binding to package package. Archive: '" + errorPath + "' Error Detail: " + errorDetail);
        }

        FileMode fm = new FileMode();
        fm.set(FileMode.INDEX_MODE_READ, true);
        fm.set(FileMode.INDEX_MODE_VOLATILE);
        boolean r_open = lib.packageOpenFile(archiveFile.getAbsolutePath(), fm);
        if (!r_open) {
            String errorDetail = lib.getString(HlOption.ERROR);
            lib.packageRemove(pkg);
            throw new ArchiveIOException("Error opening package. Archive: '" + errorPath + "' Error Detail: " + errorDetail);
        }
        return pkg;
    }

    /**
     * Extracts data from the given target within the archive, saving it to a
     * temporary disk location. The temporary file will be removed when the JVM
     * exits.
     *
     * @param lib The JHlLib object for I/O
     * @param target The item within the archive to access
     * @return A temporary File or null on failure
     */
    protected static File extractTemporary(ManagedLibrary lib, DirectoryItem target) {

        if (target == null) {
            return null;
        }


        try {
            File temp = File.createTempFile("packbsp_gcfed_", ".file");
            temp.deleteOnExit();
            target.extractToFile(temp);
            return temp;
        } catch (IOException ex) {
            log.error("Error extracting from Archive: " + target.getName(), ex);
            return null;
        }



    }
    /**
     * Returns the archive being accessed
     * @return A File object
     */
    public File getBaseFile() {
        return baseFile;
    }

    @Override
    public String toString() {
        return "ArchiveAssetSource{" + " baseFile=" + baseFile + ", prefix=" + prefix + "}";
    }
    
}
