/**
 * 
 */
package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.IllegalRemovalException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;
import pt.tecnico.myDrive.exceptions.NotAPlainFileException;
import pt.tecnico.myDrive.services.DeleteFileService;
import pt.tecnico.myDrive.services.DeleteFileService;

/**
 * @author miguel
 *
 */
public class DeleteFileTest extends AbstractServiceTest {
	
	private FileSystem _fs;
  private User _user;
  private Login _login;
  private int _id;
  private Directory _dir1;
  private Directory _dir2;
  
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

      _dir1 = new Directory (_fs, _id++, "dir1", _user.getHomeDirectory(), _user);
      _dir2 = new Directory (_fs, _id++, "dir2", _dir1                    , _user);

      new App       (_fs, _id++, "app", _user.getHomeDirectory(), _user, "app_Data");
      new PlainFile (_fs, _id++, "pf" , _user.getHomeDirectory(), _user, "pf_Data");

      new Link      (_fs, _id++, "linksucc1", _user.getHomeDirectory(), _user, "dir1/plainfile1");
      new Link      (_fs, _id++, "linksucc2", _user.getHomeDirectory(), _user, "dir1/dir2/plainfile2");

      new Link      (_fs, _id++, "linkpathsucc1", _dir2, _user, "../plainfile1");
      new Link      (_fs, _id++, "linkpathsucc2", _dir2, _user, "plainfile2");

      new Link      (_fs, _id++, "linkfail1" , _user.getHomeDirectory(), _user, "dir1/dir2");
      new Link      (_fs, _id++, "linkfail2" , _user.getHomeDirectory(), _user, ".");

      new Link      (_fs, _id++, "link2linksucc1", _user.getHomeDirectory(), _user, "linksucc1");
      new Link      (_fs, _id++, "link2linkfail1" , _user.getHomeDirectory(), _user, "linkfail1");

      new PlainFile (_fs, _id++, "plainfile1", _dir1, _user, "plainfile1_Data");
      new PlainFile (_fs, _id++, "plainfile2", _dir2, _user, "plainfile2_Data");

    } catch(Exception e) {
      e.printStackTrace();
    }
  }
	
	
	@Test
  public void deleteApp() throws Exception {
		String filename = "app";
  	boolean deleted = false;
    DeleteFileService dfs = new DeleteFileService(123l, filename);
    dfs.execute();
    Directory home = _user.getHomeDirectory();
    try{
    	home.getFileByName(filename);
    }catch (FileUnknownException e){
    	if(e.getFileName() == filename)
    		deleted = true;
    }
    assertTrue("Link has been deleted!", deleted);
  }

	
  @Test
  public void deletePF() throws Exception {
  	String filename = "pf";
  	boolean deleted = false;
    DeleteFileService dfs = new DeleteFileService(123l, filename);
    dfs.execute();
    Directory home = _user.getHomeDirectory();
    try{
    	home.getFileByName(filename);
    }catch (FileUnknownException e){
    	if(e.getFileName() == filename)
    		deleted = true;
    }
    assertTrue("Link has been deleted!", deleted);
  }
  

  @Test
  public void deleteLink() throws Exception {
  	String filename = "linksucc1";
  	boolean deleted = false;
    DeleteFileService dfs = new DeleteFileService(123l,filename );
    dfs.execute();
    Directory home = _user.getHomeDirectory();
    try{
    	home.getFileByName(filename);
    }catch (FileUnknownException e){
    	if(e.getFileName() == filename)
    		deleted = true;
    }
    assertTrue("Link has been deleted!", deleted);
  } 
  
  
  @Test
  public void deleteDirectory() throws Exception {
  	String filename = "dir1";
  	boolean deleted = false;
    DeleteFileService dfs = new DeleteFileService(123l, filename);
    dfs.execute();
    Directory home = _user.getHomeDirectory();

    try{
    	home.getFileByName(filename);
    }catch (FileUnknownException e){
    	if(e.getFileName() == filename)
    		deleted = true;
    }
    assertTrue("Directory has been deleted!", deleted);
  }  
  
  
  @Test(expected = FileUnknownException.class)
  public void fileUnknownException() throws Exception {
    DeleteFileService dfs = new DeleteFileService(123l, "dir3");
    dfs.execute();
  }
  
  
  @Test(expected = IllegalRemovalException.class)
  public void illegalRemovalException() throws Exception {
    DeleteFileService dfs = new DeleteFileService(123l, ".");
    dfs.execute();
  }
  
  
  @Test(expected = IllegalRemovalException.class)
  public void illegalRemovalException1() throws Exception {
    DeleteFileService dfs = new DeleteFileService(123l, "..");
    dfs.execute();
  }
  
  
  @Test(expected = InsufficientPermissionsException.class)
  public void InsufficientPermissionsException() throws Exception {
  	_login.setCurrentDirectory(_dir1);
  	DeleteFileService dfs = new DeleteFileService(123l, "dir2");
    dfs.execute();
  }
  
}


