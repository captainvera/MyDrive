package pt.tecnico.myDrive.presentation;
import pt.tecnico.myDrive.services.ListDirectoryService;

import java.util.TreeMap;

public class ListDirectory extends MyDriveCommand {

  public ListDirectory(MyDriveShell sh) { super(sh, "ls", "lists a directory"); }

  public void execute(String[] args) {
    ListDirectoryService ls = null;
	  if (args.length > 1)
	    throw new RuntimeException("USAGE: "+name()+" [path]");
    else if(args.length == 0)
      ls = new ListDirectoryService(shell().getActiveToken());
    /**
     * TODO::FIXME::Suport file paths here
     */
    else if(args.length == 1)
      ls = new ListDirectoryService(shell().getActiveToken());

    ls.execute();
    String res = ls.result();
    shell().println(res);
  }
}
