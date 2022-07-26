package model;

public class InstanceMatch {

    private String instanceS1;
    private String instanceS2;

    private Source source1;
    private Source source2;

    public InstanceMatch(String instanceS1, String instanceS2, Source source1, Source source2){
        this.instanceS1 = instanceS1;
        this.instanceS2 = instanceS2;
        this.source1 = source1;
        this.source2 = source2;
    }

    public Source getSource1() {
        return source1;
    }

    public Source getSource2() {
        return source2;
    }

    public String getInstanceS1() {
        return instanceS1;
    }

    public String getInstanceS2() {
        return instanceS2;
    }
}
