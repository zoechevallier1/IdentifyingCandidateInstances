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

public class LinkingDialog extends JDialog {

    String selectedSource1 = null;
    String selectedSource2 = null;
    private int dialogResult = JOptionPane.CANCEL_OPTION;
    private int allResult = JOptionPane.CANCEL_OPTION;

    public LinkingDialog(){

        setModal(true);
        setTitle("Choose Linking");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setMinimumSize(new Dimension(435, 120));
        setMaximumSize(new Dimension(435, 120));
        setPreferredSize(new Dimension(435, 120));
        setSize(new Dimension(500, 200));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton btCreate = new JButton("Run Linking");
        btCreate.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Ajouter des vérifications pour que les sources soient différentes
                        dialogResult = JOptionPane.OK_OPTION;
                        setVisible(false);
                    }
                }
        );
        panel.add(btCreate);

        JButton allLinking = new JButton("Run All Linking");
        allLinking.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Ajouter des vérifications pour que les sources soient différentes
                        allResult = JOptionPane.OK_OPTION;
                        setVisible(false);
                    }
                }
        );
        panel.add(allLinking);


        JPanel panel_1 = new JPanel();
        getContentPane().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(null);

        JLabel label_source = new JLabel("Source 1:");
        label_source.setHorizontalAlignment(SwingConstants.RIGHT);
        label_source.setSize(new Dimension(0, 26));
        label_source.setBounds(8, 48, 114, 22);
        panel_1.add(label_source);


        JComboBox comboBox = new JComboBox();
        comboBox.setBorder(new LineBorder(new Color(0, 0, 0)));
        comboBox.setBounds(124, 48, 290, 22);
        for (Source source: DI_APP.getMainWindow().getRunArchitecture().getProjectManager().getSources()){
            if (selectedSource1 == null){
                selectedSource1 = source.getFileSource().getName();
            }
            comboBox.addItem(source.getFileSource().getName());
        }
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedSource1 = e.getItem().toString();
            }
        });
        panel_1.add(comboBox);

        label_source = new JLabel("Source 2:");
        label_source.setHorizontalAlignment(SwingConstants.RIGHT);
        label_source.setSize(new Dimension(0, 26));
        label_source.setBounds(8, 88, 114, 22);
        panel_1.add(label_source);


        comboBox = new JComboBox();
        comboBox.setBorder(new LineBorder(new Color(0, 0, 0)));
        comboBox.setBounds(124, 88, 290, 22);
        for (Source source: DI_APP.getMainWindow().getRunArchitecture().getProjectManager().getSources()){
            if (selectedSource2 == null){
                selectedSource2 = source.getFileSource().getName();
            }
            comboBox.addItem(source.getFileSource().getName());
        }
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedSource2 = e.getItem().toString();
            }
        });
        panel_1.add(comboBox);

    }

    public int getDialogResult(){
        return dialogResult;
    }
    public String getSelectedSource1(){
        return this.selectedSource1;
    }
    public String getSelectedSource2(){
        return this.selectedSource2;
    }

    public int getAllResult() {
        return allResult;
    }
}
