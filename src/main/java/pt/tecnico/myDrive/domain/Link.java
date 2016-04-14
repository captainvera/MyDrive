package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import org.jdom2.DataConversionException;

import pt.tecnico.myDrive.exceptions.CannotWriteToLinkException;
import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.ImportDocumentException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;
import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

import java.util.ArrayList;

public class Link extends Link_Base {

  /** Placeholder for FenixFramework */
  public Link() {
    super();
  }

  public Link(FileSystem fs, Integer id, String name, Directory parent, User owner, String data) {
    init(fs, id, name, parent, owner, data);
    setDirtyBit(true);
  }

  /**
   * TEMPORARY
   */
  @Override
  public int getSize(){
    return 1;
  }
  @Override
  public void setData(String content, User user){
  	  if(getDirtyBit()) throw new MethodDeniedException();
	  else super.setData(content,user);
  }
  @Override
  public File getFile(ArrayList<String> tokens, User user) {
    System.out.println("DEBUG link: " + getPath());

    if(tokens.size() == 0)
      return getFileSystem().getFileByPath(getData(), user, getParent());

    checkExecutionPermissions(user);
    String remaining = "";
    remaining += getData();
    if(remaining.charAt(remaining.length()-1) == '/')
      remaining = remaining.substring(0, remaining.length()-1);

    for(String s : tokens )
      remaining += '/' + s;

    System.out.println("DEBUG!! " + remaining + " | ");
    File file = getFileSystem().getFileByPath(remaining, user, getParent());
    file.checkReadPermissions(user);
    return file;
  }

  @Override
  public String execute(User user) throws NotADirectoryException, FileUnknownException, InsufficientPermissionsException{
    return getFileObject(user).execute(user);
  }

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

  @Override
  public void remove() {
    super.remove();
  }
  /**
   * @param xml Element
   */
  public void xmlImport(Element linkElement) throws ImportDocumentException{
    try{
      setId(linkElement.getAttribute("id").getIntValue());

      Element perm = linkElement.getChild("perm");
      if (perm != null)
        setUserPermission(new String(perm.getText().getBytes("UTF-8")));

      Element value = linkElement.getChild("value");
      if (value != null)
        setData(new String(value.getText().getBytes("UTF-8")));

    } catch(UnsupportedEncodingException | DataConversionException e){
      throw new ImportDocumentException(String.valueOf(getId()));
    }
  }

  @Override
  public File getFileObject(User user) {
    return getFileSystem().getFileByPath(getData(), user, getParent());
  }

  @Override
  public String toString(){
    return "l " + getUserPermission() + getOthersPermission() + " " + getName() + "->" + getData();
  }
}
