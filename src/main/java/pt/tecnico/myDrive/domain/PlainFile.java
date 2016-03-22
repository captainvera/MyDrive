package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import org.jdom2.DataConversionException;

import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.ImportDocumentException;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class PlainFile extends PlainFile_Base {

  /** Placeholder for FenixFramework */
  protected PlainFile () {
    super();
  }

  private PlainFile(GenericPFBuilder GenericPFBuilder){
    init(GenericPFBuilder);
  }

  protected void init(GenericPFBuilder GenericPFBuilder) {
    super.init(GenericPFBuilder);
    setData(GenericPFBuilder._content);
  }

  /**
   * Generic abstract plain file builder.
   * All PlainFile's direct subclasses will inherit from this builder.
   */
  public abstract static class GenericPFBuilder
    <P extends PlainFile, T extends GenericPFBuilder<P,T>>
      extends File.GenericFileBuilder<P,T> {

      private String _content;

      public T withContent(String content){
        _content = content;
        return getThis();
      }

      @Override
      public void validate() {
        // TODO
      }
    }

  /**
   * Concrete plain file builder.
   * Since plain file is not abstract, thereby instanciable, it is 'buildable'.
   */
  public static class PFBuilder
      extends GenericPFBuilder<PlainFile, PFBuilder> {

      @Override
      public PFBuilder getThis() { return this; }

      @Override
      public PlainFile build() {
        validate();
        return new PlainFile(getThis());
      }

      public static PFBuilder create() {
        return new PFBuilder();
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
