package pt.tecnico.myDrive.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

import pt.tecnico.myDrive.domain.FileSystem; // Mockup
import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.tecnico.myDrive.services.*;
import pt.tecnico.myDrive.services.dto.*;
import pt.tecnico.myDrive.exceptions.*;

// ------------- TO DO ------------------
@RunWith(JMockit.class)
public class IntegrationTest extends AbstractServiceTest {

  protected void populate() { // populate mockup
  }

  @Test
  public void success() throws Exception {
  }
}
