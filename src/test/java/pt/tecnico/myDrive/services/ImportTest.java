package pt.tecnico.myDrive.services;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import pt.tecnico.myDrive.service.AbstractServiceTest;

import org.junit.Test;

import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.FileSystem;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.StringReader;

public class ImportTest extends AbstractServiceTest {
    private final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ "  <user username=\"jtb\">"
	+ "    <password>Fernandes</password>"
	+ "    <name>Joaquim Teofilo Braga</name>"
	+ "    <home>/home/jtb</home>"
	+ "    <mask>rwxdr-x-</mask>"
	+ "  </user>"
	+ "  <dir id=\"6\">"
	+ "    <owner>jtb</owner>"
	+ "    <path>/home/jtb</path>"
	+ "    <name>documents</name>"
	+ "  </dir>"
  + "  <plain id=\"7\">"
  + "    <path>/home/jtb</path>"
  + "    <owner>jtb</owner>"
  + "    <name>profile</name>"
  + "    <contents>E a wiki?</contents>"
  + "  </plain>"
  + "  <link id=\"8\">"
  + "    <path>/home/jtb</path>"
  + "    <owner>jtb</owner>"
  + "    <name>doc</name>"
  + "    <value>/home/jtb/documents</value>"
  + "  </link>"
  + "  <app id=\"9\">"
  + "    <path>/home/jtb/documents</path>"
  + "    <name>sum</name>"
  + "    <owner>jtb</owner>"
  + "    <method>pt.tecnico.myDrive.presentation.Hello.sun</method>"
  + "  </app>"
	+ "</myDrive>";

    protected void populate() {
        FileSystem fs = FileSystem.getInstance();
    }

    @Test
    public void success() throws Exception{
	    Document doc = new SAXBuilder().build(new StringReader(xml));
      ImportMyDriveService service = new ImportMyDriveService(doc);
      service.execute();

      // check imported data
      FileSystem fs = FileSystem.getInstance();
      User u = fs.getUserByUsername("jtb");
      u.getHomeDirectory().getFileByName("doc");
      assertEquals("JTB has dir", "/home/jtb", u.getHomeDirectory().getPath());
	    // it must be right, but more checks can be made ...
    }
}
