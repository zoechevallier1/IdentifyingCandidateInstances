package model;

import edu.uci.ics.jung.graph.DirectedGraph;
import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;


import java.io.File;
import java.util.ArrayList;

public class TargetSchema {

    private File file; // Fichier qui contient le schéma cible (en OWL)
    private Model modelTargetSchema; // Modèle sur lequel le code travaille

    public TargetSchema(File file){
        this.file = file;

        // Création du modèle à partir du fichier
        this.modelTargetSchema = ModelFactory.createDefaultModel();
        this.modelTargetSchema.read(file.getAbsolutePath());

    }

    public File getFile(){
        return file;
    }

    public Model getModelTargetSchema(){
        return this.modelTargetSchema;
    }

    // Ajoute un triplet sur le modèle du schéma cible (avec pour objet une ressource)
    public void addTriple(Resource subject, Property property, Resource object){
        this.modelTargetSchema.add(subject, property, object);
    }

    // Ajoute un triplet sur le modèle du schéma cible (avec pour objet un littéral)
    public void addTriple(Resource subject, Property property, Literal object){
        this.modelTargetSchema.add(subject, property, object);
    }

    // Compte le nombre d'instances d'une classe dans le modèle
    public int getNbInstances(String classe){
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
    }

    // Donne la liste des classes du schéma cible
    public ArrayList<Element> getClasses(){
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
            Resource r = results.nextSolution().getResource("c");
            element = new Element(r.toString(), r.getLocalName(), this);
            classes.add(element);
        }
        return classes;
    }

}
