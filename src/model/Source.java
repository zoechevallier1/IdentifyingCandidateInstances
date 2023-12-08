package model;

import java.lang.reflect.Array;
import java.util.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.query.*;

import java.io.*;


public class Source {
    private final String name;
    private final File file;

    private Model modelSource;

    //private ArrayList<Entity> entities;

    private ArrayList<Relation> matchs;

    private File ground_truth;

    public Source(File source) throws IOException {
        this.file = source;
        this.name = file.getName();
        //this.entities = new ArrayList<Entity>();
        this.matchs = new ArrayList<Relation>();

        this.modelSource = ModelFactory.createDefaultModel();
        this.modelSource.read(this.file.getAbsolutePath());






        /*FileReader fr = new FileReader(this.file);
        BufferedReader br = new BufferedReader(fr);
        if (br.ready()){
            String line = br.readLine();
            Entity e = new Entity(line, this);
            this.entities.add(e);
            line = br.readLine();
            while (line != null) {
                if (line.equals("")) {
                    line = br.readLine();
                    if (line != null){

                        e = new Entity(line, this);
                        this.entities.add(e);
                    }
                }
                else{
                    e.addProperty(line);
                }
                line = br.readLine();
            }
        }*/

    }

    public ArrayList<Entity> entities(){
        ArrayList<Entity> entities = new ArrayList<Entity>();

        String queryString =    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                                "SELECT distinct ?s " +
                                " WHERE { ?s ?p ?o }";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.modelSource);
        ResultSet results = qexec.execSelect();

        for ( ; results.hasNext() ;){
            String resource = results.nextSolution().getResource("s").toString();

            Entity e = new Entity(resource, this);
            String queryStringProperties =  "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                                            "SELECT ?p ?o" +
                                            " WHERE { <"+resource+"> ?p ?o }";

            Query queryProperties = QueryFactory.create(queryStringProperties);
            QueryExecution qexecprop = QueryExecutionFactory.create(queryProperties,this.modelSource);

            ResultSet resultsProp = qexecprop.execSelect();
            for ( ; resultsProp.hasNext() ;){

                QuerySolution solution = resultsProp.nextSolution();
                String property = solution.getResource("p").toString();
                if (property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
                    e.addGroundTruth(solution.getResource("o").toString());
                }
                else if (e.getProperties().contains(property) == Boolean.FALSE){
                    e.addProperty(property);
                }


            }

            entities.add(e);


        }


        return entities;
    }


    public void setGroundTruth(File ground_truth){
        this.ground_truth = ground_truth;
    }



    public File getFileSource(){
        return this.file;
    }

    public String getName(){
        return this.name;
    }

    public void addMatch(Relation relation){
        this.matchs.add(relation);
    }

    public ArrayList<Relation> getMatchs(){
        return this.matchs;
    }

    /*public ArrayList<Entity> getEntities(){
        return this.entities;
    }*/

    /*public ArrayList<Entity> getTypedEntities(){
        ArrayList<Entity> typedEntities = new ArrayList<Entity>();
        for (Entity e: this.entities){
            if (e.getType() != null){
                typedEntities.add(e);
            }
        }
        return typedEntities;
    }*/

    /*public ArrayList<Entity> getTypedEntities(String type){
        ArrayList<Entity> typedEntities = new ArrayList<Entity>();
        for (Entity e: this.entities){
            if (e.getType().contains(type)){
                typedEntities.add(e);
            }
        }
        return typedEntities;
    }*/

    /*public ArrayList<Entity> getUntypedEntities(String type){
        ArrayList<Entity> untypedEntities = new ArrayList<Entity>();
        for (Entity e : this.entities){
            if (!e.getType().contains(type)){
                untypedEntities.add(e);
            }
        }
        return untypedEntities;
    }*/


    /*public void addTypeToEntities(double percentage){
        ArrayList<Entity> typedEntities = new ArrayList<Entity>(this.entities);
        Collections.shuffle(typedEntities);
        int numberTypedEntities = (int) (this.entities.size() * percentage);
        for (Entity e: typedEntities.subList(0,numberTypedEntities)){
            e.setType(e.getGroundTruth());

        }
    }*/

    /*public List<String> getRandomProperties(){
        Random r = new Random();

        List<String> instancesProperties = new ArrayList<String>();
        for (Entity e : this.entities){
            for (String prop : e.getProperties()){
                if (!instancesProperties.contains(prop) && countInstancesCaracterisedByProperty(prop)> this.getEntities().size()*0.3){
                    instancesProperties.add(prop);
                }
            }
        }

        int randomInt = r.nextInt(instancesProperties.size());
        Collections.shuffle(instancesProperties);
        return instancesProperties.subList(0,randomInt);

    }*/


    /*public int countDistinctProperties() {
        ArrayList<String> instancesProperties = new ArrayList<String>();
        for (Entity e : this.entities){
            for (String prop : e.getProperties()){
                if (!instancesProperties.contains(prop)){
                    instancesProperties.add(prop);
                }
            }
        }
        return instancesProperties.size();
    }*/




}

