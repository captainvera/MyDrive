package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to perform a creation
 * of a user with an username of invalid length.
 */
public class InvalidFilenameException extends Exception {

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


