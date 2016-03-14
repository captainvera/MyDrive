package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import org.jdom2.DataConversionException;

import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.ImportDocumentException;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class PlainFile extends PlainFile_Base {

  public PlainFile(){
  }

  public PlainFile(String name, Directory parent, Integer id, User owner) {
    init(name,parent,id, owner);
  }

  /**
   * TEMPORARY
   */
  @Override
  public int getSize(){
    return 0;
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

}
