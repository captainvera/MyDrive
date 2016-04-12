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
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.WrongPasswordException;

import pt.tecnico.myDrive.services.LoginService;

/**
 * @author miguel
 *
 */
public class LoginTest extends AbstractServiceTest {

	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */
	@Override
	protected void populate() {
		try{
			FileSystem fs = FileSystem.getInstance();
			fs.createUser("testuser","test","pwd1234");

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@Test
	public void testLogin(){
		try {
			LoginService login1 = new LoginService("testuser", "pwd1234");
			LoginService login2 = new LoginService("root", "***");

			login1.execute();
			login2.execute();

			long tok1 = login1.result();
			long tok2 = login2.result();

			BigInteger token1 = BigInteger.valueOf(tok1);
			BigInteger token2 = BigInteger.valueOf(tok2);

			assertTrue("Login utilizador 1 bem sucedido.",  tok1 != 0);
			assertTrue("Login utilizador 2 bem sucedido.", tok2 != 0);
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

}
