package pt.tecnico.myDrive.presentation;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.tecnico.myDrive.services.LoginService;
import pt.tecnico.myDrive.services.LogoutService;
import pt.tecnico.myDrive.services.ImportMyDriveService;

import java.util.TreeMap;

public class MyDriveShell extends Shell {

  private String _activeUser;
  private String _currentDir;
  private Session _activeSession;
  long _token;


  private TreeMap<String, Session> _sessions = new TreeMap<String, Session>();

  public static void main(String[] args) throws Exception {
    if(args.length > 0) xmlScan(new java.io.File(args[0]));
    MyDriveShell sh = new MyDriveShell();
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
    new Quit(this);

    String guest = "nobody";
    LoginService ls = new LoginService(guest,"");
    ls.execute();

    _token = ls.result();

    addSession(guest, _token);
    setActiveSession(guest);
  }

  public void addSession(String username, long token){
    if(_sessions.containsKey(username)){
      _sessions.remove(username);
    }
    _sessions.put(username, new Session(token, "/home/"+username));
  }

  public void removeSession(String username){
    if(_sessions.containsKey(username)){
      _sessions.remove(username);
    }
  }

  public void setActiveSession(String username){
    Session session= _sessions.get(username);
    if(session != null){
      _activeUser = username;
      _activeSession = session;

      setPrompt(username + " @ " + session.getCurrentDirectory() + " ~ ");
    }else{
      println("No session present for user " + username);
    }
  };

  public String getActiveUser(){
    return _activeUser;
  }

  public long getActiveToken(){
    return _activeSession.getToken();
  }

  public Session getActiveSession(){
    return _activeSession;
  }

  public void changeCurrentDirectory(String currentDir){
    _activeSession.setCurrentDirectory(currentDir);
    setPrompt(_activeUser + " @ " + currentDir + " ~ ");
  }

  public static void xmlScan(java.io.File file){
    // FIXME TODO
    SAXBuilder builder = new SAXBuilder();
    try {
      Document document = (Document)builder.build(file);
      ImportMyDriveService imds = new ImportMyDriveService(document);
      imds.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void shutdown(){
    System.exit(0);
  }
}
