/*
 * 
 */
package com.technofovea.packbsp.crawling2;

/**
 *

 * Node equality (and {@link Object#hashCode()} values) are very important
 * since it is the mechanism used for avoiding duplicate nodes and to allow
 * paths in the dependency graph to recombine.
 * 
 * Node identity should be constant for the duration of the time it is inside
 * the graph. This means all transient or mutable information about the node 
 * needs to be stored indirectly, often inside a {@link NodeHandler}.
 * 
 * @author Darien Hager
 */
public interface Node {



}
