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

public class myDriveApplication {
  private static final Logger log = LogManager.getRootLogger();

  // FenixFramework will try automatic initialization when first accessed
  public static void main(String [] args) {
    log.trace("Welcome to myDrive");
    try {
      if(args.length > 0) xmlScan(new java.io.File(args[0]));
      else setupDrive();
      xmlPrint();
    } finally {
      // ensure an orderly shutdown
      FenixFramework.shutdown();
    }
  }

  @Atomic
  public static void init(){
  	log.trace("Initializing: " + FenixFramework.getDomainRoot());
  	FileSystem.getInstance().reset();
  }


  @Atomic
  public static void setupDrive(){
    //FIXME TODO
//    /**
//     * Basic setup to test desired functionality
//     */
//
//    log.trace("executing setupDrive()");
//    log.debug("Setting root: " + FenixFramework.getDomainRoot());
//
//    FileSystem fs = FileSystem.getInstance();
//
//    try {
//      LoginService login = new LoginService("root","***");
//      login.execute();
//      long token = login.result();
//    } catch(Exception e) {
//      System.out.println("Couldn't login with root!");
//      e.printStackTrace();
//    }
//
//    /*
//     * Criação do ficheiro README mudando o working directory
//     * */
//    try {
//      log.debug("Showing Current Directory Path");
//      System.out.println(fs.listPath());
//
//      log.debug("Changing directory to /home");
//      fs.changeDirectory("..");
//
//      log.debug("Showing Current Directory Path");
//      System.out.println(fs.listPath());
//      log.debug("Listing current Directory");
//      System.out.println(fs.listDirectory());
//      log.debug("Showing result of opening README");
//      System.out.println(fs.executeFile("README"));
//
//    } catch(Exception e) {
//      e.printStackTrace();
//    }
//
//    /*
//     * Criação do caminho /usr/local/bin absolutamente
//     */
//    try {
//      log.debug("Listing /usr/local");
//      System.out.println(fs.listFileByPathSimple("/usr/local"));
//
//      log.debug("Removing /usr/local/bin");
//      fs.removeFileByPath("/usr/local/bin");
//
//      log.debug("Listing /usr/local");
//      System.out.println(fs.listFileByPathSimple("/usr/local"));
//
//
//      log.debug("Listing /home");
//      System.out.println(fs.listFileByPathSimple("/home"));
//      log.debug("Removing /home/README");
//      //fs.removeFileByPath("/home/README");
//      log.debug("Listing /home");
//      System.out.println(fs.listFileByPathSimple("/home"));
//
//      log.debug("Number of files in file system pre-remove: " + fs.getFilesSet().size());
//      log.debug("Files pre-remove: ");
//      for (File f : fs.getFilesSet())
//        System.out.println(f);
//
//
//      log.debug("Number of files in file system pos-remove: " + fs.getFilesSet().size());
//      log.debug("Files pos-remove: ");
//      for (File f : fs.getFilesSet())
//        System.out.println(f);
//		//TODO EXECUTE APP
//	  log.debug("creating link");
//	  Link l1 = fs.createLink("cenas","/home/README");
//	  Link l2 = fs.createLink("dirlink","/home");
//	  log.debug("listing");
//	  System.out.println(fs.listDirectory());
//	  //execute
//	  log.debug("executing readme...");
//     // System.out.println(fs.executeFile("l1"));
//	  log.debug("trying to write to link");
//	  l1.setData("blabla");
//	  System.out.println(l1.execute());
//	  log.debug("changing directory");
//      fs.changeDirectory("dirlink");
//	  System.out.println(fs.listDirectory());
//    } catch(Exception e) {
//      e.printStackTrace();
//    }
//
//    log.trace("Successful default setup!");
  }

  @Atomic
  public static void xmlPrint() {
    // FIXME TODO
    log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
    Document doc = FileSystem.getInstance().xmlExport(); 
    XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
    try { xmlOutput.output(doc, new PrintStream(System.out)); 
    } catch (Exception e) { System.out.println(e); } 
  }


  @Atomic
  public static void xmlScan(java.io.File file){
    // FIXME TODO
    log.trace("xmlScan: " + FenixFramework.getDomainRoot());
    FileSystem fs = FileSystem.getInstance();
    SAXBuilder builder = new SAXBuilder(); 
    try { 
      Document document = (Document)builder.build(file); 
      fs.xmlImport(document.getRootElement()); 
    } catch (Exception e) { 
      e.printStackTrace(); 
    }
  }

}
