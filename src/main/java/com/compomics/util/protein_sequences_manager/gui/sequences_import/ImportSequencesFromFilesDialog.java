package com.compomics.util.protein_sequences_manager.gui.sequences_import;

import com.compomics.util.Util;
import com.compomics.util.experiment.io.biology.protein.FastaIndex;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import com.compomics.util.preferences.LastSelectedFolder;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import com.compomics.util.protein_sequences_manager.ProteinSequencesManager;
import static com.compomics.util.protein_sequences_manager.gui.SequenceDbDetailsDialog.lastFolderKey;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * Dialog for importing sequences from files.
 *
 * @author Marc Vaudel
 */
public class ImportSequencesFromFilesDialog extends javax.swing.JDialog {

    /**
     * A simple progress dialog.
     */
    private static ProgressDialogX progressDialog;
    /**
     * The utilities user preferences.
     */
    private UtilitiesUserPreferences utilitiesUserPreferences = null;
    /**
     * The last selected folder.
     */
    private LastSelectedFolder lastSelectedFolder = null;
    /**
     * The file selected by the user.
     */
    private File selectedFile = null;
    /**
     * The index of the file selected by the user.
     */
    private FastaIndex selectedFileIndex = null;
    /**
     * Boolean indicating whether the import has been canceled by the user.
     */
    private boolean canceled = false;
    /**
     * The parent frame.
     */
    private java.awt.Frame parentFrame;
    /**
     * The icon to display when waiting.
     */
    private Image waitingImage;
    /**
     * The normal icon.
     */
    private Image normalImange;

    /**
     * Constructor.
     *
     * @param parent the parent frame
     * @param normalImange the normal icon
     * @param waitingImage the waiting icon
     */
    public ImportSequencesFromFilesDialog(java.awt.Frame parent, Image normalImange, Image waitingImage) {
        super(parent, true);

        this.parentFrame = parent;
        this.normalImange = normalImange;
        this.waitingImage = waitingImage;

        initComponents();
        loadUserPreferences();
        clearDatabaseSelection();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        importSequencesFromFilesPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        inputPanel = new javax.swing.JPanel();
        typeLbl = new javax.swing.JLabel();
        typeCmb = new javax.swing.JComboBox();
        nameLbl = new javax.swing.JLabel();
        versionLbl = new javax.swing.JLabel();
        nameTxt = new javax.swing.JTextField();
        versionTxt = new javax.swing.JTextField();
        descriptionLbl = new javax.swing.JLabel();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionTxt = new javax.swing.JTextArea();
        parsingRuleTxt = new javax.swing.JTextField();
        parsingRuleLbl = new javax.swing.JLabel();
        fileSelectionPanel = new javax.swing.JPanel();
        fastaFileLabel = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        fastaFilesTxt = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        importSequencesFromFilesPanel.setBackground(new java.awt.Color(230, 230, 230));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        inputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Information"));
        inputPanel.setOpaque(false);

        typeLbl.setText("Type");

        typeCmb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        nameLbl.setText("Name");

        versionLbl.setText("Version");

        descriptionLbl.setText("Description");

        descriptionTxt.setColumns(20);
        descriptionTxt.setRows(5);
        descriptionScrollPane.setViewportView(descriptionTxt);

        parsingRuleTxt.setEditable(false);
        parsingRuleTxt.setEnabled(false);

        parsingRuleLbl.setText("Parsing Rule");

        javax.swing.GroupLayout inputPanelLayout = new javax.swing.GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inputPanelLayout.createSequentialGroup()
                        .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(inputPanelLayout.createSequentialGroup()
                                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nameLbl)
                                    .addComponent(typeLbl))
                                .addGap(18, 18, 18)
                                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(typeCmb, 0, 200, Short.MAX_VALUE)
                                    .addComponent(nameTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addGap(134, 134, 134)
                                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(inputPanelLayout.createSequentialGroup()
                                        .addComponent(parsingRuleLbl)
                                        .addGap(18, 18, 18)
                                        .addComponent(parsingRuleTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                    .addGroup(inputPanelLayout.createSequentialGroup()
                                        .addComponent(versionLbl)
                                        .addGap(42, 42, 42)
                                        .addComponent(versionTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))))
                            .addComponent(descriptionScrollPane))
                        .addContainerGap())
                    .addGroup(inputPanelLayout.createSequentialGroup()
                        .addComponent(descriptionLbl)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLbl)
                    .addComponent(typeCmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parsingRuleLbl)
                    .addComponent(parsingRuleTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLbl)
                    .addComponent(versionLbl)
                    .addComponent(nameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(descriptionLbl)
                .addGap(4, 4, 4)
                .addComponent(descriptionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addContainerGap())
        );

        fileSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Input"));
        fileSelectionPanel.setOpaque(false);

        fastaFileLabel.setText("FASTA File");

        browseButton.setText("Browse");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        fastaFilesTxt.setEditable(false);

        javax.swing.GroupLayout fileSelectionPanelLayout = new javax.swing.GroupLayout(fileSelectionPanel);
        fileSelectionPanel.setLayout(fileSelectionPanelLayout);
        fileSelectionPanelLayout.setHorizontalGroup(
            fileSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fileSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fastaFileLabel)
                .addGap(18, 18, 18)
                .addComponent(fastaFilesTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(browseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        fileSelectionPanelLayout.setVerticalGroup(
            fileSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fileSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(clearButton)
                        .addComponent(browseButton))
                    .addGroup(fileSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fastaFileLabel)
                        .addComponent(fastaFilesTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout importSequencesFromFilesPanelLayout = new javax.swing.GroupLayout(importSequencesFromFilesPanel);
        importSequencesFromFilesPanel.setLayout(importSequencesFromFilesPanelLayout);
        importSequencesFromFilesPanelLayout.setHorizontalGroup(
            importSequencesFromFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, importSequencesFromFilesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(importSequencesFromFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fileSelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(importSequencesFromFilesPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(inputPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        importSequencesFromFilesPanelLayout.setVerticalGroup(
            importSequencesFromFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, importSequencesFromFilesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileSelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(importSequencesFromFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(importSequencesFromFilesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(importSequencesFromFilesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Select the database.
     * 
     * @param evt 
     */
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        selectDB();
    }//GEN-LAST:event_browseButtonActionPerformed

    /**
     * Clear the database selection.
     * 
     * @param evt 
     */
    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearDatabaseSelection();
    }//GEN-LAST:event_clearButtonActionPerformed

    /**
     * Cancel the dialog.
     * 
     * @param evt 
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        canceled = true;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Close the dialog.
     * 
     * @param evt 
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel descriptionLbl;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JTextArea descriptionTxt;
    private javax.swing.JLabel fastaFileLabel;
    private javax.swing.JTextField fastaFilesTxt;
    private javax.swing.JPanel fileSelectionPanel;
    private javax.swing.JPanel importSequencesFromFilesPanel;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JLabel nameLbl;
    private javax.swing.JTextField nameTxt;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel parsingRuleLbl;
    private javax.swing.JTextField parsingRuleTxt;
    private javax.swing.JComboBox typeCmb;
    private javax.swing.JLabel typeLbl;
    private javax.swing.JLabel versionLbl;
    private javax.swing.JTextField versionTxt;
    // End of variables declaration//GEN-END:variables

    /**
     * Loads the user preferences.
     */
    public void loadUserPreferences() {
        utilitiesUserPreferences = UtilitiesUserPreferences.loadUserPreferences();
        if (utilitiesUserPreferences.getProteinSequencesManagerFolder() == null || !utilitiesUserPreferences.getProteinSequencesManagerFolder().exists()) {
            throw new IllegalArgumentException("Database folder not set.");
        }
        lastSelectedFolder = utilitiesUserPreferences.getLastSelectedFolder();
    }

    /**
     * Returns the last selected folder.
     *
     * @return the last selected folder
     */
    public String getLastSelectedFolder() {
        if (lastSelectedFolder == null) {
            return null;
        }
        String folder = lastSelectedFolder.getLastSelectedFolder(lastFolderKey);
        if (folder == null) {
            folder = lastSelectedFolder.getLastSelectedFolder();
        }
        return folder;
    }

    /**
     * Clears the database selection and information.
     */
    public void clearDatabaseSelection() {
        selectedFile = null;
        fastaFilesTxt.setText("Please select a Fasta File");
        typeCmb.setSelectedIndex(0);
        typeCmb.setEnabled(false);
        parsingRuleTxt.setText("");
        parsingRuleTxt.setEnabled(false);
        nameTxt.setText("");
        nameTxt.setEnabled(false);
        versionTxt.setText("");
        versionTxt.setEnabled(false);
        descriptionTxt.setText("");
        descriptionTxt.setEnabled(false);
        clearButton.setEnabled(false);
    }

    /**
     * Copies the selected database to the temp folder and populates the gui
     * with the relevant information.
     *
     * @throws IOException exception thrown whenever an error occurred while
     * copying the database.
     */
    public void importDatabase() throws IOException {

        final File finalFile = selectedFile;

        progressDialog = new ProgressDialogX(this, parentFrame,
                normalImange,
                waitingImage,
                true);
        progressDialog.setPrimaryProgressCounterIndeterminate(true);
        progressDialog.setTitle("Loading Database. Please Wait...");

        new Thread(new Runnable() {
            public void run() {
                try {
                    progressDialog.setVisible(true);
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
        }, "ProgressDialog").start();

        new Thread("importThread") {
            public void run() {

                try {
                    progressDialog.setTitle("Importing Database. Please Wait...");
                    progressDialog.setPrimaryProgressCounterIndeterminate(false);
                    selectedFileIndex = SequenceFactory.getFastaIndex(selectedFile, false, progressDialog);
                } catch (IOException e) {
                    progressDialog.setRunFinished();
                    JOptionPane.showMessageDialog(ImportSequencesFromFilesDialog.this,
                            new String[]{"FASTA Import Error.", "File " + finalFile.getAbsolutePath() + " not found."},
                            "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                    return;
                } catch (StringIndexOutOfBoundsException e) {
                    progressDialog.setRunFinished();
                    JOptionPane.showMessageDialog(ImportSequencesFromFilesDialog.this,
                            e.getMessage(),
                            "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                    return;
                } catch (IllegalArgumentException e) {
                    progressDialog.setRunFinished();
                    JOptionPane.showMessageDialog(ImportSequencesFromFilesDialog.this,
                            e.getMessage(),
                            "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                    return;
                }
                if (!progressDialog.isRunCanceled()) {
                    typeCmb.setSelectedItem(selectedFileIndex.getMainDatabaseType());
                    nameTxt.setText(selectedFileIndex.getName());
                    versionTxt.setText(selectedFileIndex.getVersion());
                    descriptionTxt.setText(selectedFileIndex.getDescription());
                    typeCmb.setEnabled(true);
                    nameTxt.setEnabled(true);
                    versionTxt.setEnabled(true);
                    descriptionTxt.setEnabled(true);
                } else {
                    clearDatabaseSelection();
                }
                progressDialog.setRunFinished();
            }
        }.start();
    }

    /**
     * Allows the user to select a db and loads its information.
     */
    public void selectDB() {

        File startLocation = null;
        if (utilitiesUserPreferences.getDbFolder() != null && utilitiesUserPreferences.getDbFolder().exists()) {
            startLocation = utilitiesUserPreferences.getDbFolder();
        }

        JFileChooser fc = new JFileChooser(startLocation);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File myFile) {
                return myFile.getName().toLowerCase().endsWith("fasta")
                        || myFile.getName().toLowerCase().endsWith("fas")
                        || myFile.isDirectory();
            }

            @Override
            public String getDescription() {
                return "FASTA (.fasta or .fas)";
            }
        };

        fc.setFileFilter(filter);
        int result = fc.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File userSelectedFile = fc.getSelectedFile();
            lastSelectedFolder.setLastSelectedFolder(lastFolderKey, userSelectedFile.getParent());
            try {
                String fileName = userSelectedFile.getName();
                fileName = fileName.replaceAll(" ", "_");
                File managerFolder = utilitiesUserPreferences.getProteinSequencesManagerFolder();
                File tempFolder = new File(managerFolder, ProteinSequencesManager.TEMP_FOLDER);
                tempFolder.mkdirs();
                selectedFile = new File(tempFolder, fileName);
                Util.copyFile(userSelectedFile, selectedFile);
                importDatabase();
                fastaFilesTxt.setText(selectedFile.getAbsolutePath());
                clearButton.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while importing " + selectedFile.getName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
                clearDatabaseSelection();
            }
        }
    }

    /**
     * Indicates whether the user has canceled the import.
     *
     * @return a boolean indicating whether the user has canceled the import
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Returns the file selected by the user.
     *
     * @return the file selected by the user
     */
    public File getSelectedFile() {
        return selectedFile;
    }
}
