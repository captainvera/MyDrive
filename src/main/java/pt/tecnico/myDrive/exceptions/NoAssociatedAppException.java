package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown when there is not an associated app to an extension 
 */
public class NoAssociatedAppException extends RuntimeException {

  /** The existing file's name. */
  private final String _extension;

  /**
   * @param filename the existing file's name.
   */
  public NoAssociatedAppException(String extension) {
      _extension = extension;
  }

  /**
   * @return Returns the existing file's name.
   */
  public String getExtensionName() { return _extension; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "Extension" + getExtensionName() + "' has no associated app";
  }
}
