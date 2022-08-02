package model;

import controller.ProjectManager;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import view.GraphVisualization;


import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import static controller.DataIntegration.DI_APP;

public class TreeStructureProjet {

    public JTree tree;


    public TreeStructureProjet(ProjectManager projectManager){
        DefaultMutableTreeNode classNode;
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(projectManager.getProjectName());
        DefaultMutableTreeNode targetSchemaNode = new DefaultMutableTreeNode(projectManager.getTargetSchema().getTargetSchema().getName());
        for (Element classe : projectManager.getTargetSchema().getClasses()){
            classNode = new DefaultMutableTreeNode(classe.getUri());
            targetSchemaNode.add(classNode);
        }
        top.add(targetSchemaNode);
        DefaultMutableTreeNode sources = new DefaultMutableTreeNode("Sources");
        top.add(sources);

        DefaultMutableTreeNode sourceNode;

        for (Source source : projectManager.getSources()){
            sourceNode = new DefaultMutableTreeNode(source.getName());
            for (String classe : source.getClasses()){
                classNode = new DefaultMutableTreeNode(classe);
                sourceNode.add(classNode);
            }
            sources.add(sourceNode);
        }

        this.tree = new JTree(top);
        this.tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                // Cas où on est sur le nom du projet
                if (e.getPath().getLastPathComponent().toString().equals(projectManager.getProjectName())) {
                    return;
                }

                // Cas où on est sur un schéma cible
                else if (e.getPath().getLastPathComponent().toString().equals(projectManager.getTargetSchema().getTargetSchema().getName())){
                    GraphVisualization viz = new GraphVisualization(projectManager.getTargetSchema().getModelTargetSchema(),projectManager.getTargetSchema().getTargetSchema().getName(), false);
                    DI_APP.getMainWindow().add(viz);
                    viz.setVisible(true);
                }

                // Cas où on est sur la classe d'un schéma cible
                else if (e.getPath().getPathComponent(1).toString().equals(projectManager.getTargetSchema().getTargetSchema().getName())) {

                    String classe = e.getPath().getLastPathComponent().toString();
                    System.out.println(classe);
                    Model modelClass = getModelSourceClass(projectManager.getTargetSchema().getModelTargetSchema(),classe );

                    GraphVisualization viz = new GraphVisualization(modelClass,projectManager.getTargetSchema().getTargetSchema().getName() + " : " + classe , false);
                    DI_APP.getMainWindow().add(viz);
                    viz.setVisible(true);


                }

                // Cas où on est sur le dossier "sources"
                else if (e.getPath().getLastPathComponent().toString().equals("Sources")) {
                    return;
                }
                // Cas où on est sur une source
                else if (projectManager.getSourceByName(e.getPath().getLastPathComponent().toString()) != null){
                    Source source =  projectManager.getSourceByName(e.getPath().getLastPathComponent().toString());
                    GraphVisualization viz = new GraphVisualization(source.getModelSource(), source.getName(), false);
                    DI_APP.getMainWindow().add(viz);
                    viz.setVisible(true);

                    return;


                }
                // Cas où on est sur une classe d'une source
                else if (projectManager.getSourceByName(e.getPath().getPathComponent(2).toString()) != null){
                    Source source = projectManager.getSourceByName(e.getPath().getPathComponent(2).toString());
                    String classe = e.getPath().getLastPathComponent().toString();
                    Model modelClass = getModelSourceClass(source.getModelSource(), classe );

                    GraphVisualization viz = new GraphVisualization(modelClass, source.getName() + " : " + classe,false);
                    DI_APP.getMainWindow().add(viz);
                    viz.setVisible(true);
                }

            }
        });
    }

    public Model getModelSourceClass(Model model, String URIclasse){

        Model newModel;
        Query query = QueryFactory.create(
                "CONSTRUCT {<"+URIclasse+">  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class>." +
                            "?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+URIclasse+"> . " +
                            "?prop <http://www.w3.org/2000/01/rdf-schema#domain> <"+URIclasse+"> ." +
                            "?x ?p ?z. " +
                            "?x <http://www.w3.org/2002/07/owl#sameAs> ?zSame. " +
                            "<"+URIclasse+"> <http://www.w3.org/2002/07/owl#equivalentClass> ?xEq." +
                            "?prop <http://www.w3.org/2002/07/owl#equivalentProperty> ?pEq." +
                            "?prop <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?yType .}" +


                        "WHERE {  " +

                            "?prop <http://www.w3.org/2000/01/rdf-schema#domain> <"+URIclasse+"> ." +
                            "OPTIONAL{?x ?p ?z ." +
                                     "?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + URIclasse + "> .}" +
                            "OPTIONAL{<"+URIclasse+"> <http://www.w3.org/2002/07/owl#equivalentClass> ?xEq.}" +
                            "OPTIONAL{{?prop <http://www.w3.org/2002/07/owl#equivalentClass> ?pEq.} UNION{?pEq <http://www.w3.org/2002/07/owl#equivalentClass> ?prop.}}" +
                            "OPTIONAL {" +
                                        "{?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + URIclasse + "> ." +
                                        "?x <http://www.w3.org/2002/07/owl#sameAs> ?zSame.} " +
                        "               UNION {?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + URIclasse + "> ." +
                                        "?zSame <http://www.w3.org/2002/07/owl#sameAs> ?x.}" +
                                      "}" +
                            "OPTIONAL {?prop <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?yType .}" +
                        "}");


        QueryExecution qexec = QueryExecutionFactory.create(query,model);
        newModel = qexec.execConstruct();

        return newModel ;
    }

    public Model getModelSourceClassTARGET(Model model, String URIclasse) {

        Model newModel;
        System.out.println(URIclasse);
        Query query = QueryFactory.create("CONSTRUCT {<" + URIclasse + ">  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . ?x ?y <" + URIclasse + "> } WHERE { ?x ?y <" + URIclasse + ">} ");
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        newModel = qexec.execConstruct();

        return newModel;
    }

    public JTree getTree(){
        return this.tree;
    }
}
