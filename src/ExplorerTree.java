//Mortadha 2011

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import java.awt.image.RenderedImage;
import javax.swing.tree.DefaultTreeModel;

public class ExplorerTree extends javax.swing.JFrame {

    private String iconclose = "/folderxp.png", iconopen = "/Cfolderxp.png", iconfile = "/filexp.png";
    private DefaultTreeCellRenderer tCellRenderer;
    private DefaultMutableTreeNode racine;
    private JFileChooser chooser;
    private SimpleDateFormat df;
    private File file;

    public ExplorerTree() {
        this.setSize(680, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(".:: Explorer (Explore) ::.");
        ImageIcon monIcone = new ImageIcon(getClass().getResource("/ico.png"));
        setIconImage(monIcone.getImage());
        javax.swing.Timer time = new javax.swing.Timer(1000, new ClockListener());
        time.start();
        initComponents();
        DefaultTreeModel treeModel = (DefaultTreeModel) arbre.getModel();

        treeModel.setAsksAllowsChildren(true);
        DefaultMutableTreeNode leafFolder = new DefaultMutableTreeNode("New Leaf Folder", true);
        leafFolder.setAllowsChildren(true);
    }
    private boolean _isFolder;

    public boolean FileNode(boolean isFolder) {
        racine = new DefaultMutableTreeNode();   // on appelle le constructeur parent (c'est à dire : DefaultMutableTreeNode(name) )
        _isFolder = isFolder;
        return _isFolder;
    }

    public String DroitFichier(File f) {
        String droit = "  (";
        if (f.isDirectory()) {
            droit += "d";
        } else {
            droit += "-";
        }
        if (f.canRead()) {
            droit += "r";
        } else {
            droit += "-";
        }
        if (f.canWrite()) {
            droit += "w";
        } else {
            droit += "-";
        }
        if (f.canExecute()) {
            droit += "x";
        } else {
            droit += "-";
        }
        droit += ")";
        return droit;
    }

    public void getList(DefaultMutableTreeNode node, File f) {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
        if (f.isDirectory()) {
            child = new DefaultMutableTreeNode(f.getName() + "\\" + DroitFichier(f));
            //System.out.println("DIRECTORY  -  " + f.getName());
            node.add(child);
            File fList[] = f.listFiles();
            for (File fList1 : fList) {
                getList(child, fList1);
            }
            //System.out.println("FILE  -  " + f.getName());
        } else {
            child = new DefaultMutableTreeNode(f.getName() + DroitFichier(f));
            //System.out.println("DIRECTORY  -  " + f.getName());
            node.add(child);
        }
    }

    public void getListpath(DefaultMutableTreeNode node, File f) {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(f);
        if (!f.isDirectory()) {
            System.out.println("FILE  -  " + f.getName());
            node.add(child);
        } else {
            System.out.println("DIRECTORY  -  " + f.getName());
            node.add(child);
            File fList[] = f.listFiles();
            for (File fList1 : fList) {
                getListpath(child, fList1);
            }
        }
    }

    public void getListdos(DefaultMutableTreeNode node, File f) {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName() + "\\" + DroitFichier(f));

        if (f.isDirectory()) {
            node.add(child);
            File fList[] = f.listFiles();
            for (File fList1 : fList) {
                getListdos(child, fList1);
            }
        }
    }

    class ClockListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            df = new SimpleDateFormat(" HH:mm:ss");
            heure.setText(df.format(Calendar.getInstance().getTime()));
        }
    }

    public void Parcourir() {
        try {
            this.setOpacity(60 / 100.0f);
        } catch (Exception e) {
        }
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Ouvrir (Open)");
        chooser.setApproveButtonText("Ouvrir (Open)");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chemintxt.setText(chooser.getSelectedFile().getAbsolutePath());
            file = new File(chooser.getSelectedFile().getPath());
            racine = new DefaultMutableTreeNode();
            getList(racine, new File(chooser.getSelectedFile().getAbsolutePath()));
            arbre = new JTree(racine);
            IconeCombo();
            arbre.setRootVisible(false);
            arbre.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 0, 0), new java.awt.Color(255, 0, 0)));
            jScrollPane1.setViewportView(arbre);
            check.setEnabled(true);
        }
    }

    public void EcrireFichierXml() throws IOException {
        try {
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setApproveButtonText("Enregistrer (Save) '*.xml'");
            chooser.setDialogTitle("Exporter (Export)");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                launchtxt.setText(chooser.getSelectedFile().getAbsolutePath() + ".xml");
                FileOutputStream fos = new FileOutputStream(launchtxt.getText());
                XMLEncoder o = new XMLEncoder(new BufferedOutputStream(fos));
                o.writeObject(arbre.getModel());
                o.close();
                launch.setEnabled(true);
                launch.setText("Lancer (XML)");
                JOptionPane.showMessageDialog(this, "Fichier XML créé avec succes", "Attention", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fichier XML erroné", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void LireFichierXml() throws IOException {
        launch.setEnabled(false);
        try {
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setApproveButtonText("Charger (Load) '*.xml'");
            chooser.setDialogTitle("Importer (Import)");
            launchtxt.setText("");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (chooser.getSelectedFile().getName().endsWith("xml")) {
                    try {
                        XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(chooser.getSelectedFile().getAbsolutePath())));
                        arbre.setModel((TreeModel) d.readObject());
                        d.close();
                        launchtxt.setText(chooser.getSelectedFile().getAbsolutePath());
                        launch.setEnabled(true);
                        launch.setText("Lancer (XML)");
                        JOptionPane.showMessageDialog(this, "Fichier XML importé avec succes", "Attention", JOptionPane.INFORMATION_MESSAGE);
                    } catch (ArrayIndexOutOfBoundsException mex) {
                        JOptionPane.showMessageDialog(this, chooser.getSelectedFile().getName() + " est en format '*.xml' inconnu", "Attention", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, chooser.getSelectedFile().getName() + " n'est pas un '*.xml'", "Attention", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fichier XML erroné", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void IconeCombo() {
        if (combo.getSelectedIndex() == 0) {
            tCellRenderer = new DefaultTreeCellRenderer();
            this.tCellRenderer.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/folderxp.png")));
            this.tCellRenderer.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/Cfolderxp.png")));
            this.tCellRenderer.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/filexp.png")));
            icondossier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/folderxp.png")));
            iconfichier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filexp.png")));
        }
        if (combo.getSelectedIndex() == 1) {
            tCellRenderer = new DefaultTreeCellRenderer();
            this.tCellRenderer.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/folderubuntu.png")));
            this.tCellRenderer.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/Cfolderubuntu.png")));
            this.tCellRenderer.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/fileubuntu.png")));
            icondossier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/folderubuntu.png")));
            iconfichier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fileubuntu.png")));
        }
        if (combo.getSelectedIndex() == 2) {
            tCellRenderer = new DefaultTreeCellRenderer();
            this.tCellRenderer.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/foldermac.png")));
            this.tCellRenderer.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/Cfoldermac.png")));
            this.tCellRenderer.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/filemac.png")));
            icondossier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/foldermac.png")));
            iconfichier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filemac.png")));
        }
        arbre.setCellRenderer(this.tCellRenderer);
        arbre.updateUI();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel = new javax.swing.JPanel();
        deroul = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        arbre = new javax.swing.JTree();
        arbonom = new javax.swing.JLabel();
        chemin = new javax.swing.JLabel();
        chemintxt = new javax.swing.JTextField();
        parcour = new javax.swing.JButton();
        importer = new javax.swing.JButton();
        enroul = new javax.swing.JButton();
        heure = new javax.swing.JLabel();
        launch = new javax.swing.JButton();
        iconfichier = new javax.swing.JButton();
        icondossier = new javax.swing.JButton();
        combo = new javax.swing.JComboBox();
        check = new javax.swing.JCheckBox();
        launchtxt = new javax.swing.JTextField();
        export = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(700, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jPanel.setBackground(new java.awt.Color(255, 255, 255));
        jPanel.setMinimumSize(new java.awt.Dimension(700, 500));
        jPanel.setPreferredSize(new java.awt.Dimension(636, 380));

        deroul.setIcon(new javax.swing.ImageIcon(getClass().getResource("/expand.png"))); // NOI18N
        deroul.setText("Dérouler tout (Expand)");
        deroul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deroulActionPerformed(evt);
            }
        });

        arbre = new JTree(this.racine);
        arbre.setRootVisible(false);
        arbre.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 0, 0), new java.awt.Color(255, 0, 0)));
        jScrollPane1.setViewportView(arbre);

        arbonom.setText("Arborescence (Tree)");

        chemin.setText("Chemin (Path)");

        chemintxt.setEditable(false);
        chemintxt.setBackground(new java.awt.Color(255, 255, 255));
        chemintxt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        chemintxt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                chemintxtMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                chemintxtMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                chemintxtMousePressed(evt);
            }
        });
        chemintxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chemintxtActionPerformed(evt);
            }
        });

        parcour.setText("...");
        parcour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parcourActionPerformed(evt);
            }
        });

        importer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/import.png"))); // NOI18N
        importer.setText("Importer (Import)");
        importer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importerActionPerformed(evt);
            }
        });

        enroul.setIcon(new javax.swing.ImageIcon(getClass().getResource("/collapse.png"))); // NOI18N
        enroul.setText("Enrouler tout (Collaps)");
        enroul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enroulActionPerformed(evt);
            }
        });

        launch.setText("Lancer (Launch)");
        launch.setEnabled(false);
        launch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                launchActionPerformed(evt);
            }
        });

        iconfichier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filexp.png"))); // NOI18N
        iconfichier.setText("Icon fichier (File icon)");
        iconfichier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iconfichierActionPerformed(evt);
            }
        });

        icondossier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/folderxp.png"))); // NOI18N
        icondossier.setText("Icon dossier (Folder icon)");
        icondossier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                icondossierActionPerformed(evt);
            }
        });

        combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Icône Windows", "Icône Ubuntu", "Icône MacOS" }));
        combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboActionPerformed(evt);
            }
        });

        check.setBackground(new java.awt.Color(255, 255, 255));
        check.setText("Répertoires seulements");
        check.setEnabled(false);
        check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkActionPerformed(evt);
            }
        });

        launchtxt.setEditable(false);
        launchtxt.setBackground(new java.awt.Color(255, 255, 255));
        launchtxt.setForeground(new java.awt.Color(70, 144, 60));

        export.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Exporter (XML)", "Exporter (PNG)", "Exporter (JPG)" }));
        export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                        .addComponent(arbonom)
                        .addGap(18, 18, 18)
                        .addComponent(icondossier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iconfichier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(heure, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(chemin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chemintxt)
                        .addGap(28, 28, 28)
                        .addComponent(parcour, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(deroul)
                            .addComponent(check))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(launchtxt)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(enroul)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(export, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(launch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(importer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chemin)
                            .addComponent(parcour)
                            .addComponent(chemintxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(heure, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(icondossier)
                            .addComponent(iconfichier)))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(arbonom)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(check)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(launch)
                        .addComponent(launchtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deroul)
                    .addComponent(importer)
                    .addComponent(enroul)
                    .addComponent(export, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo, iconfichier});

        jPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {export, importer});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 679, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 442, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deroulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deroulActionPerformed
        arbre.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 0, 255), new java.awt.Color(0, 0, 255)));
        int row = 0;
        while (row < arbre.getRowCount()) {
            arbre.expandRow(row);
            row++;
        }
    }//GEN-LAST:event_deroulActionPerformed

    private void enroulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enroulActionPerformed
        arbre.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 0, 0), new java.awt.Color(255, 0, 0)));
        int row = arbre.getRowCount() - 1;
        while (row >= 0) {
            arbre.collapseRow(row);
            row--;
        }
    }//GEN-LAST:event_enroulActionPerformed

    private void parcourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parcourActionPerformed
        Parcourir();
    }//GEN-LAST:event_parcourActionPerformed

    private void chemintxtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chemintxtMousePressed
        Parcourir();
}//GEN-LAST:event_chemintxtMousePressed

    private void chemintxtMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chemintxtMouseEntered
        chemintxt.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 255, 0), null));
    }//GEN-LAST:event_chemintxtMouseEntered

    private void chemintxtMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chemintxtMouseExited
        chemintxt.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 255), null));
    }//GEN-LAST:event_chemintxtMouseExited

    private void importerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importerActionPerformed
        try {
            this.setOpacity(60 / 100.0f);
        } catch (Exception e) {
        }
        try {
            LireFichierXml();
        } catch (IOException ex) {
            Logger.getLogger(ExplorerTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_importerActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        try {
            this.setOpacity(100 / 100.0f);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_formWindowActivated

    private void launchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_launchActionPerformed
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    file = new File(launchtxt.getText());
                    desktop.open(file);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ExplorerTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.lang.IllegalArgumentException ex) {
            Logger.getLogger(ExplorerTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_launchActionPerformed

    private void icondossierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_icondossierActionPerformed
        try {
            this.setOpacity(60 / 100.0f);
        } catch (Exception e) {
        }
        chooser = new JFileChooser(".");
        chooser.setDialogTitle("Ouvrir (Open)");
        chooser.setApproveButtonText("Image '*.png 16x16'");

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().getName().endsWith("png")) {
                ImageIcon i = null;
                java.net.URL test = ExplorerTree.class.getResource(chooser.getSelectedFile().getName());
                if (test != null) {
                    i = new ImageIcon(test);
                    if ((i.getIconWidth() == 16) && (i.getIconHeight() == 16)) {
                        iconclose = chooser.getSelectedFile().getName();
                        iconopen = chooser.getSelectedFile().getName();
                        icondossier.setIcon(i);
                        tCellRenderer = new DefaultTreeCellRenderer();
                        this.tCellRenderer.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource(iconclose)));
                        this.tCellRenderer.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource(iconopen)));
                        this.tCellRenderer.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource(iconfile)));
                        arbre.setCellRenderer(this.tCellRenderer);
                        arbre.updateUI();
                    } else {
                        JOptionPane.showMessageDialog(this, chooser.getSelectedFile().getName() + " est une image de taille " + i.getIconWidth() + "x" + i.getIconHeight(), "Attention", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, chooser.getSelectedFile().getName() + " n'est pas une image '*.png 16x16'", "Attention", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, chooser.getSelectedFile().getName() + " n'est pas une image '*.png 16x16'", "Attention", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_icondossierActionPerformed

    private void iconfichierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iconfichierActionPerformed
        try {
            this.setOpacity(60 / 100.0f);
        } catch (Exception e) {
        }
        chooser = new JFileChooser(".");
        chooser.setDialogTitle("Ouvrir (Open)");
        chooser.setApproveButtonText("Image '*.png 16x16'");

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().getName().endsWith("png")) {
                ImageIcon i = null;
                java.net.URL test = ExplorerTree.class.getResource(chooser.getSelectedFile().getName());
                if (test != null) {
                    i = new ImageIcon(test);
                    if ((i.getIconWidth() == 16) && (i.getIconHeight() == 16)) {
                        iconfile = chooser.getSelectedFile().getName();
                        iconfichier.setIcon(i);
                        tCellRenderer = new DefaultTreeCellRenderer();
                        this.tCellRenderer.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource(iconclose)));
                        this.tCellRenderer.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource(iconopen)));
                        this.tCellRenderer.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource(iconfile)));
                        arbre.setCellRenderer(this.tCellRenderer);
                        arbre.updateUI();
                    } else {
                        JOptionPane.showMessageDialog(this, chooser.getSelectedFile().getName() + " est une image de taille " + i.getIconWidth() + "x" + i.getIconHeight(), "Attention", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, chooser.getSelectedFile().getName() + " n'est pas une image '*.png 16x16'", "Attention", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, chooser.getSelectedFile().getName() + " n'est pas une image '*.png 16x16'", "Attention", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_iconfichierActionPerformed

    private void comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboActionPerformed
        IconeCombo();
    }//GEN-LAST:event_comboActionPerformed

    private void checkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkActionPerformed
        if (check.isSelected() == true) {
            if (!chemintxt.getText().equals("")) {
                racine = new DefaultMutableTreeNode("Root", true);
                getListdos(racine, new File(chooser.getSelectedFile().getAbsolutePath()));
                arbre = new JTree(racine);
                IconeCombo();
                arbre.setRootVisible(false);
                arbre.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 255, 0), new java.awt.Color(255, 255, 0)));
                jScrollPane1.setViewportView(arbre);
            }
        } else if (check.isSelected() == false) {
            if (!chemintxt.getText().equals("")) {
                racine = new DefaultMutableTreeNode();
                getList(racine, new File(chooser.getSelectedFile().getAbsolutePath()));
                arbre = new JTree(racine);
                IconeCombo();
                arbre.setRootVisible(false);
                arbre.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 0, 0), new java.awt.Color(255, 0, 0)));
                jScrollPane1.setViewportView(arbre);
            }
            arbre.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 0, 0), new java.awt.Color(255, 0, 0)));
        }
    }//GEN-LAST:event_checkActionPerformed

    private void exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportActionPerformed
        try {
            this.setOpacity(60 / 100.0f);
        } catch (Exception e) {
        }
        if (export.getSelectedIndex() == 0) {
            try {
                EcrireFichierXml();
            } catch (IOException ex) {
                Logger.getLogger(ExplorerTree.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        BufferedImage bufferedImage = new BufferedImage(arbre.getWidth(), arbre.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        arbre.paint(g2d);
        g2d.translate(arbre.getWidth(), arbre.getHeight());
        g2d.dispose();
        RenderedImage rendImage = bufferedImage;
        //FileOutputStream out = null;
        if (export.getSelectedIndex() == 1) {
            try {
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setApproveButtonText("Enregistrer (Save) '*.png'");
                chooser.setDialogTitle("Exporter (Export)");
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    launchtxt.setText(chooser.getSelectedFile().getAbsolutePath() + ".png");
                    file = new File(launchtxt.getText());
                    ImageIO.write(rendImage, "png", file);
                    launch.setEnabled(true);
                    launch.setText("Lancer (PNG)");
                    JOptionPane.showMessageDialog(this, "Image PNG créé avec succes", "Attention", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Image PNG erroné", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (export.getSelectedIndex() == 2) {
            try {
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setApproveButtonText("Enregistrer (Save) '*.jpg'");
                chooser.setDialogTitle("Exporter (Export)");
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    launchtxt.setText(chooser.getSelectedFile().getAbsolutePath() + ".jpg");
                    file = new File(launchtxt.getText());
                    ImageIO.write(rendImage, "JPG", file);
                    launch.setEnabled(true);
                    launch.setText("Lancer (JPG)");
                    JOptionPane.showMessageDialog(this, "Image JPG créé avec succes", "Attention", JOptionPane.INFORMATION_MESSAGE);

                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Image JPG erroné", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_exportActionPerformed

    private void chemintxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chemintxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chemintxtActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel arbonom;
    private javax.swing.JTree arbre;
    private javax.swing.JCheckBox check;
    private javax.swing.JLabel chemin;
    private javax.swing.JTextField chemintxt;
    private javax.swing.JComboBox combo;
    private javax.swing.JButton deroul;
    private javax.swing.JButton enroul;
    private javax.swing.JComboBox export;
    private javax.swing.JLabel heure;
    private javax.swing.JButton icondossier;
    private javax.swing.JButton iconfichier;
    private javax.swing.JButton importer;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton launch;
    private javax.swing.JTextField launchtxt;
    private javax.swing.JButton parcour;
    // End of variables declaration//GEN-END:variables
}