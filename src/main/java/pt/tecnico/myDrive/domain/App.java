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

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.RuntimeException;

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
  public String execute(User user, String[] arguments) throws NotADirectoryException, FileUnknownException, InsufficientPermissionsException{
    
    String appMethod = getData();

    //last part of the string, being the method name of the package
    String methodName = appMethod.substring(appMethod.lastIndexOf(".") + 1);
    //the class name to look for
    String className = appMethod.substring(0, appMethod.lastIndexOf("."));
    try{
        Method method = Class.forName(className).getMethod(methodName); 

        Object[] args = new Object[]{arguments};

        //dirty hack. confirm if UoD specifies ret values as strings, for
        //simplification purposes
        String result = (String) method.invoke(this, args);

        return result;
    } catch(NoSuchMethodException | ClassNotFoundException e ) {
      throw new RuntimeException("Unknown method or class on list file, or illegal access");
    }
    catch(IllegalAccessException e){
        throw new RuntimeException("IllegalAccessException while executing app.");
    }
    catch(InvocationTargetException e){
        throw new RuntimeException("InvocationTargetException while executing app");
    }
  }

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

  @Override
  public String toString(){
  	return "a " + getUserPermission() + getOthersPermission() + " " + getName();
  }
}
