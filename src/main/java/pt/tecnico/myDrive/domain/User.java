package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;

import pt.tecnico.myDrive.exceptions.ImportDocumentException;

public class User extends User_Base {

  public User() {
    super();
  }

  public User(Directory homedir, String username){
    super();
    setHomeDirectory(homedir);
    setUsername(username);
  }

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


  public void xmlImport(Element userElement) throws ImportDocumentException{
    try{

      Element name = userElement.getChild("name");
      if(name != null) setName(new String(name.getText().getBytes("UTF-8")));
      else setName(getUsername());

      Element pwd = userElement.getChild("password");
      if (pwd != null) setPassword(new String(pwd.getText().getBytes("UTF-8")));
      else setPassword(getUsername());

      Element umask = userElement.getChild("mask");
      if(umask != null) setUmask(new String(umask.getText().getBytes("UTF-8")));
      else setUmask("rwxd----");

    } catch(UnsupportedEncodingException e){
      throw new ImportDocumentException(getUsername());
    }
  }
}
