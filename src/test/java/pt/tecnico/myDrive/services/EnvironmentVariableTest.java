package pt.tecnico.myDrive.service;

import org.junit.Test;
import pt.tecnico.myDrive.domain.FileSystem;
//assert
import static org.junit.Assert.*;
//service
import pt.tecnico.myDrive.services.EnvironmentVariableService;
//domain
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.service.AbstractServiceTest;
import java.util.List;
import java.util.ArrayList;
import pt.tecnico.myDrive.services.dto.EnvironmentVariableDTO;

public class EnvironmentVariableTest extends AbstractServiceTest {
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
		_user = new User(_fs, "user8888", "user8888", "user8888");
    _user.setHomeDirectory(new Directory(_fs, "user8888", _fs.getHomeDirectory(), _user));

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

    List<EnvironmentVariableDTO> result = new ArrayList<EnvironmentVariableDTO>();
		result.add(new EnvironmentVariableDTO("name1", "value1"));
		result.add(new EnvironmentVariableDTO("name2", "value2"));
    result.add(new EnvironmentVariableDTO("name3", "value3"));
		System.out.println("look12" + result.get(0)._name + "\n" + _login.listEnvVar().get(0)._name);

    assertTrue("Environment Variables list is correct.",_login.listEnvVar().equals(result));
  }

  @Test
  public void simpleVariableAdd() throws Exception {
    EnvironmentVariableService avs = new EnvironmentVariableService(123l, "name", "content");
    avs.execute();
		List<EnvironmentVariableDTO> result = new ArrayList<EnvironmentVariableDTO>();
    result.add(new EnvironmentVariableDTO("name","content"));
    assertTrue(avs.result().equals(result));
  }


  @Test
  public void replaceAddedVar() throws Exception {
    EnvironmentVariableService avs = new EnvironmentVariableService(123l, "name", "content");
    avs.execute();
    avs = new EnvironmentVariableService(123l, "name", "content2");
    avs.execute();

		List<EnvironmentVariableDTO> result = new ArrayList<EnvironmentVariableDTO>();
		result.add(new EnvironmentVariableDTO("name","content2"));

    assertTrue( avs.result().equals(result) );
  }

}
