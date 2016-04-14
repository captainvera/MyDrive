/**
 *
 */
package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;

import pt.tecnico.myDrive.services.CreateFileService;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exceptions.*;
/**
 * @author miguel
 *
 */
public class CreateFileTest extends AbstractServiceTest {

	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */
	 private FileSystem _fs;
   private User _user;
   private Login _login;

	@Override
	protected void populate() {
		// TODO Auto-generated method stub
		try{
			_fs = FileSystem.getInstance();
			_user = new User(_fs, "litxo", "litxo", "litxo");
			_user.setHomeDirectory(new Directory(_fs, _fs.requestId(),
					"litxo", _fs.getHomeDirectory(), _user));
			_login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);
			new PlainFile (_fs, 9999, "pftest" , _user.getHomeDirectory(), _user, "pf_Data");
			new Directory (_fs, 9999, "dirtest" , _user.getHomeDirectory(), _user);

		} catch(Exception e) {e.printStackTrace();}
	}
	private Boolean checkIfEqual(String name, File f){
		if(f.getName().equals(name) && f.getOwner().getUsername().equals(_user.getUsername())
		&& f.getParent().getName().equals(_user.getHomeDirectory().getName()))
			return true;

		return false;
	}

	@Test
  public void createPfWithoutContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "pf", "plaINFILE");
    cfs.execute();
		Boolean result = false;
    for(File f: _user.getHomeDirectory().getFileSet()){
			if(checkIfEqual("pf", f))
				result = true;
		}
    assertEquals("Didn't create plain file!", true, result);
  }

	@Test
  public void createDirectory() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "dir", "directory");
    cfs.execute();
		Boolean result = false;
    for(File f: _user.getHomeDirectory().getFileSet()){
			if(checkIfEqual("dir", f))
				result = true;
		}
    assertEquals("Didn't create directory!", true, result);
  }

	@Test(expected = CreateLinkWithoutContentException.class)
  public void createLinkWithoutContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "l", "link");
    cfs.execute();
  }

	@Test(expected = CreateDirectoryWithContentException.class)
  public void createDirectoryWithContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "dr", "directory", "ola");
    cfs.execute();
  }

	@Test(expected = InvalidTokenException.class)
  public void tokenInvalid() throws Exception {
    CreateFileService cfs = new CreateFileService(5555, "dr", "directory", "ola");
    cfs.execute();
  }

	@Test(expected = FileExistsException.class)
  public void fileExists() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "pftest", "plainfile", "pf_Data");
    cfs.execute();
  }

	@Test(expected = InvalidFilenameException.class)
  public void invalidFilename() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "00000@££@§@£€£", "plainfile");
    cfs.execute();
  }

	@Test(expected = InsufficientPermissionsException.class)
  public void insufficientPermissions() throws Exception {
  }

	@Test
	public void createPfWithContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "pf", "plainfilE", "im a plainfile");
    cfs.execute();
		Boolean result = false;
    for(File f: _user.getHomeDirectory().getFileSet()){
			if(checkIfEqual("pf", f) && (f instanceof PlainFile)) {
					PlainFile pf = (PlainFile) f;
					if(pf.getData().equals("im a plainfile")) result = true;
			}
		}
    assertEquals("Didn't create plain file with content!", true, result);
  }

	@Test
	public void createLinkWithContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "l", "link", "/home");
    cfs.execute();
		Boolean result = false;
    for(File f: _user.getHomeDirectory().getFileSet()){
			if(checkIfEqual("l", f) && (f instanceof Link)) {
					Link l = (Link) f;
					if(l.getData().equals("/home")) result = true;
			}
		}
    assertEquals("Didn't create plain file with content!", true, result);
  }


}
