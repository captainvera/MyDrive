package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to perform an operation
 * that requires the absence of a file, but the file already exists.
 */
public class FileExistsException extends Exception {

  /** The existing file's name. */
  private final String _filename;

  /**
   * @param filename the existing file's name.
   */
  public FileExistsException(String filename) {
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
    return "File '" + getFileName() + "' already exists";
  }
}
