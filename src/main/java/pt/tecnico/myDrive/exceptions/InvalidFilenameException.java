package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt to use an invalid filename
 */
public class InvalidFilenameException extends RuntimeException {

  /** The file's name. */
  private final String _filename;

  /**
   * @param file
   */
  public InvalidFilenameException(String filename) {
    _filename = filename;
  }

  /**
   * @return Returns the file's name.
   */
  public String getFilename() { return _filename; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "Filename '" + getFilename() + "' is invalid";
  }
}


