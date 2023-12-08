package controller;

import model.*;
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;


public enum DataIntegration {
    DI_APP;

    public void run(String[] args) throws IOException {

        // Folder containing the test set :
        // --> Folder "sources" contains .ttl or .owl sources files
        // --> Folder "matches contains .xml representation of correspondences
        // If a target schema exists : it should be named "target-schema.ttl" file and located at the root of the folder

        // Link to the test set folder should be indicated here :
        File projectDirectory = new File("Dataset/DBpedia");

        ProjectManager projectManager = new ProjectManager(projectDirectory);
        RunArchitecture runArchitecture = new RunArchitecture(projectManager);



        // Print the Target Schema
        System.out.println("~ Target Schema ~");
        for (TargetClass tc : projectManager.getTargetSchema().getTargetClasses()) {
            System.out.println(" Target class : " + tc.getUri());
            for (String prop : tc.getSchemaProperties()) {
                System.out.println(prop);
            }
            System.out.println();
        }

        // Print the Data Sources Statistics
        System.out.println("~ Statistics on Data Sources ~");
        for (Source s : projectManager.getSources()){
            System.out.println("Nom source : " + s.getName());
            System.out.println("Nombre d'instances : "+ projectManager.getEntitiesSource(s).size());
            System.out.println("Nombre de propriétés disctinctes : " + projectManager.countDistinctProperties(s));
            System.out.println("Nombre moyen de propriétés par instance : " + projectManager.moyennePropParEntite(s) );
        }
        System.out.println();



        // Parameters of the evaluation
        double percentageTypedEntities = 0.2;
        double similarityThreshold = 0.6;
        double deviationThreshold = 0;

        // Random generation of type declarations
        for (Source s : projectManager.getSources()){
            projectManager.addTypeToEntities(percentageTypedEntities,s);
        }


        // Matching schemas
        runArchitecture.matchAllSources();



        runArchitecture.TypedEntityTreatment();
        // Statistics on the candidate instances set
        System.out.println("~ Results of the Typed Entities Treatment ~");
        for (TargetClass ct : projectManager.getTargetSchema().getTargetClasses()){
            System.out.println("Target class : " + ct.getUri());
            System.out.println("Number of candidate instances : " + ct.getInstances().size());
            System.out.println("True positives " + ct.getTruePositives());
        }
        System.out.println();

        // Untyped Entities Treatment
        System.out.println("~ Results of the Untyped Entities Treatment ~");
        runArchitecture.UntypedEntityTreatment(similarityThreshold, deviationThreshold);

    }





     public static void main(String[] args) throws IOException{
        DI_APP.run(args);
    }
}
