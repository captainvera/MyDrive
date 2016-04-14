package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to perform an operation
 * that requires an existing file, but the file does not exist.
 */
public class FileUnknownException extends RuntimeException {

  /** The unknown file's name. */
  private final String _filename;

  /**
   * @param filename the unknown file's name.
   */
  public FileUnknownException(String filename) {
    _filename = filename;
  }

  /**
   * @return Returns the unknown file's name.
   */
  public String getFileName() { return _filename; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "File '" + getFileName() + "' does not exist";
  }
}

