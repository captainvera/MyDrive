/**
 *
 */
package pt.tecnico.myDrive.service;
import org.junit.*;
import static org.junit.Assert.*;

import java.math.BigInteger;

import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.WrongPasswordException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.InvalidPasswordLengthException;

import pt.tecnico.myDrive.services.LoginService;

/**
 * @author miguel
 *
 */
public class LoginTest extends AbstractServiceTest {

	private FileSystem _fs;
  private User _user;

	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */
	@Override
	protected void populate() {
		try{
			_fs = FileSystem.getInstance();
			_user = new User(_fs, "testuser1","test1user1","whatchumeanb6p");
			_user.setHomeDirectory(new Directory(_fs, "testuser1", _fs.getHomeDirectory(), _user));
			new User(_fs, "testuser2","testuser2","whatchumeanb6p");
		}catch (Exception e){
			e.printStackTrace();
		}

	}

  @Test
  public void testGuestUserCreation() {
    // TODO
  }

	@Test
	public void testLogin(){
    LoginService loginservice1 = new LoginService(_user.getUsername(), "whatchumeanb6p");
    LoginService loginservice2 = new LoginService("root", "***");

    loginservice1.execute();
    loginservice2.execute();

    long tok1 = loginservice1.result();
    long tok2 = loginservice2.result();

    User user1 = _fs.getUserByToken(tok1);
    User user2 = _fs.getUserByToken(tok2);

    assertTrue("Login user1 successful.", user1 == _user);
    assertTrue("Login user2 successful.", user2.getUsername() == "root");
	}


	@Test(expected = UserUnknownException.class)
  public void userUnknown() throws Exception{
		LoginService login = new LoginService("testuser123", "whatchumeanb6p");
		login.execute();
	}

	@Test(expected = WrongPasswordException.class)
	public void wrongPassword() throws Exception{
		LoginService login = new LoginService("testuser2", "pwd9876");
		login.execute();
	}

	@Test
	public void invalidToken() throws Exception{
		LoginService loginservice1 = new LoginService(_user.getUsername(), "whatchumeanb6p");
		loginservice1.execute();

		long tok = loginservice1.result();
		Login log = _fs.getLoginByToken(tok);

		log.reduceExpirationDate(2, 0, 0);
		assertFalse("Token is invalid after 2 hours.", _fs.isValidToken(tok));
	}

	@Test
	public void cullLogins() throws Exception{
		LoginService loginservice1 = new LoginService(_user.getUsername(), "whatchumeanb6p");
		loginservice1.execute();
		long tok = loginservice1.result();

		Login log = _fs.getLoginByToken(tok);
		log.reduceExpirationDate(2, 0, 0);

		loginservice1.execute();
		assertNull("Login has been culled.", _fs.getLoginByToken(tok));
	}

  @Test (expected = InvalidPasswordLengthException.class)
    public void testInvalidPassword() throws Exception {
      User julio = new User(_fs, "hulio", "julio", "undrszd");
    }
}
