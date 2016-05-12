package pt.tecnico.myDrive.system; 
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.ArrayList;

import pt.tecnico.myDrive.domain.FileSystem; // Mockup
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.EnvironmentVariable;
import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.tecnico.myDrive.services.*;
import pt.tecnico.myDrive.services.dto.*;
import pt.tecnico.myDrive.exceptions.*;
import pt.tecnico.myDrive.visitors.PlainFileVisitor;
import pt.tecnico.myDrive.domain.Login;

public class IntegrationTest extends AbstractServiceTest {

  @Mocked EnvironmentVariableService ev;
  @Mocked ExecuteFileService efs;

  private User _user;
  private FileSystem _fs; 



  protected void populate() { // populate mockup

    _fs = FileSystem.getInstance();
    _user = new User(_fs, "testuser1","test1user1","whatchumeanb6p");
    _user.setHomeDirectory(new Directory(_fs, "testuser1", _fs.getHomeDirectory(), _user));

  }

  @Test
  public void success(){

    LoginService login = new LoginService("testuser1", "whatchumeanb6p");
    login.execute();
    long token1 = login.result();
    String args[] = {};

    new MockUp<Login>(){
      @Mock public EnvironmentVariable getEnvVarbyName(String anyString){
        return new EnvironmentVariable("envvar", "/home/testuser1");
      }
    };


    new NonStrictExpectations(){{

      new ExecuteFileService(token1, "plainfile", args);
      efs.execute();

      new EnvironmentVariableService(token1, "envvar", anyString).execute();
    }}; 


    String content = "content";
    new CreateFileService(token1, "plainfile", "plainfile",content).execute();

    PlainFileVisitor pfv = new PlainFileVisitor();
    PlainFile pf = _user.getHomeDirectory().getFileByName("plainfile").accept(pfv);
    assertTrue("App data is incorrect!", pf.getData(_user).equals(content) );


    ExecuteFileService exe = new ExecuteFileService(token1, "plainfile",args);
    exe.execute();

    new WriteFileService(token1, "plainfile", "things").execute();
    assertTrue("App data is incorrect!", pf.getData(_user).equals("things") );


    new CreateFileService(token1, "directory", "directory").execute();
    ChangeDirectoryService dir = new ChangeDirectoryService(token1, "directory");
    dir.execute();
    String path1 = dir.result();
    assertTrue(path1.equals( "/home/testuser1/directory"));

    dir = new ChangeDirectoryService(token1, "..");
    dir.execute();
    String path2 = dir.result();
    new CreateFileService(token1, "link", "link",path1).execute();
    ListDirectoryService list = new ListDirectoryService(token1);
    list.execute();
    assertTrue(list.result().equals(_user.getHomeDirectory().listFilesAll(_user)));


    dir = new ChangeDirectoryService(token1, "link");
    dir.execute();
    assertTrue(dir.result().equals(path1));

    new EnvironmentVariableService(token1, "envvar", path2).execute();
    assertTrue(_fs.getLoginByToken(token1).getEnvVarbyName("envvar").getValue().equals(path2));

    dir = new ChangeDirectoryService(token1, "..");
    dir.execute();

    new DeleteFileService(token1, "directory").execute();
    list = new ListDirectoryService(token1);
    list.execute();

    assertTrue(list.result().equals(_user.getHomeDirectory().listFilesAll(_user)));
  }
}
