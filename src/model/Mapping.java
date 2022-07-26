package model;

import java.util.ArrayList;

public class Mapping {

    private final String targetClass;
    private ArrayList<Source> sources;

    public Mapping(String targetClass){
        this.targetClass = targetClass;
        sources = new ArrayList<Source>();
    }

    public String getTargetClass() {
        return this.targetClass;
    }

    public ArrayList<Source> getSources() {
        return sources;
    }

    public void addSource(Source source){
        this.sources.add(source);
    }
}
