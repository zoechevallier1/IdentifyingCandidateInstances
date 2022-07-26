package model;

import java.util.ArrayList;

public class Mapping {

    private String targetClass;
    private ArrayList<Source> sources;

    public Mapping(String targetClass){
        this.targetClass = targetClass;
        sources = new ArrayList<Source>();
    }
}
