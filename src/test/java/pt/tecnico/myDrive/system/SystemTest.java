package pt.tecnico.myDrive.system;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.presentation.*;

public class SystemTest extends AbstractSystemTest {

    private MyDriveShell sh;

    protected void populate() {
        sh = new MyDriveShell();
        //TO DO
    }

    @Test
    public void success() {
        new Login(sh).execute(new String[] { "exemplo", "1234" } );
        new ChangeWorkingDirectory(sh).execute(new String[] { "/home/exemplo" } );
        //new Environment(sh).execute(new String[] { "TO DO" } );
        new Key(sh).execute(new String[] {} );
        new ListDirectory(sh).execute(new String[] { "/home/exemplo" } );
        new Write(sh).execute(new String[] { "/home/exemplo/ficheiro", "teste" } );
        new Execute(sh).execute(new String[] { "/home/exemplo/ficheiro" } );
    }
}
