package model;

import org.apache.jena.base.Sys;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.query.*;

import java.lang.annotation.Target;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TargetClass {
    private String uri;
    private TargetSchema targetSchema;
    private Set<String> equivalentClasses;
    private Set<String> schemaProperties;
    private ArrayList<Entity> instances;

    private Set<Description> classDescriptions;


    public TargetClass(String uri, TargetSchema targetSchema){
        this.uri = uri;
        this.targetSchema = targetSchema;
        this.schemaProperties = new HashSet<String>();
        this.instances = new ArrayList<Entity>();
        this.equivalentClasses = new HashSet<String>();

        this.classDescriptions = new HashSet<Description>();

    }

    public Set<Description> getClassDescriptions(){ return this.classDescriptions; }

    public String getUri(){
        return this.uri;
    }

    public void setUri(String uri){this.uri = uri;}

    public TargetSchema getTargetSchema(){ return this.targetSchema; }

    public void setTargetSchema(TargetSchema targetSchema){ this.targetSchema = targetSchema; }

    public Set<String> getEquivalentClasses(){return this.equivalentClasses; }

    public void setEquivalentClasses(Set<String> equivalentClasses) { this.equivalentClasses = equivalentClasses; }

    public Set<String> getSchemaProperties(){
        return this.schemaProperties;
    }



    public void addSchemaProperty(String property){
        this.schemaProperties.add(property);
    }


    public void addCandidateInstance(Entity e){
        if (!this.instances.contains(e)) {
            this.instances.add(e);
        }
    }

    public void addCandidateInstances(ArrayList<Entity> newEntities){
        for (Entity e : newEntities) {
            if (this.instances.contains(e) == Boolean.FALSE) {
                this.instances.add(e);
            }
        }
    }

    public ArrayList<Entity> getInstances(){
        return this.instances;
    }


    public Set<String> getInstancesProperties(){
        Set<String> instancesProperties = new HashSet<String>();
        for (Entity e : this.instances){
            for (String property : e.getProperties()){
                if (!instancesProperties.contains(property)){
                    instancesProperties.add(property);
                }
            }
        }
        return instancesProperties;
    }

    public double countInstancesCaracterisedByProperty(String property){
        double nb = 0;
        for (Entity i : this.instances){
            if (i.getProperties().contains(property)){
                nb++;
            };
        }

        return nb;
    }



    public double computeSchemaSimilarity(Entity e){

        return computeJaccard(e.getProperties(), this.schemaProperties);
    }

    public double computeOverlap(Set<String> set1, Set<String> set2){
        double similarity = 0.0;
        Set<String> intersection = Sets.intersection(set1, set2);
        int denominator = min(set1.size(), set2.size());

        if (denominator != 0){
            similarity = (double) intersection.size() / denominator;
        }

        return similarity;
    }

    public double computeJaccard(Set<String> set1, Set<String> set2){
        double similarity = 0.0;
        Set<String> intersection = Sets.intersection(set1, set2);
        Set<String> union = Sets.union(set1, set2);
        int denominator = union.size();

        if (denominator != 0){
            similarity = (double) intersection.size() / denominator;
        }

        return similarity;
    }

    public double computeProbability(Entity e, int iteration){

        double similarity = 0;


        double schemaSimilarity = computeSchemaSimilarity(e);
        double instanceSimilarity = computeInstanceSimilarity(e, iteration);


        for (String classeSource : e.getType()) {
            for (Relation r : this.targetSchema.getMatchs()) {
                if (r.getSourceResource().equals(classeSource) && r.getTargetResource().equals(this.uri)) {
                    return 1;
                }
            }
        }

        similarity = max(schemaSimilarity, instanceSimilarity);

        return similarity;
    }
    public double computeInstanceSimilarity(Entity e, int iteration){
        double similarity = 0;

        for (Description desc : this.classDescriptions){
            if (desc.getIteration() == iteration - 1){
                double sim = computeJaccard(desc.getDescription(), e.getProperties());
                if (sim > similarity){
                    similarity = sim;
                }
            }
        }

        return similarity;
    }

    public int getTruePositives(){

        int truePositives = 0;

        for (Entity e : this.getInstances()){

            if (e.getGroundTruth().contains(this.getUri())){
                truePositives = truePositives + 1;
            }
            else {
                for (String type : this.equivalentClasses){
                    if (e.getGroundTruth().contains(type)){
                        truePositives = truePositives + 1;
                    }
                }
            }

        }
        return truePositives;
    }

    public void deleteInstances(){
        this.instances.clear();
    }

    public void addEquivalentClass(String uri){
        this.equivalentClasses.add(uri);
    }

    public void initializeDescriptions(){

        for (Entity e : this.instances){
            Boolean alreadyIn = false;
            for (Description desc : this.classDescriptions) {
                if (e.getProperties().equals(desc.getDescription())) {
                    alreadyIn = true;
                }
            }
            if (!alreadyIn){
                this.classDescriptions.add(new Description(e.getProperties(), 0));
            }
        }

    }

    public void updateClassDescriptions(ArrayList<Entity> newEntities, int iteration){
        for (Entity e : newEntities){
            Boolean alreadyIn = false;
            for (Description desc : this.classDescriptions) {
                if (e.getProperties().equals(desc.getDescription())) {
                    alreadyIn = true;
                }
            }
            if (!alreadyIn){
                this.classDescriptions.add(new Description(e.getProperties(), iteration));
            }
        }
    }

    public void updateClassDescriptions(ArrayList<Entity> newEntities, int iteration, double delta){
        for (Entity e : newEntities){

            Boolean alreadyIn = false;
            for (Description desc : this.classDescriptions) {
                if (e.getProperties().equals(desc.getDescription())) {
                    alreadyIn = true;
                }
            }
            if (!alreadyIn){
                if (computeInstanceSimilarityReference(e) >= delta) {
                    this.classDescriptions.add(new Description(e.getProperties(), iteration));
                }
            }
        }
    }

    public double computeInstanceSimilarityReference(Entity e){
        double similarity = 0.0;


        return similarity;
    }

}


