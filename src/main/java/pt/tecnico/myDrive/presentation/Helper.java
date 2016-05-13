package pt.tecnico.myDrive.presentation;

public class Helper {

  public static void argumentTest(String[] args){
    System.out.println("-Executing argumentTest");
    System.out.println("Provided arguments:");
    for(String s : args){
      System.out.println("arg -> "+s);
    }
  }

  public static void main(String[] args){
    System.out.println("Testing default execution for apps");
    System.out.println("Helper main function executed");
    for(String s : args){
      System.out.println("arg -> "+s);
    }
  }
}
