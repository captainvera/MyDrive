package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;

import pt.tecnico.myDrive.exceptions.ImportDocumentException;
import pt.tecnico.myDrive.exceptions.MethodDeniedException;

public class User extends User_Base {

  /** Placeholder for FenixFramework */
  public User() {
    super();
  }

  public User(String username, String name, String password, String umask) {
    this(username, name, password, umask, null);
  }

  public User(String username, String name, String password, String umask, Directory homeDir) {
    init(username, name, password, umask, homeDir);
  }

  /**
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

  protected void init(String username, String name, String password, String umask, Directory homeDir) {
    super.setUsername(username);
    super.setName(name);
    super.setPassword(password);
    super.setUmask(umask);
    super.setHomeDirectory(homeDir);
  }

  public boolean verifyPassword(String password){
    return password.equals(super.getPassword());
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
    userPwd.setText(getPassword());

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
}
