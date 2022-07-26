package model;

import edu.uci.ics.jung.graph.DirectedGraph;
import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;


import java.io.File;
import java.util.ArrayList;

public class TargetSchema {
    private File targetSchema;
    private Model modelTargetSchema;
    private DirectedGraph<RDFNode, Statement> graphTargetSchema;

    public TargetSchema(File targetSchema){
        FileToModelGraph fileToModelGraph = new FileToModelGraph();
        this.targetSchema = targetSchema;
        this.graphTargetSchema = fileToModelGraph.FileToGraph(targetSchema);

        this.modelTargetSchema = ModelFactory.createDefaultModel();
        this.modelTargetSchema.read(targetSchema.getAbsolutePath());

    }

    public File getTargetSchema(){
        return targetSchema;
    }

    public DirectedGraph<RDFNode, Statement> getGraphTargetSchema(){
        return this.graphTargetSchema;
    }

    public Model getModelTargetSchema(){
        return this.modelTargetSchema;
    }

    public void addTriple(Resource subject, Property property, Resource object){
        this.modelTargetSchema.add(subject, property, object);
    }

    public void addTriple(Resource subject, Property property, Literal object){
        this.modelTargetSchema.add(subject, property, object);
    }

    public int getNbInstances(String classe){
        int count = 0;
        String queryString = "SELECT ?i WHERE {?i <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+classe+">}";
        System.out.println(classe);
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.modelTargetSchema);
        ResultSet results = qexec.execSelect();
        for ( ; results.hasNext() ;){
            results.nextSolution();
            count++;
        }
        return count;
    }



    /*public void showModel(){
        StmtIterator x = modelTargetSchema.listStatements();
        if (!x.hasNext()) {
            System.out.println("...rien");
        }
        while (x.hasNext()) {
            Statement sol = x.next();
            System.out.println("..." + sol.toString());
        }
    }*/

    public void setModelTargetSchema(Model modelTargetSchema){
        this.modelTargetSchema = modelTargetSchema;
    }

    public void addInstances(Resource subject, Property property, Resource object){
        this.modelTargetSchema.add(subject, property, object);
    }

    public ArrayList<String> getClasses(){
        ArrayList<String> classes = new ArrayList<String>();
        String queryString = "SELECT ?c WHERE {?c <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class>} GROUP BY ?c";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.modelTargetSchema);
        ResultSet results = qexec.execSelect();
        for ( ; results.hasNext() ;){
            classes.add(results.nextSolution().getResource("c").toString());
        }
        return classes;
    }

}
