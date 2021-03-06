package pt.tecnico.myDrive.presentation;
import pt.tecnico.myDrive.services.LoginService;
import pt.tecnico.myDrive.services.LogoutService;

import java.util.TreeMap;

public class Login extends MyDriveCommand {

  public Login(MyDriveShell sh) { super(sh, "login", "logs a user"); }

  public void execute(String[] args) {
    LoginService ls = null;


	  if (args.length < 1)
	    throw new RuntimeException("USAGE: "+name()+" username" + " [password]");
	  else if(args.length == 1) {
      if(shell().getActiveUser().equals("nobody")){
        shell().removeSession("nobody");
        new LogoutService(shell().getActiveToken()).execute();
      }
      ls = new LoginService(args[0], "");
    }
    else {
      if(shell().getActiveUser().equals("nobody")){
        shell().removeSession("nobody");
        new LogoutService(shell().getActiveToken()).execute();
      }
      ls = new LoginService(args[0], args[1]);
    }

    ls.execute();


    long token = ls.result();
    String username = args[0];
    shell().println("Logged in, token:" + token);
    shell().addSession(username, token);
    shell().setActiveSession(username);
  }
}
