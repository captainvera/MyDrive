package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever a password entered for a user is incorrect.
 */
public class WrongPasswordException extends RuntimeException {

  /** The username whose password is wrong. */
  private final String _username;

  /**
   * @param username the username whose password is wrong.
   */
  public WrongPasswordException(String username) {
    _username = username;
  }

  /**
   * @return Returns the username.
   */
  public String getUsername() { return _username; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "Wrong password from User: '" + getUsername();
  }
}
