package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.uci.ics.jung.graph.DirectedGraph;
import model.*;
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

    private ArrayList<Entity> entities;


    public ProjectManager(String projectName, File projectDirectory, TargetSchema targetSchema, ArrayList<Source> sources){
        this.projectName = projectName;
        this.projectDirectory = projectDirectory;
        this.targetSchema = targetSchema;
        this.sources = sources;
        this.entities = new ArrayList<Entity>();
    }

    public ProjectManager(File projectDirectory) throws IOException{
        this.entities = new ArrayList<Entity>();
        this.projectDirectory = projectDirectory;
        if (!projectDirectory.exists()) throw new IOException("Project directory " + projectDirectory + " does not exist");
        if (!projectDirectory.isDirectory())
            throw new IOException("Project directory projectName " + projectDirectory + " exists but is not a directory");

        this.projectName = projectDirectory.getName();
        File sources = new File(projectDirectory + "/sources");
        for (File file : sources.listFiles()){
            if (!file.isDirectory()) {
                //System.out.println("File : " + file.getName());
                Source s = new Source(file);
                this.sources.add(s);

                for (Entity e : s.entities()){
                    this.entities.add(e);
                }


            }
        }

        //If a target schema is on the folder
        File target = new File(projectDirectory + "/target-schema.owl");
        if (target.exists()) {
            this.targetSchema = new TargetSchema(target);
        }
        else {
            this.targetSchema = new TargetSchema(this);
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
    public ArrayList<Source> getSources() {
        return sources;
    }

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

    public File getProjectDirectory() {
        return projectDirectory;
    }

    public Entity getEntityByURI(String uri){
        for (Entity e: this.entities){
            if (e.getUri().equals(uri)){
                return e;
            }
        }
        return null;
    }

    public double moyennePropParEntite(Source s){
        double nbEntity = 0 ;
        double nbProp = 0;
        for (Entity e : this.entities){
            if (e.getSources().contains(s)) {
                nbEntity++;
                nbProp = nbProp + e.getProperties().size();
            }
        }
        return nbProp / nbEntity;
    }

    public double countInstancesCaracterisedByProperty(String property, Source s){
        double nb = 0;
        for (Entity i : this.entities){
            if (i.getSources().contains(s)) {
                if (i.getProperties().contains(property)) {
                    nb++;
                }
                ;
            }
        }
        return nb;
    }

    public ArrayList<Entity> getTypedEntities(String type, Source s){
        ArrayList<Entity> typedEntities = new ArrayList<Entity>();
        for (Entity e: this.entities){
            if (e.getSources().contains(s)) {
                if (e.getType().contains(type)) {
                    typedEntities.add(e);
                }
            }
        }
        return typedEntities;
    }

    public ArrayList<Entity> getTypedEntities(){
        ArrayList<Entity> typedEntities = new ArrayList<Entity>();
        for (Entity e : this.entities){
            if (!e.getType().isEmpty()){
                typedEntities.add(e);
            }
        }
        return typedEntities;
    }

    public ArrayList<Entity> getUntypedEntities(String type){
        ArrayList<Entity> untypedEntities = new ArrayList<Entity>();
        for (Entity e : this.entities){
            if (!e.getType().contains(type)){
                untypedEntities.add(e);
            }
        }
        return untypedEntities;
    }

    public void addTypeToEntities(double percentage, Source s){
        ArrayList<Entity> typedEntities = new ArrayList<Entity>();
        for (Entity e : this.entities){
            if (e.getSources().contains(s)){
                typedEntities.add(e);
            }
        }

        Collections.shuffle(typedEntities);
        int numberTypedEntities = (int) (typedEntities.size() * percentage);
        for (Entity e: typedEntities.subList(0,numberTypedEntities)){

            e.addTypes(e.getGroundTruth());


        }
    }
    public List<String> getRandomProperties(Source s){
        Random r = new Random();

        List<String> instancesProperties = new ArrayList<String>();

        for (Entity e : this.entities){

            if (e.getSources().contains(s)) {

                for (String prop : e.getProperties()) {
                    if (!instancesProperties.contains(prop) && countInstancesCaracterisedByProperty(prop,s) > getEntitiesSource(s).size() * 0.3) {
                        instancesProperties.add(prop);
                    }
                }
            }
        }

        int randomInt = r.nextInt(instancesProperties.size());
        Collections.shuffle(instancesProperties);
        return instancesProperties.subList(0,randomInt);

    }

    public int countDistinctProperties(Source s) {
        ArrayList<String> instancesProperties = new ArrayList<String>();
        for (Entity e : this.entities){
            if (e.getSources().contains(s)) {
                for (String prop : e.getProperties()) {
                    if (!instancesProperties.contains(prop)) {
                        instancesProperties.add(prop);
                    }
                }
            }
        }
        return instancesProperties.size();
    }

    public ArrayList<Entity> getEntitiesSource(Source s){
        ArrayList<Entity> entitiesSourceS = new ArrayList<>();
        for (Entity e : this.entities){
            if (e.getSources().contains(s)){
                entitiesSourceS.add(e);
            }
        }
        return entitiesSourceS;
    }

    public ArrayList<Entity> getEntities(){
        return this.entities;
    }

}
