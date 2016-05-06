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

import org.apache.commons.lang3.StringUtils;
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
  private User _otherUser;
  private Login _login;
  private Login _loginOther;

  @Override
  protected void populate() {
    _fs = FileSystem.getInstance();

    _user = new User(_fs, "litxo", "litxo", "litxo");
    _user.setHomeDirectory(new Directory(_fs, "litxo", _fs.getHomeDirectory(), _user));
    _login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);

    _otherUser = new User(_fs, "leicxo", "leicxo", "leicxo");
    _otherUser.setHomeDirectory(_fs.getHomeDirectory());
    _loginOther = new Login(_fs, _otherUser, _otherUser.getHomeDirectory(), 124l);

    new PlainFile (_fs, "pftest" , _user.getHomeDirectory(), _user, "pf_Data");
    new Directory (_fs, "dirtest" , _user.getHomeDirectory(), _user);
  }

  @Test(expected=FileExistsException.class)
  public void createPfWithoutContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "pf", "plaINFILE");
    cfs.execute();
    cfs.execute();
  }

  @Test(expected=FileExistsException.class)
  public void createDirectory() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "dir", "directory");
    cfs.execute();
    cfs.execute();
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
      CreateFileService cfs = new CreateFileService(124l, "pf", "plainfile");
      cfs.execute();
    }

  @Test(expected=FileExistsException.class)
  public void createPfWithContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "pf", "plainfilE", "im a plainfile");
    cfs.execute();
    cfs.execute();
  }

  @Test(expected=FileExistsException.class)
  public void createLinkWithContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "l", "link", "/home");
    cfs.execute();
    cfs.execute();
  }

  @Test(expected=InvalidFilepathSizeException.class)
  public void createFileWithHugeName() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, StringUtils.leftPad("test", 1025, 'a'), "link", "/home");
    cfs.execute();
  }

}
