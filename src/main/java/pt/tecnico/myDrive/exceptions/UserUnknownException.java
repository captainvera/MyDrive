package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to use an unknown
 * username.
 */
public class UserUnknownException extends Exception {

  /** The unknown username. */
  private final String _username;

  /**
   * @param username the unknown username.
   */
  public UserUnknownException(String username) {
    _username = username;
  }

  /**
   * @return Returns the unknown username.
   */
  public String getUsername() { return _username; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "User '" + getUsername() + "' does not exist'";
  }
}

