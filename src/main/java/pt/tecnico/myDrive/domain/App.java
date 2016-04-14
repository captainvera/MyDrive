package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import org.jdom2.DataConversionException;

import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.ImportDocumentException;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class App extends App_Base {

  /** Placeholder for FenixFramework */
  protected App () {
    super();
  }

  public App(FileSystem fs, Integer id, String name, Directory parent, User owner) {
    this(fs, id, name, parent, owner, "");
  }

  public App(FileSystem fs, Integer id, String name, Directory parent, User owner, String data) {
    init(fs, id, name, parent, owner, data);
  }

  /**
   * TEMPORARY
   */
  @Override
  public int getSize() {
    return 0;
  }

  @Override
  public String execute(){
    return "App execution not implemented yet.";
  }

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

  @Override
  public void remove() {
    super.remove();
  }

  public void xmlImport(Element appElement) throws ImportDocumentException{
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
