package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an user tries to write a Link file. 
 */
public class CannotWriteToLinkException extends Exception {

  /** The existing file's name. */
  private final String _filename;

  /**
   * @param filename the existing file's name.
   */
  public CannotWriteToLinkException(String filename) {
    _filename = filename;
  }

  /**
   * @return Returns the existing file's name.
   */
  public String getFileName() { return _filename; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "Link '" + getFileName() + "' cannot be written";
  }
}
