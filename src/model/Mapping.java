package model;

import java.util.ArrayList;

public class Mapping {

    private final Element targetClass;
    private ArrayList<Element> sourcesClass;

    public Mapping(Element targetClass){
        this.targetClass = targetClass;
        sourcesClass = new ArrayList<Element>();
    }

    public Element getTargetClass() {
        return this.targetClass;
    }

    public ArrayList<Source> getSources() {
        ArrayList<Source> sources = new ArrayList<Source>();
        for (Element classSource : this.sourcesClass){
            sources.add(classSource.getProvenance());
        }
        return sources;
    }

    public ArrayList<Element> getSourcesClass() {
        return sourcesClass;
    }

    public void addSource(Element source){
        this.sourcesClass.add(source);
    }
}
