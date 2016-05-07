package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to create
 * a file with an unknown type
 */
public class UnknownTypeException extends RuntimeException {

  /** The unknown typename. */
  private String _typename;

  public UnknownTypeException(String typename) {
    _typename = typename;
  }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "The type specified " + _typename + " is unknown";
  }
}


