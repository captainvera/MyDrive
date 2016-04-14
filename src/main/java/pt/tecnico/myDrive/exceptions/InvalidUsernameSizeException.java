package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to create an user with
 * an username with invalid length.
 */
public class InvalidUsernameSizeException extends RuntimeException {

  /** The minimum valid username size. */
  private final int _minSize;

  /**
   * @param minSize
   */
  public InvalidUsernameSizeException(int minSize) {
    _minSize = minSize;
  }

  /**
   * @return Returns minimum valid username size.
   */
  public int getMinSize() { return _minSize; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "An username must have atleast '" + getMinSize() + "' characters";
  }
}
