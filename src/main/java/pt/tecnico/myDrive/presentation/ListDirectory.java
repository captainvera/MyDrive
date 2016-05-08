package pt.tecnico.myDrive.presentation;
import pt.tecnico.myDrive.services.ListDirectoryService;

import java.util.TreeMap;

public class ListDirectory extends MyDriveCommand {

  public ListDirectory(MyDriveShell sh) { super(sh, "ls", "lists a directory"); }

  public void execute(String[] args) {
    ListDirectoryService ls;
	  if (args.length == 0)
      ls = new ListDirectoryService(shell().getActiveToken());
    else if(args.length == 1)
      ls = new ListDirectoryService(shell().getActiveToken(), args[0]);
    else
	    throw new RuntimeException("USAGE: " + name() + " [path]");

    ls.execute();
    String res = ls.result();
    shell().println(res);
  }
}
