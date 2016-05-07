package pt.tecnico.myDrive.presentation;

public class Session {
  String _currentDirectory;
  long _token;

  public Session(long token, String currentDir){
    _token = token;
    _currentDirectory = currentDir;
  }

  public String getCurrentDirectory(){
    return _currentDirectory;
  }

  public long getToken(){
    return _token;
  }

  public void setCurrentDirectory(String currentDir){
    _currentDirectory = currentDir;
  }

  public void setToken(long token){
    _token = token;
  }
 }
