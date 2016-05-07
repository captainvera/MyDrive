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
import pt.tecnico.myDrive.domain.GuestUser;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exceptions.*;

import pt.tecnico.myDrive.visitors.PlainFileVisitor;
import pt.tecnico.myDrive.visitors.DirectoryVisitor;

import java.util.Arrays;

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

  private User _anotherOne;
  private Login _guestLogin;
  private GuestUser _guestUser;
  private long _guestToken;

  @Override
  protected void populate() {
    _fs = FileSystem.getInstance();

    _user = new User(_fs, "litxoe5sQu3nt0u", "litxoe5sQu3nt0u", "litxoe5sQu3nt0u");
    _user.setHomeDirectory(new Directory(_fs, "litxoe5sQu3nt0u", _fs.getHomeDirectory(), _user));
    _login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);

    _otherUser = new User(_fs, "leicxoe5sQu3nt0u", "leicxoe5sQu3nt0u", "leicxoe5sQu3nt0u");
    _otherUser.setHomeDirectory(_fs.getHomeDirectory());
    _loginOther = new Login(_fs, _otherUser, _otherUser.getHomeDirectory(), 124l);

    _anotherOne = new User (_fs, "swaglordus", "swaglordus", "swaglordus", "rwxdrwxd");
    _anotherOne.setHomeDirectory(new Directory(_fs, "swaglordus", _fs.getHomeDirectory(), _anotherOne));

    _guestToken = 120398l;
    _guestUser = _fs.getGuestUser();
    _guestLogin = new Login(_fs, _guestUser, _guestUser.getHomeDirectory(), _guestToken);

    new PlainFile (_fs, "pftest" , _user.getHomeDirectory(), _user, "pf_Data");
    new Directory (_fs, "dirtest", _user.getHomeDirectory(), _user);

    new Directory (_fs, "dirZ", _guestUser.getHomeDirectory(), _guestUser);
    new PlainFile (_fs, "pf", _guestUser.getHomeDirectory(), _user, "pf_Data");
  }

  public void testGuestCreatePFSuccess() throws Exception {
    _guestLogin.setCurrentDirectory(_guestUser.getHomeDirectory(), _guestUser);
    CreateFileService cfs = new CreateFileService(_guestToken, "pf", "plainfile");
    cfs.execute();

    File f = _guestUser.getHomeDirectory().getFileByName("pf");

    PlainFile pf = f.accept(new PlainFileVisitor());

    assertTrue(pf != null && pf.getData(_guestUser).equals("plainfile") && pf.getOwner().equals(_guestUser));
  }

  public void testCreateFileWithCiph() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "p$f$$$6p", "plainfile");
    cfs.execute();

    File f = _user.getHomeDirectory().getFileByName("p$f$$$6p");

    PlainFile pf = f.accept(new PlainFileVisitor());

    assertTrue(pf != null && pf.getData(_user).equals("plainfile") && pf.getOwner().equals(_user));
  }

  public void testGuestCreateDirSuccess() throws Exception {
    _guestLogin.setCurrentDirectory(_guestUser.getHomeDirectory(), _guestUser);
    CreateFileService cfs = new CreateFileService(_guestToken, "dir", "directory");
    cfs.execute();

    File f = _guestUser.getHomeDirectory().getFileByName("dir");

    Directory dir = f.accept(new DirectoryVisitor());

    assertTrue(dir != null && dir.getOwner().equals(_guestUser));
  }

  @Test(expected = UnknownTypeException.class)
  public void testFileUnknownType() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "directory", "diectory");
    cfs.execute();
  }

  @Test(expected = InvalidFilepathSizeException.class)
  public void testFilePathTooBig() throws Exception {
    char[] chars = new char[1024];
    Arrays.fill(chars, 'a');
    String text = new String(chars);
    CreateFileService cfs = new CreateFileService(123l, text, "directory");
    cfs.execute();
  }

  @Test(expected = InsufficientPermissionsException.class)
  public void testGuestCreatePF() throws Exception {
    _guestLogin.setCurrentDirectory(_anotherOne.getHomeDirectory(), _guestUser);
    CreateFileService cfs = new CreateFileService(_guestToken, "pf", "plainfile");
    cfs.execute();
  }

  @Test(expected=FileExistsException.class)
  public void createPfWithoutContent() throws Exception {
    CreateFileService cfs = new CreateFileService(123l, "pf", "plainfile");
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
      CreateFileService cfs = new CreateFileService(123l, "00000\\\\\\\0", "plainfile");
      cfs.execute();
    }

  @Test(expected = InvalidFilenameException.class)
    public void invalidFilenameWithCiph() throws Exception {
      CreateFileService cfs = new CreateFileService(123l, "$something", "plainfile");
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
