package model;

import java.util.ArrayList;
import java.util.Set;

public class Description {

    private Set<String> properties;

    private int iteration;

    public Description(Set<String> properties, int iteration){
        this.properties = properties;
        this.iteration = iteration;
    }

    public Set<String> getDescription(){ return this.properties; }

    public int getIteration(){ return this.iteration;}
}
