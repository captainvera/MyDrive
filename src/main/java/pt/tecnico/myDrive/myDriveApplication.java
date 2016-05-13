package pt.tecnico.myDrive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.services.LoginService;
import pt.tecnico.myDrive.domain.Link;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.myDrive.presentation.MyDriveShell;

import pt.tecnico.myDrive.services.ImportMyDriveService;

public class myDriveApplication {
  private static final Logger log = LogManager.getRootLogger();

  // FenixFramework will try automatic initialization when first accessed
  public static void main(String [] args) {
    log.trace("Welcome to myDrive");
    try {
      if(args.length > 0) xmlScan(new java.io.File(args[0]));
      else setupDrive();
      //xmlPrint();

      MyDriveShell.main(new String[0]);

    } catch(Exception e){
      e.printStackTrace();
    }finally {
      // ensure an orderly shutdown
      FenixFramework.shutdown();
    }
  }

  @Atomic
  public static void init(){
  	log.trace("Initializing: " + FenixFramework.getDomainRoot());
  	FileSystem.getInstance();
  }


  @Atomic
  public static void setupDrive(){
    init();
  }

  @Atomic
  public static void xmlPrint() {
    log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
    Document doc = FileSystem.getInstance().xmlExport();
    XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
    try { xmlOutput.output(doc, new PrintStream(System.out));
    } catch (Exception e) { System.out.println(e); }
  }


  @Atomic
  public static void xmlScan(java.io.File file){
    log.trace("xmlScan: " + FenixFramework.getDomainRoot());
    SAXBuilder builder = new SAXBuilder();
    try {
      Document document = (Document)builder.build(file);
      ImportMyDriveService imds = new ImportMyDriveService(document);
      imds.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
