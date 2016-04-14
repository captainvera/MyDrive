package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;

import pt.tecnico.myDrive.services.ListDirectoryService;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exceptions.NotAPlainFileException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;

public class ListDirectoryTest extends AbstractServiceTest {

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
       * |- dirapp
       *     |- app
       * |- dirpf
       *     |- pf
       * |- dirlink
       *     |- link1 -> ../dir1/dir2
       * |- dir1
       *     |- dir2
       *          |- link1 -> dir3 
       *          |- plainfile2
       *          |- dir3
       *              |- testfile
       * */

      Directory dirapp = new Directory (_fs, _id++, "dirapp", _user.getHomeDirectory(), _user);
      new App (_fs, _id++, "app", dirapp, _user, "app_Data");

      Directory dirpf = new Directory (_fs, _id++, "dirpf", _user.getHomeDirectory(), _user);
      new PlainFile (_fs, _id++, "pf" , dirpf, _user, "pf_Data");

      Directory dirlink = new Directory (_fs, _id++, "dirlink", _user.getHomeDirectory(), _user);
      new Link (_fs, _id++, "link1", dirlink, _user, "../dir1/dir2");

      Directory dir1 = new Directory (_fs, _id++, "dir1", _user.getHomeDirectory(), _user);
      Directory dir2 = new Directory (_fs, _id++, "dir2", dir1, _user);
      Directory dir3 = new Directory (_fs, _id++, "dir3", dir2, _user);
      new Link (_fs, _id++, "link", dir2, _user, "dir3");
      new PlainFile (_fs, _id++, "plainfile2", dir2, _user, "plainfile2_Data");
      new PlainFile (_fs, _id++, "testfile", dir3, _user, "testfile_Data");

    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Listing directories with only 1 kind of file
   */

  @Test
  public void listApp() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dirapp");
    lds.execute();
    String result = lds.result();

    assertEquals("List App is incorrect", "FIXME", result);
  }

  @Test
  public void listPF() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dirpf");
    lds.execute();
    String result = lds.result();

    assertEquals("List PlainFile is incorrect", "FIXME", result);
  }

  @Test
  public void listLink() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dirlink");
    lds.execute();
    String result = lds.result();

    assertEquals("List Link is incorrect", "FIXME", result);
  }

  @Test
  public void listLinkedDirectory() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dir1/dir2/link");
    lds.execute();
    String result = lds.result();

    assertEquals("List Link is incorrect", "FIXME", result);
  }

  @Test
  public void listDoubleLinkedDirectory() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dirlink/link/link1");
    lds.execute();
    String result = lds.result();

    assertEquals("List Link is incorrect", "FIXME", result);
  }

  @Test
  public void listDirectory() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dir1");
    lds.execute();
    String result = lds.result();

    assertEquals("List Directory is incorrect", "FIXME", result);
  }

  @Test
  public void listDeepDirectory() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dir1/dir2");
    lds.execute();
    String result = lds.result();

    assertEquals("List Deep Directory is incorrect", "FIXME", result);
  }

  /**
   * Not a Directory Exception 
   */

  @Test(expected = NotADirectoryException.class)
  public void appNotADirectoryException() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dirapp/app");
    lds.execute();
    String result = lds.result();
  }

  @Test(expected = NotADirectoryException.class)
  public void pfNotADirectoryException() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dirpf/pf");
    lds.execute();
    String result = lds.result();
  }
  
  @Test(expected = NotADirectoryException.class)
  public void appLinkNotADirectoryException() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "dirlink/link");
    lds.execute();
    String result = lds.result();
  }


  @Test(expected = NotADirectoryException.class)
  public void pfLinkNotADirectoryException() throws Exception { 
    ListDirectoryService lds = new ListDirectoryService(123l, "dirlink/link");
    lds.execute();
    String result = lds.result();
  }
  /**
   * File Not Found Exception
   */

  @Test(expected = FileUnknownException.class)
  public void fileNotFoundException() throws Exception {
    ListDirectoryService lds = new ListDirectoryService(123l, "inexistentfile");
    lds.execute();
    String result = lds.result();
  }
  
  /**
   * 
   */
}
