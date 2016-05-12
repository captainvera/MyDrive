package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;

import pt.tecnico.myDrive.exceptions.ImportDocumentException;
import pt.tecnico.myDrive.exceptions.MethodDeniedException;
import pt.tecnico.myDrive.exceptions.InvalidUsernameSizeException;
import pt.tecnico.myDrive.exceptions.InvalidUsernameException;
import pt.tecnico.myDrive.exceptions.InvalidPasswordLengthException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

import pt.tecnico.myDrive.domain.FileSystem;

import org.joda.time.DateTime;

public class User extends User_Base {

  /** Placeholder for FenixFramework */
  public User() {
    super();
  }

  public User(FileSystem fs, String username) {
  	init(fs, username, username, username, "rwxd----");
  }

  public User(FileSystem fs, String username, String name, String password) {
    init(fs, username, name, password, "rwxd----");
  }

  public User(FileSystem fs, String username, String name, String password, String umask) {
    init(fs, username, name, password, umask);
  }


  public User(FileSystem fs, String username, String name, String password, String umask, Directory homeDir) {
    init(fs, username, name, password, umask, homeDir);
  }

  protected void init(FileSystem fs, String username, String name, String password, String umask, Directory homeDir) {
    checkUsernameSize(username);
    checkPassword(password);

    super.setFileSystem(fs);
    super.setUsername(username);
    super.setName(name);
    super.setPassword(password);
    super.setUmask(umask);
    super.setHomeDirectory(homeDir);
  }

  protected void init(FileSystem fs, String username, String name, String password, String umask) {
    init(fs, username, name, password, umask, null);
  }

  /**
   * Checks for invalid characters in username
   */
  private void checkUsername(String username) {
    checkUsernameSize(username);

    char[] characters = username.toCharArray();

    for (char c: characters) {
      if (!Character.isLetter(c) && !Character.isDigit(c)) {
        throw new InvalidUsernameException(username);
      }
    }
  }

  /**
   * Verifies if username has atleast 3 characters
   * @param username
   */
  private void checkUsernameSize(String username) {
    if(username.length() < 3) throw new InvalidUsernameSizeException(3);
  }

  public boolean isOwner(File file) {
    return file.getOwner().equals(this);
  }

  protected void checkPassword(String password) {
    if (password.length() < 8) throw new InvalidPasswordLengthException(8);
  }

  public boolean verifyPassword(String password){
    return password.equals(super.getPassword());
  }

  protected DateTime getNextExpirationDate() {
    return new DateTime().plusHours(2);
  }

  protected String getPermissions(File file) {
    return isOwner(file) ? file.getUserPermission() : file.getOthersPermission();
  }

  /**
   * Verifies if user has permission to perform some operation on file
   *
   * @param user
   * @param index
   * @param c
   */
  protected void checkPermissions(File file, int index, char c) {
    String permissions = getPermissions(file);
    if(permissions.charAt(index) != c)
      throw new InsufficientPermissionsException();
  }

  protected void checkReadPermissions(File file) {
    checkPermissions(file, 0, 'r');
  }

  protected void checkWritePermissions(File file) {
    checkPermissions(file, 1, 'w');
  }

  protected void checkExecutionPermissions(File file) {
    checkPermissions(file, 2, 'x');
  }

  protected void checkDeletionPermissions(File file) {
    checkPermissions(file, 3, 'd');
  }

  @Override
  public void addUserExtension(Extension extension){
      throw new MethodDeniedException();
  }

  //empty on purpose, to be mocked 
  public App getAssociation(String extension){
    return null;
  }

  /**
   * Basic remove implementation for User objects
   */
  public void remove() {
    nullifyRelations();
    deleteDomainObject();
  }

  /**
   * Nullifies relations, that is, deletes/cancels any relation between this
   * object and eventual others.
   */
  protected void nullifyRelations() {
    setHomeDirectory(null);
    setFileSystem(null);
  }

  public Element xmlExport(){
    Element user = new Element("user");
    user.setAttribute("username", getUsername());

    Element userName = new Element("name");
    userName.setText(getName());

    Element userPwd = new Element("password");
    userPwd.setText(super.getPassword());

    Element userHomeDir = new Element("home");
    userHomeDir.setText(getHomeDirectory().getPath());

    Element userUmask = new Element("umask");
    userUmask.setText(getUmask());

    user.addContent(userName);
    user.addContent(userPwd);
    user.addContent(userHomeDir);
    user.addContent(userUmask);

    return user;
  }

  /**
   * Fenix fenixframework stuff
   * Overriding methods for class protection
   */

  @Override
  public String getPassword(){
    throw new MethodDeniedException();
  }

  @Override
  public void setPassword(String password){
    throw new MethodDeniedException();
  }

  @Override
  public void setUmask(String umask){
    throw new MethodDeniedException();
  }

  @Override
  public void setName(String umask) {
    throw new MethodDeniedException();
  }
}
