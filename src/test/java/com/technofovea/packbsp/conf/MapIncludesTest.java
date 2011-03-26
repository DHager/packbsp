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
package com.technofovea.packbsp.conf;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Darien Hager
 */
public class MapIncludesTest {

    @Test
    public void testConfig() throws Exception {

        final String mapname = "testmap";

        final Set<String> expected = new HashSet<String>();
        expected.add("materials/vgui/maps/menu_photos_testmap.vmt");
        expected.add("scripts/soundscapes_testmap.txt");
        expected.add("maps/testmap_portuguese.txt");
        expected.add("maps/testmap.nav");
        expected.add("maps/testmap.txt");
        expected.add("maps/testmap_commentary.txt");

        expected.add("maps/testmap_danish.txt");
        expected.add("maps/testmap_finnish.txt");
        expected.add("maps/testmap_particles.txt");
        expected.add("maps/testmap_dutch.txt");
        expected.add("maps/testmap_norwegian.txt");
        expected.add("maps/testmap_english.txt");
        expected.add("maps/testmap_italian.txt");
        expected.add("maps/testmap_schinese.txt");
        expected.add("maps/testmap_russian.txt");
        expected.add("maps/testmap_korean.txt");
        expected.add("maps/testmap_swedish.txt");
        expected.add("maps/testmap_spanish.txt");
        expected.add("maps/testmap_tchinese.txt");
        expected.add("maps/testmap_french.txt");
        expected.add("maps/testmap_polish.txt");
        expected.add("maps/testmap_japanese.txt");
        expected.add("maps/testmap_german.txt");


        File src = new File("conf/map_includes.xml");
        MapIncludes conf2 = MapIncludes.fromXml(src);


        Set<IncludeItem> results = conf2.getItems("Team Fortress 2", "orangebox");
        Set<String> actual = new HashSet<String>();
        for (IncludeItem item : results) {
            actual.addAll(item.dereference(mapname));
        }
        Assert.assertEquals(expected, actual);



    }
}
