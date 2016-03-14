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
		System.out.println("-- Welcome to myDrive");
		try {
			// if(args.length == 0) setupDrive();
			System.out.println("-- setupDrive() return to main");
		  System.out.println("-- executing testRun()");  
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

		System.out.println("-- Setting root: " + FenixFramework.getDomainRoot());

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
      System.out.println("-- Showing Path");
			System.out.println(fs.listPath());

      System.out.println("-- Creating README");
		  PlainFile readme = fs.createPlainFile("README");
      readme.setData("Lista de utilizadores");
      System.out.println("-- Listing current Directory");
			System.out.println(fs.listDirectory());
      System.out.println("-- Showing result of opening README");
      System.out.println(fs.executeFile("README"));
	  }catch(Exception e){
			System.out.println("-- COuldn't create README!");
		}
		
    /*
     * Criação do caminho /usr/local/bin absolutamente
     */
    try{
      System.out.println("-- Creating /usr/local/bin by path");
      fs.createDirectoryByPath("/usr/local/bin");
      System.out.println("-- Listing /usr/local");
      System.out.println(fs.listFileByPathSimple("/usr/local"));
    } catch(Exception e){
			System.out.println(e.getMessage());
		}

		System.out.println("-- Successful default setup!");
	}

	@Atomic
	public static void testRun() {
		System.out.println("TestRun: " + FenixFramework.getDomainRoot());

		FileSystem fs = FileSystem.getInstance();
		try{
      fs.login("root","***");
      System.out.println("-- Showing path");
			System.out.println(fs.listPath());
      System.out.println("-- Listing current Directory");
			System.out.println(fs.listDirectory());


      System.out.println("-- Removing README");
      fs.removeFileByPath("/home/root/README");
      System.out.println("-- Listing current Directory");
			System.out.println(fs.listDirectory());

      System.out.println("-- Listing /user/local");
      System.out.println(fs.listFileByPathSimple("/usr/local"));

    }catch(Exception e){
      System.out.println("-- Exception on testRun!");
			System.out.println(e.getMessage());
		}
	}


	@Atomic
	public static void xmlOutput(){
		System.out.println("");
	}

	@Atomic
	public static void xmlInput(){
		System.out.println("");
	}

}
