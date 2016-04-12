package pt.tecnico.myDrive.visitors;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.NotAAppException;

/**
 * Simple App visitor. Allows throws and exception if the element being visited is not a App
 */

public class AppVisitor implements GenericVisitor<App> {

  @Override
  public App visit(Directory dir) {
    return null;
  }

  @Override
  public App visit(PlainFile pf) {
    return null;
  }

  @Override
  public App visit(Link l) {
    return null;
  }

  @Override
  public App visit(App a) {
    return a;
  }

}
