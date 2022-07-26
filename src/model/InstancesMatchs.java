package model;

import org.apache.logging.log4j.CloseableThreadContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class InstancesMatchs {

    private final File fileMatchs;
    private final Source source1;
    private final Source source2;

    private ArrayList<InstanceMatch> links = new ArrayList<InstanceMatch>();

    public InstancesMatchs(File fileMatchs, Source source1, Source source2 ){
        this.fileMatchs = fileMatchs;
        this.source1 = source1;
        this.source2 = source2;
        constructMatchs();
    }

    public File getFileMatchs() {
        return fileMatchs;
    }

    public ArrayList<InstanceMatch> getLinks() {
        return links;
    }

    public void constructMatchs() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fileMatchs);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("Cell");

            for (int temp=0; temp < list.getLength(); temp++){

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE){

                    Element element = (Element) node;

                    String elt1 = element.getElementsByTagName("entity1").item(0).getAttributes().getNamedItem("rdf:resource").getTextContent();
                    String elt2 = element.getElementsByTagName("entity2").item(0).getAttributes().getNamedItem("rdf:resource").getTextContent();
                    String relation = element.getElementsByTagName("relation").item(0).getTextContent();

                    if (relation.equals("=")){
                        this.links.add(new InstanceMatch(elt1, elt2, source1, source2));
                    }
                }
            }


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    public Source getSource1(){
        return this.source1;
    }

    public Source getSource2(){
        return this.source2;
    }
}
