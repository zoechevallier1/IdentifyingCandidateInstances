package model;

import java.util.ArrayList;

public class SchemaMatchs {

    private ArrayList<Match> matchs;
    private Source source;

    public SchemaMatchs(Source source, ArrayList<Match> matchs){
        this.matchs = matchs;
        this.source = source;
    }

    public ArrayList<Match> getMatchs() {
        return matchs;
    }
}
