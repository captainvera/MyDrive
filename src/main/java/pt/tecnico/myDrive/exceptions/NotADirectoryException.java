package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an invalid attempt is made to a file.
 */
public class NotADirectoryException extends RuntimeException {

  /** The file's name. */
  private final String _filename;

  /**
   * @param filename the file's name.
   */
  public NotADirectoryException(String filename) {
    _filename = filename;
  }

  /**
   * @return Returns the file's name.
   */
  public String getFileName() { return _filename; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "File '" + getFileName() + "' is not a directory";
  }
}
