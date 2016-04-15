package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;

import pt.tecnico.myDrive.domain.Login;

public class EnvironmentVariable extends EnvironmentVariable_Base {

    public EnvironmentVariable() {
        super();
    }

    @Override
    public void setName(String s) {
      throw new MethodDeniedException();
    }

    @Override
    public void setValue(String s) {
      throw new MethodDeniedException();
    }

    @Override
    public Login getLogin () {
      throw new MethodDeniedException();
    }

    @Override
    public void setLogin (Login login) {
      throw new MethodDeniedException();
    }
}
