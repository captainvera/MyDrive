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

  /**
   * Root User shouldn't be removable
   */
  @Override
  public void remove() {
    log.warn("Can't remove root user!");
  }

}
