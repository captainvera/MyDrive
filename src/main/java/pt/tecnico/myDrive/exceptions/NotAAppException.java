package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an invalid attempt is made to an app.
 */
public class NotAAppException extends Exception {

  /** The app's name. */
  private final String _filename;

  /**
   * @param filename the app's name.
   */
  public NotAAppException(String filename) {
    _filename = filename;
  }

  /**
   * @return Returns the app's name.
   */
  public String getFileName() { return _filename; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "File '" + getFileName() + "' is not an app";
  }
}
