package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Random;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Calendar;
import java.util.Date;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.MethodDeniedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import java.util.List;
import java.util.ArrayList;
import pt.tecnico.myDrive.services.dto.EnvironmentVariabledto;
public class Login extends Login_Base {
  protected static final Logger log = LogManager.getRootLogger();

  /**
   * Login constructor, receives logged user, currentDirectory and expiration date.
   */
  public Login(FileSystem fs, User user, Directory currentDirectory, Long token) {
    super.setFileSystem(fs);
    super.setUser(user);
    super.setCurrentDirectory(currentDirectory);
    super.setToken(token);
    extendToken();
  }

  /**
   * Basic remove implementation for Login objects
   */
  public void remove() {
    nullifyRelations();
    deleteDomainObject();
  }

  /**
   * Nullifies relations, that is, deletes/cancels any relation between this
   * object and eventual others.
   */
  protected void nullifyRelations() {
    super.setUser(null);
    super.setCurrentDirectory(null);
    super.setFileSystem(null);
  }

  public void extendToken() {
    super.setExpirationDate(super.getUser().getNextExpirationDate());
  }

  public boolean compareToken(long token) {
    return token == super.getToken();
  }

  public boolean hasExpired () {
    return new DateTime().compareTo(super.getExpirationDate()) >= 0;
  }

  public void reduceExpirationDate(int hours, int minutes, int seconds){
    super.setExpirationDate(super.getExpirationDate().minusHours(hours).minusMinutes(minutes).minusSeconds(seconds));
  }

  @Override
  public Long getToken() {
    throw new MethodDeniedException();
  }

  @Override
  public void setToken(Long token) {
    throw new MethodDeniedException();
  }

  @Override
  public DateTime getExpirationDate() {
    throw new MethodDeniedException();
  }

  @Override
  public void setExpirationDate(DateTime expirationDate) {
    throw new MethodDeniedException();
  }

  @Override
  public FileSystem getFileSystem() {
    throw new MethodDeniedException();
  }

  @Override
  public void setFileSystem(FileSystem fileSystem) {
    throw new MethodDeniedException();
  }

  @Override
  public void setCurrentDirectory(Directory dir){
    throw new MethodDeniedException();
  }

  public void setCurrentDirectory(Directory dir, User owner) {
    owner.checkExecutionPermissions(dir);
    super.setCurrentDirectory(dir);
  }

  @Override
  public void setUser(User user) {
    throw new MethodDeniedException();
  }

  @Override
  public Set<EnvironmentVariable> getEnvVarSet() {
    throw new MethodDeniedException();
  }

  @Override
  public void removeEnvVar(EnvironmentVariable ev) {
    throw new MethodDeniedException();
  }

  @Override
  public void addEnvVar(EnvironmentVariable ev) {
    throw new MethodDeniedException();
  }

  public void addEnvVar(String name, String value) {
    EnvironmentVariable envvar = getEnvVarbyName(name);
    if(envvar == null){
      envvar = new EnvironmentVariable(name, value);
      super.addEnvVar(envvar); 
    } else {
      envvar.setValue(value);
    } 
  }

  private EnvironmentVariable getEnvVarbyName(String name){
    for (EnvironmentVariable envvar: super.getEnvVarSet()) {
      if (envvar.getName().equalsIgnoreCase(name)) {
        return envvar;
      }
    }
    return null;
  }

  public List<EnvironmentVariabledto> listEnvVar(){
    List result = new ArrayList<EnvironmentVariabledto>();
    for (EnvironmentVariable envvar: super.getEnvVarSet()){
      result.add( new EnvironmentVariable(envvar.getName(), envvar.getValue()));
    } 
    return result; 
  }

}
