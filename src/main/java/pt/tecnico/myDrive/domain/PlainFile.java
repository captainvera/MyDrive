package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import org.jdom2.DataConversionException;

import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.ImportDocumentException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;

import pt.tecnico.myDrive.visitors.GenericVisitor;
import java.util.ArrayList;

public class PlainFile extends PlainFile_Base {

  /** Placeholder for FenixFramework */
  protected PlainFile () {
    super();
  }

  public PlainFile(FileSystem fs, Integer id, String name, Directory parent, User owner) {
    this(fs, id, name, parent, owner, "");
  }

  public PlainFile(FileSystem fs, Integer id, String name, Directory parent, User owner, String data) {
    init(fs, id, name, parent, owner, data);
  }

  protected void init(FileSystem fs, Integer id, String name, Directory parent, User owner, String data) {
    super.init(fs, id, name, parent, owner);
    setData(data);
  }

  /**
   * TEMPORARY
   */
  @Override
  public int getSize(){
    return 0;
  }

  @Override
  public File getFile(ArrayList<String> tokens, User user) throws NotADirectoryException, FileUnknownException {
    throw new NotADirectoryException(getName());
  }

  @Override
  public String execute(){
    return getData();
  }

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

  @Override
  public void remove() {
    super.remove();
  }

  public void xmlImport(Element plainElement) throws ImportDocumentException, UserUnknownException{
    try{
      setId(plainElement.getAttribute("id").getIntValue());

      Element perm = plainElement.getChild("perm");
      if (perm != null)
        setUserPermission(new String(perm.getText().getBytes("UTF-8")));

      Element value = plainElement.getChild("contents");
      if (value != null)
        setData(new String(value.getText().getBytes("UTF-8")));

    } catch(UnsupportedEncodingException | DataConversionException e){
      throw new ImportDocumentException(String.valueOf(getId()));
    }
  }

  @Override
  public String toString(){
  	return "- " + getUserPermission() + getOthersPermission() + " " + getName();
  }
}
