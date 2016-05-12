
/**
 *
 */
package pt.tecnico.myDrive.service;
import org.junit.*;
import static org.junit.Assert.*;
import org.apache.commons.lang.StringUtils;
import java.math.BigInteger;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.services.LogoutService;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.WrongPasswordException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.InvalidPasswordLengthException;

import pt.tecnico.myDrive.services.LogoutService;

/**
 * @author miguel
 *
 */
public class LogoutTest extends AbstractServiceTest {

	private FileSystem _fs;
  private User _user;
  private User _user2;

	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */
	@Override
	protected void populate() {
		try{
			_fs = FileSystem.getInstance();
			_user = new User(_fs, "testuser1","test1user1","whatchumeanb6p");
			_user.setHomeDirectory(new Directory(_fs, "testuser1", _fs.getHomeDirectory(), _user));
			_user2 = new User(_fs, "testuser2","test1user2","whatchumeanb6p");
			_user.setHomeDirectory(new Directory(_fs, "testuser2", _fs.getHomeDirectory(), _user));
      new Login(_fs, _user, _user.getHomeDirectory(), 123l);
      new Login(_fs, _user2, _user2.getHomeDirectory(), 124l);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@Test
	public void testLogout(){
    long tok1 = 123l;
    long tok2 = 124l;

    LogoutService logoutservice1 = new LogoutService(tok1);
    LogoutService logoutservice2 = new LogoutService(tok2);


    User user1 = _fs.getUserByToken(tok1);
    User user2 = _fs.getUserByToken(tok2);

    logoutservice1.execute();
    logoutservice2.execute();

    User user3 = _fs.getUserByToken(tok1);
    User user4 = _fs.getUserByToken(tok2);

    assertTrue("Logout user1 successful.", user1 == _user && user3 == null);
    assertTrue("Logout user2 successful.", user2 == _user2 && user4 == null);
	}

	@Test(expected=InvalidTokenException.class)
	public void invalidToken() throws Exception{
		LogoutService logoutservice1 = new LogoutService(125l);
		logoutservice1.execute();
	}

}
