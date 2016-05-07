package pt.tecnico.myDrive.presentation;
import pt.tecnico.myDrive.services.ExecuteFileService;

import java.util.TreeMap;

public class Execute extends MyDriveCommand {

  public Execute(MyDriveShell sh) { super(sh, "do", "executes the file"); }

  public void execute(String[] args) {
    ExecuteFileService cws = null;
	  if (args.length < 1)
	    throw new RuntimeException("USAGE: "+name()+" path [args]");
    else if(args.length == 1){
      cws = new ExecuteFileService(shell().getActiveToken(), args[0], null);
    }else{
      String[] args_list = new String[args.length-1];
      for(int i = 1; i < args.length; i++){
        args_list[i-1] = args[i]; 
      }
      cws = new ExecuteFileService(shell().getActiveToken(), args[0], args_list);
    }
    cws.execute();
  }
}
