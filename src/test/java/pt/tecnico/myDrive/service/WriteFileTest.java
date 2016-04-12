package pt.tecnico.myDrive.service;
import org.junit.Test;
import pt.tecnico.myDrive.exceptions.FileExistsException;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.IllegalRemovalException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;
import pt.tecnico.myDrive.exceptions.NotAPlainFileException;

public class WriteFileTest extends AbstractServiceTest {
	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */
	@Override
	protected void populate() throws Exception{
		FileSystem fs = FileSystem.getInstance();
		PlainFile readme = fs.createPlainFileByPath("/home/README");
	}

	// private PlainFile getPlainFile(String path, String filename){
  //   return new PlainFile();
	// }	

    @Test
    public void success() {
    }

  //   @Test(expected = IllegalRemovalException.class)
  //   public void invalidFileRemoval() {
  //   }
  //
  //   @Test(expected = InsufficientPermissionsException.class)
  //   public void invalidFileAccessPermissions() {
  //   }
  //
  //   @Test(expected = NotAPlainFileException.class)
  //   public void notAPlainFile() {
	// }
  //
  //
  //   @Test(expected = FileUnknownException.class)
  //   public void fileNonExistant() {
  //   }
}
