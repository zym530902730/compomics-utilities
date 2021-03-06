package com.compomics.util.experiment.identification;

import com.compomics.util.db.ObjectsCache;
import com.compomics.util.db.ObjectsDB;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.experiment.personalization.UrParameter;
import com.compomics.util.waiting.WaitingHandler;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class uses a database to manage identification matches.
 *
 * @author Marc Vaudel
 */
public class IdentificationDB implements Serializable {

    static final long serialVersionUID = 691986038787590646L;
    /**
     * The name which will be used for the database.
     */
    public String dbName;
    /**
     * The name of the protein table.
     */
    private static String proteinTableName = "proteins";
    /**
     * The suffix for protein parameters tables.
     */
    private static String proteinParametersTableSuffix = "_protein_parameters";
    /**
     * The name of the peptide table.
     */
    private static String peptideTableName = "peptides";
    /**
     * The suffix for a peptide parameters table.
     */
    private static String peptideParametersTableSuffix = "_peptide_parameters";
    /**
     * The suffix for a PSM table.
     */
    private static String psmTableSuffix = "_psms";
    /**
     * The suffix for an assumptions table.
     */
    private static String assumptionsTableSuffix = "_assumptions";
    /**
     * The suffix for a raw assumptions table.
     */
    private static String rawAssumptionsTableSuffix = "_raw_assumptions";
    /**
     * The suffix for a PSM parameters table.
     */
    private static String psmParametersTableSuffix = "_psm_parameters";
    /**
     * List of all raw assumptions tables.
     */
    private ArrayList<String> rawAssumptionsTables = new ArrayList<String>();
    /**
     * List of all assumptions tables.
     */
    private ArrayList<String> assumptionsTables = new ArrayList<String>();
    /**
     * List of all psms tables.
     */
    private ArrayList<String> psmTables = new ArrayList<String>();
    /**
     * List of all psm parameters tables.
     */
    private ArrayList<String> psmParametersTables = new ArrayList<String>();
    /**
     * List of all peptide parameters tables.
     */
    private ArrayList<String> peptideParametersTables = new ArrayList<String>();
    /**
     * List of all proteins parameters tables.
     */
    private ArrayList<String> proteinParametersTables = new ArrayList<String>();
    /**
     * The database which will contain the objects.
     */
    private ObjectsDB objectsDB;

    /**
     * Constructor creating the database and the protein and protein parameters
     * tables.
     *
     * @param folder the folder where to put the database
     * @param name the database name
     * @param deleteOldDatabase if true, tries to delete the old database
     * @param objectCache the objects cache
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public IdentificationDB(String folder, String name, boolean deleteOldDatabase, ObjectsCache objectCache) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        this.dbName = name;
        objectsDB = new ObjectsDB(folder, dbName, deleteOldDatabase, objectCache);
        if (deleteOldDatabase) {
            objectsDB.addTable(proteinTableName);
            objectsDB.addTable(peptideTableName);
        }
    }

    /**
     * Indicates whether a spectrum match is loaded.
     *
     * @param spectrumKey the spectrumMatch key
     *
     * @return a boolean indicating whether a spectrum match is loaded in the
     * given table
     *
     * @throws SQLException exception thrown whenever an exception occurred
     * while interrogating the database
     * @throws InterruptedException exception thrown if a threading error occurs
     */
    public boolean spectrumMatchLoaded(String spectrumKey) throws SQLException, InterruptedException {
        String tableName = getSpectrumMatchTable(spectrumKey);
        return objectsDB.inDB(tableName, spectrumKey, true);
    }

    /**
     * Indicates whether a peptide match is loaded.
     *
     * @param peptideKey the peptide key
     *
     * @return a boolean indicating whether a peptide match is loaded in the
     * given table
     *
     * @throws SQLException exception thrown whenever an exception occurred
     * while interrogating the database
     * @throws InterruptedException exception thrown if a threading error occurs
     */
    public boolean peptideMatchLoaded(String peptideKey) throws SQLException, InterruptedException {
        return objectsDB.inDB(peptideTableName, peptideKey, true);
    }

    /**
     * Indicates whether a protein match is loaded.
     *
     * @param proteinKey the protein key
     *
     * @return a boolean indicating whether a protein match is loaded in the
     * given table
     *
     * @throws SQLException exception thrown whenever an exception occurred
     * while interrogating the database
     * @throws InterruptedException exception thrown if a threading error occurs
     */
    public boolean proteinMatchLoaded(String proteinKey) throws SQLException, InterruptedException {
        return objectsDB.inDB(proteinTableName, proteinKey, true);
    }

    /**
     * Updates a protein match.
     *
     * @param proteinMatch the protein match
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * updating a match in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updateProteinMatch(ProteinMatch proteinMatch) throws SQLException, IOException, InterruptedException {
        objectsDB.updateObject(proteinTableName, proteinMatch.getKey(), proteinMatch);
    }

    /**
     * Updates a peptide match.
     *
     * @param peptideMatch the peptide match
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * updating a match in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updatePeptideMatch(PeptideMatch peptideMatch) throws SQLException, IOException, InterruptedException {
        objectsDB.updateObject(peptideTableName, peptideMatch.getKey(), peptideMatch);
    }

    /**
     * Updates a spectrum match.
     *
     * @param spectrumMatch the spectrum match
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * updating a match in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updateSpectrumMatch(SpectrumMatch spectrumMatch) throws SQLException, IOException, InterruptedException {
        String key = spectrumMatch.getKey();
        String tableName = getSpectrumMatchTable(key);
        objectsDB.updateObject(tableName, key, spectrumMatch);
    }

    /**
     * Updates the map of assumptions for a given spectrum.
     *
     * @param spectrumKey the key of the spectrum
     * @param assumptionsMap map of assumptions
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * updating a match in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updateAssumptions(String spectrumKey, HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptionsMap) throws SQLException, IOException, InterruptedException {
        String tableName = getAssumptionTable(spectrumKey);
        objectsDB.updateObject(tableName, spectrumKey, assumptionsMap);
    }

    /**
     * Updates the map of raw assumptions for a given spectrum.
     *
     * @param spectrumKey the key of the spectrum
     * @param rawAssumptionsMap map of assumptions
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * updating a match in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updateRawAssumptions(String spectrumKey, HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> rawAssumptionsMap) throws SQLException, IOException, InterruptedException {
        String tableName = getRawAssumptionTable(spectrumKey);
        objectsDB.updateObject(tableName, spectrumKey, rawAssumptionsMap);
    }

    /**
     * Updates a match.
     *
     * @param match the match to update
     * @throws SQLException exception thrown whenever an error occurred while
     * updating a match in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updateMatch(IdentificationMatch match) throws SQLException, IOException, InterruptedException {
        switch (match.getType()) {
            case Spectrum:
                updateSpectrumMatch((SpectrumMatch) match);
                return;
            case Peptide:
                updatePeptideMatch((PeptideMatch) match);
                return;
            case Protein:
                updateProteinMatch((ProteinMatch) match);
        }
    }

    /**
     * Updates a protein match parameter.
     *
     * @param key the key of the protein match
     * @param urParameter the parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * updating the parameter in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updateProteinParameter(String key, UrParameter urParameter) throws SQLException, IOException, InterruptedException {
        String tableName = getProteinParameterTable(urParameter);
        objectsDB.updateObject(tableName, key, urParameter);
    }

    /**
     * Updates a peptide match parameter.
     *
     * @param key the key of the peptide match
     * @param urParameter the parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * updating the parameter in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updatePeptideParameter(String key, UrParameter urParameter) throws SQLException, IOException, InterruptedException {
        String tableName = getPeptideParameterTable(urParameter);
        objectsDB.updateObject(tableName, key, urParameter);
    }

    /**
     * Updates a spectrum match parameter.
     *
     * @param key the key of the spectrum match
     * @param urParameter the parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * updating the parameter in the table
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void updateSpectrumParameter(String key, UrParameter urParameter) throws SQLException, IOException, InterruptedException {
        String tableName = getSpectrumParameterTable(key, urParameter);
        objectsDB.updateObject(tableName, key, urParameter);
    }

    /**
     * Deletes a protein match from the database.
     *
     * @param key the key of the match
     * @throws SQLException exception thrown whenever an error occurred while
     * deleting the match
     * @throws IOException exception thrown whenever an error occurred while
     * reading or writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void removeProteinMatch(String key) throws SQLException, IOException, InterruptedException {
        objectsDB.deleteObject(proteinTableName, key);
        for (String proteinParameterTable : proteinParametersTables) {
            objectsDB.deleteObject(proteinParameterTable, key);
        }
    }

    /**
     * Deletes a peptide match from the database.
     *
     * @param key the key of the match
     * @throws SQLException exception thrown whenever an error occurred while
     * deleting the match
     * @throws IOException exception thrown whenever an error occurred while
     * reading or writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void removePeptideMatch(String key) throws SQLException, IOException, InterruptedException {
        objectsDB.deleteObject(peptideTableName, key);
        for (String peptideParameterTable : peptideParametersTables) {
            objectsDB.deleteObject(peptideParameterTable, key);
        }
    }

    /**
     * Deletes a spectrum match from the database.
     *
     * @param key the key of the match
     * @throws SQLException exception thrown whenever an error occurred while
     * deleting the match
     * @throws IOException exception thrown whenever an error occurred while
     * reading or writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void removeSpectrumMatch(String key) throws SQLException, IOException, InterruptedException {
        for (String psmTable : psmTables) {
            objectsDB.deleteObject(psmTable, key);
        }
        for (String psmParameterTable : psmParametersTables) {
            objectsDB.deleteObject(psmParameterTable, key);
        }
    }

    /**
     * Deletes the assumptions corresponding to a given psm from the database.
     *
     * @param key the key of the psm
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * deleting the match
     * @throws IOException exception thrown whenever an error occurred while
     * reading or writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void removeAssumptions(String key) throws SQLException, IOException, InterruptedException {
        for (String table : assumptionsTables) {
            objectsDB.deleteObject(table, key);
        }
    }

    /**
     * Deletes the raw assumptions corresponding to a given psm from the database.
     *
     * @param key the key of the psm
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * deleting the match
     * @throws IOException exception thrown whenever an error occurred while
     * reading or writing in the database
     * @throws java.lang.InterruptedException if the thread is interrupted
     */
    public void removeRawAssumptions(String key) throws SQLException, IOException, InterruptedException {
        for (String table : rawAssumptionsTables) {
            objectsDB.deleteObject(table, key);
        }
    }

    /**
     * Returns the names of the tables containing peptide parameters.
     *
     * @return the names of the tables containing peptide parameters
     */
    public ArrayList<String> getPeptideParametersTables() {
        return peptideParametersTables;
    }

    /**
     * Returns the assumptions of the given spectrum in a map: advocate id →
     * score → list of assumptions.
     *
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     * @param key the key of the spectrum
     *
     * @return the assumptions
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * loading the object from the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the object in the database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while casting the database input in the desired match class
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> getAssumptions(String key, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String tableName = getAssumptionTable(key);
        checkTable(assumptionsTables, tableName);
        return (HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>>) objectsDB.retrieveObject(tableName, key, useDB);
    }

    /**
     * Adds assumptions for a given match to the database.
     *
     * @param spectrumKey the key of the spectrum
     * @param assumptions map of all assumptions
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addAssumptions(String spectrumKey, HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptions) throws SQLException, IOException, InterruptedException {
        String tableName = getAssumptionTable(spectrumKey);
        checkTable(assumptionsTables, tableName);
        objectsDB.insertObject(tableName, spectrumKey, assumptions, true);
    }

    /**
     * Returns the raw assumptions of the given spectrum in a map: advocate id →
     * score → list of assumptions.
     *
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     * @param key the key of the spectrum
     *
     * @return the assumptions
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * loading the object from the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the object in the database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while casting the database input in the desired match class
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> getRawAssumptions(String key, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String tableName = getRawAssumptionTable(key);
        checkTable(rawAssumptionsTables, tableName);
        return (HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>>) objectsDB.retrieveObject(tableName, key, useDB);
    }

    /**
     * Adds raw assumptions for a given match to the database.
     *
     * @param spectrumKey the key of the spectrum
     * @param assumptions map of all assumptions
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addRawAssumptions(String spectrumKey, HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptions) throws SQLException, IOException, InterruptedException {
        String tableName = getRawAssumptionTable(spectrumKey);
        checkTable(rawAssumptionsTables, tableName);
        objectsDB.insertObject(tableName, spectrumKey, assumptions, true);
    }

    /**
     * Returns the desired spectrum match.
     *
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     * @param key the PSM key
     * @return the spectrum match
     * @throws SQLException exception thrown whenever an error occurred while
     * loading the object from the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the object in the database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while casting the database input in the desired match class
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public SpectrumMatch getSpectrumMatch(String key, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String tableName = getSpectrumMatchTable(key);
        return (SpectrumMatch) objectsDB.retrieveObject(tableName, key, useDB);
    }

    /**
     * Adds a spectrum match to the database.
     *
     * @param spectrumMatch the spectrum match to be added
     * 
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addSpectrumMatch(SpectrumMatch spectrumMatch) throws SQLException, IOException, InterruptedException {
        String key = spectrumMatch.getKey();
        String tableName = getSpectrumMatchTable(key);
        checkTable(psmTables, tableName);
        objectsDB.insertObject(tableName, key, spectrumMatch, true);
    }

    /**
     * Indicates whether the table for the given spectrum match key has been
     * created.
     *
     * @param spectrumMatchKey the key of the spectrum match of interest
     *
     * @return true if the table for the given spectrum match key has been
     * created
     */
    public boolean spectrumMatchTableCreated(String spectrumMatchKey) {
        String tableName = getSpectrumMatchTable(spectrumMatchKey);
        return psmTables.contains(tableName);
    }

    /**
     * Returns the desired peptide match.
     *
     * @param key the peptide key
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     * @return the peptide match
     * @throws SQLException exception thrown whenever an error occurred while
     * loading the object from the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the object in the database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while casting the database input in the desired match class
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public PeptideMatch getPeptideMatch(String key, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        return (PeptideMatch) objectsDB.retrieveObject(peptideTableName, key, useDB);
    }

    /**
     * Adds a peptide match to the database.
     *
     * @param peptideMatch the peptide match to be added
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addPeptideMatch(PeptideMatch peptideMatch) throws SQLException, IOException, InterruptedException {
        objectsDB.insertObject(peptideTableName, peptideMatch.getKey(), peptideMatch, true);
    }

    /**
     * Returns the desired protein match.
     *
     * @param key the protein key
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     * @return the protein match
     * @throws SQLException exception thrown whenever an error occurred while
     * loading the object from the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the object in the database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while casting the database input in the desired match class
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public ProteinMatch getProteinMatch(String key, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        return (ProteinMatch) objectsDB.retrieveObject(proteinTableName, key, useDB);
    }

    /**
     * Adds a protein match to the database.
     *
     * @param proteinMatch the protein match to be added
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addProteinMatch(ProteinMatch proteinMatch) throws SQLException, IOException, InterruptedException {
        objectsDB.insertObject(proteinTableName, proteinMatch.getKey(), proteinMatch, true);
    }

    /**
     * Adds an identification match to the database.
     *
     * @param match the match to be added
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addMatch(IdentificationMatch match) throws SQLException, IOException, InterruptedException {
        switch (match.getType()) {
            case Spectrum:
                addSpectrumMatch((SpectrumMatch) match);
                return;
            case Peptide:
                addPeptideMatch((PeptideMatch) match);
                return;
            case Protein:
                addProteinMatch((ProteinMatch) match);
        }
    }

    /**
     * Loads all assumptions of the given file in the cache of the database.
     *
     * @param fileName the file name
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadAssumptions(String fileName, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String testKey = Spectrum.getSpectrumKey(fileName, "test");
        String tableName = getAssumptionTable(testKey);
        objectsDB.loadObjects(tableName, waitingHandler, displayProgress);
    }

    /**
     * Loads all assumptions of the given spectra in the cache of the database.
     *
     * @param spectrumKeys the key of the spectra
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadAssumptions(ArrayList<String> spectrumKeys, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (waitingHandler != null && displayProgress) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(false);
            waitingHandler.setSecondaryProgressCounter(0);
            waitingHandler.setMaxSecondaryProgressCounter(2 * spectrumKeys.size());
        }
        HashMap<String, ArrayList<String>> sortedKeys = new HashMap<String, ArrayList<String>>();
        for (String spectrumKey : spectrumKeys) {
            String tableName = getAssumptionTable(spectrumKey);
            if (!sortedKeys.containsKey(tableName)) {
                sortedKeys.put(tableName, new ArrayList<String>());
            }
            sortedKeys.get(tableName).add(spectrumKey);
            if (waitingHandler != null) {
                if (displayProgress) {
                    waitingHandler.increaseSecondaryProgressCounter();
                }
                if (waitingHandler.isRunCanceled()) {
                    break;
                }
            }
        }
        for (String tableName : sortedKeys.keySet()) {
            if (objectsDB.hasTable(tableName)) { // Escape for old projects which don't contain this table
                objectsDB.loadObjects(tableName, sortedKeys.get(tableName), waitingHandler, displayProgress);
            }
        }
    }

    /**
     * Loads all raw assumptions of the given file in the cache of the database.
     *
     * @param fileName the file name
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadRawAssumptions(String fileName, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String testKey = Spectrum.getSpectrumKey(fileName, "test");
        String tableName = getRawAssumptionTable(testKey);
        objectsDB.loadObjects(tableName, waitingHandler, displayProgress);
    }

    /**
     * Loads all raw assumptions of the given spectra in the cache of the database.
     *
     * @param spectrumKeys the key of the spectra
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadRawAssumptions(ArrayList<String> spectrumKeys, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (waitingHandler != null && displayProgress) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(false);
            waitingHandler.setSecondaryProgressCounter(0);
            waitingHandler.setMaxSecondaryProgressCounter(2 * spectrumKeys.size());
        }
        HashMap<String, ArrayList<String>> sortedKeys = new HashMap<String, ArrayList<String>>();
        for (String spectrumKey : spectrumKeys) {
            String tableName = getRawAssumptionTable(spectrumKey);
            if (!sortedKeys.containsKey(tableName)) {
                sortedKeys.put(tableName, new ArrayList<String>());
            }
            sortedKeys.get(tableName).add(spectrumKey);
            if (waitingHandler != null) {
                if (displayProgress) {
                    waitingHandler.increaseSecondaryProgressCounter();
                }
                if (waitingHandler.isRunCanceled()) {
                    break;
                }
            }
        }
        for (String tableName : sortedKeys.keySet()) {
            if (objectsDB.hasTable(tableName)) { // Escape for old projects which don't contain this table
                objectsDB.loadObjects(tableName, sortedKeys.get(tableName), waitingHandler, displayProgress);
            }
        }
    }

    /**
     * Loads all spectrum matches of the given file in the cache of the
     * database.
     *
     * @param fileName the file name
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadSpectrumMatches(String fileName, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String testKey = Spectrum.getSpectrumKey(fileName, "test");
        String tableName = getSpectrumMatchTable(testKey);
        objectsDB.loadObjects(tableName, waitingHandler, displayProgress);
    }

    /**
     * Loads all given spectrum matches in the cache of the database.
     *
     * @param spectrumKeys the key of the spectrum matches to be loaded
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadSpectrumMatches(ArrayList<String> spectrumKeys, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (waitingHandler != null && displayProgress) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(false);
            waitingHandler.setSecondaryProgressCounter(0);
            waitingHandler.setMaxSecondaryProgressCounter(2 * spectrumKeys.size());
        }
        HashMap<String, ArrayList<String>> sortedKeys = new HashMap<String, ArrayList<String>>();
        for (String spectrumKey : spectrumKeys) {
            String tableName = getSpectrumMatchTable(spectrumKey);
            if (!sortedKeys.containsKey(tableName)) {
                sortedKeys.put(tableName, new ArrayList<String>());
            }
            sortedKeys.get(tableName).add(spectrumKey);
            if (waitingHandler != null) {
                if (displayProgress) {
                    waitingHandler.increaseSecondaryProgressCounter();
                }
                if (waitingHandler.isRunCanceled()) {
                    break;
                }
            }
        }
        for (String tableName : sortedKeys.keySet()) {
            objectsDB.loadObjects(tableName, sortedKeys.get(tableName), waitingHandler, displayProgress);
        }
    }

    /**
     * Loads all spectrum match parameters of the given type in the cache of the
     * database.
     *
     * @param fileName the file name
     * @param urParameter the parameter type
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadSpectrumMatchParameters(String fileName, UrParameter urParameter, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String testKey = Spectrum.getSpectrumKey(fileName, "test");
        String tableName = getSpectrumParameterTable(testKey, urParameter);
        objectsDB.loadObjects(tableName, waitingHandler, displayProgress);
    }

    /**
     * Loads all desired spectrum match parameters in the cache of the database.
     *
     * @param spectrumKeys the key of the spectrum match of the parameters to be
     * loaded
     * @param urParameter the parameter type
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws InterruptedException if an InterruptedException is thrown
     */
    public void loadSpectrumMatchParameters(ArrayList<String> spectrumKeys, UrParameter urParameter, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (waitingHandler != null && displayProgress) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(false);
            waitingHandler.setSecondaryProgressCounter(0);
            waitingHandler.setMaxSecondaryProgressCounter(2 * spectrumKeys.size());
        }
        HashMap<String, ArrayList<String>> sortedKeys = new HashMap<String, ArrayList<String>>();
        for (String spectrumKey : spectrumKeys) {
            String tableName = getSpectrumParameterTable(spectrumKey, urParameter);
            if (!sortedKeys.containsKey(tableName)) {
                sortedKeys.put(tableName, new ArrayList<String>());
            }
            sortedKeys.get(tableName).add(spectrumKey);
            if (waitingHandler != null) {
                if (displayProgress) {
                waitingHandler.increaseSecondaryProgressCounter();
                }
                if (waitingHandler.isRunCanceled()) {
                    break;
                }
            }
        }
        for (String tableName : sortedKeys.keySet()) {
            objectsDB.loadObjects(tableName, sortedKeys.get(tableName), waitingHandler, displayProgress);
        }
    }

    /**
     * Loads all peptide matches in the cache of the database.
     *
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadPeptideMatches(WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException, InterruptedException {
        objectsDB.loadObjects(peptideTableName, waitingHandler, displayProgress);
    }

    /**
     * Loads the desired peptide matches of the given type in the cache of the
     * database.
     *
     * @param peptideKeys the list of peptide keys to load
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadPeptideMatches(ArrayList<String> peptideKeys, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (waitingHandler != null && displayProgress) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(false);
            waitingHandler.setSecondaryProgressCounter(0);
            waitingHandler.setMaxSecondaryProgressCounter(peptideKeys.size());
        }
        objectsDB.loadObjects(peptideTableName, peptideKeys, waitingHandler, displayProgress);
    }

    /**
     * Loads all peptide match parameters of the given type in the cache of the
     * database.
     *
     * @param urParameter the parameter type
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadPeptideMatchParameters(UrParameter urParameter, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String tableName = getPeptideParameterTable(urParameter);
        objectsDB.loadObjects(tableName, waitingHandler, displayProgress);
    }

    /**
     * Loads the desired peptide match parameters of the given type in the cache
     * of the database.
     *
     * @param peptideKeys the list of peptide keys of the parameters to load
     * @param urParameter the parameter type
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadPeptideMatchParameters(ArrayList<String> peptideKeys, UrParameter urParameter, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (waitingHandler != null && displayProgress) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(false);
            waitingHandler.setSecondaryProgressCounter(0);
            waitingHandler.setMaxSecondaryProgressCounter(peptideKeys.size());
        }
        String tableName = getPeptideParameterTable(urParameter);
        objectsDB.loadObjects(tableName, peptideKeys, waitingHandler, displayProgress);
    }

    /**
     * Loads all protein matches in the cache of the database.
     *
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadProteinMatches(WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        objectsDB.loadObjects(proteinTableName, waitingHandler, displayProgress);
    }

    /**
     * Loads the desired protein matches of the given type in the cache of the
     * database.
     *
     * @param proteinKeys the list of protein keys to load
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadProteinMatches(ArrayList<String> proteinKeys, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException, InterruptedException {
        if (waitingHandler != null && displayProgress) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(false);
            waitingHandler.setSecondaryProgressCounter(0);
            waitingHandler.setMaxSecondaryProgressCounter(proteinKeys.size());
        }
        objectsDB.loadObjects(proteinTableName, proteinKeys, waitingHandler, displayProgress);
    }

    /**
     * Loads all protein match parameters of the given type in the cache of the
     * database.
     *
     * @param urParameter the parameter type
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadProteinMatchParameters(UrParameter urParameter, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String tableName = getProteinParameterTable(urParameter);
        objectsDB.loadObjects(tableName, waitingHandler, displayProgress);
    }

    /**
     * Loads the desired protein match parameters of the given type in the cache
     * of the database.
     *
     * @param proteinKeys the list of protein keys of the parameters to load
     * @param urParameter the parameter type
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interrogating the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws ClassNotFoundException exception thrown whenever the class of the
     * object is not found when deserializing it.
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void loadProteinMatchParameters(ArrayList<String> proteinKeys, UrParameter urParameter, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (waitingHandler != null && displayProgress) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(false);
            waitingHandler.setSecondaryProgressCounter(0);
            waitingHandler.setMaxSecondaryProgressCounter(proteinKeys.size());
        }
        String tableName = getProteinParameterTable(urParameter);
        objectsDB.loadObjects(tableName, proteinKeys, waitingHandler, displayProgress);
    }

    /**
     * Returns the desired spectrum match parameter.
     *
     * @param key the PSM key
     * @param urParameter the match parameter
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     *
     * @return the spectrum match parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * loading the object from the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the object in the database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while casting the database input in the desired match class
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public UrParameter getSpectrumMatchParameter(String key, UrParameter urParameter, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String tableName = getSpectrumParameterTable(key, urParameter);
        return (UrParameter) objectsDB.retrieveObject(tableName, key, useDB);
    }

    /**
     * Adds a spectrum match parameter to the database.
     *
     * @param key the PSM key
     * @param urParameter the match parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addSpectrumMatchParameter(String key, UrParameter urParameter) throws SQLException, IOException, InterruptedException {
        String tableName = getSpectrumParameterTable(key, urParameter);
        checkTable(psmParametersTables, tableName);
        objectsDB.insertObject(tableName, key, urParameter, true);
    }

    /**
     * Returns the desired peptide match parameter.
     *
     * @param key the peptide key
     * @param urParameter the match parameter
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     * @return the peptide match parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * loading the object from the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the object in the database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while casting the database input in the desired match class
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public UrParameter getPeptideMatchParameter(String key, UrParameter urParameter, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String tableName = getPeptideParameterTable(urParameter);
        return (UrParameter) objectsDB.retrieveObject(tableName, key, useDB);
    }

    /**
     * Adds a peptide match parameter to the database.
     *
     * @param key the peptide key
     * @param urParameter the match parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addPeptideMatchParameter(String key, UrParameter urParameter) throws SQLException, IOException, InterruptedException {
        String tableName = getPeptideParameterTable(urParameter);
        checkTable(peptideParametersTables, tableName);
        objectsDB.insertObject(tableName, key, urParameter, true);
    }

    /**
     * Returns the desired protein match parameter.
     *
     * @param key the protein key
     * @param urParameter the match parameter
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     * @return the protein match parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * loading the object from the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the object in the database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while casting the database input in the desired match class
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public UrParameter getProteinMatchParameter(String key, UrParameter urParameter, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        String tableName = getProteinParameterTable(urParameter);
        return (UrParameter) objectsDB.retrieveObject(tableName, key, useDB);
    }

    /**
     * Adds a protein match parameter to the database.
     *
     * @param key the protein key
     * @param urParameter the match parameter
     * @throws SQLException exception thrown whenever an error occurred while
     * adding the object in the database
     * @throws IOException exception thrown whenever an error occurred while
     * writing the object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading issue occurred when interacting with the database
     */
    public void addProteinMatchParameter(String key, UrParameter urParameter) throws SQLException, IOException, InterruptedException {
        String tableName = getProteinParameterTable(urParameter);
        checkTable(proteinParametersTables, tableName);
        objectsDB.insertObject(tableName, key, urParameter, true);
    }

    /**
     * Verifies that a table exists and creates it if not.
     *
     * @param tableList the list containing the created tables
     * @param tableName the name of the table
     *
     * @throws SQLException if an SQLException occurs
     * @throws IOException if an IOException occurs
     * @throws InterruptedException if an InterruptedException occurs
     */
    public synchronized void checkTable(Collection<String> tableList, String tableName) throws SQLException, IOException, InterruptedException {
        if (!tableList.contains(tableName)) {
            objectsDB.addTable(tableName);
            tableList.add(tableName);
        }
    }

    /**
     * Returns an object from the database.
     *
     * @param table the name of the table
     * @param objectKey the key of the object
     * @param useDB if useDB is false, null will be returned if the object is
     * not in the cache
     *
     * @return an object from the database
     *
     * @throws SQLException if an SQLException occurs
     * @throws IOException if an IOException occurs
     * @throws InterruptedException if an InterruptedException occurs
     * @throws ClassNotFoundException if an ClassNotFoundException occurs
     */
    public Object getObject(String table, String objectKey, boolean useDB) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        return objectsDB.retrieveObject(table, objectKey, true);
    }

    /**
     * Returns the assumptions table name associated with the given spectrum
     * key.
     *
     * @param spectrumKey the given spectrum key
     * @return the table name of the given spectrum
     */
    public String getAssumptionTable(String spectrumKey) {
        String tableName = Spectrum.getSpectrumFile(spectrumKey) + assumptionsTableSuffix;
        tableName = objectsDB.correctTableName(tableName);
        return tableName;
    }

    /**
     * Returns the raw assumptions table name associated with the given spectrum
     * key.
     *
     * @param spectrumKey the given spectrum key
     * @return the table name of the given spectrum
     */
    public String getRawAssumptionTable(String spectrumKey) {
        String tableName = Spectrum.getSpectrumFile(spectrumKey) + rawAssumptionsTableSuffix;
        tableName = objectsDB.correctTableName(tableName);
        return tableName;
    }

    /**
     * Returns the PSM table name associated with the given spectrum key.
     *
     * @param spectrumKey the given spectrum key
     * @return the table name of the given spectrum
     */
    public String getSpectrumMatchTable(String spectrumKey) {
        String tableName = Spectrum.getSpectrumFile(spectrumKey) + psmTableSuffix;
        tableName = objectsDB.correctTableName(tableName);
        return tableName;
    }

    /**
     * Returns the table name associated with the given spectrum parameter.
     *
     * @param spectrumKey the given spectrum key
     * @param urParameter the parameter
     * @return the table name of the given spectrum parameter
     */
    public String getSpectrumParameterTable(String spectrumKey, UrParameter urParameter) {
        String fileName = Spectrum.getSpectrumFile(spectrumKey);
        String tableName = urParameter.getParameterKey() + "_" + fileName + psmParametersTableSuffix;
        tableName = objectsDB.correctTableName(tableName);
        return tableName;
    }

    /**
     * Returns the table name associated with the given peptide parameter.
     *
     * @param urParameter the parameter
     * @return the table name of the given peptide parameter
     */
    public String getPeptideParameterTable(UrParameter urParameter) {
        String tableName = urParameter.getParameterKey() + peptideParametersTableSuffix;
        tableName = objectsDB.correctTableName(tableName);
        return tableName;
    }

    /**
     * Returns the table name associated with the given protein parameter.
     *
     * @param urParameter the parameter
     * @return the table name of the given protein parameter
     */
    public String getProteinParameterTable(UrParameter urParameter) {
        String tableName = urParameter.getParameterKey() + proteinParametersTableSuffix;
        tableName = objectsDB.correctTableName(tableName);
        return tableName;
    }

    /**
     * Restores the connection to the database.
     *
     * @param dbFolder the folder where the database is located
     * @param deleteOldDatabase if true, tries to delete the old database
     * @param objectsCache the objects cache
     *
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public void restoreConnection(String dbFolder, boolean deleteOldDatabase, ObjectsCache objectsCache) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        objectsDB.establishConnection(dbFolder, deleteOldDatabase, objectsCache);
        objectsCache.addDb(objectsDB);
    }
    
    /**
     * Indicates whether the connection to the DB is active.
     * 
     * @return true if the connection to the DB is active
     */
    public boolean isConnectionActive() {
        return objectsDB.isConnectionActive();
    }
    
    /**
     * Returns the database used to store matches.
     * 
     * @return the database used to store matches
     */
    public ObjectsDB getObjectsDB() {
        return objectsDB;
    }

    /**
     * Finishes queued operations and closes the db connection.
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * closing the database connection
     * @throws InterruptedException exception thrown if a threading error occurs
     */
    public void close() throws SQLException, InterruptedException {
        objectsDB.close();
    }
}
