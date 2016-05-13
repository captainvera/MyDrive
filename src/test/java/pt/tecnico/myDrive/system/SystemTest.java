package pt.tecnico.myDrive.system;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.presentation.*;

public class SystemTest extends AbstractSystemTest {

    private MyDriveShell sh;
    private FileSystem _fs;
    private User _user1;
	private User _user2;
    private PlainFile _pf;
    private App _app;
    private pt.tecnico.myDrive.domain.Login _login;

    protected void populate() {
        sh = new MyDriveShell();
        try{

            _fs = FileSystem.getInstance();
            _user1 = new User(_fs, "user8888", "user8888", "user8888");
            _user2 = new User(_fs, "user9999", "user9999", "user9999");
            _user1.setHomeDirectory(new Directory(_fs, "user8888", _fs.getHomeDirectory(), _user1));

            _pf = new PlainFile (_fs, "pf" , _user1.getHomeDirectory(), _user1, "pf_Data");
            _app = new App (_fs, "app", _user1.getHomeDirectory(), _user1, "pt.tecnico.myDrive.presentation.Helper.argumentTest");

            _login = new pt.tecnico.myDrive.domain.Login(_fs, _user2, _user2.getHomeDirectory(), 123l);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void success() {
        new pt.tecnico.myDrive.presentation.Login(sh).execute(new String[] { "user8888", "user8888" } );
        new pt.tecnico.myDrive.presentation.Login(sh).execute(new String[] { "user9999", "user9999" } );
        new Key(sh).execute(new String[] {} );
        new Key(sh).execute(new String[] {"user8888"} );
        new ChangeWorkingDirectory(sh).execute(new String[] { "/home/user8888" } );
        new ChangeWorkingDirectory(sh).execute(new String[] {} );
        //new Environment(sh).execute(new String[] { "TODO" } );
        new ListDirectory(sh).execute(new String[] { "/home/user8888" } );
        new ListDirectory(sh).execute(new String[] {} );
        new Write(sh).execute(new String[] { "/home/user8888/pf", "/home/user8888/app" } );
        new Execute(sh).execute(new String[] { "/home/user8888/pf" } );
    }

    @Test(expected = RuntimeException.class)
    public void loginError() throws Exception{
        new pt.tecnico.myDrive.presentation.Login(sh).execute(new String[] {} );
    }

    @Test(expected = RuntimeException.class)
    public void keyError() throws Exception{
        new Key(sh).execute(new String[] {"teste1", "teste2"} );
    }

    @Test(expected = RuntimeException.class)
    public void changeWorkingDirectoryError() throws Exception{
        new ChangeWorkingDirectory(sh).execute(new String[] {"/home/user8888/pf", "/home/user8888/app"} );
    }

    @Test(expected = RuntimeException.class)
	public void listDirectoryError() throws Exception{
		new ListDirectory(sh).execute(new String[] {"/home/user8888/pf", "/home/user8888/app"} );
	}

    @Test(expected = RuntimeException.class)
	public void writeError() throws Exception{
		new Write(sh).execute(new String[] {} );
	}

    @Test(expected = RuntimeException.class)
	public void executeError() throws Exception{
		new Execute(sh).execute(new String[] {} );
	}
}
