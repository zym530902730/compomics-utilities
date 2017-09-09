package com.compomics.util.parameters.identification.tool_specific;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.gui.parameters.identification.IdentificationAlgorithmParameter;

/**
 * The MS Amanda specific parameters.
 *
 * @author Harald Barsnes
 */
public class MsAmandaParameters implements IdentificationAlgorithmParameter {

    /**
     * Version number for deserialization.
     */
    static final long serialVersionUID = -8458620189315975268L;
    /**
     * Defines whether a decoy database shall be created and searched against.
     * Decoy FASTS files are generated by reverting protein sequences,
     * accessions are marked with the prefix “REV_”.
     */
    private boolean generateDecoy = false;
    /**
     * The MS Amanda instrument ID.
     */
    private String instrumentID = "b, y";
    /**
     * The maximum rank.
     */
    private Integer maxRank = 10;
    /**
     * Defines whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     */
    private boolean monoisotopic = true;
    /**
     * Defines whether the low memory mode is used.
     */
    private Boolean lowMemoryMode = true;
    /**
     * The maximum allowed length of the FASTA file name.
     */
    public static final int MAX_MS_AMANDA_FASTA_FILE_NAME_LENGTH = 80;

    /**
     * Constructor.
     */
    public MsAmandaParameters() {
    }

    @Override
    public Advocate getAlgorithm() {
        return Advocate.msAmanda;
    }

    @Override
    public boolean equals(IdentificationAlgorithmParameter identificationAlgorithmParameter) {

        if (identificationAlgorithmParameter instanceof MsAmandaParameters) {
            MsAmandaParameters msAmandaParameters = (MsAmandaParameters) identificationAlgorithmParameter;
            if (generateDecoy != msAmandaParameters.generateDecoy()) {
                return false;
            }
            if (monoisotopic != msAmandaParameters.isMonoIsotopic()) {
                return false;
            }
            if (!instrumentID.equalsIgnoreCase(msAmandaParameters.getInstrumentID())) {
                return false;
            }
            if (!maxRank.equals(msAmandaParameters.getMaxRank())) {
                return false;
            }
            if (isLowMemoryMode() != msAmandaParameters.isLowMemoryMode()) {
                return false;
            }
            return true;
        }

        return false;
    }

    @Override
    public String toString(boolean html) {
        String newLine = System.getProperty("line.separator");

        if (html) {
            newLine = "<br>";
        }

        StringBuilder output = new StringBuilder();
        Advocate advocate = getAlgorithm();
        output.append("# ------------------------------------------------------------------");
        output.append(newLine);
        output.append("# ").append(advocate.getName()).append(" Specific Parameters");
        output.append(newLine);
        output.append("# ------------------------------------------------------------------");
        output.append(newLine);
        output.append(newLine);

        output.append("SEARCH_DECOY=");
        output.append(generateDecoy);
        output.append(newLine);
        output.append("INSTRUMENT_ID=");
        output.append(instrumentID);
        output.append(newLine);
        output.append("MONOISOTOPIC=");
        output.append(monoisotopic);
        output.append(newLine);
        output.append("MAX_RANK=");
        output.append(maxRank);
        output.append(newLine);
        output.append("LOW_MEMORY_MODE=");
        output.append(lowMemoryMode);
        output.append(newLine);

        return output.toString();
    }

    /**
     * Returns whether a decoy database shall be created and searched against.
     *
     * @return whether a decoy database shall be created and searched against
     */
    public boolean generateDecoy() {
        return generateDecoy;
    }

    /**
     * Set whether a decoy database shall be created and searched against.
     *
     * @param generateDecoy the generateDecoy to set
     */
    public void setGenerateDecoyDatabase(boolean generateDecoy) {
        this.generateDecoy = generateDecoy;
    }

    /**
     * Returns whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     *
     * @return monoisotopic mass values shall be used (in contrast to average
     * mass values)
     */
    public boolean isMonoIsotopic() {
        return monoisotopic;
    }

    /**
     * Set whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     *
     * @param monoisotopic the monoisotopic to set
     */
    public void setMonoIsotopic(boolean monoisotopic) {
        this.monoisotopic = monoisotopic;
    }

    /**
     * Return the instrument ID.
     *
     * @return the instrumentID
     */
    public String getInstrumentID() {
        return instrumentID;
    }

    /**
     * Set the instrument ID.
     *
     * @param instrumentID the instrumentID to set
     */
    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    /**
     * Returns the maximum rank.
     *
     * @return the maxRank
     */
    public Integer getMaxRank() {
        return maxRank;
    }

    /**
     * Set the maximum rank.
     *
     * @param maxRank the maxRank to set
     */
    public void setMaxRank(Integer maxRank) {
        this.maxRank = maxRank;
    }

    /**
     * Returns whether the low memory mode is used.
     * 
     * @return the lowMemoryMode
     */
    public boolean isLowMemoryMode() {
        if (lowMemoryMode == null) {
            lowMemoryMode = true;
        }
        return lowMemoryMode;
    }

    /**
     * Set whether the low memory mode is used.
     * 
     * @param lowMemoryMode the lowMemoryMode to set
     */
    public void setLowMemoryMode(boolean lowMemoryMode) {
        this.lowMemoryMode = lowMemoryMode;
    }
}