/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import java.util.Set;

/**
 *
 * @author Darien Hager
 */
public interface HandlingResult {
    public boolean isLastHandler();
    public Set<GraphAddition> getAdditions();
}
