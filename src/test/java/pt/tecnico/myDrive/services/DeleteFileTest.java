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
import pt.tecnico.myDrive.domain.GuestUser;
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
  private User _otherUser;
  private User _anotherOne;
  private Login _login;
  private Directory _dir1;
  private Directory _dir2;

  private Login _guestLogin;
  private GuestUser _guestUser;
  private long _guestToken;

	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */
	@Override
	protected void populate() {
		try {
      _fs = FileSystem.getInstance();
      _user = new User(_fs, "litxoe5sQu3nt0u", "litxoe5sQu3nt0u", "litxoe5sQu3nt0u");
			_user.setHomeDirectory(new Directory(_fs, "litxoe5sQu3nt0u", _fs.getHomeDirectory(), _user));
      _login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);

      // Only for permission testing
      _otherUser  = new User (_fs, "verixoe5sQu3nt0u", "verixoe5sQu3nt0u", "verixoe5sQu3nt0u");
      _anotherOne = new User (_fs, "swaglordus", "swaglordus", "swaglordus", "rwxdrwxd");
      _anotherOne.setHomeDirectory(new Directory(_fs, "swaglordus", _fs.getHomeDirectory(), _anotherOne));

      _guestToken = 120398l;
      _guestUser = _fs.getGuestUser();
      _guestLogin = new Login(_fs, _guestUser, _guestUser.getHomeDirectory(), _guestToken);

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

      _dir1 = new Directory (_fs, "dir1", _user.getHomeDirectory(), _user);
      _dir2 = new Directory (_fs, "dir2", _dir1                   , _otherUser);

      new App       (_fs, "app", _user.getHomeDirectory(), _user, "app_Data");
      new PlainFile (_fs, "pf" , _user.getHomeDirectory(), _user, "pf_Data");

      new Link      (_fs, "linksucc1", _user.getHomeDirectory(), _user, "dir1/plainfile1");
      new Link      (_fs, "linksucc2", _user.getHomeDirectory(), _user, "dir1/dir2/plainfile2");

      new Link      (_fs, "linkpathsucc1", _dir2, _user, "../plainfile1");
      new Link      (_fs, "linkpathsucc2", _dir2, _user, "plainfile2");

      new Link      (_fs, "linkfail1" , _user.getHomeDirectory(), _user, "dir1/dir2");
      new Link      (_fs, "linkfail2" , _user.getHomeDirectory(), _user, ".");

      new Link      (_fs, "link2linksucc1", _user.getHomeDirectory(), _user, "linksucc1");
      new Link      (_fs, "link2linkfail1" , _user.getHomeDirectory(), _user, "linkfail1");

      new PlainFile (_fs, "plainfile1", _dir1, _user, "plainfile1_Data");
      new PlainFile (_fs, "plainfile2", _dir2, _user, "plainfile2_Data");

      new PlainFile (_fs, "plainfile3", _anotherOne.getHomeDirectory(), _anotherOne, "plainfile3_Data");
      new PlainFile (_fs, "plainfile4", _guestUser.getHomeDirectory(), _guestUser, "plainfile3_Data");

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
    try {
    	home.getFileByName(filename);
    } catch (FileUnknownException e) {
    	if(e.getFileName().equals(filename))
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
    try {
    	home.getFileByName(filename);
    } catch (FileUnknownException e) {
    	if(e.getFileName().equals(filename))
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
    try {
    	home.getFileByName(filename);
    } catch (FileUnknownException e) {
    	if(e.getFileName().equals(filename))
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

    try {
    	home.getFileByName(filename);
    } catch (FileUnknownException e) {
    	if(e.getFileName().equals(filename))
    		deleted = true;
    }
    assertTrue("Directory has been deleted!", deleted);
  }

  @Test
  public void testGuestDeletionSuccess() throws Exception {
    boolean deleted = false;

    _guestLogin.setCurrentDirectory(_guestUser.getHomeDirectory(), _guestUser);

    DeleteFileService dfs = new DeleteFileService(_guestToken, "plainfile4");
    dfs.execute();

    try {
    	_guestLogin.getCurrentDirectory().getFileByName("plainfile4");
    } catch (FileUnknownException e) {
    	if(e.getFileName().equals("plainfile4"))
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
  	_login.setCurrentDirectory(_dir1, _user);
  	DeleteFileService dfs = new DeleteFileService(123l, "dir2");
    dfs.execute();
  }

  @Test(expected = InsufficientPermissionsException.class)
    public void testGuestDeletionOtherOwner() throws Exception {
      _guestLogin.setCurrentDirectory(_anotherOne.getHomeDirectory(), _guestUser);

      DeleteFileService dfs = new DeleteFileService(_guestToken, "plainfile3");
      dfs.execute();
    }

}


