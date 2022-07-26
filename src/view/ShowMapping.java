package view;

import model.Mapping;
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

    public ShowMapping(Mapping mapping){

        if (mapping == null) {
            mapping = new Mapping("test");
        }

        JFrame frame = new JFrame("MAPPING : " + mapping.getTargetClass());
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));

        JLabel sources = new JLabel("Sources class :");
        panel1.add(sources);
        for (Source source : mapping.getSources()){
            JLabel lab = new JLabel(source.getName());
            panel1.add(lab);
        }

        panel.add(panel1);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.CENTER);


        frame.setSize(300, 150);
        frame.setVisible(true);
    }

}
