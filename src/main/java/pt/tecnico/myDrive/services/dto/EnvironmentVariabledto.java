package pt.tecnico.myDrive.services.dto;

public class EnvironmentVariabledto {

  public String _name, _value;

  public EnvironmentVariabledto(String name, String value) {

    _name = (name);
    _value = (value);

  }

  public boolean equals(Object dto ){
    if(dto instanceof EnvironmentVariabledto){
      EnvironmentVariabledto temp =  (EnvironmentVariabledto) dto;
      if(temp._name == _name && temp._value == _value)
        return true;
    }
    return false;
  }
}
