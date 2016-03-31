package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to perform an operation
 * that requires a non existent user, but the user already exists.
 */
public class InvalidFilenameException extends Exception {

  /** The user's username. */
  private final String _filename;

  /**
   * @param username the user's username.
   */
  public InvalidFilenameException(String filename) {
    _filename = filename;
  }

  /**
   * @return Returns the user's username.
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


