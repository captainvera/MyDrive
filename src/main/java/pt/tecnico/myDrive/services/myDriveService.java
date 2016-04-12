
package pt.tecnico.myDrive.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import pt.tecnico.myDrive.domain.User;
//import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.ist.fenixframework.Atomic;

public abstract class myDriveService {
  protected static final Logger log = LogManager.getRootLogger();

  public myDriveService() {
  }

  static FileSystem getFileSystem(){
    return FileSystem.getInstance();
  }

  @Atomic
  public final void execute() throws Exception {
    dispatch();
  }

  protected boolean updateSession(long token){
    return getFileSystem().updateSession(token);
  }

  protected abstract void dispatch() throws Exception;

}
