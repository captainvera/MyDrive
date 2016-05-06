package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import org.jdom2.DataConversionException;

import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.ImportDocumentException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import java.util.ArrayList;

public class App extends App_Base {

  /** Placeholder for FenixFramework */
  protected App () {
    super();
  }

  public App(FileSystem fs, String name, Directory parent, User owner) {
    super.init(fs, fs.requestId(), name, parent, owner, "");
  }

  public App(FileSystem fs, String name, Directory parent, User owner, String data) {
    super.init(fs, fs.requestId(), name, parent, owner, data);
  }

  /**
   * TEMPORARY
   */
  @Override
  public int getSize() {
    return super.getSize();
  }


  @Override
  public File getFile(ArrayList<String> tokens, User user) {
    throw new NotADirectoryException(getName());
  }

  @Override
  public String execute(User user, String arguments) throws NotADirectoryException, FileUnknownException, InsufficientPermissionsException{
    return "App execution not implemented yet.";
  }

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

  public void xmlImport(Element appElement) {
    try{
      setId(appElement.getAttribute("id").getIntValue());

      Element perm = appElement.getChild("perm");
      if (perm != null)
        setUserPermission(new String(perm.getText().getBytes("UTF-8")));

      Element value = appElement.getChild("method");
      if (value != null)
        setData(new String(value.getText().getBytes("UTF-8")));

    } catch(UnsupportedEncodingException | DataConversionException e){
      throw new ImportDocumentException(String.valueOf(getId()));
    }
  }

  @Override
  public String toString(){
  	return "a " + getUserPermission() + getOthersPermission() + " " + getName();
  }
}
