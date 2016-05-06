package pt.tecnico.myDrive.domain;

// Loggers
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;

public class GuestUser extends GuestUser_Base {

  private static final Logger log = LogManager.getRootLogger();

  public GuestUser(FileSystem fs) {
    // TODO: Password?
    super.init(fs, "nobody", "Guest", "passworddddd", "rwxdr-x-");
  }


  /**
   * Guest user isn't removable.
   */
  @Override
  public void remove() {
    log.warn("Can't remove guest user!");
  }
}
