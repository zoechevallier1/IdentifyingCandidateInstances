package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedGraph;
import model.FileToModelGraph;
import model.Source;
import model.TargetSchema;
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

public class ProjectManager {

    // Nom du projet
    private final String projectName;

    // Dossier de localisation du projet
    private File projectDirectory;

    // Schéma Cible (unique)
    private TargetSchema targetSchema;

    // Liste de sources
    private ArrayList<Source> sources = new ArrayList<Source>();


    public ProjectManager(String projectName, File projectDirectory, TargetSchema targetSchema, ArrayList<Source> sources){
        this.projectName = projectName;
        this.projectDirectory = projectDirectory;
        this.targetSchema = targetSchema;
        this.sources = sources;
    }

    public ProjectManager(File projectDirectory) throws IOException{
        this.projectDirectory = projectDirectory;
        if (!projectDirectory.exists()) throw new IOException("Project directory " + projectDirectory + " does not exist");
        if (!projectDirectory.isDirectory())
            throw new IOException("Project directory projectName " + projectDirectory + " exists but is not a directory");

        this.projectName = projectDirectory.getName();
        File sources = new File(projectDirectory + "/sources");
        //System.out.println("Sources : " + sources.getAbsolutePath());
        for (File file : sources.listFiles()){
            if (!file.isDirectory())
                this.sources.add(new Source(file));
        }
        for (File file : projectDirectory.listFiles()){
            if (!file.isDirectory()){
                this.targetSchema = new TargetSchema(file);
            }
        }

    }

    public String getProjectName() {
        return projectName;
    }

    public TargetSchema getTargetSchema(){
        return targetSchema;
    }

    public File getSource(){
        return sources.get(0).getFileSource();
    }
    public ArrayList<Source> getSources(){
        return sources;
    }

    /*private void checkGraphFile(File file) throws IOException {
        if (file == null) {
            file = new File(projectDirectory, file.getName());
            if (!file.isFile() || !file.exists())
                throw new IOException("Graph location error");
        }
    }*/

    public DirectedGraph<RDFNode, Statement> loadGraph(File file){
        return FileToModelGraph.FileToGraph(file.getAbsolutePath());
    }

    public DirectedGraph<RDFNode, Statement> loadGraph(Model model){
        return FileToModelGraph.ModelToGraph(model);
    }

    public Source getSourceByName(String name){
        for (Source source : sources){
            if (source.getFileSource().getName().equals(name)){
                return source;
            }
        }
        return null;
    }

    public Source getSourceByResourceClass(Resource resource){
        for (Source source: sources){
            if (source.getModelSource().contains(resource, RDF.type ,OWL.Class)){
                System.out.println(source.getFileSource().getName());
                return source;
            }
        }
        return null;
    }

    public Source getSourceBySameAs(Resource sameAs){
        String[] nameSource = sameAs.toString().split("#")[0].split("/");

        return getSourceByName(nameSource[nameSource.length-1]+ ".owl");
    }

    public ProjectManager addSource(Source source){
        this.sources.add(source);
        return this;
    }

    public File getProjectDirectory() {
        return projectDirectory;
    }
}
