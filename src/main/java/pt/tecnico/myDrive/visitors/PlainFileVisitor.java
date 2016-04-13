package pt.tecnico.myDrive.visitors;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.NotAPlainFileException;

/**
 * Simple directory visitor. Allows throws and exception if the element being visited is not a PlainFile
 */

public class PlainFileVisitor implements GenericVisitor<PlainFile> {

  @Override
  public PlainFile visit(Directory dir) {
    return null;
  }

  @Override
  public PlainFile visit(PlainFile pf) {
    return pf;
  }

  @Override
  public PlainFile visit(Link l) {
    return l;
  }

  @Override
  public PlainFile visit(App a) {
    return a;
  }

}

