/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.hl2parse.ParseUtil;
import com.technofovea.hl2parse.registry.BlobFolder;
import com.technofovea.hl2parse.registry.BlobParseFailure;
import com.technofovea.hl2parse.registry.ClientRegistry;
import com.technofovea.hl2parse.registry.RegParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Darien Hager
 */
public class RegistryFactory  {

    private static final Logger logger = LoggerFactory.getLogger(RegistryFactory.class);

    public ClientRegistry create(File steamDir) throws PhaseFailedException {
        final String STEAM_BLOB_NAME = "clientregistry.blob";
        final File regFile = new File(steamDir, STEAM_BLOB_NAME);
        if (!regFile.isFile()) {
            throw new PhaseFailedException("Missing registry blob").addLocalization("error.input.no_clientregistry_blob", STEAM_BLOB_NAME);
        }

        logger.debug("Copying clientregistry from {}", regFile);
        final File regCopy;
        try {
            regCopy = createTempCopy(regFile);
            logger.debug("Created temporary clientregistry copy at {}", regCopy);

        }
        catch (IOException ex) {
            throw new PhaseFailedException("Couldn't copy client registry blob", ex).addLocalization("error.cant_copy_blob");
        }

        logger.debug("Attempting to parse client registry blob", regCopy);
        final ClientRegistry reg;



        try {
            BlobFolder bf = RegParser.parseClientRegistry(ParseUtil.mapFile(regCopy));
            reg = new ClientRegistry(bf);
        }
        catch (IOException ex) {
            throw new PhaseFailedException("Can't access blob file", ex).addLocalization("error.cant_read_registry");
        }
        catch (BlobParseFailure ex) {
            throw new PhaseFailedException("Can't parse blob file", ex).addLocalization("error.cant_parse_registry");
        }

        return reg;
    }

    protected static File createTempCopy(File source) throws IOException {
        String ext = FilenameUtils.getExtension(source.getName());
        File dest = File.createTempFile("packbsp_temp_", "." + ext);
        dest.deleteOnExit();
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(dest);
        IOUtils.copy(fis, fos);
        fis.close();
        fos.close();
        return dest;
    }
}
