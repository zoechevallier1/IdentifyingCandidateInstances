package model;


// Definition (ENTITY) : An entity is an RDF resource that is neither a class, nor a property, nor a literal, nor a blank node

import org.apache.jena.query.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Entity {

    private final String uri;
    private final Set<String> properties;
    private final ArrayList<Source> sources;

    private ArrayList<String> types;

    private final ArrayList<String> groundTruth;
    public Entity(String uri, Source source){
        this.uri = uri;
        this.sources = new ArrayList<Source>();
        this.sources.add(source);
        this.properties = new HashSet<String>();
        this.types = new ArrayList<String>();
        this.groundTruth = new ArrayList<String>();
    }


    public ArrayList<Source> getSources(){
        return this.sources;
    }


    public ArrayList<String> getGroundTruth(){return groundTruth;}

    public void addGroundTruth(String type){
        if (this.groundTruth.contains(type) == Boolean.FALSE){
            this.groundTruth.add(type);
        }
    }


    public ArrayList<String> getType(){
        return this.types;
    }
    public void addProperty(String property){
        this.properties.add(property);
    }

    public Set<String> getProperties(){ return this.properties;}

    public String getUri(){
        return this.uri;
    }

    public void addTypes(ArrayList<String> types){
        for (String type : types){
            if (this.types.contains(type) == Boolean.FALSE) {
                this.types.add(type);
            }
        }
    }

    public void addType(String type){

        if (this.types.contains(type) == Boolean.FALSE){
            this.types.add(type);
        }
    }

    public void addSource(Source s){
        this.sources.add(s);
    }

    public void resetType(){
        this.types = new ArrayList<String>();
    }



}
