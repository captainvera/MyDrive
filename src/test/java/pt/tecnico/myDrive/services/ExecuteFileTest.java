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
      _user2 = new User(_fs, "esquentador" , "esquentador", "rwxdrwxd", "esquentador");
      _login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);

      /* We'll have something like this
       * |- app
       * |- pf
       * |
       * |- link2fail1 -> linkfail1
       * |- linkfail1 -> dir1/dir2
       * |- linkfail2 -> .
       * |
       * |- link2linksucc1 -> linksuc1
       * |- linksucc1 -> dir1/plainfile1
       * |- linksucc2 -> dir1/dir2/plainfile2
       * |
       * |- dir1
       *     |- plainfile1
       *     |- dir2
       *          |- linkpathsucc1 -> ../plainfile1
       *          |- linkpathsucc2 -> plainfile2
       *          |- plainfile2
       * */

      Directory dir1 = new Directory (_fs, "dir1", _user.getHomeDirectory(), _user);
      Directory dir2 = new Directory (_fs, "dir2", dir1                    , _user);

      app = new App       (_fs, "app", _user.getHomeDirectory(), _user, "pt.tecnico.myDrive.domain.FileSystem.apptest");
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
  public void executeApp() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "app", args );
    efs.execute();
    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void executePF() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "pf", args);
    efs.execute();
    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void executePFByPath() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "/home/litxo88888/pf", args);
    efs.execute();


    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void executeLinkSucc1() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linksucc1", args);
    efs.execute();
    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void executeLinkSucc2() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linksucc2", args);
    efs.execute();

    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void executeLinkByRelPathSucc1() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "dir1/dir2/linkpathsucc1", args);
    efs.execute();

    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void executeLinkByRelPathSucc2() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "dir1/dir2/linkpathsucc2", args);
    efs.execute();

    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void executeLinkByAbsPathSucc() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "/home/litxo88888/linksucc1", args);
    efs.execute();

    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void link2linkSucc () throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "link2linksucc1", args);
    efs.execute();
    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void link2linkPath () throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linkfail1/plainfile2", args);
    efs.execute();

    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void executeExtension() throws Exception {
    new MockUp<User>(){
        @Mock
        public App getAssociation(String extension){
            return app;
        }
    };

    ExecuteFileService efs = new ExecuteFileService(123l, "otheruser.txt", args);
    efs.execute();

    new Verifications(){
        {
            _fs.apptest(args);
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

  //no execution permission, with extension, tries to execute but no permissions
  @Test(expected = InsufficientPermissionsException.class)
  public void executeOtherUserFile() throws Exception {
    new MockUp<User>(){
        @Mock
        public App getAssociation(String extension){
            return otherUserApp;
        }
    };

    ExecuteFileService efs = new ExecuteFileService(123l, "otheruser.txt", args);
    efs.execute();
  }

  @Test(expected = NoAssociatedAppException.class)
  public void executeNoAppFound() throws Exception {
    new MockUp<User>(){
        @Mock
        public App getAssociation(String extension){
            return null;
        }
    };

    ExecuteFileService efs = new ExecuteFileService(123l, "otheruser.txt", args);
    efs.execute();
  }
  //no execution permission, tries to execute with extension, but none of it was
  //found
  @Test(expected = NoExtensionException.class)
  public void executeNoExtensionFile() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "noextension", args);
    efs.execute();
  }
  //test with extension with success
  
  @Test(expected = InvalidTokenException.class)
  public void executeInvalidToken() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(911112l, "app", args );
    efs.execute();
  }
}
