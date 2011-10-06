package nl.finalist.xjc.plugin;

import static nl.finalist.xjc.plugin.EnumHelper.addEnumerationToClass;
import static nl.finalist.xjc.plugin.ComplexTypeWithRestrictedSimpleContent.*;

import org.xml.sax.ErrorHandler;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;

/**
 * Generates enums
 * @author ton.swieb
 *
 */
public class EnumFromComplexTypePlugin extends Plugin {


	public static final String OPTION_NAME = "XEnumGenerator";
	public static final String USAGE = "-" + OPTION_NAME + " : creates enums in classes constructed from complex types";
	
	@Override
	public String getOptionName() {
		return OPTION_NAME;
	}

	@Override
	public String getUsage() {
		return USAGE;
	}

	@Override
	public boolean run(Outline outline, Options options, ErrorHandler arg2) {
		
		for (ClassOutline classOutline : outline.getClasses()) {
			try {
				handleRestrictionSimpleTypes(classOutline);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	private void handleRestrictionSimpleTypes(ClassOutline classOutline) throws JClassAlreadyExistsException {
		
		XSComponent schemaComponent = classOutline.target.getSchemaComponent();

		//Only process classes that are complextypes with restricted content.
		if (isComplexTypeWithRestrictedSimpleContent(schemaComponent)) {
			ComplexTypeWithRestrictedSimpleContent content = getNewInstance(schemaComponent);
			addEnumerationToClass(classOutline.implClass, content.getXMLStringEnmerations());
		}
	}
}