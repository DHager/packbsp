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

import com.technofovea.hl2parse.mdl.PhyData;
import com.technofovea.hl2parse.mdl.PhyParseException;
import com.technofovea.hl2parse.vdf.PropDataReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EdgeImpl;
import com.technofovea.packbsp.crawling.OutgoingPair;
import com.technofovea.packbsp.crawling.nodes.ModelNode;
import com.technofovea.packbsp.crawling.nodes.PhyNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
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
public class PhyHandler implements NodeHandler<Edge, PhyNode> {

    private static final Logger logger = LoggerFactory.getLogger(PhyHandler.class);

    public List<OutgoingPair> handle(Edge edge, PhyNode node, File dataSource) throws HandlingException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();

        if(!node.isMovable()){
            logger.debug("Skipping non-dynamic PHY file.");
            return ret;
        }

        logger.debug("Loading PHY data from: {}", dataSource.getAbsolutePath());

        MappedByteBuffer mbb;
        PhyData pd;
        try {
            FileInputStream fos = new FileInputStream(dataSource);
            FileChannel fc = fos.getChannel();
            mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, dataSource.length());
            mbb.order(ByteOrder.LITTLE_ENDIAN);
        } catch (IOException ioe) {
            throw new HandlingException("Unable to load PHY file", ioe);
        }
        try {
            pd = new PhyData(mbb);
        } catch (PhyParseException ex) {
            throw new HandlingException("Unable to interpret PHY file", ex);
        }



        String propData = pd.getPropData();
        logger.debug("Found prop-data in PHY file, length {}", propData.length());


        CharStream cs = new ANTLRStringStream(propData);
        ValveTokenLexer lexer = new ValveTokenLexer(cs);
        SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
        VdfRoot root;
        try {
            root = parser.main();
        } catch (RecognitionException ex) {
            throw new HandlingException("Unable to interpret PHY file prop-data", ex);
        }


        PropDataReader pdr = new PropDataReader(root);

        Set<String> gibs = pdr.getAllGibs();
        if (gibs.size() > 0) {
            logger.debug("Found gibs in PHY file: {}", gibs);
        }
        for (String path : pdr.getAllGibs()) {
            path = ModelHandler.MODEL_DIR + "/" + path + ".mdl";
            ret.add(new OutgoingPair(new EdgeImpl("Gib"), new ModelNode(path, null, true)));
        }

        return ret;

    }
}
