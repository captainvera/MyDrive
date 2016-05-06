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
  protected Link() {
    super();
  }

  public Link(FileSystem fs, String name, Directory parent, User owner, String data) {
    init(fs, fs.requestId(), name, parent, owner, data);
    super.setDirtyBit(true);
  }

  /**
   * TEMPORARY
   */
  @Override
  public int getSize(){
    return 1;
  }

  @Override
  public void setDirtyBit(boolean state) {
    throw new MethodDeniedException();
  }

  @Override
  public boolean getDirtyBit() {
    throw new MethodDeniedException();
  }

  @Override
  public void setData(String content, User user){
  	  if(super.getDirtyBit()) throw new MethodDeniedException();
	  else super.setData(content,user);
  }
  @Override
  public File getFile(ArrayList<String> tokens, User user) {
    System.out.println("DEBUG link: " + getPath());

    if(tokens.size() == 0)
      return getFileSystem$6p().getFileByPath(getData(user), user, getParent());

    user.checkExecutionPermissions(this);
    String remaining = "";
    remaining += getData(user);
    if(remaining.charAt(remaining.length()-1) == '/')
      remaining = remaining.substring(0, remaining.length()-1);

    for(String s : tokens )
      remaining += '/' + s;

    System.out.println("DEBUG!! " + remaining + " | ");
    File file = getFileSystem$6p().getFileByPath(remaining, user, getParent());
    user.checkReadPermissions(file);
    return file;
  }

  @Override
  public String execute(User user) {
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

  @Override
  public File getFileObject(User user) {
    return getFileSystem$6p().getFileByPath(getData(user), user, getParent());
  }

  @Override
  public String toString(){
    return "l " + getUserPermission() + getOthersPermission() + " " + getName() + "->" + getData(getOwner());
  }
}
