package pt.tecnico.myDrive.visitors;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.NotALinkException;

/**
 * Simple link visitor. Allows throws and exception if the element being visited is not a Link
 */

public class LinkVisitor implements GenericVisitor<Link> {

  @Override
  public Link visit(Directory dir) {
    return null;
  }

  @Override
  public Link visit(PlainFile pf) {
    return null;
  }

  @Override
  public Link visit(Link l) {
    return l;
  }

  @Override
  public Link visit(App a) {
    return null;
  }

}
