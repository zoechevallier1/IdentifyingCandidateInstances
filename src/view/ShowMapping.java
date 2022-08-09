package view;

import controller.ProjectManager;
import model.Element;
import model.Mapping;
import javax.swing.*;

import java.awt.*;

import static controller.DataIntegration.DI_APP;


public class ShowMapping {

    private String selectedSource = "all";

    public ShowMapping(Mapping mapping){

        if (mapping == null) {
            //mapping = new Mapping(new Element("",""));
        }

        JFrame frame = new JFrame("MAPPING : " + mapping.getTargetClass().getLocalName());
        JPanel panel = new JPanel(new BorderLayout());


        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));

        ProjectManager pm = DI_APP.getMainWindow().getProjectManager();
        Element targetClass = mapping.getTargetClass();
        JLabel targetClass_label = new JLabel("Target class : " + targetClass.getLocalName() + " (" + pm.getTargetSchema().getNbInstances(targetClass.getUri()) + " instances)");
        panel1.add(targetClass_label);
        JLabel sources = new JLabel("Sources class :");
        panel1.add(sources);
        for (Element sourceClass : mapping.getSourcesClass()){
            JLabel lab = new JLabel(sourceClass.getProvenance().getName() + "  " + sourceClass.getLocalName() + " (" + sourceClass.getProvenance().getNbInstances(sourceClass.getUri()) + " instances)");
            panel1.add(lab);
        }

        panel.add(panel1, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.CENTER);

        frame.setSize(300, 150);
        frame.setVisible(true);
    }
}
