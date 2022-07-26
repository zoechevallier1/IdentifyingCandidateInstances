package view;

import controller.RunArchitecture;
import org.apache.jena.base.Sys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static controller.DataIntegration.DI_APP;

// Creation de la barre d'accueil
public class MainMenuBar extends JMenuBar {

    private final static Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    public MainMenuBar(DesktopPane desktop){

        //Menu PROJET
        JMenu projectMenu = new JMenu("Project");

        JMenuItem menuItem = new JMenuItem("New Project");
        menuItem.addActionListener(this::newProjectActionPerformed);
        projectMenu.add(menuItem);

        menuItem = new JMenuItem("Open Project");
        menuItem.addActionListener(this::openProject);
        projectMenu.add(menuItem);

        menuItem = new JMenuItem("Save Project");
        //menuItem.addActionListener(this::openProject);
        projectMenu.add(menuItem);

        menuItem = new JMenuItem("Close Project");
        //menuItem.addActionListener(this::openProject);
        projectMenu.add(menuItem);

        add(projectMenu);

        // EDIT MENU
        JMenu editMenu = new JMenu("Edit");

        menuItem = new JMenuItem("Add Source");
        menuItem.addActionListener(this::addSourceActionPerformed);
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Delete Source");
        //menuItem.addActionListener(this::openProject);
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Add Target Schema");
        //menuItem.addActionListener(this::openProject);
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Delete Target Schema");
        //menuItem.addActionListener(this::openProject);
        editMenu.add(menuItem);

        add(editMenu);


        // VIEW
        JMenu viewMenu = new JMenu("View");

        menuItem = new JMenuItem("matches");
        menuItem.addActionListener(this::showMatches);
        viewMenu.add(menuItem);

        menuItem = new JMenuItem("links");
        menuItem.addActionListener(this::showLinks);
        viewMenu.add(menuItem);

        menuItem = new JMenuItem("mappings");
        menuItem.addActionListener(this::showMappings);
        viewMenu.add(menuItem);

        add(viewMenu);

        // INTEGRATION MENU
        JMenu integrationMenu = new JMenu("Integration");

        menuItem = new JMenuItem("Matching");
        menuItem.addActionListener(this::runMatching);
        integrationMenu.add(menuItem);

        menuItem = new JMenuItem("Linking");
        menuItem.addActionListener(this::runLinking);
        integrationMenu.add(menuItem);

        menuItem = new JMenuItem("Instanciating");
        menuItem.addActionListener(this::runInstanciating);
        integrationMenu.add(menuItem);

        /*menuItem = new JMenuItem("Clear");
        menuItem.addActionListener(this::runClear);
        integrationMenu.add(menuItem);*/

        add(integrationMenu);

    }

    private void runMatching(ActionEvent evt) {
        try {
            RunArchitecture runArchitecture = DI_APP.getMainWindow().getRunArchitecture();
            if (runArchitecture == null){
                runArchitecture = DI_APP.getMainWindow().startRunArchitecture();
            }

            MatchingDialog matchingDialog = new MatchingDialog();
            matchingDialog.setVisible(true);
            if (matchingDialog.getDialogResult()== JOptionPane.OK_OPTION){
                if (matchingDialog.getSelectedSource().equals("all")){
                    runArchitecture.matching();
                }
                else { runArchitecture.matching(matchingDialog.getSelectedSource());}
                JOptionPane.showMessageDialog(null, "Matching complete");

            }

        } catch (Exception ex){

        }
    }

    public void showMatches(ActionEvent evt){

        ShowMatches showMatches = new ShowMatches();
        //showMatches.setVisible(true);

    }

    public void showLinks(ActionEvent evt){

        ShowLinks showLinks = new ShowLinks();
        //showLinks.setVisible(true);

    }

    public void showMappings(ActionEvent evt){
        ShowMappingDialog showMappingDialog = new ShowMappingDialog();
        showMappingDialog.setVisible(true);
        if (showMappingDialog.getDialogResult()== JOptionPane.OK_OPTION){
            System.out.println(showMappingDialog.getSelectedClass());
            ShowMapping showMapping = new ShowMapping(DI_APP.getMainWindow().getRunArchitecture().getMapping(showMappingDialog.getSelectedClass()));

        }

    }

    public void runLinking(ActionEvent evt){
        try {
            RunArchitecture runArchitecture = DI_APP.getMainWindow().getRunArchitecture();
            if (runArchitecture == null){
                runArchitecture = DI_APP.getMainWindow().startRunArchitecture();
            }

            LinkingDialog linkingDialog = new LinkingDialog();
            linkingDialog.setVisible(true);
            if (linkingDialog.getDialogResult()== JOptionPane.OK_OPTION){
                runArchitecture.linking(linkingDialog.getSelectedSource1(), linkingDialog.getSelectedSource2());
                JOptionPane.showMessageDialog(null, "Linking complete");
            }
            if (linkingDialog.getAllResult()== JOptionPane.OK_OPTION){
                runArchitecture.linking();
                JOptionPane.showMessageDialog(null, "Linking complete");
            }

        } catch (Exception ex){

        }
    }

    public void runInstanciating(ActionEvent evt){
        RunArchitecture runArchitecture = DI_APP.getMainWindow().getRunArchitecture();
        runArchitecture.instanciating();
        JOptionPane.showMessageDialog(null, "Instanciation complete");
    }

    private void newProjectActionPerformed(ActionEvent evt) {
        try {
            CreateProjectDialog chooser = new CreateProjectDialog();
            chooser.setVisible(true);
            System.out.println(chooser.getSources());
            if (chooser.getDialogResult() == JOptionPane.OK_OPTION) {
                DI_APP.getMainWindow().newProject(chooser.getProjectName(), chooser.getProjectLocation(), chooser.getTargetSchema(), chooser.getSources());
            }
        } catch (Exception ex) {

        }
    }

    private void addSourceActionPerformed(ActionEvent evt){
        try {
            AddSourceDialog addSource = new AddSourceDialog();
            addSource.setVisible(true);


            if (addSource.getDialogResult() == JOptionPane.OK_OPTION){
                DI_APP.getMainWindow().addSource(addSource.getTxtPathSource());

            }

        } catch (Exception ex){

        }
    }

    private void openProject(ActionEvent evt) {
        try {
            JFileChooser chooser = new JFileChooser(new File(".").getAbsolutePath());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showDialog(DI_APP.getMainWindow(), "Project location");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                DI_APP.getMainWindow().openProject(chooser.getSelectedFile());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(DI_APP.getMainWindow(), ex.getMessage(), "Project loading error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
