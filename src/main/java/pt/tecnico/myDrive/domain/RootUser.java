package pt.tecnico.myDrive.domain;

// Loggers
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;

public class RootUser extends RootUser_Base {

  private static final Logger log = LogManager.getRootLogger();

  public RootUser(FileSystem fs) {
    super.init(fs, "root", "Super User", "***", "rwxdr-x-", null);
  }

  @Override
  public String getPassword(){
    throw new MethodDeniedException();
  }

  @Override
  public void setPassword(String password){
    throw new MethodDeniedException();
  }

  @Override
  public void setUmask(String umask){
    throw new MethodDeniedException();
  }

  /**
   * Root User shouldn't be removable
   */
  public void remove() {
    log.warn("Can't remove root user!");
  }

  /**
   * Nullifies relations, that is, deletes/cancels any relation between this
   * object and eventual others.
   */
  protected void nullifyRelations() {
    setHomeDirectory(null);
    setFileSystem(null);
  }
}
