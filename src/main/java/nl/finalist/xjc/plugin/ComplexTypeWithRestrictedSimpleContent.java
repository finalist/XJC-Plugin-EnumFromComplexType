package nl.finalist.xjc.plugin;

import java.util.ArrayList;
import java.util.Collection;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSType;

/**
 * Represents a XML Complextype which contains restricted simple content.
 * 
 * @author ton.swieb
 */
public class ComplexTypeWithRestrictedSimpleContent {
		
	public static String DATATYPE_ENUMERATION = "enumeration";
	public static String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static String XML_STRING = "string";
	public static String XML_ANYTYPE = "anyType";
	
	private final XSRestrictionSimpleType simpleType;
		
	/**
	 * @param component the XML component
	 * @return <code>true</code> if the component is an XML complextype with restricted content, <code>false</code> otherwise.
	 */
	public static boolean isComplexTypeWithRestrictedSimpleContent(XSComponent component) {
		
		if (component instanceof XSComplexType) {
			XSContentType contentType = ((XSComplexType) component).getContentType();
			if (contentType instanceof XSRestrictionSimpleType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param component must be an XML complextype with restricted simple content
	 * @return a {@link ComplexTypeWithRestrictedSimpleContent} instance
	 * @throws IllegalArgumentException in case {@link #isComplexTypeWithRestrictedSimpleContent(XSComponent)} returns <code>false</code>
	 */
	public static ComplexTypeWithRestrictedSimpleContent getNewInstance(XSComponent component) {	
		
		if (isComplexTypeWithRestrictedSimpleContent(component)) {
			XSRestrictionSimpleType simpleType = (XSRestrictionSimpleType) ((XSComplexType) component).getContentType();
			return new ComplexTypeWithRestrictedSimpleContent(simpleType);
		} else {			
			throw new IllegalArgumentException("Component is not a ComplexType with restricted simple content");
		}
	}
	
	/*
	 * Private constructor to prevent instantiation
	 */
	private ComplexTypeWithRestrictedSimpleContent(XSRestrictionSimpleType simpleType) throws IllegalArgumentException {
		this.simpleType = simpleType;
	}
	
	/**
	 * @return <code>true</code> if simple content of this complex type is of type String
	 */
	public boolean isXmlStringSimpleType() {
		
		return isXmlStringSimpleType(simpleType);
//		return isXMLString(simpleType) || isXMLString(simpleType.getSimpleBaseType());
	}
	
	private boolean isXmlStringSimpleType(XSType type) {		
		if (isXMLString(type)) {
			return true;
		} else if (isXMLAnyType(type)) {
			return false; //reached the bottom of the recursion. Nothing found.
		} else {
			return isXmlStringSimpleType(type.getBaseType()); //traversing down
		}
	}
	
	
	/**
	 * @return the enumeration which restricts the content of this complex type or an empty list in case the content is 
	 * not stricted by a XML String enumeration.
	 */
	public Collection<String> getXMLStringEnmerations() {
		return isXmlStringSimpleType() ? getEnumerations() : new ArrayList<String>();
	}	
	
	/**
	 * @return the enumeration which restricts the content of this complex type or an empty list in case the content 
	 * is not restricted by an enumeration.
	 */
	public Collection<String> getEnumerations() {
		Collection<String> enumerations = new ArrayList<String>();
		for (XSFacet declaredFacet : simpleType.getDeclaredFacets()) {
			if (declaredFacet.getName().equals(DATATYPE_ENUMERATION)) {
				enumerations.add(declaredFacet.getValue().value);
			}
		}
		return enumerations;
	}

	private static boolean isXMLAnyType(XSDeclaration declaration) {
		return XML_SCHEMA.equals(declaration.getTargetNamespace()) && XML_ANYTYPE.equals(declaration.getName());
	}
	
	private static boolean isXMLString(XSDeclaration declaration) {
		return XML_SCHEMA.equals(declaration.getTargetNamespace()) && XML_STRING.equals(declaration.getName());
	}
}