package nl.finalist.xjc.plugin;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.parser.XSOMParser;

public abstract class XSDBaseTest {

	public final static String NAME_SPACE="http://www.niso.org/2008/ncip";
	public final static String schemaFile = "src/test/resources/schemas/test.xsd";
	public static XSSchemaSet schemaSet;
	public static XSComplexType stringEnum;
	public static XSComplexType intEnum;
	public static XSComplexType stringPattern;
	public static XSComplexType withouSimpleContent;
	public static XSSimpleType simpletype;
	public static XSComplexType withSimpleContentAttributExtension;
	public static XSComplexType derivedStringEnum;

	@BeforeClass
	public static void init() throws SAXException, IOException {
		schemaSet = parseSchema(schemaFile);
		stringEnum = schemaSet.getComplexType(NAME_SPACE, "ComplexTypeWithStringEnumRestrictions");		
		intEnum = schemaSet.getComplexType(NAME_SPACE, "ComplexTypeWithIntegerEnumRestrictions");		
		stringPattern = schemaSet.getComplexType(NAME_SPACE, "ComplexTypeWithStringPattern");		
		withouSimpleContent = schemaSet.getComplexType(NAME_SPACE, "ComplexTypeWithOutSimpleContent");
		withSimpleContentAttributExtension = schemaSet.getComplexType(NAME_SPACE, "SchemeValuePair");
		derivedStringEnum = schemaSet.getComplexType(NAME_SPACE, "DerivedComplexTypeWithStringEnumRestrictions");
		simpletype = schemaSet.getSimpleType(NAME_SPACE, "SimpleType");
	}
	
	private static XSSchemaSet parseSchema(String file) throws SAXException, IOException {
		XSOMParser parser = new XSOMParser();
		parser.parse(new File(file));
		return parser.getResult();
	}	
}
