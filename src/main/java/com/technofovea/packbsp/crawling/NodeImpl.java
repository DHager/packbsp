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
package com.technofovea.packbsp.crawling;

import com.technofovea.packbsp.crawling.Node;

/**
 *
 * @author Darien Hager
 */
public abstract class NodeImpl implements Node {

    public static String stringify(NodeImpl imp, Object... values){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(imp.getClass().getSimpleName());
        sb.append(" : ");
        for(int i = 0; (i+1) < values.length; i+=2){
            Object k = values[i];
            Object v = values[i+1];

            if(v == null && "".equals(v)){
                continue;
            }
            sb.append(k);
            sb.append("=");
            sb.append(v);
            if(i+3 < values.length){
                sb.append(", ");
            }
        }

        sb.append("}");
        return sb.toString();

    }

   

}
