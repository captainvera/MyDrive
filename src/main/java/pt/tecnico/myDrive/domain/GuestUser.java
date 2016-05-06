package pt.tecnico.myDrive.domain;

// Loggers
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;

import org.joda.time.DateTime;

public class GuestUser extends GuestUser_Base {

  private static final Logger log = LogManager.getRootLogger();

  public GuestUser(FileSystem fs) {
    super.init(fs, "nobody", "Guest", "", "rwxdr-x-");
  }

  @Override
  protected void checkPassword(String password) {}

  @Override
  protected DateTime getNextExpirationDate() {
    return new DateTime().plusHours(Integer.MAX_VALUE);
  }

  @Override
  protected String getPermissions(File file) {
    return isOwner(file) ? file.getUserPermission() : "r-x-";
  }

  /**
   * Guest user isn't removable.
   */
  @Override
  public void remove() {
    log.warn("Can't remove guest user!");
  }
}
