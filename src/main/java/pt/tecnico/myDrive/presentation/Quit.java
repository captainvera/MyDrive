
package pt.tecnico.myDrive.presentation;
import pt.tecnico.myDrive.services.LogoutService;

import java.util.TreeMap;

public class Quit extends MyDriveCommand {

  public Quit(MyDriveShell sh) { super(sh, "quit", "Logout a certain token"); }

  public void execute(String[] args) {
    if(args.length == 0){
      if(shell().getActiveUser().equals("nobody")){
        new LogoutService(shell().getActiveToken()).execute();
        shell().removeSession("nobody");
      }
      shell().shutdown();
    } else
      throw new RuntimeException("USAGE: " + name());

    shell().println("Logged out token.");
  }
}
