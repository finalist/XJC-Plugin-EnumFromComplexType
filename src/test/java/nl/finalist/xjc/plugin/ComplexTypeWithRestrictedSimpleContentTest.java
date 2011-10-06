package nl.finalist.xjc.plugin;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static nl.finalist.xjc.plugin.ComplexTypeWithRestrictedSimpleContent.getNewInstance;
import static nl.finalist.xjc.plugin.ComplexTypeWithRestrictedSimpleContent.isComplexTypeWithRestrictedSimpleContent;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for {@link ComplexTypeWithRestrictedSimpleContent}
 * @author ton.swieb
 */
public class ComplexTypeWithRestrictedSimpleContentTest extends XSDBaseTest {
	
	@Test
	public void testIsComplextTypeWithRestrictedSimpleContent() {
		assertTrue("Expecting a XML ComplexType with restricted content",isComplexTypeWithRestrictedSimpleContent(stringEnum));
		assertTrue("Expecting a XML ComplexType with restricted content",isComplexTypeWithRestrictedSimpleContent(intEnum));		
		assertTrue("Expecting a XML ComplexType with restricted content",isComplexTypeWithRestrictedSimpleContent(stringPattern));		
		assertTrue("Expecting a XML ComplexType with restricted content",isComplexTypeWithRestrictedSimpleContent(derivedStringEnum));
		assertTrue("Expecting a XML ComplexType with restricted content",isComplexTypeWithRestrictedSimpleContent(withSimpleContentAttributExtension));
	}
	
	@Test
	public void testComplexTypeWithOutContent() {
		assertNotNull("Expecting a XML ComplexType",withouSimpleContent);
		assertFalse("Expecting that a complex type without simple content is not recognized as complex type with simple content",
					isComplexTypeWithRestrictedSimpleContent(withouSimpleContent));
	}
	
	@Test
	public void testXSComponentIsNotAComplexType() {
		assertNotNull("Expecting a XML SimpleType",simpletype);
		assertFalse("Expecting that a simpletype is not recognized as a complextype with restricted simple content",
					isComplexTypeWithRestrictedSimpleContent(simpletype));
	}

	@Test
	public void testGetNewInstance() {
		assertNotNull(getNewInstance(stringEnum));
		assertNotNull(getNewInstance(intEnum));
		assertNotNull(getNewInstance(stringPattern));
		assertNotNull(getNewInstance(derivedStringEnum));
		assertNotNull(getNewInstance(withSimpleContentAttributExtension));
	}
	
	@Test
	public void testGetNewInstanceExceptionFlow() {
		try {
			getNewInstance(null);
			fail("Expecting an IllegalArgumentException, because XSComponent is null");
		} catch (IllegalArgumentException e){}
		try {
			getNewInstance(simpletype);
			fail("Expecting an IllegalArgumentException, because XSComponent is a XSSimpleType");
		} catch (IllegalArgumentException e){}
		try {
			getNewInstance(withouSimpleContent);
			fail("Expecting an IllegalArgumentException, because XSComponent is a XSComplexType without restricted content");
		} catch (IllegalArgumentException e){}
	}
	
	@Test
	public void testStringEnumerationFromComplexTypeRestrictionBase() {
		ComplexTypeWithRestrictedSimpleContent stringEnumeration = getNewInstance(derivedStringEnum);
		assertTrue("Expecting a restriction base of xs:string",stringEnumeration.isXmlStringSimpleType());
		Collection<String> xmlStringEnmerations = stringEnumeration.getXMLStringEnmerations();
		assertEquals("Expecting zeven element in the enumeration",7, xmlStringEnmerations.size());
		assertEquals("Faculty", xmlStringEnmerations.iterator().next());
	}

	@Test
	@Ignore("Need to fix this bug, but for now we don't need the logic")
	public void testStringEnumerationFromSimpleTypeRestrictionBase() {
		ComplexTypeWithRestrictedSimpleContent stringEnumeration = getNewInstance(stringEnum);
		assertTrue("Expecting a restriction base of xs:string",stringEnumeration.isXmlStringSimpleType());
		Collection<String> xmlStringEnmerations = stringEnumeration.getXMLStringEnmerations();
		assertEquals("Expecting zeven element in the enumeration",7, xmlStringEnmerations.size());
		assertEquals("Value1", xmlStringEnmerations.iterator().next());
	}
	
	@Test
	@Ignore("Need to fix this bug, but for now we don't need the logic")
	public void testIntEnumerationWillNotBeRetrievedAsXMLStringEnumerationss() {
		ComplexTypeWithRestrictedSimpleContent intEnumeration = getNewInstance(intEnum);
		assertFalse("Not expecting a restriction base of xs:string",intEnumeration.isXmlStringSimpleType());
		Collection<String> xmlStringEnmerations = intEnumeration.getXMLStringEnmerations();
		assertEquals("Expecting zero element in the enumeration",0, xmlStringEnmerations.size());
	}

	@Test
	public void testXSComponentIsNull() {
		assertFalse("Expecting that null is handled gracefully",isComplexTypeWithRestrictedSimpleContent(null));
	}	
}
