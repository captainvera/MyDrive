
package pt.tecnico.myDrive.presentation;
import pt.tecnico.myDrive.services.LogoutService;

import java.util.TreeMap;

public class Quit extends MyDriveCommand {

  public Quit(MyDriveShell sh) { super(sh, "quit", "Logout a certain token"); }

  public void execute(String[] args) {
    LogoutService ls;
    if(args.length == 0){
      ls = new LogoutService(shell().getActiveToken());
      shell().shutdown();
    } else
	    throw new RuntimeException("USAGE: " + name());

    ls.execute();
    shell().println("Logged out token.");
  }
}
