package model;

public class Element {

    private Source source = null;

    private TargetSchema targetSchema = null;
    private String uri;
    private String localName;

    public Element(String uri, String localName){
        this.uri = uri;
        this.localName = localName;
    }

    public Element(String uri, String localName, Source source){
        this.uri = uri;
        this.localName = localName;
        this.source = source;
    }

    public Element(String uri, String localName, TargetSchema targetSchema){
        this.uri = uri;
        this.localName = localName;
        this.targetSchema = targetSchema;
    }

    public String getLocalName() {
        return localName;
    }

    public String getUri() {
        return uri;
    }

    public Source getProvenance(){
        return this.source;
    }


}
