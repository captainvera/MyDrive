package pt.tecnico.myDrive.component;

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

public class ExecuteAssociationTest extends AbstractComponentTest {

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
  public void executeExtension(@Mocked final Helper hp) throws Exception {
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
            hp.argumentTest((String[])any);
        }
    };
  }

  //no execution permission, tries to execute with extension, but none of it was
  //found
  @Test(expected = NoExtensionException.class)
  public void executeNoExtensionFile() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "noextension", args);
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
}
