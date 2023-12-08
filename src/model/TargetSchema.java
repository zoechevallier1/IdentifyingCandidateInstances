package model;

import controller.ProjectManager;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;


import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class TargetSchema {

    private File file;

    private Model modelTargetSchema;

    private ArrayList<TargetClass> targetClasses;

    private ArrayList<Relation> matchs ;

    public TargetSchema(File file) throws IOException {
        this.file = file;
        this.targetClasses = new ArrayList<TargetClass>();
        this.matchs = new ArrayList<Relation>();

        this.modelTargetSchema = ModelFactory.createDefaultModel();
        this.modelTargetSchema.read(file.getAbsolutePath());

        System.out.println("is empty ? " + this.modelTargetSchema.isEmpty());


        String queryString = " SELECT ?c WHERE { ?c <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> }";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.modelTargetSchema);
        ResultSet results = qexec.execSelect();

        for (; results.hasNext(); ) {
            QuerySolution solution = results.nextSolution();
            TargetClass ct = new TargetClass(solution.getResource("c").toString(), this);
            this.targetClasses.add(ct);
            String queryStringProp = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                    "SELECT ?p " +
                    "WHERE { " +
                    "?p   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?property." +
                    " VALUES ?property {owl:ObjectProperty owl:DatatypeProperty }. " +
                    " ?p <http://www.w3.org/2000/01/rdf-schema#domain>  <" + solution.getResource("c") + "> .  " +
                    "}";

            Query queryProp = QueryFactory.create(queryStringProp);
            QueryExecution qexecProp = QueryExecutionFactory.create(queryProp, this.modelTargetSchema);
            ResultSet resultsProp = qexecProp.execSelect();


            for (; resultsProp.hasNext(); ) {
                ct.addSchemaProperty(resultsProp.nextSolution().getResource("p").toString());
            }
        }
    }

    //Random Generation of the Target Schema
    public TargetSchema(ProjectManager projectManager){
        this.file = null;
        this.targetClasses = new ArrayList<TargetClass>();
        this.matchs = new ArrayList<Relation>();
        this.modelTargetSchema = ModelFactory.createDefaultModel();

        for (Source s : projectManager.getSources()){
            String className = "http://target-schema/" + s.getName().split("\\.")[0];
            TargetClass targetClass = new TargetClass(className, this);
            randomGeneration(className, projectManager.getRandomProperties(s));

        }


        //Random Generation of the Target Class
        /**/
    }

    public Model getModelTargetSchema(){return this.modelTargetSchema;}



    public void randomGeneration(String className, List<String> properties){

        TargetClass targetClass = new TargetClass(className,this);
        this.targetClasses.add(targetClass);


        for (String property : properties){
            targetClass.addSchemaProperty(property);
        }
    }






        /*FileReader fr = new FileReader(this.file);
        BufferedReader br = new BufferedReader(fr);

        if (br.ready()){
            String line = br.readLine();
            TargetClass c = new TargetClass(line, this);
            this.targetClasses.add(c);
            line = br.readLine();
            while (line != null) {
                if (line.equals("")) {
                    line = br.readLine();
                    if (line != null){
                        c = new TargetClass(line, this);
                        this.targetClasses.add(c);
                    }
                }
                else{
                    c.addSchemaProperty(line);
                }
                line = br.readLine();
            }
        }

        for (TargetClass c : this.targetClasses){
            c.computeProfile();
        }*/





    public void instantiate(Entity e, TargetClass c){

    }



    // Ajoute un triplet sur le modèle du schéma cible (avec pour objet une ressource)


    // Compte le nombre d'instances d'une classe dans le modèle
    /*public int getNbInstances(String classe){
        int count = 0;
        String queryString = "SELECT ?i WHERE {?i <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+classe+">}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.modelTargetSchema);
        ResultSet results = qexec.execSelect();
        for ( ; results.hasNext() ;){
            results.nextSolution();
            count++;
        }
        return count;
    }*/

    // Donne la liste des classes du schéma cible
    /*public ArrayList<Element> getClasses(){
        ArrayList<Element> classes = new ArrayList<Element>();
        Element element;

        String queryString = "SELECT ?c " +
                             "WHERE {" +
                                "?c <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> " +
                             "} GROUP BY ?c";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.modelTargetSchema);
        ResultSet results = qexec.execSelect();

        for ( ; results.hasNext() ;){
            org.apache.jena.rdf.model.Resource r = results.nextSolution().getResource("c");
            element = new Element(r.toString(), r.getLocalName(), this);
            classes.add(element);
        }
        return classes;
    }*/

    public ArrayList<TargetClass> getTargetClasses(){return this.targetClasses;}

    public void addMatch(Relation m){ this.matchs.add(m);}

    public ArrayList<Relation> getMatchs() {
        return this.matchs;
    }

    public TargetClass getTargetClass(String uri){
        for (TargetClass ct : this.targetClasses){
            if (ct.getUri().equals(uri)){
                return ct;
            }
        }
        return null;
    }
}
