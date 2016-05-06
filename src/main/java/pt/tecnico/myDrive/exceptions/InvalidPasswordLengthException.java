package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to create a
 * password with invalid length.
 */
public class InvalidPasswordLengthException extends RuntimeException {

  /** The maximum valid password size. */
  private final int _maxLength;

  /**
   * @param maxLength
   */
  public InvalidPasswordLengthException(int maxLength) {
    _maxLength = maxLength;
  }

  /**
   * @return Returns maximum valid file path size.
   */
  public int getMaxLength() { return _maxLength; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "A password must have atleast '" + getMaxLength() + "' characters";
  }
}

