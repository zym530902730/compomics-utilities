package com.compomics.util.experiment.identification.matches;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.IdentificationMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.identification.SpectrumIdentificationAssumption;
import com.compomics.util.experiment.identification.spectrum_assumptions.TagAssumption;
import com.compomics.util.experiment.identification.amino_acid_tags.matchers.TagMatcher;
import com.compomics.util.experiment.identification.protein_inference.PeptideMapper;
import com.compomics.util.experiment.identification.protein_inference.PeptideProteinMapping;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import java.io.IOException;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * This class models a spectrum match.
 *
 * @author Marc Vaudel
 */
public class SpectrumMatch extends IdentificationMatch {

    /**
     * The version UID for Serialization/Deserialization compatibility.
     */
    static final long serialVersionUID = 3227760855215444318L;
    /**
     * The index of the matched spectrum.
     */
    private String spectrumFile;
    /**
     * The index of the matched spectrum.
     */
    private String spectrumTitle;
    /**
     * Map of the identification algorithm assumption: advocate number &gt;
     * score &gt; assumptions.
     */
    private HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptions = null;
    /**
     * Map of the identification algorithm assumption: advocate number &gt;
     * score &gt; assumptions.
     */
    private HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> rawAssumptions = new HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>>();
    /**
     * The size of the keys used for the tag assumptions map.
     */
    private int tagAssumptionsMapKeySize = -1;
    /**
     * The best peptide assumption.
     */
    private PeptideAssumption bestPeptideAssumption;
    /**
     * The best tag assumption.
     */
    private TagAssumption bestTagAssumption;
    /**
     * The spectrum number in the mgf file. Will be used in case the spectrum
     * title does not match.
     */
    private int spectrumNumber = 0;

    /**
     * Constructor for the spectrum match.
     */
    public SpectrumMatch() {
    }
    
    public void setTagAssumptionMapKeySize(int tagAssumptionsMapKeySize){
        zooActivateWrite();
        setModified(true);
        this.tagAssumptionsMapKeySize = tagAssumptionsMapKeySize;
    }
    
    /**
     * Constructor for the spectrum match.
     *
     * @param spectrumFile the matched spectrum file name
     * @param spectrumTitle the matched spectrum title
     * @param assumption the matching assumption
     */
    public SpectrumMatch(String spectrumFile, String spectrumTitle, SpectrumIdentificationAssumption assumption) {
        int advocateId = assumption.getAdvocate();
        assumptions = new HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>>(1);
        assumptions.put(advocateId, new HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>());
        assumptions.get(advocateId).put(assumption.getScore(), new ArrayList<SpectrumIdentificationAssumption>());
        assumptions.get(advocateId).get(assumption.getScore()).add(assumption);
        
        this.spectrumFile = spectrumFile;
        this.spectrumTitle = spectrumTitle;
    }
    
    public int getTagAssumptionMapKeySize(){
        zooActivateRead();
        return tagAssumptionsMapKeySize;
    }
    
    public void setAssumptionMap(HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptionsMap){
        
        zooActivateWrite();
        setModified(true);
        this.assumptions = assumptionsMap;
    }
    
    public void setRawAssumptions(HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> rawAssumptions){
        
        zooActivateWrite();
        setModified(true);
        this.rawAssumptions = rawAssumptions;
    }

    /**
     * Constructor for the spectrum match.
     *
     * @param spectrumFile the matched spectrum file name
     * @param spectrumTitle the matched spectrum title
     */
    public SpectrumMatch(String spectrumFile, String spectrumTitle) {
        this.spectrumFile = spectrumFile;
        this.spectrumTitle = spectrumTitle;
        assumptions = new HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>>(1);
    }

    /**
     * Getter for the best peptide assumption.
     *
     * @return the best peptide assumption for the spectrum
     */
    public PeptideAssumption getBestPeptideAssumption() {
        zooActivateRead();
        return bestPeptideAssumption;
    }

    /**
     * Setter for the best peptide assumption.
     *
     * @param bestAssumption the best peptide assumption for the spectrum
     */
    public void setBestPeptideAssumption(PeptideAssumption bestAssumption) {
        zooActivateWrite();
        setModified(true);
        this.bestPeptideAssumption = bestAssumption;
    }
    
    

    /**
     * Getter for the best tag assumption.
     *
     * @return the best tag assumption for the spectrum
     */
    public TagAssumption getBestTagAssumption() {
        zooActivateRead();
        return bestTagAssumption;
    }

    /**
     * Setter for the best tag assumption.
     *
     * @param bestTagAssumption the best tag assumption for the spectrum
     */
    public void setBestTagAssumption(TagAssumption bestTagAssumption) {
        zooActivateWrite();
        setModified(true);
        this.bestTagAssumption = bestTagAssumption;
    }

    @Override
    public String getKey() {
        return Spectrum.getSpectrumKey(spectrumFile, spectrumTitle);
    }

    /**
     * Return all assumptions for the specified search engine indexed by their
     * e-value. Null if none found.
     *
     * @param advocateId the desired advocate ID
     *
     * @return all assumptions
     */
    public HashMap<Double, ArrayList<SpectrumIdentificationAssumption>> getAllAssumptions(int advocateId) {
        if (assumptions == null) {
            return null;
        }
        return assumptions.get(advocateId);
    }

    /**
     * Return all assumptions for all identification algorithms as a list. Null
     * if none found.
     *
     * @return all assumptions
     */
    public ArrayList<SpectrumIdentificationAssumption> getAllAssumptions() {
        if (getAssumptionsMap() == null) {
            return null;
        }
        ArrayList<SpectrumIdentificationAssumption> result = new ArrayList<SpectrumIdentificationAssumption>();
        for (HashMap<Double, ArrayList<SpectrumIdentificationAssumption>> seMap : getAssumptionsMap().values()) {
            for (double eValue : seMap.keySet()) {
                result.addAll(seMap.get(eValue));
            }
        }
        return result;
    }

    /**
     * Returns the assumptions map: advocate id &gt; score &gt; list of
     * assumptions.
     *
     * @return the assumptions map
     */
    public HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> getAssumptionsMap() {
        zooActivateRead();
        return assumptions;
    }

    /**
     * Returns the raw assumptions map: advocate id &gt; score &gt; list of
     * assumptions.
     *
     * @return the assumptions map
     */
    public HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> getRawAssumptions() {
        zooActivateRead();
        return rawAssumptions;
    }

    /**
     * Add a first hit.
     *
     * @param otherAdvocateId the index of the new advocate
     * @param otherAssumption the new identification assumption
     * @param ascendingScore indicates whether the score is ascending when hits
     * get better
     */
    public void addHit(int otherAdvocateId, SpectrumIdentificationAssumption otherAssumption, boolean ascendingScore) {
        HashMap<Double, ArrayList<SpectrumIdentificationAssumption>> advocateMap = assumptions.get(otherAdvocateId);
        if (advocateMap == null) {
            advocateMap = new HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>();
            assumptions.put(otherAdvocateId, advocateMap);
        }
        Double score = otherAssumption.getScore();
        ArrayList<SpectrumIdentificationAssumption> assumptionList = advocateMap.get(score);
        if (assumptionList == null) {
            assumptionList = new ArrayList<SpectrumIdentificationAssumption>();
            advocateMap.put(score, assumptionList);
        }
        assumptionList.add(otherAssumption);
        setModified(true);
    }

    @Override
    public MatchType getType() {
        return MatchType.Spectrum;
    }

    /**
     * Replaces the new key. The key of the PSM should always be the same as the
     * spectrum key it links to.
     *
     * @param spectrumFile the spectrum file
     * @param spectrumTitle the spectrum tile
     */
    public void setKey(String spectrumFile, String spectrumTitle) {
        this.spectrumFile = spectrumFile;
        this.spectrumTitle = spectrumTitle;
    }

    /**
     * Returns the spectrum number in the spectrum file. Returns null if not
     * implemented (versions older than 3.4.17). 1 is the first spectrum.
     *
     * @return the spectrum number in the spectrum file
     */
    public int getSpectrumNumber() {
        zooActivateRead();
        return spectrumNumber;
    }

    /**
     * Sets the spectrum number in the spectrum file. 1 is the first spectrum.
     *
     * @param spectrumNumber the spectrum number in the spectrum file
     */
    public void setSpectrumNumber(int spectrumNumber) {
        zooActivateWrite();
        this.spectrumNumber = spectrumNumber;
        setModified(true);
    }

    /**
     * Removes an assumption from the mapping.
     *
     * @param assumption the peptide assumption to remove
     */
    public void removeAssumption(SpectrumIdentificationAssumption assumption) {
        ArrayList<Integer> seToRemove = new ArrayList<Integer>();
        for (int se : assumptions.keySet()) {
            ArrayList<Double> eValueToRemove = new ArrayList<Double>();
            for (double eValue : assumptions.get(se).keySet()) {
                assumptions.get(se).get(eValue).remove(assumption);
                if (assumptions.get(se).get(eValue).isEmpty()) {
                    eValueToRemove.add(eValue);
                }
            }
            for (double eValue : eValueToRemove) {
                assumptions.get(se).remove(eValue);
            }
            if (assumptions.get(se).isEmpty()) {
                seToRemove.add(se);
            }
        }
        for (int se : seToRemove) {
            assumptions.remove(se);
        }
        setModified(true);
    }

    /**
     * Indicates whether the spectrum match contains a peptide assumption from a
     * search engine.
     *
     * @return a boolean indicating whether the spectrum match contains an
     * assumption
     */
    public boolean hasAssumption() {
        for (int se : assumptions.keySet()) {
            for (ArrayList<SpectrumIdentificationAssumption> assumptionsAtScore : assumptions.get(se).values()) {
                if (!assumptionsAtScore.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Indicates whether the spectrum match contains a peptide assumption for
     * the given advocate (for example a search engine, see the Advocate class)
     *
     * @param advocateId The index of the advocate
     * @return a boolean indicating whether the spectrum match contains a
     * peptide assumption for the given advocate
     */
    public boolean hasAssumption(int advocateId) {
        if (assumptions.containsKey(advocateId)) {
            for (ArrayList<SpectrumIdentificationAssumption> assumptionsAtEvalue : assumptions.get(advocateId).values()) {
                if (!assumptionsAtEvalue.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a peptide based spectrum match where peptide assumptions are
     * deduced from tag assumptions. The original tag assumption is added to the
     * peptide match as refinement parameter
     *
     * @param peptideMapper the selected peptide mapper
     * @param sequenceMatchingPreferences the sequence matching preferences
     * @param massTolerance the MS2 mass tolerance to use
     * @param scoreInAscendingOrder boolean indicating whether the tag score is
     * in the ascending order; ie the higher the score, the better the match.
     * @param tagMatcher the tag matcher to use
     * @param ascendingScore indicates whether the score is ascending when hits
     * get better
     *
     * @return a new spectrum match containing the peptide assumptions made from
     * the tag assumptions.
     *
     * @throws IOException if an IOException occurs
     * @throws SQLException if an SQLException occurs
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws InterruptedException if an InterruptedException occurs
     */
    public SpectrumMatch getPeptidesFromTags(PeptideMapper peptideMapper, TagMatcher tagMatcher, SequenceMatchingPreferences sequenceMatchingPreferences, Double massTolerance,
            boolean scoreInAscendingOrder, boolean ascendingScore)
            throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        
        SpectrumMatch spectrumMatch = new SpectrumMatch(spectrumFile, spectrumTitle);

        for (int advocateId : assumptions.keySet()) {

            int rank = 1;
            ArrayList<Double> scores = new ArrayList<Double>(assumptions.get(advocateId).keySet());

            if (scoreInAscendingOrder) {
                Collections.sort(scores);
            } else {
                Collections.sort(scores, Collections.reverseOrder());
            }

            for (double score : scores) {
                ArrayList<SpectrumIdentificationAssumption> originalAssumptions = assumptions.get(advocateId).get(score);
                for (SpectrumIdentificationAssumption assumption : originalAssumptions) {
                    if (assumption instanceof TagAssumption) {
                        TagAssumption tagAssumption = (TagAssumption) assumption;
                        ArrayList<PeptideProteinMapping> proteinMapping
                                = peptideMapper.getProteinMapping(tagAssumption.getTag(), tagMatcher, sequenceMatchingPreferences, massTolerance);
                        for (Peptide peptide : PeptideProteinMapping.getPeptides(proteinMapping, sequenceMatchingPreferences)) {
                            PeptideAssumption peptideAssumption = new PeptideAssumption(peptide, rank, advocateId,
                                    assumption.getIdentificationCharge(), score, assumption.getIdentificationFile());
                            peptideAssumption.setRawScore(score);
                            peptideAssumption.addUrParam(tagAssumption);
                            spectrumMatch.addHit(advocateId, peptideAssumption, ascendingScore);
                        }
                    }
                }
            }
        }

        return spectrumMatch;
    }
}
