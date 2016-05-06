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
  public long getActiveToken(long token){
    return _activeToken;
  }

  @Override
  public TreeMap<String, Long> getTokens() {
    return _allTokens;
  }
  public static void main(String[] args) throws Exception {
    MyDriveShell sh = new MyDriveShell();
    sh.execute();
  }

  public MyDriveShell() { // add commands here
    super("MyDrive");
    new Login(this);
  }

}
