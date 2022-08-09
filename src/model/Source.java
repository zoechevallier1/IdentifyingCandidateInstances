package model;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.io.File;
import java.util.ArrayList;

public class Source {
    private final String name;
    private final String uri;
    private final File file;
    private Model modelSource ;
    private ArrayList<Match> matchsSchema = new ArrayList<Match>();
    private PertinentSubset pertinentSubset;

    public Source(File source){
        this.file = source;
        this.name = file.getName();
        this.uri = "";


        this.modelSource = ModelFactory.createDefaultModel();
        this.modelSource.read(this.file.getAbsolutePath());

        this.pertinentSubset = new PertinentSubset();
    }

    public File getFileSource(){
        return this.file;
    }

    public Model getModelSource(){
        return this.modelSource;
    }

    public ArrayList<Match> getMatchsSchema(){
        return this.matchsSchema;
    }

    public void setMatchsSchema(ArrayList<Match> matchsSchema){
        this.matchsSchema = matchsSchema;
    }

    public void addTriple(Resource subject, Property property, Resource object){
        this.modelSource.add(subject, property, object);
    }

    public PertinentSubset getPertinentSubset(){
        return this.pertinentSubset;
    }

    // Retourne les classes de la source
    public ArrayList<String> getClasses(){
        ArrayList<String> classes = new ArrayList<String>();

        String queryString =
                "SELECT ?c " +
                "WHERE {" +
                    "?c <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class>" +
                "} GROUP BY ?c";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.modelSource);
        ResultSet results = qexec.execSelect();

        for ( ; results.hasNext() ;){
            classes.add(results.nextSolution().getResource("c").toString());
        }
        return classes;
    }

    // Calcule le nombre d'instance d'une classe
    public int getNbInstances(String classe){
        int count = 0;

        String queryString =
                "SELECT ?i " +
                "WHERE {?i <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+classe+">}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.modelSource);
        ResultSet results = qexec.execSelect();

        for ( ; results.hasNext() ;){
            results.nextSolution();
            count++;
        }
        return count;
    }

    public ArrayList<String> getMatchProperties(String classeSource, String classeTarget){
        ArrayList<String> prop = new ArrayList<String>();
        for (Match match : matchsSchema){
            if (match.getTargetClass().equals(classeTarget) && match.getSourceClass().equals(classeSource)){
                prop.add(match.getTargetProp());
            }
        }
        return prop;
    }

    public String getName(){
        return this.name;
    }

    public String getUri(){
        return this.uri;
    }
}

