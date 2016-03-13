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
  static final Logger log = LogManager.getRootLogger();

  // FenixFramework will try automatic initialization when first accessed
  public static void main(String [] args) {
    System.out.println("*paaaaaan* Welcome to myDrive *paaaaaan*");
    try {
      if(args.length == 0) setupDrive();
      else for (String s: args xmlInput());
      testRun();
    } finally {
      // ensure an orderly shutdown
      FenixFramework.shutdown();
    }
  }

  @Atomic
  public static void setupDrive(){
    log.trace("Setup: " + FenixFramework.getDomainRoot());

    FileSystem fs = FileSystem.getInstance();
    if (!fs.getUserSet().isEmpty())
      return;

    /*create a default filesystem*/
    User user = fs.createUser("root", "***");
    fs.login(user.getUsername(), "***");
    fs.changeDirectory(user.getHome().getParent());
    Directory dir = fs.createDirectory("usr", fs.getCurrentDirectory());
    fs.createDirectory("local", dir);

  }

  public static void testRun() {
    log.trace("TestRun: " + FenixFramework.getDomainRoot());

    FileSystem fs = FileSystem.getInstance();

    User user = fs.login("root", "***");
    PlainFile readme = fs.createPlainFile("README", user.getHome());
    Directory dir = fs.createDirectory("/usr/local/bin");
    fs.readPlainFile(readme);
    removeDirectory(dir);
    xmlOutput(); /*To be done by blackbelly*/
    fs.remove(readme);
    fs.listCurrentDirectory();
    fs.logout();
  }


  @Atomic
  public static xmlOutput(){
    System.out.println("blackbelly, please do")
  }

  @Atomic
  public static xmlInput(){
    System.out.println("blackbelly, please do")
  }

}
