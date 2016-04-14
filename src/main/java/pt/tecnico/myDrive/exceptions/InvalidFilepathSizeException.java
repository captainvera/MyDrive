package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to use a filepath with
 * invalid length.
 */
public class InvalidFilepathSizeException extends RuntimeException {

  /** The maximum valid filepath size. */
  private final int _maxSize;

  /**
   * @param maxSize
   */
  public InvalidFilepathSizeException(int maxSize) {
    _maxSize = maxSize;
  }

  /**
   * @return Returns maximum valid file path size.
   */
  public int getMaxSize() { return _maxSize; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "A filepath must have atmost '" + getMaxSize() + "' characters";
  }
}
