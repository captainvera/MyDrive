package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;

import pt.tecnico.myDrive.exceptions.ImportDocumentException;

public class User extends User_Base {

  /** Placeholder for FenixFramework */
  public User() {
    super();
  }

  // TODO: Remove this, has dependencies on XML, use builders to fix those *first*
  public User(Directory homedir, String username){
    super();
    setHomeDirectory(homedir);
    setUsername(username);
  }

  private User(GenericUserBuilder gub) {
    init(gub);
  }

  protected void init(GenericUserBuilder gub){
    setUsername(gub._username);
    setPassword(gub._password);
    setName(gub._name);
    setUmask(gub._umask);
    setFileSystem(gub._fileSystem);
  }

  /**
   * Generic abstract user builder.
   * All User's direct subclasses will inherit from this builder.
   */
  public abstract static class GenericUserBuilder
    <P extends User, T extends GenericUserBuilder<P,T>> {
      private FileSystem _fileSystem;
      private String _username;
      private String _password;
      private String _name;
      private String _umask;

      /**
       * @return A new file
       */
      public abstract P build();

      /**
       * Argument validation.
       */
      protected abstract void validate();

      /**
       * Allow the recovery of the type of 'this' on a self-referenced sub-type
       */
      protected abstract T getThis();

      public T withFileSystem(FileSystem fileSystem){
        _fileSystem = fileSystem;
        return getThis();
      }

      public T withUsername(String username) {
        _username = username;
        return getThis();
      }

      public T withPassword(String password) {
        _password = password;
        return getThis();
      }

      public T withName(String name) {
        _name = name;
        return getThis();
      }

      public T withUmask(String umask) {
        _umask = umask;
        return getThis();
      }
    }

  /**
   * Concrete user builder.
   * Since user is not abstract, thereby instanciable, it is 'buildable'.
   */
  public static class UserBuilder
      extends GenericUserBuilder<User, UserBuilder> {

      @Override
      public UserBuilder getThis() {
        return this;
      }

      @Override
      public User build() {
        validate();
        return new User(getThis());
      }

      @Override
      public void validate() {
        // TODO
      }

      public static UserBuilder create () {
        return new UserBuilder();
      }

  }

  public void remove() { }

  public Element xmlExport(){
    Element user = new Element("user");
    user.setAttribute("username", getUsername());

    Element userName = new Element("name");
    userName.setText(getName());

    Element userPwd = new Element("password");
    userPwd.setText(getPassword());

    Element userHomeDir = new Element("home");
    userHomeDir.setText(getHomeDirectory().getPath());

    Element userUmask = new Element("umask");
    userUmask.setText(getUmask());

    user.addContent(userName);
    user.addContent(userPwd);
    user.addContent(userHomeDir);
    user.addContent(userUmask);

    return user;
  }
}
