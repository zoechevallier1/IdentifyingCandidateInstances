package controller;

import model.*;
import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.exec.RowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;


public class RunArchitecture {
    private ProjectManager projectManager;
    //private ArrayList<Ressource> instanciate;

    public RunArchitecture(ProjectManager projectManager){

        this.projectManager = projectManager;

    }

    public void matchAllSources() throws IOException {
        for (Source s : this.projectManager.getSources()){
            matching(s.getName());
        }
    }
    public void matching(String sourceName) throws IOException {
        Source source = projectManager.getSourceByName(sourceName);
        ArrayList<Relation> matchs = new ArrayList<Relation>();

        // Select the file that contains the source's matchs
        File matchingFile = new File(projectManager.getProjectDirectory().getAbsolutePath()+"/matchs/"+sourceName.split("\\.")[0]+".xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(matchingFile);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("match");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String classeSource = element.getElementsByTagName("classeSource").item(0).getTextContent() ;
                    String propSource = element.getElementsByTagName("propSource").item(0).getTextContent();
                    String classeCible = element.getElementsByTagName("classeCible").item(0).getTextContent();
                    String propCible = element.getElementsByTagName("propCible").item(0).getTextContent();

                    if (propSource.equals("")){
                        Relation m = new Relation(classeSource,classeCible);
                        source.addMatch(m);
                        this.projectManager.getTargetSchema().addMatch(m);
                        TargetClass ct = this.projectManager.getTargetSchema().getTargetClass(classeCible);
                        ct.addEquivalentClass(classeSource);
                    }
                    else {
                        Relation m = new Relation(propSource,propCible);
                        source.addMatch(m);
                        this.projectManager.getTargetSchema().addMatch(m);
                    }

                }
            }
        }catch (Exception e){

        }
    }

    public void TypedEntityTreatment(Source s){
        for (TargetClass tc : this.projectManager.getTargetSchema().getTargetClasses() ){
            for (Relation r : s.getMatchs()){
                if (tc.getUri().equals(r.getTargetResource())){
                    for (Entity e : projectManager.getTypedEntities(r.getSourceResource(),s)){
                        tc.addCandidateInstance(e);

                        e.addType(tc.getUri());

                    }
                }
            }
        }
    }

    public void TypedEntityTreatment(){
        for (Source s : this.projectManager.getSources()){
            TypedEntityTreatment(s);
        }
    }


    public void UntypedEntityTreatment(double tau) {
        for (TargetClass tc : this.projectManager.getTargetSchema().getTargetClasses()){
            UntypedEntityTreatment(tc, tau);
        }
    }

    public void UntypedEntityTreatment(double tau, double delta) {
        for (TargetClass tc : this.projectManager.getTargetSchema().getTargetClasses()){
            UntypedEntityTreatment(tc, tau, delta);
        }
    }

    public void UntypedEntityTreatment(TargetClass tc, double tau){
        ArrayList<Entity> newEntities;
        Boolean condition = Boolean.TRUE;
        int k = 1;

        tc.initializeDescriptions();

        while (condition){
            newEntities = new ArrayList<Entity>();
            ArrayList<Entity> untypedEntities = projectManager.getUntypedEntities(tc.getUri());

            for (Entity e : untypedEntities){
                double prob = tc.computeProbability(e, k);
                if (prob >= tau){
                    newEntities.add(e);
                }
            }

            if (newEntities.size() == 0){
                condition = Boolean.FALSE;
            }

            tc.addCandidateInstances(newEntities);
            for (Entity e : newEntities){
                e.addType(tc.getUri());
            }

            tc.updateClassDescriptions(newEntities, k);


            System.out.println("Results for iteration : " + k);
            System.out.println("Target class : " + tc.getUri());
            printResults(tc);

            k = k+1;


        }
        
        

    }

    public void UntypedEntityTreatment(TargetClass tc, double tau, double delta){
        ArrayList<Entity> newEntities;
        Boolean condition = Boolean.TRUE;
        int k = 1;

        tc.initializeDescriptions();

        while (condition){
            newEntities = new ArrayList<Entity>();
            ArrayList<Entity> untypedEntities = projectManager.getUntypedEntities(tc.getUri());

            for (Entity e : untypedEntities){
                double prob = tc.computeProbability(e, k);
                if (prob >= tau){
                    newEntities.add(e);
                }
            }

            if (newEntities.size() == 0){
                condition = Boolean.FALSE;
            }

            for (Entity e : newEntities){
                if (tc.computeInstanceSimilarityReference(e) >= delta){
                    tc.addCandidateInstance(e);
                    e.addType(tc.getUri());
                }
            }

            tc.updateClassDescriptions(newEntities, k, delta);


            System.out.println("Results for iteration : " + k);
            System.out.println("Target class : " + tc.getUri());
            printResults(tc);

            k = k+1;
        }
    }
    
    public void printResults (TargetClass ct){
        System.out.println("Number of candidate instances : " + ct.getInstances().size());
        System.out.println("True positives " + ct.getTruePositives());
        
        double recall = 0; double precision = 0;
        if (entityByGroundTruth(ct) != 0){
            recall = (double)ct.getTruePositives()/entityByGroundTruth(ct);
        }
        if (ct.getInstances().size() != 0){
            precision = (double)ct.getTruePositives() / ct.getInstances().size();
        }
        System.out.println("Recall : " + recall);
        System.out.println("Precision : " + precision);

    }
    
    public int entityByGroundTruth(TargetClass targetClass){
        int number = 0;
        
        for (Entity e : this.projectManager.getEntities()){
            if (e.getGroundTruth().contains(targetClass)){
                number = number + 1;
            }
            else {
                for (String type : targetClass.getEquivalentClasses()){
                    if (e.getGroundTruth().contains(type)){
                        number = number + 1;
                    }
                }
            }
        }
        
        return number;
        
    }

}
