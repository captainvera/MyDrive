package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Verifications;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import pt.tecnico.myDrive.services.ExecuteFileService;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;
import pt.tecnico.myDrive.exceptions.CannotExecuteDirectoryException;
import pt.tecnico.myDrive.exceptions.NoExtensionException;
import pt.tecnico.myDrive.exceptions.NoAssociatedAppException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;

import pt.tecnico.myDrive.presentation.Helper;

@RunWith(JMockit.class)
public class ExecuteFileTest extends AbstractServiceTest {

  private FileSystem _fs;
  private User _user, _user2;
  private Login _login;
  private String args[] = {"1","2"};
  private App app, otherUserApp;

  /* (non-Javadoc)
   * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
   */
  @Override
  protected void populate() {
    try {
      _fs = FileSystem.getInstance();
      _user = new User(_fs, "litxo88888", "litxo88888", "litxo8888");
      _user.setHomeDirectory(new Directory(_fs, "litxo88888", _fs.getHomeDirectory(), _user));
      _user2 = new User(_fs, "esquentador" , "esquentador", "esquentador", "rwxdr---");
      _login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);

      Directory dir1 = new Directory (_fs, "dir1", _user.getHomeDirectory(), _user);
      Directory dir2 = new Directory (_fs, "dir2", dir1                    , _user);

      app = new App       (_fs, "app", _user.getHomeDirectory(), _user, "pt.tecnico.myDrive.presentation.Helper.argumentTest");
      app = new App       (_fs, "appdefault", _user.getHomeDirectory(), _user, "pt.tecnico.myDrive.presentation.Helper");
      otherUserApp = new App       (_fs, "app2", _user.getHomeDirectory(), _user2, "");

      new PlainFile (_fs, "pf" , _user.getHomeDirectory(), _user, "/home/litxo88888/app 1 2");

      new PlainFile (_fs, "noextension" , _user.getHomeDirectory(), _user2, "/home/litxo88888/otheruser.txt 1 2");
      new PlainFile (_fs, "otheruser.txt", _user.getHomeDirectory(), _user2, "");

      new Link      (_fs, "linksucc1", _user.getHomeDirectory(), _user, "dir1/plainfile1");
      new Link      (_fs, "linksucc2", _user.getHomeDirectory(), _user, "dir1/dir2/plainfile2");

      new Link      (_fs, "linkpathsucc1", dir2, _user, "../plainfile1");
      new Link      (_fs, "linkpathsucc2", dir2, _user, "plainfile2");

      new Link      (_fs, "linkfail1" , _user.getHomeDirectory(), _user, "dir1/dir2");
      new Link      (_fs, "linkfail2" , _user.getHomeDirectory(), _user, ".");

      new Link      (_fs, "link2linksucc1", _user.getHomeDirectory(), _user, "linksucc1");
      new Link      (_fs, "link2linkfail1" , _user.getHomeDirectory(), _user, "linkfail1");

      new PlainFile (_fs, "plainfile1", dir1, _user, "/home/litxo88888/app 1 2");
      new PlainFile (_fs, "plainfile2", dir2, _user, "/home/litxo88888/app 1 2");

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void executeApp(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "app", args);
    efs.execute();
    new Verifications(){
      {
        hp.argumentTest(args);
      }
    };
  }

  @Test
  public void executeAppDefault(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "appdefault", args);
    efs.execute();
    new Verifications(){
      {
        hp.main(args);
      }
    };
  }

  @Test
  public void executePF(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "pf", args);
    efs.execute();
    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test
  public void executePFByPath(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "/home/litxo88888/pf", args);
    efs.execute();


    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test
  public void executeLinkSucc1(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linksucc1", args);
    efs.execute();
    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test
  public void executeLinkSucc2(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linksucc2", args);
    efs.execute();

    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test
  public void executeLinkByRelPathSucc1(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "dir1/dir2/linkpathsucc1", args);
    efs.execute();

    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test
  public void executeLinkByRelPathSucc2(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "dir1/dir2/linkpathsucc2", args);
    efs.execute();

    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test
  public void executeLinkByAbsPathSucc(@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "/home/litxo88888/linksucc1", args);
    efs.execute();

    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test
  public void link2linkSucc (@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "link2linksucc1", args);
    efs.execute();
    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test
  public void link2linkPath (@Mocked final Helper hp) throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linkfail1/plainfile2", args);
    efs.execute();

    new Verifications(){
      {
        hp.argumentTest((String[])any);
      }
    };
  }

  @Test(expected = FileUnknownException.class)
    public void executeUnknownFile() throws Exception {
      ExecuteFileService efs = new ExecuteFileService(123l, "nofile", args);
      efs.execute();
    }

  @Test(expected = CannotExecuteDirectoryException.class)
    public void executeDirectory() throws Exception {
      ExecuteFileService efs = new ExecuteFileService(123l, "dir1/dir2/", args);
      efs.execute();
    }

  @Test(expected = CannotExecuteDirectoryException.class)
    public void executeLinkDirectoryPath() throws Exception {
      ExecuteFileService efs = new ExecuteFileService(123l, "linkfail1", args);
      efs.execute();
    }


  @Test(expected = InvalidTokenException.class)
    public void executeInvalidToken() throws Exception {
      ExecuteFileService efs = new ExecuteFileService(911112l, "app", args );
      efs.execute();
    }
}
