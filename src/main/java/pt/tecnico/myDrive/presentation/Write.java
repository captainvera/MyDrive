package pt.tecnico.myDrive.presentation;
import pt.tecnico.myDrive.services.WriteFileService;

import java.util.TreeMap;

public class Write extends MyDriveCommand {

  public Write(MyDriveShell sh) { super(sh, "update", "changes the file content of the file in path to content"); }

  public void execute(String[] args) {
    WriteFileService cws = null;
	  if (args.length != 2)
	    throw new RuntimeException("USAGE: "+name()+" path content");
    else
      cws = new WriteFileService(shell().getActiveToken(), args[0], args[1]);

    cws.execute();
  }
}
