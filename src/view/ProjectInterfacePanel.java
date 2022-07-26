package view;

import controller.ProjectManager;
import edu.uci.ics.jung.graph.Tree;
import model.Source;
import model.TreeStructureProjet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static controller.DataIntegration.DI_APP;

public class ProjectInterfacePanel extends JPanel {

    private ProjectManager projectManager;
    private TreeStructureProjet arborescence;

    public ProjectInterfacePanel(ProjectManager projectManager){

        this.projectManager = projectManager;
        this.setPreferredSize(new Dimension(300,300));
        this.setBackground(Color.lightGray);
        this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));

        TreeStructureProjet arborescence = new TreeStructureProjet(projectManager);
        JScrollPane arborescenceView = new JScrollPane(arborescence.getTree());

        add(arborescenceView);

    }




}
