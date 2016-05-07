package pt.tecnico.myDrive.domain;

// Loggers
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;

import org.joda.time.DateTime;

public class RootUser extends RootUser_Base {

  private static final Logger log = LogManager.getRootLogger();

  public RootUser(FileSystem fs) {
    super.init(fs, "root", "Super User", "***", "rwxdr-x-", null);
  }

  @Override
  protected void checkPassword(String password) {}

  @Override
  protected DateTime getNextExpirationDate() {
    return new DateTime().plusMinutes(10);
  }

  /**
   * Root user can perform any operation
   */
  @Override
  protected String getPermissions(File file) {
    return "rwxd";
  }

  /**
   * Root User shouldn't be removable
   */
  @Override
  public void remove() {
    log.warn("Can't remove root user!");
  }

}
