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
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

public class ListDirectoryTest extends AbstractServiceTest {

  private FileSystem _fs;
  private User _user;
  private User _user2;
  private Login _login;
  private Directory _dirdirectories;
  private Directory _dirall;
  private Directory _dirapppf;
  private Directory _dirapplink;
  private Directory _dirpflink;
  private Directory _dirapp;
  private Directory _dirpf;
  private Directory _dirlink;

  /* (non-Javadoc)
   * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
   */
  @Override
  protected void populate() {
    try {
      _fs = FileSystem.getInstance();
      _user = new User(_fs, "litxo", "litxo", "litxo");
      _user.setHomeDirectory(new Directory(_fs, "litxo", _fs.getHomeDirectory(), _user));
      _user2 = new User(_fs, "user2", "user2", "litxo");
      _user2.setHomeDirectory(new Directory(_fs, "user2", _fs.getHomeDirectory(), _user));
      _login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);

      /* We'll have something like this
       * |- dirapp
       *     |- app
       * |- dirpf
       *     |- pf
       * |- dirlink
       *     |- link1 -> ../dirdirectories/dirdall
       * |- dirdirectories
       *     |- dir4
       *     |- dirdall
       *          |- link1 -> dir3
       *          |- plainfile2
       *          |- dir3
       *              |- testfile
       * */

      _dirapp = new Directory (_fs, "dirapp", _user.getHomeDirectory(), _user);
      new App (_fs, "app", _dirapp, _user, "app_Data");

      _dirpf = new Directory (_fs, "dirpf", _user.getHomeDirectory(), _user);
      new PlainFile (_fs, "pf" , _dirpf, _user, "pf_Data");

      _dirlink = new Directory (_fs, "dirlink", _user.getHomeDirectory(), _user);
      new Link (_fs, "link1", _dirlink, _user, "../dirdirectories/dirdall");

      _dirdirectories = new Directory (_fs, "dirdirectories", _user.getHomeDirectory(), _user);
      _dirall = new Directory (_fs, "dirdall", _dirdirectories, _user);
      _dirapplink = new Directory (_fs, "dir4", _dirdirectories, _user);
      _dirpflink = new Directory (_fs, "dirpflink", _dirdirectories, _user);

      /*
       * adding all tipes of files to _dirall
       */
      _dirapppf = new Directory (_fs, "dir3", _dirall, _user);
      new App (_fs, "app", _dirall, _user, "app_Data");
      new PlainFile (_fs, "pf" , _dirall, _user, "pf_Data");
      new Link (_fs, "link1", _dirall, _user, "../dirdirectories/dirdall");

      /*
       * adding app and plainfile to _dirapppf
       */
      new App (_fs, "app", _dirapppf, _user, "app_Data");
      new PlainFile (_fs, "pf" , _dirapppf, _user, "pf_Data");

      /*
       * adding app and plainfile to _dirapplink
       */
      new App (_fs, "app", _dirapppf, _user, "app_Data");
      new Link (_fs, "link", _dirall, _user, "../dirdirectories/dirdall");

      /*
       * adding pf and link to _dirpflink
       */
      new PlainFile (_fs, "pf" , _dirapppf, _user, "pf_Data");
      new Link (_fs, "link", _dirall, _user, "../dirdirectories/dirdall");

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Listing directories with only 1 kind of file
   */

  @Test
  public void listCurrent() throws Exception {
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List user1", _user.getHomeDirectory().listFilesSimple(), result);
  }

  @Test
  public void listdirdirectories() throws Exception {
  	_login.setCurrentDirectory(_dirdirectories, _user);
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List dirdirectories", _dirdirectories.listFilesSimple(), result);
  }

  @Test
  public void listdirall() throws Exception {
  	_login.setCurrentDirectory(_dirall, _user);
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List dirall", _dirall.listFilesSimple() , result);
  }

  @Test
  public void listdirapp() throws Exception {
  	_login.setCurrentDirectory(_dirapp, _user);
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List dirapp", _dirapp.listFilesSimple() , result);
  }

  @Test
  public void listdirpf() throws Exception {
  	_login.setCurrentDirectory(_dirpf, _user);
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List dirapp", _dirpf.listFilesSimple() , result);
  }

  @Test
  public void listdirlink() throws Exception {
  	_login.setCurrentDirectory(_dirlink, _user);
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List dirapp", _dirlink.listFilesSimple() , result);
  }
  @Test
  public void listdirapppf() throws Exception {
  	_login.setCurrentDirectory(_dirapppf, _user);
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List dirdapppf", _dirapppf.listFilesSimple() , result);
  }

  @Test
  public void listdirapplink() throws Exception {
  	_login.setCurrentDirectory(_dirapplink, _user);
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List dirdapplink", _dirapplink.listFilesSimple() , result);
  }

  @Test
  public void listdirpflink() throws Exception {
  	_login.setCurrentDirectory(_dirpflink, _user);
  	ListDirectoryService lds = new ListDirectoryService(123l);
  	lds.execute();
  	String result = lds.result();

  	assertEquals("List dirpflink", _dirpflink.listFilesSimple() , result);
  }


}
