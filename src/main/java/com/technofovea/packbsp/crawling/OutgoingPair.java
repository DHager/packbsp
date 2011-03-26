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

/**
 *
 * @author Darien Hager
 */
public class OutgoingPair {
    Edge edge;
    Node target;

    public OutgoingPair(Edge edge, Node target) {
        this.edge = edge;
        this.target = target;
    }

    public Edge getEdge() {
        return edge;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OutgoingPair other = (OutgoingPair) obj;
        if (this.edge != other.edge && (this.edge == null || !this.edge.equals(other.edge))) {
            return false;
        }
        if (this.target != other.target && (this.target == null || !this.target.equals(other.target))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.edge != null ? this.edge.hashCode() : 0);
        hash = 23 * hash + (this.target != null ? this.target.hashCode() : 0);
        return hash;
    }

    
}
