package model;

public class Relation {
    private String sourceResource;
    private String targetResource;

    public Relation(String sourceResource, String targetResource){
        this.sourceResource = sourceResource;
        this.targetResource = targetResource;
    }

    public String getSourceResource() {
        return sourceResource;
    }

    public String getTargetResource() {
        return targetResource;
    }
}
