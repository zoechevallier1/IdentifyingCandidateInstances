package model;

import org.apache.jena.rdf.model.Model;

import java.io.File;

public class PertinentSubset {

    private File file;
    private Model model;

    public PertinentSubset(){
        this.file = null;
        this.model = null;

    }

    public void createPertinentSubset(File file, Model model){
        this.file = file;
        this.model = model;

    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
