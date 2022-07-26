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

    public ShowMapping(String targetClass){


        JFrame frame = new JFrame("MAPPING : " + targetClass);
        JPanel panel = new JPanel(new BorderLayout());

        JLabel sources = new JLabel("Sources class :");
        panel.add(sources);




        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.CENTER);


        frame.setSize(300, 150);
        frame.setVisible(true);
    }

}
