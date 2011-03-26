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

import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.hl2parse.vdf.MaterialReader;
import com.technofovea.hl2parse.xml.MaterialRefList;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EdgeImpl;
import com.technofovea.packbsp.crawling.OutgoingPair;
import com.technofovea.packbsp.crawling.nodes.MaterialNode;
import com.technofovea.packbsp.crawling.nodes.MiscFileNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;




import java.util.List;
import javax.xml.bind.JAXBException;

import org.apache.commons.jxpath.JXPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class VmtHandler extends VdfBaseParser<Edge, MaterialNode> {

    private static final Logger logger = LoggerFactory.getLogger(VmtHandler.class);
    MaterialRefList defaultList;
    MaterialRefList customList = null;

    /**
     *
     * @throws JAXBException If default XML configs for material directives could not be loaded.
     */
    public VmtHandler() throws JAXBException {
        File confFile = new File("conf/materials.xml");
        defaultList = MaterialReader.getDefaultMaterialSettings();
        if(confFile.isFile()){
            logger.debug("Attempting to load custom material configs from {}",confFile);
            try{
                customList = MaterialReader.loadFromXml(new FileInputStream(confFile));
                logger.debug("Load successful");
            }catch(FileNotFoundException fnfe){
                logger.error("Unable to load material config",fnfe);
            }
        }

    }

    @Override
    protected List<OutgoingPair> innerParse(MaterialNode node, VdfRoot root) throws HandlingException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        try {
            MaterialRefList matRefs = defaultList;
            if(customList != null){
                matRefs = customList;
            }
            MaterialReader vd = new MaterialReader(root,matRefs);

            for (String path : vd.getMaterials()) {
                // Screen out "placeholders" like "_rt_waterreflection"
                if (!path.toLowerCase().endsWith(".vmt")) {
                    logger.debug("Skipping potential material due to wrong extension: {}", path);
                    continue;
                }
                ret.add(new OutgoingPair(new EdgeImpl(), new MaterialNode(path)));

            }

            for (String path : vd.getTextures()) {
                // Screen out "placeholders" like "env_cubemap"
                if (!path.toLowerCase().endsWith(".vtf")) {
                    logger.debug("Skipping potential texture due to wrong extension: {}", path);
                    continue;
                }
                ret.add(new OutgoingPair(new EdgeImpl("Texture"), new MiscFileNode(path)));

            }
        } catch (JXPathException e) {
            throw new HandlingException("Structure invalid", e);
        }
        return ret;
    }
}
