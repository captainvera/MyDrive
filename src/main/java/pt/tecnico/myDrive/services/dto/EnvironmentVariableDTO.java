package pt.tecnico.myDrive.services.dto;

public class EnvironmentVariableDTO {

  public String _name, _value;

  public EnvironmentVariableDTO(String name, String value) {
    _name = (name);
    _value = (value);
  }

  public boolean equals(Object dto ){
    if(dto instanceof EnvironmentVariableDTO){
      EnvironmentVariableDTO temp =  (EnvironmentVariableDTO) dto;
      if(temp._name == _name && temp._value == _value)
        return true;
    }
    return false;
  }

  public String getName(){
    return _name;
  }
 
  public String getValue(){
    return _value;
  }
}
