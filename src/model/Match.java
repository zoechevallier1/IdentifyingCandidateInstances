package model;

public class Match {

    private String sourceClass;
    private String propSource;
    private String targetClass;
    private String targetProp;

    public Match(String sourceClass, String propSource, String targetClass, String targetProp){
        this.sourceClass = sourceClass;
        this.propSource = propSource;
        this.targetClass = targetClass;
        this.targetProp = targetProp;
    }

    public String getPropSource() {
        return propSource;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public String getTargetProp() {
        return targetProp;
    }
}
