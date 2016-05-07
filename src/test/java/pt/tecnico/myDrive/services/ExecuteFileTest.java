/**
 *
 */
package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;

import pt.tecnico.myDrive.services.ExecuteFileService;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

public class ExecuteFileTest extends AbstractServiceTest {

  private FileSystem _fs;
  private User _user, _user2;
  private Login _login;
  private String args[] = {"1","2"};

  /* (non-Javadoc)
   * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
   */
  @Override
  protected void populate() {
    try {
      _fs = FileSystem.getInstance();
      _user = new User(_fs, "litxo88888", "litxo88888", "litxo8888");
			_user.setHomeDirectory(new Directory(_fs, "litxo88888", _fs.getHomeDirectory(), _user));
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

      new App       (_fs, "app", _user.getHomeDirectory(), _user, "app_Data");
      new PlainFile (_fs, "pf" , _user.getHomeDirectory(), _user, "pf_Data");

      new Link      (_fs, "linksucc1", _user.getHomeDirectory(), _user, "dir1/plainfile1");
      new Link      (_fs, "linksucc2", _user.getHomeDirectory(), _user, "dir1/dir2/plainfile2");

      new Link      (_fs, "linkpathsucc1", dir2, _user, "../plainfile1");
      new Link      (_fs, "linkpathsucc2", dir2, _user, "plainfile2");

      new Link      (_fs, "linkfail1" , _user.getHomeDirectory(), _user, "dir1/dir2");
      new Link      (_fs, "linkfail2" , _user.getHomeDirectory(), _user, ".");

      new Link      (_fs, "link2linksucc1", _user.getHomeDirectory(), _user, "linksucc1");
      new Link      (_fs, "link2linkfail1" , _user.getHomeDirectory(), _user, "linkfail1");

      new PlainFile (_fs, "plainfile1", dir1, _user, "plainfile1_Data");
      new PlainFile (_fs, "plainfile2", dir2, _user, "plainfile2_Data");

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void executeApp() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "app", args );
    efs.execute();

    ////assertEquals("App data is incorrect!", "app_Data", result);
  }

  @Test
  public void executePF() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "pf", args);
    efs.execute();

    ////assertEquals("PlainFile data is incorrect!", "pf_Data", result);
  }

  @Test
  public void executePFByPath() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "dir1/plainfile1", args);
    efs.execute();

    ////assertEquals("PlainFile data is incorrect!", "plainfile1_Data", result);
  }

  @Test
  public void executeLinkSucc1() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linksucc1", args);
    efs.execute();

    ////assertEquals("Link in current directory Data is incorrect!", "plainfile1_Data", result);
  }

  @Test
  public void executeLinkSucc2() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linksucc2", args);
    efs.execute();

    ////assertEquals("Link in current directory Data is incorrect!", "plainfile2_Data", result);
  }

  @Test
  public void executeLinkByRelPathSucc1() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "dir1/dir2/linkpathsucc1", args);
    efs.execute();

    //assertEquals("Link by relative path Data is incorrect!", "plainfile1_Data", result);
  }

  @Test
  public void executeLinkByRelPathSucc2() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "dir1/dir2/linkpathsucc2", args);
    efs.execute();

    //assertEquals("Link by relative path Data is incorrect!", "plainfile2_Data", result);
  }

  @Test
  public void executeLinkByAbsPathSucc() throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "/home/litxo/linksucc1", args);
    efs.execute();

    //assertEquals("Link by absolute path Data is incorrect!", "plainfile1_Data", result);
  }

  @Test
  public void link2linkSucc () throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "link2linksucc1", args);
    efs.execute();

    //assertEquals("Link to another link Data is incorrect!", "plainfile1_Data", result);
  }

   @Test
  public void link2linkPath () throws Exception {
    ExecuteFileService efs = new ExecuteFileService(123l, "linkfail1/plainfile2", args);
    efs.execute();

    //assertEquals("Link to another link Data is incorrect!", "plainfile2_Data", result);
  }

  @Test(expected = FileUnknownException.class)
    public void executeUnknownFile() throws Exception {
      ExecuteFileService efs = new ExecuteFileService(123l, "nofile", args);
      efs.execute();
    }


  @Test(expected = InsufficientPermissionsException.class)
    public void executeLinkDirectoryPath() throws Exception {
      ExecuteFileService efs = new ExecuteFileService(123l, "linkfail1", args);
      efs.execute();
    }
}
