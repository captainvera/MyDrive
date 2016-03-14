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
			System.out.println("-- Setup returning to main");
			// UNCOMMENT TO TEST OBJECT PERSISTENCE!
			testRun();
		} finally {
			// ensure an orderly shutdown
			FenixFramework.shutdown();
		}
	}

	@Atomic
	public static void setupDrive(){
		/**
		 * Temporary main with basic tests
		 * TODO: Change to match necessary description
		 */

		System.out.println("Setup: " + FenixFramework.getDomainRoot());

		FileSystem fs = FileSystem.getInstance();

		try{
			fs.login("root","***");
		}catch(Exception e){
			System.out.println("PROBLEMO LOGIN");
		}

		try{
			System.out.println(fs.listPath());
			fs.changeDirectory("home");
			System.out.println(fs.listPath());
			fs.changeDirectory("root");
		}catch(Exception e){
			System.out.println("CD ERROR");
		}
		PlainFile readme = fs.createPlainFile("README");
		try{
			System.out.println(fs.listDirectory());
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println(fs.listPath());
		System.out.println(fs.currentDirectory());
		try{
			fs.createDirectory("test");
			fs.changeDirectory("test");
			System.out.println(fs.listPath());
			fs.changeDirectory("..");
			System.out.println(fs.listPath());
			fs.changeDirectory(".");
			System.out.println(fs.listPath());
			fs.changeDirectory("..");
			System.out.println(fs.listPath());
			fs.changeDirectory("..");
			System.out.println(fs.listPath());
			fs.changeDirectory("..");
		}catch(Exception e){
		}	

		System.out.println("Successful setup!");
	}

	@Atomic
	public static void testRun() {
		System.out.println("TestRun: " + FenixFramework.getDomainRoot());

		FileSystem fs = FileSystem.getInstance();
		try{
			fs.login("root","***");
			System.out.println(fs.listDirectory());
		}catch(Exception e){
			System.out.println("ERROR!");
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
