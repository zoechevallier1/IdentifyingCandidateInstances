package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CreateProjectDialog extends JDialog {

    private JTextPane txtProjectName = null;
    private JTextPane txtProjectLocation = null;
    private JTextPane txtTargetSchema = null;
    private JTextPane txtSources = null;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private JRadioButton rbtMove = null;

    private JFileChooser fc = null;



    public CreateProjectDialog(){
        setModal(true);
        setTitle("Create a project");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setMinimumSize(new Dimension(435, 160));
        setMaximumSize(new Dimension(435, 160));
        setPreferredSize(new Dimension(435, 160));
        setSize(new Dimension(500, 300));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        // Ajout des boutons CREATE et CANCEL
        JButton btCreate = new JButton("Create");
        btCreate.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Ajouter des vérifications pour que tous les champs soient remplis
                        dialogResult = JOptionPane.OK_OPTION;
                        setVisible(false);
                    }
                }
        );

        panel.add(btCreate);

        JButton btCancel = new JButton("Cancel");
        btCancel.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        txtProjectName.setText(null);
                        txtProjectLocation.setText(null);
                        setVisible(false);
                        dispose();
                    }
                });
        panel.add(btCancel);


        JPanel panel_1 = new JPanel();
        getContentPane().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(null);

        //NOM DU PROJET
        JLabel lblProjectName = new JLabel("Project Name :");
        lblProjectName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblProjectName.setPreferredSize(new Dimension(100, 26));
        //lblProjectName.setMinimumSize(new Dimension(100, 26));
        //lblProjectName.setMaximumSize(new Dimension(100, 26));
        lblProjectName.setSize(new Dimension(100, 26));
        lblProjectName.setBounds(8, 11, 114, 22);
        panel_1.add(lblProjectName);

        txtProjectName = new JTextPane();
        txtProjectName.setBorder(new LineBorder(new Color(0, 0, 0)));
        txtProjectName.setBounds(124, 11, 290, 22);
        panel_1.add(txtProjectName);

        //LOCALISATION DU PROJET
        JLabel lblProjectLocation = new JLabel("Project Location :");
        lblProjectLocation.setHorizontalAlignment(SwingConstants.RIGHT);
        lblProjectLocation.setSize(new Dimension(0, 26));
        lblProjectLocation.setBounds(8, 48, 114, 22);
        panel_1.add(lblProjectLocation);

        txtProjectLocation = new JTextPane();
        txtProjectLocation.setBorder(new LineBorder(new Color(0, 0, 0)));
        txtProjectLocation.setBounds(124, 48, 290, 22);
        panel_1.add(txtProjectLocation);

        JButton btProjectLocation = new JButton("...");
        btProjectLocation.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int returnVal = fc.showDialog(CreateProjectDialog.this, "Project location");
                        if(returnVal == JFileChooser.APPROVE_OPTION)
                            txtProjectLocation.setText( fc.getSelectedFile().getAbsolutePath() );
                    }
                }
        );
        btProjectLocation.setBounds(420, 48, 22, 22);
        panel_1.add(btProjectLocation);

        //SCHEMA CIBLE
        JLabel lblTargetSchema = new JLabel("Target Schema :");
        lblTargetSchema.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTargetSchema.setBounds(8,81,114,22);
        panel_1.add(lblTargetSchema);

        txtTargetSchema = new JTextPane();
        txtTargetSchema.setBorder(new LineBorder((new Color(0,0,0))));
        txtTargetSchema.setBounds(124, 81, 290, 22);
        panel_1.add(txtTargetSchema);
        JButton btTargetSchema = new JButton("...");
        btTargetSchema.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
                        int returnVal = fc.showDialog(CreateProjectDialog.this, "Target Schema");
                        if(returnVal == JFileChooser.APPROVE_OPTION)
                            txtTargetSchema.setText( fc.getSelectedFile().getAbsolutePath() );
                    }
                }
        );
        btTargetSchema.setBounds(420, 81, 22, 22);
        panel_1.add(btTargetSchema);

        //SOURCES
        JLabel lblSources = new JLabel("Sources :");
        lblSources.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSources.setBounds(8,114,114,22);
        panel_1.add(lblSources);

        txtSources = new JTextPane();
        txtSources.setBorder(new LineBorder((new Color(0,0,0))));
        txtSources.setBounds(124,114,290,22);
        panel_1.add(txtSources);
        JButton btSources = new JButton("...");
        btSources.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        getFilesChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
                        int returnVal = fc.showDialog(CreateProjectDialog.this, "Sources");
                        if(returnVal == JFileChooser.APPROVE_OPTION){
                            File[] files = fc.getSelectedFiles();
                            String path = "";
                            for (File file : files){
                                path = path + file.getAbsolutePath() + "/";
                            }
                            txtSources.setText(path);
                        }

                    }
                }
        );
        btSources.setBounds(420, 114, 22, 22);
        panel_1.add(btSources);

    }
    private JFileChooser getFileChooser()
    {
        if(fc == null)
            fc = new JFileChooser( new File(".").getAbsolutePath() );
        return fc;
    }

    private JFileChooser getFilesChooser()
    {
        if(fc == null)
            fc = new JFileChooser( new File(".").getAbsolutePath() );
        fc.setMultiSelectionEnabled(true);
        return fc;
    }
    public String getProjectName() {
        return txtProjectName.getText().trim();
    }

    public String getProjectLocation() {
        return txtProjectLocation.getText().trim();
    }

    public String getTargetSchema() {
        return txtTargetSchema.getText().trim();
    }

    public String getSources(){
        return txtSources.getText().trim();
    }

    public int getDialogResult(){
        return dialogResult;
    }


}
