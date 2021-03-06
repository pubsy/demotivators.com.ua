package model;

import java.util.List;

import models.Demotivator;
import models.User;
import net.sf.oval.guard.Post;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class DemotivatorTest extends UnitTest{
	
	@Before
	public void deleteModels(){
		Fixtures.deleteAllModels();
	}

	@Test
	public void createDemotivator() {
		Fixtures.loadModels("data/single.yml");
		
	    // Test that the demotivator has been created
	    assertEquals(1, Demotivator.count());
	    
	    User martin = User.find("byEmail", "martin.fowler@gmail.com").first();
	    
	    List<Demotivator> martinDemos = Demotivator.findAll();
	    
	    // Tests
	    assertEquals(1, martinDemos.size());
	    Demotivator demo = martinDemos.get(0);
	    assertEquals(martin, demo.getAuthor());
	    assertEquals("An ugly demotivator", demo.getTitle());
	    assertEquals("ugly.jpg", demo.getFileName());
	    assertNotNull(demo.getDate());
	    assertEquals("localhost", demo.getDomain().getName());
	    assertEquals("Some ugly text", demo.getText());
	    assertNotNull(demo.getComments());
	    assertEquals(2, demo.getComments().size());
	    
	}
}
