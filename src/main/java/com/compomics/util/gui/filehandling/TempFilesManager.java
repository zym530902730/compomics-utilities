package com.compomics.util.gui.filehandling;

import com.compomics.util.Util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class can be used to reference temp files and manage them.
 *
 * @author Marc Vaudel
 */
public class TempFilesManager {

    /**
     * List of the temp folders created during this instance.
     */
    private static final ArrayList<File> tempFolders = new ArrayList<File>();

    /**
     * Adds a temp folder to the references temp folders.
     *
     * @param tempFolder the temp folder to register
     */
    public static void registerTempFolder(File tempFolder) {
        tempFolders.add(tempFolder);
    }

    /**
     * Deletes the temp folders created.
     *
     * @throws IOException if an IOException occurs
     */
    public static void deleteTempFolders() throws IOException {
        ArrayList<String> exceptions = new ArrayList<String>();
        for (File tempFolder : tempFolders) {
            try {
                if (tempFolder.exists()) {
                    boolean success = Util.deleteDir(tempFolder); // @TODO: what to do if the file could not be deleted?
                }
            } catch (Exception e) {
                e.printStackTrace();
                exceptions.add(tempFolder.getAbsolutePath());
            }
        }
        if (!exceptions.isEmpty()) {
            String error = "An error occurred while attempting to delete the following temporary folder(s):\n";
            for (String filePath : exceptions) {
                error += filePath + "\n";
            }
            throw new IOException(error);
        }
    }
}
