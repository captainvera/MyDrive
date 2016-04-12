package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;

public class RootUser extends RootUser_Base {

  public RootUser(FileSystem fs, Directory dir) {
    init(fs, dir);
  }

  @Override
  public String getPassword(){
    throw new MethodDeniedException();
  }

  @Override
  public void setPassword(String password){
    throw new MethodDeniedException();
  }

  @Override
  public void setUmask(String umask){
    throw new MethodDeniedException();
  }

  protected void init(FileSystem fs, Directory homeDir) {
    super.setUsername("root");
    super.setName("Super User");
    super.setPassword("***");
    super.setUmask("rwxdr-x-");
    super.setHomeDirectory(homeDir);
    super.setFileSystem(fs);
  }

  public boolean verifyPassword(String password){
    return password.equals(super.getPassword());
  }
  /**
   * Root User shouldn't be removable
   */
  public void remove() {
    throw new MethodDeniedException();
  }

  /**
   * Nullifies relations, that is, deletes/cancels any relation between this
   * object and eventual others.
   */
  protected void nullifyRelations() {
    setHomeDirectory(null);
    setFileSystem(null);
  }
}
