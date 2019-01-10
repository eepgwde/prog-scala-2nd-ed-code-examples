// src/test/java/progscala2/javainterop/SMapTest.java
package progscala2.javainterop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import scala.Option;
import scala.collection.mutable.LinkedHashMap;

public class SMapTest {       // <1>
  
	// Apparently JUnitSuite is serializable, so we need this:
	private static final long serialVersionUID = 693445694552874517L;

  static class Name {
    public String firstName;
    public String lastName;

    public Name(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName  = lastName;
    }
  }

  LinkedHashMap<Integer, Name> map;

  @BeforeAll
  public void setup() {
    map = new LinkedHashMap<Integer, Name>();
    map.update(1, new Name("Dean", "Wampler"));
  }

  @Test
  public void usingMapGetWithOptionName() {                          // <2>
    assertEquals(1, map.size());
    Option<Name> n1 = map.get(1);  // Note: Option<Name>
    assertTrue(n1.isDefined());
    assertEquals("Dean", n1.get().firstName);
  }

  @Test
  public void usingMapGetWithOptionExistential() {                   // <3>
    assertEquals(1, map.size());
    Option<?> n1 = map.get(1);    // Note: Option<?>
    assertTrue(n1.isDefined());
    assertEquals("Dean", ((Name) n1.get()).firstName);
 }
}
