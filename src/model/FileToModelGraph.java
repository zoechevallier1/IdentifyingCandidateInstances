package model;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

import java.io.FileOutputStream;
import java.util.Collection;
public class FileToModelGraph {

    public FileToModelGraph(){

    }

    public static DirectedGraph FileToGraph(String owlFile){
        Model mapModel = FileManager.get().loadModelInternal(owlFile);
        DirectedGraph<RDFNode, Statement> g = new JenaJungGraph(mapModel);
        return g;
    }

    public static DirectedGraph FileToGraph(File owlFile){
        //System.out.println(owlFile);
        // Lecture du fichier OWL.
        Model mapModel = FileManager.get().loadModel(owlFile.getAbsolutePath());
        /*StmtIterator x = mapModel.listStatements();
        if (!x.hasNext()) {
            System.out.println("...rien");
        }
        while (x.hasNext()) {
            Statement sol = x.next();
            System.out.println("..." + sol.toString());
        }
        //  Model mm = FileManager.get().loadModel( owlFile, "TURTLE");
        System.out.println("j'ai eu le model   " + owlFile);*/
        DirectedGraph<RDFNode, Statement> g = new JenaJungGraph(mapModel);
        return g;
    }

    public static DirectedGraph ModelToGraph(Model model){
        DirectedGraph<RDFNode, Statement> g = new JenaJungGraph(model);
        return g;
    }

    /*public static DirectedGraph<RDFNode, Statement> ModelToGraph(Model model) {
        // Creation d'un modele d'ontologie
        DirectedGraph<RDFNode, Statement> graph = new net.rootdev.jenajung.JenaJungGraph(model);

        return graph;
    }*/
}
