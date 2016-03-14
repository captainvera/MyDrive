package pt.tecnico.myDrive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

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

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

public class myDriveApplication {
  private static final Logger log = LogManager.getRootLogger();

  // FenixFramework will try automatic initialization when first accessed
  public static void main(String [] args) {
    log.trace("-- Welcome to myDrive");
    try {
      // if(args.length == 0) setupDrive();
      log.trace("-- setupDrive() return to main");
      log.trace("-- executing testRun()");
      testRun();
    } finally {
      // ensure an orderly shutdown
      FenixFramework.shutdown();
    }
  }

  @Atomic
  public static void setupDrive(){
    /**
     * Basic setup to test desired functionality
     */

    log.trace("-- Setting root: " + FenixFramework.getDomainRoot());

    FileSystem fs = FileSystem.getInstance();

    try{
      fs.login("root","***");
    }catch(Exception e){
      System.out.println("-- Couldn't login with root!");
    }

    /*
     * Criação do ficheiro README mudando o working directory
     * */
    try{
      log.trace("-- Showing Path");
      System.out.println(fs.listPath());

      log.trace("-- Creating README");
      PlainFile readme = fs.createPlainFile("README");
      readme.setData("Lista de utilizadores");
      log.trace("-- Listing current Directory");
      log.trace(fs.listDirectory());
      log.trace("-- Showing result of opening README");
      log.trace(fs.executeFile("README"));
    }catch(Exception e){
      System.out.println("-- Couldn't create README!");
    }

    /*
     * Criação do caminho /usr/local/bin absolutamente
     */
    try{
      log.trace("-- Creating /usr/local/bin by path");
      fs.createDirectoryByPath("/usr/local/bin");
      log.trace("-- Listing /usr/local");
      System.out.println(fs.listFileByPathSimple("/usr/local"));
    } catch(Exception e){
      System.out.println(e.getMessage());
    }

    log.trace("-- Successful default setup!");
  }

  @Atomic
  public static void testRun() {
    log.trace("TestRun: " + FenixFramework.getDomainRoot());

    FileSystem fs = FileSystem.getInstance();
    try{
      fs.login("root","***");
      log.trace("-- Showing path");
      System.out.println(fs.listPath());
      log.trace("-- Listing current Directory");
      System.out.println(fs.listDirectory());


      log.trace("-- Removing README");
      fs.removeFileByPath("/home/root/README");
      log.trace("-- Listing current Directory");
      System.out.println(fs.listDirectory());

      log.trace("-- Listing /user/local");
      System.out.println(fs.listFileByPathSimple("/usr/local"));

    }catch(Exception e){
      log.trace("-- Exception on testRun!");
      System.out.println(e.getMessage());
    }
  }


  @Atomic
  public static void xmlOutput(){
    log.trace("");
  }

  @Atomic
  public static void xmlInput(){
    log.trace("");
  }

}
