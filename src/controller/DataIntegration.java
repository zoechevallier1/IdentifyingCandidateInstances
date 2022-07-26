package controller;

import model.*;
import view.MainWindow;
import view.ProjectInterfacePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public enum DataIntegration {
    DI_APP;
    private final static Logger LOGGER = Logger.getLogger(MainWindow.class.getName());
    private MainWindow mainWindow;

    public MainWindow getMainWindow() { return mainWindow; }

    public void run(String[] args) throws IOException {
        mainWindow = new MainWindow();
        java.awt.EventQueue.invokeLater(() -> mainWindow.setVisible(true));

        /*String projectName = "TEST";
        Path parentDirectory = FileSystems.getDefault().getPath("C:\\Users\\z.chevallier\\Documents\\UseCaseHoraireMediatheques\\TST ARCHITECTURE");
        Path projectDirectory = Files.createDirectory(parentDirectory.resolve(projectName));
        Path targetSchemaFile = FileSystems.getDefault().getPath("C:\\Users\\z.chevallier\\Documents\\UseCaseHoraireMediatheques\\schemaCible\\schemaCiblev2.owl");
        Path targetSchemaDestinationFile = projectDirectory.resolve(targetSchemaFile.getFileName());
        Files.copy(targetSchemaFile, targetSchemaDestinationFile, REPLACE_EXISTING);

        TargetSchema targetSchema = new TargetSchema(targetSchemaFile.toFile());

        ArrayList<Source> sourcesFiles = new ArrayList<Source>() ;


        Path pathSourceFile = FileSystems.getDefault().getPath("C:\\Users\\z.chevallier\\Documents\\UseCaseHoraireMediatheques\\Sources\\Mediathèques\\mediatheques-gps-nov2020.ttl");
        Path pathSourceDestinationFile = projectDirectory.resolve(pathSourceFile.getFileName());
        Files.copy(pathSourceFile, pathSourceDestinationFile, REPLACE_EXISTING);
        Source mediatheques = new Source(pathSourceDestinationFile.toFile());
        sourcesFiles.add(mediatheques);
        pathSourceFile = FileSystems.getDefault().getPath("C:\\Users\\z.chevallier\\Documents\\UseCaseHoraireMediatheques\\Sources\\Indicateurs médiathèques\\indicateurs-mediatheques.ttl");
        pathSourceDestinationFile = projectDirectory.resolve(pathSourceFile.getFileName());
        Files.copy(pathSourceFile, pathSourceDestinationFile, REPLACE_EXISTING);
        Source indicateursMediatheques = new Source(pathSourceDestinationFile.toFile());
        sourcesFiles.add(indicateursMediatheques);

        ProjectManager projectManager = new ProjectManager(projectName, projectDirectory.toFile(), targetSchema, sourcesFiles);


        ArrayList<Match> matchs = new ArrayList<Match>();
        Match match = new Match("https://data.grandparissud.fr/ld/ontologies/indicateurs-mediatheques/bibliotheque_mediatheque_ludotheque", "http://www.semanticweb.org/z.chevallier/ontologies/2022/0/untitled-ontology-28#nom_mediatheque");
        matchs.add(match);
        match = new Match("https://data.grandparissud.fr/ld/ontologies/indicateurs-mediatheques/superficie_actuelle", "http://www.semanticweb.org/z.chevallier/ontologies/2022/0/untitled-ontology-28#superficie");
        matchs.add(match);
        indicateursMediatheques.setMatchsSchema(matchs);


        matchs = new ArrayList<Match>();
        match = new Match("https://data.grandparissud.fr/ld/ontologies/mediatheques-gps-nov2020/nom_de_l_etablissement", "http://www.semanticweb.org/z.chevallier/ontologies/2022/0/untitled-ontology-28#nom_mediatheque");
        matchs.add(match);
        match = new Match("https://data.grandparissud.fr/ld/ontologies/mediatheques-gps-nov2020/adresse", "http://www.semanticweb.org/z.chevallier/ontologies/2022/0/untitled-ontology-28#adresse");
        matchs.add(match);
        mediatheques.setMatchsSchema(matchs);

        mainWindow = new MainWindow();
        java.awt.EventQueue.invokeLater(() -> mainWindow.setVisible(true));

        mainWindow.setProjectManager(projectManager);
        ProjectInterfacePanel projectInterfacePanel = new ProjectInterfacePanel(projectManager);
        mainWindow.setLayout(new BorderLayout());
        mainWindow.add(projectInterfacePanel, BorderLayout.WEST);

        mainWindow.pack();
        mainWindow.setVisible(true);
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);


        RunArchitecture run = new RunArchitecture(projectManager);

        InstancesMatchs instancesMatchs = new InstancesMatchs(new File("C:/Users/z.chevallier/Documents/UseCaseHoraireMediatheques/linking/mediatheques-indicateurs/alignment.xml"), mediatheques, indicateursMediatheques);
        run.extractionSousEnsembles();*/

        //Path pathSourceFile = FileSystems.getDefault().getPath("C:\\Users\\z.chevallier\\Documents\\UseCaseHoraireMediatheques\\Sources\\Mediathèques\\mediatheques-gps-nov2020.ttl");
        //Path pathSourceDestinationFile = projectDirectory.resolve(pathSourceFile.getFileName());
        //Files.copy(pathSourceFile, pathSourceDestinationFile, REPLACE_EXISTING);
        //Source mediatheques = new Source(pathSourceFile.toFile());
        //mediatheques.getClasses();

    }

     public static void main(String[] args) throws IOException{
        DI_APP.run(args);
    }
}
