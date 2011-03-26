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
package com.technofovea.packbsp.crawling.handlers;

import com.technofovea.hl2parse.mdl.ModelData;
import com.technofovea.hl2parse.mdl.ModelParseException;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EdgeImpl;
import com.technofovea.packbsp.crawling.OutgoingPair;
import com.technofovea.packbsp.crawling.nodes.MiscFileNode;
import com.technofovea.packbsp.crawling.nodes.ModelNode;
import com.technofovea.packbsp.crawling.nodes.MultiPathNode;
import com.technofovea.packbsp.crawling.nodes.PhyNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class ModelHandler implements NodeHandler<Edge, ModelNode> {

    private static final Logger logger = LoggerFactory.getLogger(ModelHandler.class);
    protected static final String EXTENSION_PHYSDATA = "phy";
    protected static final String EXTENSION_TEXSTRIPS = "vtx";
    protected static final String EXTENSION_VVD = "vvd";
    protected static final String EXTENSION_ANIMATIONS = "ani";
    protected static final String MODEL_DIR = "models";
    //TODO explore ways to replace this with a softer references
    /*
     * The reason we cache on File rather than ModelNode is this: ModelNodes
     * have an equality contract which makes them different depending on which
     * skins are needed. Because of this, you could easily have many different
     * ModelNode objects that all rely on the same MDL file.
     *
     * Almost the entire benefit to this cache in normal operation is to prevent
     * re-reads for jobs which differ only in which skins they plan to look at.
     */
    protected final WeakHashMap<File, ModelData> modelDataCache = new WeakHashMap<File, ModelData>();

    protected static enum VtexMode {

        SW("Software"),
        DX80("DirectX 8"),
        DX90("Directx 9"),;
        String name;

        VtexMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public void clearModelDataCache() {
        synchronized (modelDataCache) {
            modelDataCache.clear();
        }
    }

    public List<OutgoingPair> handle(Edge edge, ModelNode node, File dataSource) throws HandlingException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        ModelData md = null;
        synchronized (modelDataCache) {
            md = modelDataCache.get(dataSource);
            if (md == null) {
                logger.debug("Parsing model data for: {}", dataSource.getAbsolutePath());
                try {


                    FileInputStream fos = new FileInputStream(dataSource);
                    FileChannel fc = fos.getChannel();
                    MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, dataSource.length());
                    mbb.order(ByteOrder.LITTLE_ENDIAN);


                    md = new ModelData(mbb);
                    modelDataCache.put(dataSource, md);
                } catch (ModelParseException mpe) {
                    throw new HandlingException("Could not parse the model", mpe);
                } catch (IOException ioe) {
                    throw new HandlingException("Could not parse the model", ioe);
                }
            } else {
                logger.debug("Model data retrieved from cache for: {}", dataSource.getAbsolutePath());
            }
        }

        String baseName = MODEL_DIR + "/" + md.getModelName();
        if (baseName.toLowerCase().endsWith(".mdl")) {
            baseName = baseName.substring(0, baseName.length() - 4);
        }

        List<String> externalModels = md.getIncludedModels();
        logger.debug("Found references to additional models: {}", externalModels);
        List<String> paths = md.getTextureSearchPaths();
        logger.debug("Found material search paths: {}", paths);
        logger.trace("Found material names: {}", md.getTextureNames());

        //TODO better OOP way that uses internal names to find the ANI file
        for (String path : externalModels) {
            ret.add(new OutgoingPair(new EdgeImpl("Included Model (anims,seq)"), new MiscFileNode(path)));
            String aniPath = path.replaceAll("(?i)\\.mdl$", ".ani");
            ret.add(new OutgoingPair(new EdgeImpl("Included Model (ani)"), new MiscFileNode(aniPath)));

        }

        final int numSkins = md.getSkinCount();
        Set<Integer> skinIndicesNeeded = node.getSkins();

        if (skinIndicesNeeded == null) {
            logger.debug("Creating dependencies for all skins ({} total)",numSkins );
            skinIndicesNeeded = new HashSet<Integer>();
            for(int i = 0; i < numSkins; i++){
                skinIndicesNeeded.add(i);
            }
        }

        //Specific skins requested
        for (Integer skin : skinIndicesNeeded) {
            if (skin >= numSkins) {
                logger.error("Invalid skin #{} requested, but only {} exist. Assuming default skin.", skin, numSkins);
                skin = 0;
            }
            if (numSkins == 0) {
                logger.error("No skins available for this model, yet #{} was requested.", skin);
                break;
            }
            Set<String> texturesUsed= new HashSet<String>();
            texturesUsed.addAll(md.getTexturesForSkin(skin));
            for(String texName : texturesUsed){
                ret.add(new OutgoingPair(new EdgeImpl("Skin for family "+skin), new MultiPathNode(texName, paths)));
            }
        }
        



        // Physics data for mass and center of gravity etc, only required sometimes
        boolean isStationary = (!node.isMovable());
        final String phyPath = baseName + "." + EXTENSION_PHYSDATA;
        ret.add(new OutgoingPair(new EdgeImpl("Physdata", isStationary), new PhyNode(phyPath, node.isMovable())));


        // Optimized rendering information for various render modes (ex. sw, dx80, dx90)
        for (VtexMode m : VtexMode.values()) {
            final String vtxPath = baseName + "." + m.toString() + "." + EXTENSION_TEXSTRIPS;
            ret.add(new OutgoingPair(new EdgeImpl(m.getName() + " tex-strip"), new MiscFileNode(vtxPath)));
        }

        // Extra Valve data for the model (does this include gibbing data?)
        //TODO is this required for prop_static
        final String vvdPath = baseName + "." + EXTENSION_VVD;
        ret.add(new OutgoingPair(new EdgeImpl("VVD"), new MiscFileNode(vvdPath)));

        // Optional animations file
        final String aniPath = baseName + "." + EXTENSION_ANIMATIONS;
        ret.add(new OutgoingPair(new EdgeImpl("ANI", true), new MiscFileNode(aniPath)));

        return ret;
    }
}
