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

import com.technofovea.hl2parse.ParseUtil;
import com.technofovea.hl2parse.bsp.BspParseException;
import com.technofovea.hl2parse.entdata.EntdataException;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.hl2parse.bsp.SourceMapAnalyzer;
import com.technofovea.hl2parse.entdata.DefaultPathFixer;
import com.technofovea.hl2parse.entdata.DependencyFinder;
import com.technofovea.hl2parse.entdata.ValueSource;
import com.technofovea.hl2parse.entdata.MapEntity;
import com.technofovea.hl2parse.fgd.FgdSpec;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.packbsp.conf.IncludeItem;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EdgeImpl;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.OutgoingPair;
import com.technofovea.packbsp.crawling.nodes.MapNode;
import com.technofovea.packbsp.crawling.nodes.MaterialNode;
import com.technofovea.packbsp.crawling.nodes.MiscFileNode;
import com.technofovea.packbsp.crawling.nodes.ModelNode;
import com.technofovea.packbsp.crawling.nodes.ParticleManifestNode;
import com.technofovea.packbsp.crawling.nodes.SkyboxNode;
import com.technofovea.packbsp.crawling.nodes.SoundscapeNode;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class MapHandler implements NodeHandler<Edge, MapNode> {

    protected static final String SUFFIX_BSP = ".bsp";
    private static final Logger logger = LoggerFactory.getLogger(MapHandler.class);
    protected FgdSpec spec;
    protected Set<IncludeItem> includes;

    public MapHandler(FgdSpec spec, Set<IncludeItem> includes) {
        this.spec = spec;
        this.includes = includes;
    }

    public List<OutgoingPair> handle(Edge edge, MapNode node, File targetFile) throws HandlingException {

        List<OutgoingPair> successors = new ArrayList<OutgoingPair>();
        if (targetFile == null) {
            // We *could* grab it from MapNode, but that erodes the contract from NodeHandler
            throw new HandlingException("No file for map given");
        }
        // First, do the VBSPinfo call to get static/dynamic props and world brush textures
        Collection<String> matPaths;
        Map<String, Set<Integer>> mdlPathsWithSkins;

        SourceMapAnalyzer sma;
        try {
            logger.info("Loading binary data from map \"{}\": {}", node.getMapName(), targetFile);
            ByteBuffer bb = ParseUtil.mapFile(targetFile);
            sma = new SourceMapAnalyzer(bb);
        } catch (IOException ex) {
            throw new HandlingException(ex);
        } catch (BspParseException ex) {
            throw new HandlingException(ex);
        }

        String mapBaseName = stripMapExtension(node.getMapName());
        matPaths = sma.getBrushTextures();
        logger.debug("Found {} materials in BSP", matPaths.size());
        mdlPathsWithSkins = sma.getStaticPropSkins();
        logger.debug("Found {} static models with skin information in BSP", mdlPathsWithSkins.size());

        for (String path : matPaths) {

            if (!path.toLowerCase().endsWith(".vmt")) {
                path = path + ".vmt";
            }

            successors.add(new OutgoingPair(new EdgeImpl("Brush texture"), new MaterialNode(path)));
        }

        for (String path : mdlPathsWithSkins.keySet()) {
            Set<Integer> skins = mdlPathsWithSkins.get(path);
            // "model/" prefix should already be present
            if (!path.toLowerCase().endsWith(".mdl")) {
                path = path + ".mdl";
            }

            successors.add(new OutgoingPair(new EdgeImpl("Static prop"), new ModelNode(path, skins, false)));

        }

        try {
            // Now pull out the entdata section and parse it for more game-specific stuff.
            List<OutgoingPair> entitySuccessors = parseEntityData(sma.getEntData());
            successors.addAll(entitySuccessors);
        } catch (EntdataException ex) {
            throw new HandlingException("Could not parse entity data", ex);
        } catch (RecognitionException ex) {
            throw new HandlingException("Could not parse entity data", ex);
        } catch (IOException ex) {
            throw new HandlingException("Could not parse entity data", ex);
        }

        // Now "map includes" which are based on the map base-name

        for (IncludeItem item : this.includes) {
            logger.debug("Map include {}: {}", item.getId(), item.getPath());
            for (String path : item.dereference(mapBaseName)) {
                switch (item.getType()) {
                    case MATERIAL:
                        successors.add(new OutgoingPair(new EdgeImpl(), new MaterialNode(path)));
                        break;
                    case PARTICLE_MANIFEST:
                        successors.add(new OutgoingPair(new EdgeImpl(), new ParticleManifestNode(path)));
                        break;

                    case SOUNDSCAPE:
                        successors.add(new OutgoingPair(new EdgeImpl(), new SoundscapeNode(path)));
                        break;

                    default:
                        logger.error("Unrecognized map-include type {}", item.getType());
                    // Fallthrough
                    case PLAIN:
                        successors.add(new OutgoingPair(new EdgeImpl(), new MiscFileNode(path)));
                        break;
                }
            }
        }

        return successors;
    }

    private List<OutgoingPair> parseEntityData(String entData) throws IOException, RecognitionException, EntdataException {
        List<OutgoingPair> successors = new ArrayList<OutgoingPair>();

        logger.debug("Parsing map entity data");
        CharStream ais = new ANTLRStringStream(entData);
        ValveTokenLexer lexer = new ValveTokenLexer(ais);
        SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
        VdfRoot entVr = parser.main();
        List<MapEntity> ents = MapEntity.fromVdf(entVr);

        logger.debug("Initializing dependency finder");
        DependencyFinder depFinder = new DependencyFinder(spec, ents, DefaultPathFixer.getInstance());

        //TODO detail sprites?

        for (ValueSource ep : depFinder.getPropertiesByType(DependencyFinder.PROPTYPE_MATERIAL)) {
            for (String path : depFinder.getValues(ep)) {
                Edge e = new EdgeImpl("Entity " + ep.getClassName() + "." + ep.getPropertyName());
                Node n = new MaterialNode(path);
                successors.add(new OutgoingPair(e, n));
            }
        }


        for (ValueSource ep : depFinder.getPropertiesByType(DependencyFinder.PROPTYPE_MODEL)) {
            for (String path : depFinder.getValues(ep)) {
                Edge e = new EdgeImpl("Entity " + ep.getClassName() + "." + ep.getPropertyName());
                Node n = new ModelNode(path, null, true);
                successors.add(new OutgoingPair(e, n));
            }
        }

        for (ValueSource ep : depFinder.getPropertiesByType(DependencyFinder.PROPTYPE_SOUND)) {
            for (String path : depFinder.getValues(ep)) {
                // Note this is separate from PathFixer implementations which
                // only handle data from map entities.
                if (!SoundscapeHandler.isSoundPath(path)) {
                    continue; // Don't pack these, they're non-file references like "Ambient.Rain"
                }
                Edge e = new EdgeImpl("Entity " + ep.getClassName() + "." + ep.getPropertyName());
                Node n = new MiscFileNode(path);
                successors.add(new OutgoingPair(e, n));
            }
        }


        //TODO check that this works with real sprites and not material equivalents
        for (ValueSource ep : depFinder.getPropertiesByType(DependencyFinder.PROPTYPE_SPRITE)) {
            for (String path : depFinder.getValues(ep)) {
                Edge e = new EdgeImpl("Entity " + ep.getClassName() + "." + ep.getPropertyName());
                /*
                 * Sometimes sprites are actually materials. It's a wierd legacy system.
                 * If they *are* materials, we need to make sure they get read.
                 */
                Node n;
                if(path.toLowerCase().endsWith(".vmt")){
                    n = new MaterialNode(path);
                }else{
                    n = new MiscFileNode(path);
                }
                successors.add(new OutgoingPair(e, n));
            }
        }

        for (ValueSource ep : depFinder.getPropertiesByType(DependencyFinder.PROPTYPE_DECAL)) {
            for (String path : depFinder.getValues(ep)) {
                Edge e = new EdgeImpl("Entity " + ep.getClassName() + "." + ep.getPropertyName());
                Node n = new MaterialNode(path);
                successors.add(new OutgoingPair(e, n));
            }
        }


        for (ValueSource ep : depFinder.getPropertiesByType(DependencyFinder.PROPTYPE_SCENE)) {
            for (String path : depFinder.getValues(ep)) {
                Edge e = new EdgeImpl("Entity " + ep.getClassName() + "." + ep.getPropertyName());
                Node n = new MiscFileNode(path);
                successors.add(new OutgoingPair(e, n));
            }
        }


        //Do skybox materials
        final ValueSource skyProp = DependencyFinder.TYPE_SKYBOX;
        for (String sky : depFinder.getValues(skyProp)) {
            Edge e = new EdgeImpl("Entity " + skyProp.getClassName() + "." + skyProp.getPropertyName());
            Node n = new SkyboxNode(sky);
            successors.add(new OutgoingPair(e, n));
            // More than one sky? Unlikely, but the file format makes it possible.
            // Shouldn't harm anything to keep iterating.
        }

        // trigger_vphysics_motion
        ValueSource tvmSrc = new ValueSource("trigger_vphysics_motion", "ParticleTrailMaterial",DependencyFinder.PROPTYPE_MATERIAL);
        for(String path : depFinder.getValues(tvmSrc)){
            Edge e = new EdgeImpl("Vphysics motion trail");
            Node n = new MaterialNode(path);
            successors.add(new OutgoingPair(e, n));
        }

        // material_modify_control
        ValueSource mmcSrc = new ValueSource("material_modify_control", "materialName", "vtf");
        for(String path : depFinder.getValues(mmcSrc)){
            Edge e = new EdgeImpl("Material modifier");
            Node n = new MiscFileNode(path);
            successors.add(new OutgoingPair(e, n));
        }

        // Do env_screenoverlay values (VTFs with custom prefix)
        Set<String> overlayFiles = new HashSet<String>();
        for (int i = 1; i <= 10; i++) {
            ValueSource src = new ValueSource("env_screenoverlay", "OverlayName" + i, "string");
            overlayFiles.addAll(depFinder.getValues(src));
        }
        for (String path : overlayFiles) {
            if(path.trim().length() == 0){continue;}
            
            // Paths are not fixed by the path-fixer, since the datatype is 'string'
            path = "materials/effects/"+path+".vtf";
            Edge e = new EdgeImpl("Overlay effect");
            Node n = new MiscFileNode(path);
            successors.add(new OutgoingPair(e, n));
        }


        // Do color-correction
        Set<String> ccFiles = new HashSet<String>();
        overlayFiles.addAll(depFinder.getValues(DependencyFinder.TYPE_COLOR_CORRECTION));
        overlayFiles.addAll(depFinder.getValues(DependencyFinder.TYPE_COLOR_CORRECTION_VOLUME));
        for (String path : overlayFiles) {
            Edge e = new EdgeImpl("Color-correction file");
            Node n = new MiscFileNode(path);
            successors.add(new OutgoingPair(e, n));
        }



        return successors;

    }

    public static String stripMapExtension(String name) {
        if (name.toLowerCase().endsWith(SUFFIX_BSP)) {
            return name.substring(0, name.length() - SUFFIX_BSP.length());
        } else {
            logger.warn("The map did not end with the expected suffix: {}", name);
            return name;
        }


    }

    private static boolean isCubemap(String mapName, String texPath) {
        //TODO ensure all inteCubemaprnal paths are sanitized to use forward (/) slashes
        String prefix = "materials/maps/" + mapName + "/";
        if (!texPath.startsWith(prefix)) {
            return false;
        }
        return texPath.matches("^c-?(\\d+)_-?(\\d+)_-?(\\d+)(\\.vtf)?$");

    }
}
