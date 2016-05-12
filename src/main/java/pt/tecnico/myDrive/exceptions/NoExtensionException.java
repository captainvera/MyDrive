package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever a file with no extension is to be executed
 * with an extension.
 */
public class NoExtensionException extends RuntimeException {

  /** The existing file's name. */
  private final String _filename;

  /**
   * @param filename the existing file's name.
   */
  public NoExtensionException(String filename) {
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
    return "File '" + getFileName() + "' has no extension";
  }
}
