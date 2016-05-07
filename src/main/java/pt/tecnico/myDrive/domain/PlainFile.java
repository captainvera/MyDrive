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
import org.apache.commons.lang3.ArrayUtils;

public class PlainFile extends PlainFile_Base {

  /** Placeholder for FenixFramework */
  protected PlainFile () {
    super();
  }

  public PlainFile(FileSystem fs, String name, Directory parent, User owner) {
    init(fs, fs.requestId(), name, parent, owner, "");
  }

  public PlainFile(FileSystem fs, String name, Directory parent, User owner, String data) {
    init(fs, fs.requestId(), name, parent, owner, data);
  }

  protected void init(FileSystem fs, Integer id, String name, Directory parent, User owner, String data) {
    super.init(fs, id, name, parent, owner);
    super.setData(data);
  }

  @Override
  public int getSize(){
    return super.getData().length();
  }

  @Override
  public File getFile(ArrayList<String> tokens, User user) {
    throw new NotADirectoryException(getName());
  }

  @Override
  public String execute(User user, String[] arguments) {
    return executePlainFile(user);
  }

  public String executePlainFile(User user){
    String data = getData();
    String[] fileLines = data.split("\\r?\\n");

    for(String line : fileLines){
        String[] tokens = line.split("\\s+");
        String path = tokens[0];
        String[] arguments = ArrayUtils.removeElement(tokens,0);

        //FIXME what to do with directory
        File file = getFileSystem().getFileByPath(path, user, new Directory());
        //TODO fixme - user extension to execute non executable files      
        file.execute(user, arguments);
        //if non execable do executewithextension
    }
    //placeholder - ret values? void?
    return null;
  }

  public String executeWithExtensionApp(User user, String extension, String[] arguments){
    App app = user.getAssociation(extension);
    return app.execute(user, arguments);
  }

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

  @Override
  public void setData(String data) {
    throw new MethodDeniedException();
  }

  @Override
  public String getData() {
    throw new MethodDeniedException();
  }

  public void setData(String data, User user) {
    checkWritePermissions(user);
    super.setData(data);
    touch();
  }

  public String getData(User user) {
    checkReadPermissions(user);
    return super.getData();
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
