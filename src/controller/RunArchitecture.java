package controller;

import model.*;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;


public class RunArchitecture {

    private ProjectManager projectManager;
    private ArrayList<SchemaMatchs> matchs;
    private ArrayList<Source> sourcesAlreadyMatched = new ArrayList<Source>();
    private ArrayList<InstancesMatchs> links;
    private ArrayList<Resource> instanciate = new ArrayList<Resource>();

    private ArrayList<Mapping> mappings;

    ArrayList<Resource[]> classMatch = new ArrayList<Resource[]>();

    public RunArchitecture(ProjectManager projectManager){

        this.projectManager = projectManager;
        this.matchs = new ArrayList<SchemaMatchs>();
        this.links = new ArrayList<InstancesMatchs>();
        this.mappings = new ArrayList<Mapping>();

    }

    public void matching() throws IOException {
        for (Source source : projectManager.getSources()){
            matching(source.getFileSource().getName());
        }
    }

    public void matching(String sourceName) throws IOException {
        Source source = projectManager.getSourceByName(sourceName);
        ArrayList<Match> matchs = new ArrayList<Match>();
        Match match;

        File matchingFile = new File(projectManager.getProjectDirectory().getAbsolutePath()+"/matchs/"+sourceName.split("\\.")[0]+".xml");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(matchingFile);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("match");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String classeSource = element.getElementsByTagName("classeSource").item(0).getTextContent();
                    String propSource = element.getElementsByTagName("propSource").item(0).getTextContent();
                    String classeCible = element.getElementsByTagName("classeCible").item(0).getTextContent();
                    String propCible = element.getElementsByTagName("propCible").item(0).getTextContent();

                    Match m = new Match(classeSource, propSource, classeCible, propCible);
                    matchs.add(m);
                }
            }
        }catch (Exception e){

        }
        // On ajoute les matchs à la source
        source.setMatchsSchema(matchs);

        // On créé le sous ensemble pertinent
        extractionSousEnsembles(source);


        // On ajoute les matchs au run Architecture
        SchemaMatchs schemaMatchs = new SchemaMatchs(source, matchs);
        if (!this.sourcesAlreadyMatched.contains(source)) {
            this.matchs.add(schemaMatchs);
        }
        this.sourcesAlreadyMatched.add(source);
    }

    public void instanciating(){

        Model modeltargetSchema = projectManager.getTargetSchema().getModelTargetSchema();

        // ON SELECTIONNE TOUS LES COUPLES classeCible - ClasseSource
        String queryString = "SELECT ?targetClass ?sourceClass WHERE {?targetClass <http://www.w3.org/2002/07/owl#equivalentClass> ?sourceClass}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, modeltargetSchema);
        ResultSet results = qexec.execSelect();
        for ( ; results.hasNext() ;){
            QuerySolution solution = results.nextSolution();
            Resource resource[] = new Resource[2];
            resource[0] = solution.getResource("targetClass");
            resource[1] = solution.getResource("sourceClass");

            classMatch.add(resource);
        }

        Mapping mapping;
        // Pour chaque couple classeCible/classeSource
        for (Resource[] r : classMatch){
             mapping = getMapping(r[0].toString());

             if (mapping == null){
                 mapping = new Mapping(r[0].toString());
             }

            mappings.add(mapping);
            Source source = projectManager.getSourceByResourceClass(r[1]);
            mapping.addSource(source);
            //System.out.println("Source que l'on regarde : " + source.getFileSource().getName());
            //System.out.println(r[1].toString());

            // ON REGARDE TOUTES LES INSTANCES DE LA CLASSE SOURCE
            queryString = "SELECT ?s WHERE {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + r[1].toString() + "> }";
            query = QueryFactory.create(queryString);
            qexec = QueryExecutionFactory.create(query, source.getModelSource());
            results = qexec.execSelect();

            for ( ; results.hasNext() ;){
                Resource instance =  results.nextSolution().getResource("s");
                if (!this.instanciate.contains(instance)) {
                    projectManager.getTargetSchema().addTriple(instance, RDF.type, r[0]);
                    this.instanciate.add(instance);
                    // On regarde les différentes propriétés qui matchent
                    for (Match match : source.getMatchsSchema()){
                        queryString = "SELECT ?o WHERE {<"+ instance.toString()+ "> <"+ match.getPropSource()+"> ?o }";
                        query = QueryFactory.create(queryString);
                        qexec = QueryExecutionFactory.create(query, source.getModelSource());
                        ResultSet resultsValues = qexec.execSelect();
                        for ( ; resultsValues.hasNext() ;){
                            QuerySolution solution = resultsValues.nextSolution();
                            if (solution.get("o").isLiteral()){
                                projectManager.getTargetSchema().addTriple(instance,projectManager.getTargetSchema().getModelTargetSchema().getProperty(match.getTargetProp()), solution.getLiteral("o"));
                            }
                            else {
                                if (this.instanciate.contains(solution.getResource("o"))) {
                                    Resource sameAs = findSameAs(solution.getResource("o"), source);
                                    projectManager.getTargetSchema().addTriple(instance, projectManager.getTargetSchema().getModelTargetSchema().getProperty(match.getTargetProp()), sameAs);
                                }
                                else {
                                    projectManager.getTargetSchema().addTriple(instance, projectManager.getTargetSchema().getModelTargetSchema().getProperty(match.getTargetProp()), solution.getResource("o"));
                                }
                            }

                        }
                    }

                    // On regarde les liens sameAs pour cette instance ->
                    queryString = "SELECT ?o WHERE {{<"+ instance.toString()+ "> <http://www.w3.org/2002/07/owl#sameAs> ?o.} UNION {?o <http://www.w3.org/2002/07/owl#sameAs> <"+ instance.toString()+ ">.}}";
                    query = QueryFactory.create(queryString);
                    qexec = QueryExecutionFactory.create(query, source.getModelSource());
                    ResultSet resultsSameAs = qexec.execSelect();

                    for ( ; resultsSameAs.hasNext() ;){
                        Resource sameAsInstance = resultsSameAs.nextSolution().getResource("o");

                        // Vérification à ajouter pour savoir si l'instance a déjà été instanciée
                        this.instanciate.add(sameAsInstance);

                        Source sourceSameAs = projectManager.getSourceBySameAs(sameAsInstance);
                        //System.out.println(sameAsInstance + " : " + sourceSameAs);
                        if (sourceSameAs != null) {
                            for (Match match : sourceSameAs.getMatchsSchema()){

                                queryString = "SELECT ?o WHERE {<" + sameAsInstance.toString() + "> <" + match.getPropSource() + "> ?o }";
                                query = QueryFactory.create(queryString);
                                qexec = QueryExecutionFactory.create(query, sourceSameAs.getModelSource());
                                ResultSet resultVal = qexec.execSelect();
                                for (; resultVal.hasNext(); ) {
                                    QuerySolution solution = resultVal.nextSolution();
                                    if (solution.get("o").isLiteral()){
                                        projectManager.getTargetSchema().addTriple(instance, projectManager.getTargetSchema().getModelTargetSchema().getProperty(match.getTargetProp()), solution.getLiteral("o"));
                                    }
                                    else {
                                        projectManager.getTargetSchema().addTriple(instance, projectManager.getTargetSchema().getModelTargetSchema().getProperty(match.getTargetProp()), solution.getResource("o"));

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public void linking(String source1, String source2){
        Source s1, s2;
        File links = null;
        s1 = projectManager.getSourceByName(source1);
        s2 = projectManager.getSourceByName(source2);
        try {

            links = new File(projectManager.getProjectDirectory().getAbsolutePath()+"/links/"+s1.getName().split("\\.")[0]+"_"+s2.getName().split("\\.")[0]+".xml");
            if (!links.exists()){
                s1 = projectManager.getSourceByName(source2);
                s2 = projectManager.getSourceByName(source1);
                links = new File(projectManager.getProjectDirectory().getAbsolutePath()+"/links/"+source2.split("\\.")[0]+"_"+source1.split("\\.")[0]+".xml");
            }
        } catch (Exception e){

        }
        System.out.println(s1.getName());
        System.out.println("fichier liens : " + links);

        InstancesMatchs instancesMatchs = new InstancesMatchs(links, s1, s2);

        for (InstancesMatchs matchs : this.links){
            if (matchs.getSource1().equals(s1) && matchs.getSource2().equals(s2)){
                //Enlever les liens sameAs
            }
        }

        this.links.add(instancesMatchs);


        for (InstanceMatch link : instancesMatchs.getLinks()){
            Resource resource1 = s1.getModelSource().getResource(link.getInstanceS1());
            Resource resource2 = s2.getModelSource().getResource(link.getInstanceS2());
            if (!s1.getModelSource().contains(resource1, OWL.sameAs, resource2)){
                s1.addTriple(resource1, OWL.sameAs, resource2);
            }
            if (!s2.getModelSource().contains(resource2, OWL.sameAs, resource1)){
                s2.addTriple(resource2, OWL.sameAs, resource1);
            }
        }

    }

    public Resource findSameAs(Resource resource, Source source){
        Resource sameAs = null;
        String queryString = "SELECT ?o WHERE {{<"+ resource.toString()+ "> <http://www.w3.org/2002/07/owl#sameAs> ?o.} UNION {?o <http://www.w3.org/2002/07/owl#sameAs> <"+ resource.toString()+ ">.}}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, source.getModelSource());
        ResultSet resultsSameAs = qexec.execSelect();

        for ( ; resultsSameAs.hasNext() ;) {
            sameAs = resultsSameAs.nextSolution().getResource("o");

        }
        return sameAs;
    }

    public void linking() {
        for (Source source1 : projectManager.getSources()) {
            for (Source source2 : projectManager.getSources()) {
                if (source1 != source2) {
                    linking(source1.getFileSource().getName(), source2.getFileSource().getName());
                }
            }
        }
    }

    public void extractionSousEnsembles(Source source) throws IOException {
        //ProjectManager projectManager = DI_APP.getMainWindow().getProjectManager();
        TargetSchema schemaCible = projectManager.getTargetSchema();


        //Model subsetSource = ModelFactory.createDefaultModel();
        for (Match match : source.getMatchsSchema()){
            Resource targetProperty = schemaCible.getModelTargetSchema().getProperty(match.getTargetProp());
            Resource targetClass = schemaCible.getModelTargetSchema().getProperty(match.getTargetClass());
            if (schemaCible.getModelTargetSchema().contains(targetProperty, RDF.type, OWL.DatatypeProperty) || (schemaCible.getModelTargetSchema().contains(targetProperty, RDF.type, OWL.ObjectProperty))) {

                Resource object = ResourceFactory.createResource(match.getPropSource());
                //schemaCible.addTriple(targetProperty, OWL.equivalentProperty, object);

            }
            if (schemaCible.getModelTargetSchema().contains(targetClass, RDF.type, OWL.Class)){
                Resource object = ResourceFactory.createResource(match.getSourceClass());
                schemaCible.addTriple(targetClass, OWL.equivalentClass, object);
            }

        }
        //createPertinentSubset(source);
    }


    public File createPertinentSubset(Source source) throws IOException {
        File fileSource = source.getFileSource();
        String pathSubsetSource = fileSource.getAbsolutePath().replace(fileSource.getName(), "subset-"+fileSource.getName());  ;
        File pertinentSubset = new File(pathSubsetSource);
        pertinentSubset.createNewFile();

        //String sparql = "SELECT ?s ?p ?o WHERE { " +
        //                "?p a <https://data.grandparissud.fr/ld/ontologies/mediatheques-gps-nov2020/adresse>." ;
        //Query query = QueryFactory.create(sparql);
        //QueryExecution qe = QueryExecutionFactory.create(query, getModel());
        return fileSource;

    }

    public void linking(InstancesMatchs instancesMatchs){

        Source source1 = instancesMatchs.getSource1();
        Source source2 = instancesMatchs.getSource2();

        for (InstanceMatch link : instancesMatchs.getLinks()){
            Resource resource1 = source1.getModelSource().getResource(link.getInstanceS1());
            Resource resource2 = source2.getModelSource().getResource(link.getInstanceS2());

            source1.addTriple(resource1, OWL.sameAs, resource2);
            source2.addTriple(resource2, OWL.sameAs, resource1);
        }
    }

    public ProjectManager getProjectManager(){
        return this.projectManager;
    }

    public ArrayList<SchemaMatchs> getMatchs(){
        return this.matchs;
    }

    public ArrayList<InstancesMatchs> getLinks() {
        return links;
    }

    public ArrayList<Resource[]> getClassMatch() {
        return classMatch;
    }

    //Supprimer les sameAs déjà présents
    public void deleteSameAs(){
    }

    public Mapping getMapping(String targetClass){
        if (this.mappings.size() == 0)
            return  null;
        for (Mapping mapping : this.mappings)
            if (mapping.getTargetClass().equals(targetClass)) {
                return mapping;
            }
        return null;
    }
}
