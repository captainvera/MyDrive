package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.User;

// Loggers
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RootDirectory extends RootDirectory_Base {

  private static final Logger log = LogManager.getRootLogger();

  /** Placeholder for FenixFramework */
  public RootDirectory() {
    super();
  }

  public RootDirectory(FileSystem fs, Integer id, String name, User owner) {
    init(fs, id, name, this, owner);
  }

  /**
   * The parent of the root directory should actually be itself, but since
   * FenixFramework adds objects to both sides of the relation, and we don't
   * want that, this should do it.
   */

  @Override
  public void setParent(Directory parent){
    /* do nothing */
  }

  @Override
  public Directory getParent() { return this; }

  @Override
  public void remove() {
    log.warn("Can't remove root directory!");
    log.warn("Removing all other files...");
    removeAllFiles();
  }

}
