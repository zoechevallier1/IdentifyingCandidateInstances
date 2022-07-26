package view;

import controller.ProjectManager;
import controller.RunArchitecture;
import model.Source;
import model.TargetSchema;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static controller.DataIntegration.DI_APP;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MainWindow extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());
    private static final String MAIN_WINDOW_TITLE = "Data Integration Architecture";
    private Container contentPane;
    private DesktopPane desktop;

    private ProjectInterfacePanel projectInterfacePanel;

    private JInternalFrame internalFrame = null;

    private RunArchitecture runArchitecture = null;


    private ProjectManager projectManager = null;
    private Path projectDirectory;
    private Path sourcesDirectory;
    private ProjectInterface projectInterface;

    public MainWindow(){

        // Parametres de la fenetre
        setTitle(MAIN_WINDOW_TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        desktop = new DesktopPane();

        // Creation du conteneur de la page d'accueil
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // Mettre la fenetre a la taille de l'ecran
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Barre d'acceuil
        MainMenuBar menuBar = new MainMenuBar(desktop);
        setJMenuBar(menuBar);

    }

    public void setInternalFrame(JInternalFrame internalFrame){
        if (internalFrame != null) {
            this.remove(internalFrame);
        }
        this.internalFrame = internalFrame;
    }

    public JInternalFrame getInternalFrame(){
        return internalFrame;
    }

    public void newProject(String projectName, String projectLocation, String pathTargetSchema, String pathSources ) throws IOException {
        Path parentDirectory = FileSystems.getDefault().getPath(projectLocation);

        if (!Files.exists(parentDirectory) || !Files.isDirectory(parentDirectory)){
            String msg = "Project location " + parentDirectory + " does not exist or is not a directory";
            LOGGER.log(Level.SEVERE, msg);
            throw new IOException(msg);
        }

        try {
            //Création de l'espace de travail, et copie des fichiers utilisés.
            projectDirectory = Files.createDirectory(parentDirectory.resolve(projectName));
            sourcesDirectory = Files.createDirectory(projectDirectory.resolve("sources"));


            Path targetSchemaFile = FileSystems.getDefault().getPath(pathTargetSchema);
            Path targetSchemaDestinationFile = projectDirectory.resolve(targetSchemaFile.getFileName());
            Files.copy(targetSchemaFile, targetSchemaDestinationFile, REPLACE_EXISTING);
            TargetSchema targetSchema = new TargetSchema(targetSchemaDestinationFile.toFile());



            ArrayList<Source> sourcesFiles = new ArrayList<Source>() ;
            String[] sources = pathSources.split("/");
            for (String source : sources){

                Path pathSourceFile = FileSystems.getDefault().getPath(source);
                Path pathSourceDestinationFile = sourcesDirectory.resolve(pathSourceFile.getFileName());
                Files.copy(pathSourceFile, pathSourceDestinationFile, REPLACE_EXISTING);

                Source s = new Source(pathSourceDestinationFile.toFile());
                sourcesFiles.add(s);
            }

            // On créé notre manager de projet
            projectManager = new ProjectManager(projectName, projectDirectory.toFile(), targetSchema, sourcesFiles);
            this.setTitle(MAIN_WINDOW_TITLE + " : " + projectName);


            projectInterfacePanel = new ProjectInterfacePanel(projectManager);
            for (Source s : projectManager.getSources()) {
                //System.out.println(s.getClasses());
            }
            this.setLayout(new BorderLayout());
            this.add(projectInterfacePanel, BorderLayout.WEST);

            this.pack();
            this.setVisible(true);
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        } catch (IOException ex){
            String msg = "Unable to create project directory " + parentDirectory.resolve(projectName);
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new IOException(msg, ex);
        }


    }

    DesktopPane getDesktop(){
        return desktop;
    }

    public void setProjectManager(ProjectManager projectManager){
        this.projectManager = projectManager;
    }

    public ProjectManager getProjectManager(){
        return this.projectManager;
    }

    public void addSource(String pathSource) throws IOException {

        Path pathSourceFile = FileSystems.getDefault().getPath(pathSource);
        Path pathSourceDestinationFile = sourcesDirectory.resolve(pathSourceFile.getFileName());

        Files.copy(pathSourceFile, pathSourceDestinationFile, REPLACE_EXISTING);

        projectManager = projectManager.addSource(new Source(pathSourceDestinationFile.toFile()));

        this.setVisible(false);

        projectInterfacePanel = new ProjectInterfacePanel(projectManager);
        BorderLayout layout = new BorderLayout();
        layout.addLayoutComponent(projectInterfacePanel, BorderLayout.WEST);
        this.setLayout(new BorderLayout());
        Container content = new Container();
        content.setLayout(new BorderLayout());
        content.add(projectInterfacePanel, BorderLayout.WEST);
        this.setContentPane(content);
        //this.add(projectInterfacePanel, BorderLayout.WEST);


        this.pack();
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

    }

    public RunArchitecture getRunArchitecture(){
        return this.runArchitecture;
    }

    public void setRunArchitecture(RunArchitecture runArchitecture){
        this.runArchitecture = runArchitecture;
    }

    public RunArchitecture startRunArchitecture(){
        this.runArchitecture = new RunArchitecture(projectManager);
        return this.runArchitecture;
    }

    public void openProject(File projectDirectory) throws IOException {
        if(projectManager != null){
            this.remove(projectInterfacePanel);
            if (this.internalFrame != null){
                this.remove(internalFrame);
            }
        }
        projectManager = new ProjectManager(projectDirectory);
        this.setTitle(MAIN_WINDOW_TITLE + " : " + projectManager.getProjectName());


        projectInterfacePanel = new ProjectInterfacePanel(projectManager);
        for (Source s : projectManager.getSources()) {
        }
        this.setLayout(new BorderLayout());
        this.add(projectInterfacePanel, BorderLayout.WEST);

        this.pack();
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }


}
