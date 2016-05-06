package pt.tecnico.myDrive.service;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.After;
import org.junit.AfterClass;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.tecnico.myDrive.myDriveApplication;

public abstract class AbstractServiceTest {
  private static final Logger log = LogManager.getRootLogger();

  @BeforeClass
  public static void initialSetup(){
    //Initiate stuff
    myDriveApplication.init();
  }

  @Before
  public void setup() throws Exception {
    //Initiate more stuff
    try {
      FenixFramework.getTransactionManager().begin(false);
      populate();
    } catch (WriteOnReadError | NotSupportedException | SystemException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @After
  public void rollback(){
    //Undo stuff
    try {
      FenixFramework.getTransactionManager().rollback();
    } catch (IllegalStateException | SecurityException | SystemException e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void finish(){
    // Orderly shutdown
    FenixFramework.shutdown();
  }

  //Populate Filesystem with data specific to each test
  protected abstract void populate() throws Exception;
}
