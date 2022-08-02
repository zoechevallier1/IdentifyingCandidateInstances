package view;

import model.Link;
import model.Links;
import model.Source;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import static controller.DataIntegration.DI_APP;

public class ShowLinks {

    private String selectedSource1;
    private String selectedSource2;

    public ShowLinks(){

        String header[] = {"Source 1 : instances", "Source 2 : instances"};
        ArrayList<Links> allSourcesLinks = DI_APP.getMainWindow().getRunArchitecture().getLinks();
        int countLinks = 0;
        for (Links sourcesLinks : allSourcesLinks){
            countLinks += sourcesLinks.getLinks().size();
        }
        //Création du tableau vide
        Object data[][] = new Object[countLinks][2];

        //On ajoute les valeurs dans les cases du tableau
        int i=0;
        for (Links sourcesLinks : allSourcesLinks){
            for (Link link : sourcesLinks.getLinks()){
                data[i][0] = link.getInstanceS1();
                data[i][1] = link.getInstanceS2();
                i++;
            }
        }

        JFrame frame = new JFrame("SHOW LINKS");

        final JTable table = new JTable(data, header);
        JScrollPane jscrollPane = new JScrollPane(table);

        TableRowSorter<TableModel> sort = new TableRowSorter<>(table.getModel());

        TableColumnModel columnModel = table.getColumnModel();
        table.setRowSorter(sort);

        JComboBox comboBox = new JComboBox();
        comboBox.addItem("all");
        for (Source source: DI_APP.getMainWindow().getProjectManager().getSources()){
            if (selectedSource1 == null){
                selectedSource1 = source.getFileSource().getName();
            }
            comboBox.addItem(source.getFileSource().getName());
        }
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedSource1 = e.getItem().toString();
                if (selectedSource1.trim().equals("all")) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + selectedSource1.split("\\.")[0], 0));
                }
            }
        });
       JPanel panel1 = new JPanel((new BorderLayout()));
       panel1.add(new JLabel("Source 1 :"), BorderLayout.WEST);
       panel1.add(comboBox, BorderLayout.CENTER);


       JComboBox comboBox2 = new JComboBox();
       comboBox2.addItem("all");
        for (Source source: DI_APP.getMainWindow().getProjectManager().getSources()){
            if (selectedSource2 == null){
                selectedSource2 = source.getFileSource().getName();
            }
            comboBox2.addItem(source.getFileSource().getName());
        }
        comboBox2.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedSource2 = e.getItem().toString();
                if (selectedSource2.trim().equals("all")) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + selectedSource2.split("\\.")[0], 1));
                }
            }
        });
        JPanel panel2 = new JPanel((new BorderLayout()));
        panel2.add(new JLabel("Source 2 :"), BorderLayout.WEST);
        panel2.add(comboBox2, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(panel1);
        panel.add(panel2);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);



        frame.add(jscrollPane, BorderLayout.CENTER);


        frame.setSize(300, 150);
        frame.setVisible(true);








    }

}
