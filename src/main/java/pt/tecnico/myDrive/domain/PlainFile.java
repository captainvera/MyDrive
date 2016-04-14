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

import pt.tecnico.myDrive.exceptions.MethodDeniedException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

import org.joda.time.DateTime;

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
    super.setData(data);
  }

  @Override
  public int getSize(){
    return getData().length();
  }

  @Override
  public File getFile(ArrayList<String> tokens, User user) throws
  NotADirectoryException, FileUnknownException, InsufficientPermissionsException {
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
  public void setData(String data) {
    throw new MethodDeniedException();
  }

  // TODO: Exceptions
  public void setData(String data, User user) {
    super.setData(data);
    setLastModified(new DateTime());
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

  public void writeToFile(String s, User user){
    /*
     * TODO::XXX:CHECK PERMISSIONS
     */
    super.setData(s);
  }
}
