package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AddSourceDialog extends JDialog {

    private JTextPane txtPathSource = null;

    private JFileChooser fc = null;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    public AddSourceDialog(){
        setModal(true);
        setTitle("Add source");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setMinimumSize(new Dimension(435, 120));
        setMaximumSize(new Dimension(435, 120));
        setPreferredSize(new Dimension(435, 120));
        setSize(new Dimension(500, 200));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton btCreate = new JButton("Add");
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
                        txtPathSource.setText(null);
                        setVisible(false);
                        dispose();
                    }
                });
        panel.add(btCancel);

        JPanel panel_1 = new JPanel();
        getContentPane().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(null);

        JLabel lblProjectLocation = new JLabel("Source :");
        lblProjectLocation.setHorizontalAlignment(SwingConstants.RIGHT);
        lblProjectLocation.setSize(new Dimension(0, 26));
        lblProjectLocation.setBounds(8, 48, 114, 22);
        panel_1.add(lblProjectLocation);

        txtPathSource = new JTextPane();
        txtPathSource.setBorder(new LineBorder(new Color(0, 0, 0)));
        txtPathSource.setBounds(124, 48, 290, 22);
        panel_1.add(txtPathSource);

        JButton btProjectLocation = new JButton("...");
        btProjectLocation.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
                        int returnVal = fc.showDialog(AddSourceDialog.this, "Add source");
                        if(returnVal == JFileChooser.APPROVE_OPTION)
                            txtPathSource.setText( fc.getSelectedFile().getAbsolutePath() );
                    }
                }
        );
        btProjectLocation.setBounds(420, 48, 22, 22);
        panel_1.add(btProjectLocation);

    }

    public int getDialogResult(){
        return dialogResult;
    }
    private JFileChooser getFileChooser()
    {
        if(fc == null)
            fc = new JFileChooser( new File(".").getAbsolutePath() );
        return fc;
    }

    public String getTxtPathSource() {
        return txtPathSource.getText().trim();
    }


}
