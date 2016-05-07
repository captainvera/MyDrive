package pt.tecnico.myDrive.presentation;

import java.util.TreeMap;

public class Key extends MyDriveCommand {

  public Key(MyDriveShell sh) { super(sh, "token", "prints the active user in the shell, or changes the active user in the shell"); }

  public void execute(String[] args) {
	  if (args.length > 1)
	    throw new RuntimeException("USAGE: "+name()+" [username]");
	  else if(args.length == 1){
      shell().setActiveSession(args[0]);
    }else{
      shell().println("Active user:" + shell().getActiveUser() + " | Active Token: " + shell().getActiveToken());
    }
  }
}
