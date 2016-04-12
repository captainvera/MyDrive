package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;

public class RootUser extends RootUser_Base {

  public RootUser(Directory dir) {
    init(dir);
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

  protected void init(Directory homeDir) {
    super.setUsername("root");
    super.setName("Super User");
    super.setPassword("***");
    super.setUmask("rwxdr-x-");
    super.setHomeDirectory(homeDir);
  }

  public boolean verifyPassword(String password){
    return password.equals(super.getPassword());
  }
  /**
   * Basic remove implementation for User objects
   */
  public void remove() { 
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
