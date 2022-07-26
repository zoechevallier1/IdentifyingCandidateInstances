package view;

import model.Source;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static controller.DataIntegration.DI_APP;

public class MatchingDialog extends JDialog {

    String selectedSource = null;
    private int dialogResult = JOptionPane.CANCEL_OPTION;

    public MatchingDialog(){

        setModal(true);
        setTitle("Choose Matching");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setMinimumSize(new Dimension(435, 120));
        setMaximumSize(new Dimension(435, 120));
        setPreferredSize(new Dimension(435, 120));
        setSize(new Dimension(500, 200));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton btCreate = new JButton("Run Matching");
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


        JPanel panel_1 = new JPanel();
        getContentPane().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(null);

        JLabel lblProjectLocation = new JLabel("Source :");
        lblProjectLocation.setHorizontalAlignment(SwingConstants.RIGHT);
        lblProjectLocation.setSize(new Dimension(0, 26));
        lblProjectLocation.setBounds(8, 48, 114, 22);
        panel_1.add(lblProjectLocation);



        JComboBox comboBox = new JComboBox();
        comboBox.setBorder(new LineBorder(new Color(0, 0, 0)));
        comboBox.setBounds(124, 48, 290, 22);

        comboBox.addItem("all");
        selectedSource = "all";
        for (Source source: DI_APP.getMainWindow().getRunArchitecture().getProjectManager().getSources()){
            comboBox.addItem(source.getFileSource().getName());
        }
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedSource = e.getItem().toString();
            }
        });
        panel_1.add(comboBox);

    }

    public int getDialogResult(){
        return dialogResult;
    }
    public String getSelectedSource(){
        return this.selectedSource;
    }
}
