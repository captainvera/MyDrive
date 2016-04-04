package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;

import pt.tecnico.myDrive.exceptions.ImportDocumentException;

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

  protected void init(String username, String name, String password, String umask, Directory homeDir) {
    setUsername(username);
    setName(name);
    setPassword(password);
    setUmask(umask);
    setHomeDirectory(homeDir);
  }

  // TODO
  public void remove() { }

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
