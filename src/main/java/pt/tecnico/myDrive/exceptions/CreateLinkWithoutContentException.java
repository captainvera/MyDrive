package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever the logged user attempts to create a link without
 * content
 */
public class CreateLinkWithoutContentException extends RuntimeException {

  public CreateLinkWithoutContentException() {
    super("Cannot create link without content");
  }
}
