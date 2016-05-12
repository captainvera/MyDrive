package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;
import org.apache.commons.lang.StringUtils;
import mockit.*;

import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exceptions.*;
import pt.tecnico.myDrive.services.*;

import pt.tecnico.myDrive.visitors.PlainFileVisitor;

import java.util.*;

public class EnvironmentLinksTest extends AbstractServiceTest {

  private FileSystem _fs;
  private User _user1;
  private User _user2;
  private PlainFile _pf;
  private App _app;
  private Login _login;
  private String args[] = {"1","2"};


  /* (non-Javadoc)
  * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
  */
  @Override
  protected void populate() {
    try{

      _fs = FileSystem.getInstance();
      _user1 = new User(_fs, "user8888", "user8888", "user8888");
      _user2 = new User(_fs, "user9999", "user9999", "user9999");
      _user1.setHomeDirectory(new Directory(_fs, "user8888", _fs.getHomeDirectory(), _user1));

      System.out.println(_user1.getHomeDirectory().getPath());

      _pf = new PlainFile (_fs, "pf" , _user1.getHomeDirectory(), _user1, "pf_Data");
      _app = new App (_fs, "app", _user1.getHomeDirectory(), _user1, "pt.tecnico.myDrive.domain.FileSystem.apptest");

      _login = new Login(_fs, _user1, _user1.getHomeDirectory(), 123l);


    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void changeDirectory() {

    new MockUp<FileSystem> () {
      @Mock public ArrayList<String> processEnvVars(String token){
        ArrayList<String> a = new ArrayList<String>();
        a.add("user8888");
        return a;
      }
    };

    ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/home/$HOME");

    changeDirectory.execute();
    String result = changeDirectory.result();

    assertEquals("Directory doesn't match expected", _user1.getHomeDirectory().getPath(), result);
  }

 /*
  * TODO
  *

  @Test
  public void createFile() {

    new MockUp<FileSystem> () {
      @Mock public ArrayList<String> processEnvVars(String token){
        ArrayList<String> a = new ArrayList<String>();
        a.add("test");
        return a;
      }
    };

    CreateFileService cf = new CreateFileService(123l, "/home/user8888/$FILE", "plainfile", "ola");

    cf.execute();

    File f = _user1.getHomeDirectory().getFileByName("test");

    PlainFile plain = f.accept(new PlainFileVisitor());

    assertTrue(plain != null && plain.getData(_user1).equals("ola") && plain.getOwner().equals(_user1));
  }

  */

  /*
   * TODO
   *
  @Test
  public void deleteFile() {

    new MockUp<FileSystem> () {
      @Mock public ArrayList<String> processEnvVars(String token){
        ArrayList<String> a = new ArrayList<String>();
        a.add("pf");
        return a;
      }
    };

    boolean deleted = false;

    DeleteFileService df = new DeleteFileService(123l, "$FILE");

    df.execute();

    try {
    	_user1.getHomeDirectory().getFileByName("pf");
    } catch (FileUnknownException e) {
    	if(e.getFileName().equals("pf"))
    		deleted = true;
    }
    assertTrue("File has been deleted!", deleted);
  }

  */

  @Test
  public void executeFile() {

    new MockUp<FileSystem> () {
      @Mock public ArrayList<String> processEnvVars(String token){
        ArrayList<String> a = new ArrayList<String>();
        a.add("app");
        return a;
      }
    };

    ExecuteFileService ef = new ExecuteFileService(123l, "/home/user8888/$APP", args);

    ef.execute();

    new Verifications(){
        {
            _fs.apptest(args);
        }
    };
  }

  @Test
  public void listDirectory() {

    new MockUp<FileSystem> () {
      @Mock public ArrayList<String> processEnvVars(String token){
        ArrayList<String> a = new ArrayList<String>();
        a.add("user8888");
        return a;
      }
    };

    ListDirectoryService ld = new ListDirectoryService(123l, "/home/$DIR");

    ld.execute();
    String result = ld.result();

    String self = ((String) _user1.getHomeDirectory().toString().replaceAll(_user1.getHomeDirectory().getName(), ".") + "\n");
		String parent = ((String) _user1.getHomeDirectory().getParent().toString().replaceAll(_user1.getHomeDirectory().getParent().getName(), "..") + "\n");
    String app = _app.toString() + "\n";
    String plainfile = _pf.toString() + "\n";
		String list = self + parent + app + plainfile;

    System.out.println(list);
    System.out.println("aqui");
    System.out.println(result);

    assertTrue("List directories", list.equals(result));
  }

  @Test
  public void readFile() {

    new MockUp<FileSystem> () {
      @Mock public ArrayList<String> processEnvVars(String token){
        ArrayList<String> a = new ArrayList<String>();
        a.add("pf");
        return a;
      }
    };

    ReadFileService rf = new ReadFileService(123l, "/home/user8888/$FILE");

    rf.execute();
    String result = rf.result();

    assertEquals("PlainFile data is incorrect!", "pf_Data", result);
  }

  @Test
  public void writeFile() {

    new MockUp<FileSystem> () {
      @Mock public ArrayList<String> processEnvVars(String token){
        ArrayList<String> a = new ArrayList<String>();
        a.add("pf");
        return a;
      }
    };

    WriteFileService wf = new WriteFileService(123l, "/home/user8888/$FILE", "test");

    wf.execute();

    assertEquals("PlainFile data is incorrect!", "test", _pf.getData(_user1));
  }
}
