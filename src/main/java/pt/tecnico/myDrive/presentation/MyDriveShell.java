package pt.tecnico.myDrive.presentation;

import java.util.TreeMap;

public class MyDriveShell extends Shell {
  private long _activeToken;
  private TreeMap<String, Long> _allTokens = new TreeMap<String, Long>();

  @Override
  public void setActiveToken (long token){
    _activeToken = token;
  }

  @Override
  public long getActiveToken(){
    return _activeToken;
  }

  @Override
  public TreeMap<String, Long> getTokens() {
    return _allTokens;
  }
  public static void main(String[] args) throws Exception {
    MyDriveShell sh = new MyDriveShell();
    sh.setDir("/");
    sh.execute();
  }

  public MyDriveShell() { 
    super("MyDrive");
    new Login(this);
    new ChangeWorkingDirectory(this);
    new ListDirectory(this);
    new Execute(this);
    new Write(this);
    new Environment(this);
    new Key(this);
  }

}
