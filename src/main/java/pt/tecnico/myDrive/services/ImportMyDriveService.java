package pt.tecnico.myDrive.services;

import java.io.File;
import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import pt.tecnico.myDrive.domain.FileSystem;


public class ImportMyDriveService extends myDriveService {
    private final Document doc;

    public ImportMyDriveService(Document doc) {
        this.doc = doc;
    }

    @Override
    protected void dispatch(){
      FileSystem fs = FileSystem.getInstance();
      fs.xmlImport(doc.getRootElement());
    }
}
