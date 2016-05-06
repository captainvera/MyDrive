package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.exceptions.WrongPasswordException;

public class AddVariableService extends myDriveService {

  private long _token;
  private String _name;
  private String _value;
  private String _result;

  /**
   * Add Enviroment Variable Service Constructor
   */
  public AddVariableService(long token, String name, String value) {
    super();
    _token = token;
    _name = name;
    _value = value;

  }

  @Override
  protected void dispatch() {
    FileSystem fs = FileSystem.getInstance();
    _result = fs.addEnvironmentVariable(_token, _name, _value);
  }

  public String result(){
    return _result;
  }

}
