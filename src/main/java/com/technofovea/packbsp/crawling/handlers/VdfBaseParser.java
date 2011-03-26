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

import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.OutgoingPair;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Darien Hager
 */
public abstract class VdfBaseParser<E extends Edge, T extends Node> implements NodeHandler<E, T> {

    public VdfBaseParser() {
    }

    public List<OutgoingPair> handle(E edge, T node, File targetFile) throws HandlingException {

        VdfRoot root = null;

        try {
            CharStream cs = new ANTLRFileStream(targetFile.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(cs);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            root = parser.main();
        } catch (RecognitionException ex) {
            throw new HandlingException("Could not recognize the contents of: "+targetFile.getAbsolutePath(), ex);
        } catch (IOException ioe) {
            throw new HandlingException("Could not access file: "+targetFile.getAbsolutePath(), ioe);
        }

        return innerParse(node, root);


    }

    protected abstract List<OutgoingPair> innerParse(T node, VdfRoot root) throws HandlingException;
}
