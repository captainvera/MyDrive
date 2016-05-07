package pt.tecnico.myDrive.presentation;
import pt.tecnico.myDrive.services.ChangeDirectoryService;

import java.util.TreeMap;

public class ChangeWorkingDirectory extends MyDriveCommand {

  public ChangeWorkingDirectory(MyDriveShell sh) { super(sh, "cwd", "changes the current working directory to the specified directory"); }

  public void execute(String[] args) {
    ChangeDirectoryService cds = null;
	  if (args.length > 1)
	    throw new RuntimeException("USAGE: "+name()+" [path]");
    else if(args.length == 0)
      cds = new ChangeDirectoryService(shell().getActiveToken(), ".");
    else if(args.length == 1)
      cds = new ChangeDirectoryService(shell().getActiveToken(), args[0]);

    cds.execute();
    String res = cds.result();
    shell().println(res);
    shell().changeCurrentDirectory(res);
  }
}
