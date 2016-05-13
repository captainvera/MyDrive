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

  @Override
  public int getSize() {
    return super.getSize();
  }

  @Override
  public File getFile(ArrayList<String> tokens, User user) {
    throw new NotADirectoryException(getName());
  }

  @Override
  public void execute(User user, String[] arguments){
      user.checkExecutionPermissions(this);
      String appMethod = getData(user);
      try{
        //First we assume no method is provided
        Class[] arg = new Class[1];
        arg[0] = String[].class;
        Method method = Class.forName(appMethod).getDeclaredMethod("main", arg);
        Object[] args = new Object[]{arguments};
        
        method.invoke(this, args);
    } catch(NoSuchMethodException e) {
      throw new RuntimeException("Unknown method on list file");
    } catch(ClassNotFoundException e ) {
      try{
        //If no class is found maybe the method is specified
        
        String methodName = appMethod.substring(appMethod.lastIndexOf(".") + 1);
        //the class name to look for
        String className = appMethod.substring(0, appMethod.lastIndexOf("."));
        Class[] arg = new Class[1];
        arg[0] = String[].class;
        Method method = Class.forName(className).getDeclaredMethod(methodName, arg);
        Object[] args = new Object[]{arguments};
        
        method.invoke(this, args);
      }catch(InvocationTargetException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException e1) {
        throw new RuntimeException("No method nor class on app");
      } 
    } catch(IllegalAccessException e){ 
      throw new RuntimeException("IllegalAccessException while executing app.");
    } catch(InvocationTargetException e){
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
