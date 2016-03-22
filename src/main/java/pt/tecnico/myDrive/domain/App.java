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

  private App(AppBuilder appBuilder){
    super.init(appBuilder);
  }

  public static class AppBuilder
      extends GenericPFBuilder<App, AppBuilder> {

      @Override
      public AppBuilder getThis() { return this; }

      @Override
      public App build() {
        validate();
        return new App(getThis());
      }

      public static AppBuilder create() {
        return new AppBuilder();
      }
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
}
