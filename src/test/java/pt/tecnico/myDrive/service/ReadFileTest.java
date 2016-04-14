/**
 *
 */
package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;

import pt.tecnico.myDrive.services.ReadFileService;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exceptions.NotAPlainFileException;

public class ReadFileTest extends AbstractServiceTest {

  private FileSystem _fs;
  private User _user;
  private Login _login;
  private int _id;

  /* (non-Javadoc)
   * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
   */
  @Override
  protected void populate() {
    try {
      _fs = FileSystem.getInstance();
      _user = new User(_fs, "litxo", "litxo", "litxo");
			_user.setHomeDirectory(new Directory(_fs, _fs.requestId(),
					"litxo", _fs.getHomeDirectory(), _user));
      _login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);
      _id = 9999;

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

      Directory dir1 = new Directory (_fs, _id++, "dir1", _user.getHomeDirectory(), _user);
      Directory dir2 = new Directory (_fs, _id++, "dir2", dir1                    , _user);

      new App       (_fs, _id++, "app", _user.getHomeDirectory(), _user, "app_Data");
      new PlainFile (_fs, _id++, "pf" , _user.getHomeDirectory(), _user, "pf_Data");

      new Link      (_fs, _id++, "linksucc1", _user.getHomeDirectory(), _user, "dir1/plainfile1");
      new Link      (_fs, _id++, "linksucc2", _user.getHomeDirectory(), _user, "dir1/dir2/plainfile2");

      new Link      (_fs, _id++, "linkpathsucc1", dir2, _user, "../plainfile1");
      new Link      (_fs, _id++, "linkpathsucc2", dir2, _user, "plainfile2");

      new Link      (_fs, _id++, "linkfail1" , _user.getHomeDirectory(), _user, "dir1/dir2");
      new Link      (_fs, _id++, "linkfail2" , _user.getHomeDirectory(), _user, ".");

      new Link      (_fs, _id++, "link2linksucc1", _user.getHomeDirectory(), _user, "linksucc1");
      new Link      (_fs, _id++, "link2linkfail1" , _user.getHomeDirectory(), _user, "linkfail1");

      new PlainFile (_fs, _id++, "plainfile1", dir1, _user, "plainfile1_Data");
      new PlainFile (_fs, _id++, "plainfile2", dir2, _user, "plainfile2_Data");

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void readApp() throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "app");
    rfs.execute();
    String result = rfs.result();

    assertEquals("App data is incorrect!", "app_Data", result);
  }

  @Test
  public void readPF() throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "pf");
    rfs.execute();
    String result = rfs.result();

    assertEquals("PlainFile data is incorrect!", "pf_Data", result);
  }

  @Test
  public void readPFByPath() throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "dir1/plainfile1");
    rfs.execute();
    String result = rfs.result();

    assertEquals("PlainFile data is incorrect!", "plainfile1_Data", result);
  }

  @Test
  public void readLinkSucc1() throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "linksucc1");
    rfs.execute();
    String result = rfs.result();

    assertEquals("Link in current directory Data is incorrect!", "plainfile1_Data", result);
  }

  @Test
  public void readLinkSucc2() throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "linksucc2");
    rfs.execute();
    String result = rfs.result();

    assertEquals("Link in current directory Data is incorrect!", "plainfile2_Data", result);
  }

  @Test
  public void readLinkByRelPathSucc1() throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "dir1/dir2/linkpathsucc1");
    rfs.execute();
    String result = rfs.result();

    assertEquals("Link by relative path Data is incorrect!", "plainfile1_Data", result);
  }

  @Test
  public void readLinkByRelPathSucc2() throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "dir1/dir2/linkpathsucc2");
    rfs.execute();
    String result = rfs.result();

    assertEquals("Link by relative path Data is incorrect!", "plainfile2_Data", result);
  }

  @Test
  public void readLinkByAbsPathSucc() throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "/home/litxo/linksucc1");
    rfs.execute();
    String result = rfs.result();

    assertEquals("Link by absolute path Data is incorrect!", "plainfile1_Data", result);
  }

  @Test
  public void link2linkSucc () throws Exception {
    ReadFileService rfs = new ReadFileService(123l, "link2linksucc1");
    rfs.execute();
    String result = rfs.result();

    assertEquals("Link to another link Data is incorrect!", "plainfile1_Data", result);
  }

  @Test(expected = NotAPlainFileException.class)
    public void link2linkFail() throws Exception {
      ReadFileService rfs = new ReadFileService(123l, "link2linkfail1");
      rfs.execute();
    }


  @Test(expected = NotAPlainFileException.class)
    public void readLinkDirectoryPath() throws Exception {
      ReadFileService rfs = new ReadFileService(123l, "linkfail1");
      rfs.execute();
    }

  @Test(expected = NotAPlainFileException.class)
    public void readLinkDirectorySelf() throws Exception {
      ReadFileService rfs = new ReadFileService(123l, "linkfail2");
      rfs.execute();
    }

  @Test(expected = NotAPlainFileException.class)
    public void readDirectory() throws Exception {
      ReadFileService rfs = new ReadFileService(123l, "dir1");
      rfs.execute();
    }

  @Test(expected = NotAPlainFileException.class)
    public void readDirectoryPath() throws Exception {
      ReadFileService rfs = new ReadFileService(123l, "dir1/dir2");
      rfs.execute();
    }

}
