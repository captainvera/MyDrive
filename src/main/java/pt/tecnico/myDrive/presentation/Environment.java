package pt.tecnico.myDrive.presentation;

//import pt.tecnico.myDrive.services.EnvironmentVariableService;

import java.util.TreeMap;

public class Environment extends MyDriveCommand {

  public Environment(MyDriveShell sh) { super(sh, "env", "creates or changes an enviroment variable associated to the current active user"); }

  public void execute(String[] args) {
  //  EnvironmentVariableService cws = null;
  //  List<EnvironmentVariableDTO> result;
	//  if (args.length > 2)
	//    throw new RuntimeException("USAGE: "+name()+" [name [value]]");
  //  else if(args.length == 1) {
  //    cws = new EnvironmentVariableService(shell().getActiveToken(), "", "");
  //    cws.execute();
  //    result = cws.result();
  //    for(EnvironmentVariableDTO ev : result){
  //      if(ev.getName().equals(args[0]))
  //        shell().println(ev.getName() + " = " + ev.getValue()); 
  //    }
  //  } else {
  //    cws = new EnvironmentVariableService(shell().getActiveToken(), "", "");
  //    cws.execute();
  //    result = cws.result();
  //    for(EnvironmentVariableDTO ev : result){
  //      shell().println(ev.getName() + " = " + ev.getValue()); 
  //    }
  //  }
  //  cws.execute();
  //}
   }
}
