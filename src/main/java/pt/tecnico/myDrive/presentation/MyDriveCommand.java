package pt.tecnico.myDrive.presentation;

public abstract class MyDriveCommand extends Command {
  private MyDriveShell _mdShell; 

  public MyDriveCommand(MyDriveShell sh, String n) { 
    super(sh, n); 
    _mdShell = sh;
  }

  public MyDriveCommand(MyDriveShell sh, String n, String h) {
    super(sh, n, h); 
    _mdShell = sh;
  }

  public MyDriveShell shell(){
    return _mdShell;
  }

}
