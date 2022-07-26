package view;

import model.Source;
import org.apache.jena.rdf.model.Resource;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import static controller.DataIntegration.DI_APP;

public class ShowMapping {

    private String selectedSource = "all";

    public ShowMapping(){

        String header[] = {"TARGET (+nb instances)", "SOURCE", "SOURCE PROPERTIES", "INSTANCES"};
        ArrayList<Resource[]> ClassMatchs = DI_APP.getMainWindow().getRunArchitecture().getClassMatch();
        int countMatchs = 0;
        for (Resource[] match : ClassMatchs){
            countMatchs ++;;
        }
        //Création du tableau vide
        Object data[][] = new Object[countMatchs][4];

        //On ajoute les valeurs dans les cases du tableau
        int i=0;
        for (Resource[] sourceMatchs : ClassMatchs) {
            Source source = DI_APP.getMainWindow().getProjectManager().getSourceBySameAs(sourceMatchs[1]);
            data[i][0] = sourceMatchs[0] + "("+ DI_APP.getMainWindow().getProjectManager().getTargetSchema().getNbInstances(sourceMatchs[0].toString())+" instances)";
            data[i][1] = sourceMatchs[1];
            data[i][2] = "<html>";
            for (String prop : source.getMatchProperties(sourceMatchs[1].toString(), sourceMatchs[0].toString())){
                data[i][2] += prop + "<br/>";
            }

            data[i][3] = source.getNbInstances(sourceMatchs[1].toString());
            i++;
        }

        JFrame frame = new JFrame("SHOW MAPPING");
        final JTable table = new JTable(data, header);
        JScrollPane jScrollPane = new JScrollPane(table);
        table.setRowHeight(100);

        TableRowSorter<TableModel> sort = new TableRowSorter<>(table.getModel());
        TableColumnModel columnModel = table.getColumnModel();
        table.setRowSorter(sort);

        JComboBox comboBox = new JComboBox();
        comboBox.addItem("all");
        for (String classe: DI_APP.getMainWindow().getProjectManager().getTargetSchema().getClasses()){
            if (selectedSource == null){
                selectedSource = classe;
            }
            comboBox.addItem(classe);
        }

        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                selectedSource = e.getItem().toString();
                if (selectedSource.trim().equals("all")) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + selectedSource));
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Target class : "), BorderLayout.WEST);
        panel.add(comboBox, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.CENTER);
        frame.add(jScrollPane, BorderLayout.CENTER);

        frame.setSize(300, 150);
        frame.setVisible(true);
    }

}
