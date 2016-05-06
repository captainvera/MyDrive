package pt.tecnico.myDrive.service;

import org.junit.Test;
import pt.tecnico.myDrive.domain.FileSystem;
//assert
import static org.junit.Assert.*;
//service
import pt.tecnico.myDrive.services.AddVariableService;
//domain
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Directory;
public class AddVariableTest extends AbstractServiceTest {
	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */

	private FileSystem _fs;
	private User _user;
	private Login _login;
  
	@Override
	protected void populate() throws Exception{
      try{

		_fs = FileSystem.getInstance();
		_user = new User(_fs, "litxo");
    _user.setHomeDirectory(new Directory(_fs, "litxo", _fs.getHomeDirectory(), _user));

		_login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);
    

	  } catch(Exception e) {
	    e.printStackTrace();
	  }
	}

  @Test
  public void listEnvVars() throws Exception {
    _login.addEnvVar("name3","value3");
    _login.addEnvVar("name2","value2");
    _login.addEnvVar("name1","value1");
    
    String result = "name: "+ "name1" + "value: "+ "value1" + "\n" + "name: "+ "name2" + "value: "+ "value2" + "\n"+"name: "+ "name3" + "value: "+ "value3" + "\n";
    
    assertEquals("Environment Variables list is correct.",_login.listEnvVar(),result);
  }
  
  @Test
  public void simpleVariableAdd() throws Exception {
    AddVariableService avs = new AddVariableService(123l, "name", "content");
    avs.execute();
    assertEquals(_login.listEnvVar(), "name: " + "name" + "value: " + "content" + "\n");
  }
 

  @Test
  public void replaceAddedVar() throws Exception {
    AddVariableService avs = new AddVariableService(123l, "name", "content");
    avs.execute();
    avs = new AddVariableService(123l, "name", "content2");
    avs.execute();
    assertEquals( "name: " + "name" + "value: " + "content2" + "\n", _login.listEnvVar());
  }
  
}
