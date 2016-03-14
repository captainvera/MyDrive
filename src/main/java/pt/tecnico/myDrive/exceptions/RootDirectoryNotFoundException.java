package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an invalid attempt is made to a file.
 */
public class RootDirectoryNotFoundException extends Exception {
  /**
   * @param filename the file's name.
   */
  public RootDirectoryNotFoundException () {
  }


  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "The system could not find the current FileSystem's root directory.";
  }
}
