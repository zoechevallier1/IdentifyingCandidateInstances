package view;

import model.Match;
import model.SchemaMatchs;
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

public class ShowMatches {

    private String selectedSource = "all";


    public ShowMatches(){

        String header[] = {"Source class", "Source property", "Target class", "Target property"};
        ArrayList<SchemaMatchs> allSourcesMatchs = DI_APP.getMainWindow().getRunArchitecture().getMatchs();
        int countMatchs = 0;
        for (SchemaMatchs sourceMatchs : allSourcesMatchs) {
            countMatchs += sourceMatchs.getMatchs().size();
        }
        //Création du tableau vide
        Object data[][] = new Object[countMatchs][4];

        //On ajoute les valeurs dans les cases du tableau
        int i = 0;
        for (SchemaMatchs sourceMatchs : allSourcesMatchs) {
            for (Match match : sourceMatchs.getMatchs()) {
                data[i][0] = match.getSourceClass();
                data[i][1] = match.getPropSource();
                data[i][2] = match.getTargetClass();
                data[i][3] = match.getTargetProp();
                i++;
            }
        }


        JFrame frame = new JFrame("SHOW MATCHES");



        final JTable table = new JTable(data, header);
        JScrollPane jscrollPane = new JScrollPane(table);


        TableRowSorter<TableModel> sort = new TableRowSorter<>(table.getModel());

        TableColumnModel columnModel = table.getColumnModel();
        //columnModel.getColumn(2).setPreferredWidth(200);

        table.setRowSorter(sort);


        JComboBox comboBox = new JComboBox();
        //comboBox.setBorder(new LineBorder(new Color(0, 0, 0)));
        //comboBox.setBounds(124, 48, 290, 22);
        comboBox.addItem("all");
        for (Source source: DI_APP.getMainWindow().getProjectManager().getSources()){
            comboBox.addItem(source.getFileSource().getName());
        }
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedSource = e.getItem().toString();
                if (selectedSource.trim().equals("all")) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + selectedSource.split("\\.")[0], 0));
                }


            }
        });

        JPanel panel= new JPanel(new BorderLayout());
        panel.add(new JLabel("Source"), BorderLayout.WEST);
        panel.add(comboBox, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());

        frame.add(panel,BorderLayout.NORTH);
        frame.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.CENTER);
        frame.add(jscrollPane, BorderLayout.CENTER);


        frame.setSize(300, 150);
        frame.setVisible(true);
    }









}
