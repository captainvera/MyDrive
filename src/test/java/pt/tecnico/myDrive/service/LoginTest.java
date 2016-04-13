/**
 *
 */
package pt.tecnico.myDrive.service;
import org.junit.*;
import static org.junit.Assert.*;

import java.math.BigInteger;

import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.WrongPasswordException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;

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
			_user = new User(_fs, "testuser1","test1","pwd1234");
			new User(_fs, "testuser","test","pwd1234");
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@Test
	public void testLogin(){
		try {
			LoginService loginservice1 = new LoginService(_user.getUsername(), "pwd1234");
			LoginService loginservice2 = new LoginService("root", "***");

			loginservice1.execute();
			loginservice2.execute();

			long tok1 = loginservice1.result();
			long tok2 = loginservice2.result();
			
			User user1 = _fs.getUserByToken(tok1);
			User user2 = _fs.getUserByToken(tok2);
			
			assertTrue("Login user1 successful.", user1 == _user);
			assertTrue("Login user2 successful.", user2.getUsername() == "root");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Test(expected = UserUnknownException.class)
  public void userUnknown() throws Exception{
		LoginService login = new LoginService("testuser123", "pwd1234");
		login.execute();
	}

	@Test(expected = WrongPasswordException.class)
	public void wrongPassword() throws Exception{
		LoginService login = new LoginService("testuser", "pwd9876");
		login.execute();
	}
	
	@Test
	public void invalidToken() throws Exception{
		LoginService loginservice1 = new LoginService(_user.getUsername(), "pwd1234");
		loginservice1.execute();
		
		long tok = loginservice1.result();
		Login log = _fs.getLoginByToken(tok);
		
		log.reduceExpirationDate(2, 0, 0);
		assertFalse("Token is invalid after 2 hours.", _fs.isValidToken(tok));
	}

	@Test
	public void cullLogins() throws Exception{
		LoginService loginservice1 = new LoginService(_user.getUsername(), "pwd1234");
		loginservice1.execute();
		long tok = loginservice1.result();
		
		Login log = _fs.getLoginByToken(tok);
		log.reduceExpirationDate(2, 0, 0);
	
		loginservice1.execute();
		assertNull("Login has been culled.", _fs.getLoginByToken(tok));
	}
}
