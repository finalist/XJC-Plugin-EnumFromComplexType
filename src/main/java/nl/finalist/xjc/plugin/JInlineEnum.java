package nl.finalist.xjc.plugin;

import java.util.Collection;
import java.util.logging.Logger;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JJavaName;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * Represents an inline java enum
 * @author ton.swieb
 */
public class JInlineEnum {
	
	public final Logger LOG = Logger.getLogger(JInlineEnum.class.getCanonicalName());
	
	private final JDefinedClass enumClass;
	private final JMethod findMethod;
	private final JMethod getValueMethod;
	
	
	private static final String ENUM_VALUE="value";
	private static final String ENUM_VALUE_GETTER="GetValue";
	private static final String ENUM_METHOD_VALUES="values";
	private static final String ENUM_METHOD_FIND="find";	
	
	public JInlineEnum(JDefinedClass _class, String name, Collection<String> enumValues) throws JClassAlreadyExistsException {
		enumClass = _class._enum(name);
		JFieldVar field = enumClass.field(JMod.PRIVATE + JMod.FINAL, String.class, ENUM_VALUE); //Add member field
		addConstructor(enumClass, field);
		getValueMethod = addGetValueMethod(field);
		findMethod = addFindMethod(getValueMethod);
		addEnumInstances(enumClass, enumValues);
	}
	
	private void addConstructor(JDefinedClass _enum, JFieldVar field) {
		
		JMethod constructor = enumClass.constructor(JMod.PRIVATE);
		JVar param = constructor.param(JMod.FINAL, String.class, ENUM_VALUE);
		constructor.body().assign(JExpr._this().ref(field), param);
	}
	
	private JMethod addGetValueMethod (JFieldVar field) {
		
		//Add getter method which returns the value field
		JMethod method = enumClass.method(JMod.PUBLIC, String.class, ENUM_VALUE_GETTER);
		JBlock body = method.body();
		body._return(field);
		return method;
	}
	
	private JMethod addFindMethod(JMethod getValueMethod) {
		
		//Add lookup method which returns the enum or null
		JMethod findMethod = enumClass.method(JMod.PUBLIC + JMod.STATIC, enumClass, ENUM_METHOD_FIND);
		JVar valueParam = findMethod.param(JMod.FINAL,String.class,ENUM_VALUE);
		JBlock findBody = findMethod.body();
		JForEach forEach = findBody.forEach(enumClass, "_enum", JExpr.invoke(ENUM_METHOD_VALUES));
		forEach.body()._if(JExpr.invoke(forEach.var(),getValueMethod).invoke("equals").arg(valueParam))
					  ._then()._return(forEach.var());
		findBody._return(JExpr._null());
		return findMethod;
	}
	
	private void addEnumInstances(JDefinedClass _enum, Collection<String> enumValues) {
		
		for (String enumValue : enumValues) {			
			String typeSafeEnum = getTypeSafeEnum(enumValue).toUpperCase();
			
            if(JJavaName.isJavaIdentifier(typeSafeEnum)) {
    			_enum.enumConstant(typeSafeEnum).arg(JExpr.lit(enumValue));
            } else {
            	LOG.warning(String.format("Skipping enum value %s because it cannot be converted to a valid Java Identifier",enumValue));
            }
		}
	}
	
	private String getTypeSafeEnum(String enumValue) {
        StringBuilder sb = new StringBuilder();
        for( int i=0; i < enumValue.length(); i++) {
            char ch = enumValue.charAt(i);
            if( Character.isJavaIdentifierPart(ch) ) {
                sb.append(ch);                	
            }
            else {
                sb.append('_');                	
            }
        }
        return sb.toString();
	}

	public JDefinedClass getEnumClass() {
		return enumClass;
	}

	public JMethod getFindMethod() {
		return findMethod;
	}

	public JMethod getGetValueMethod() {
		return getValueMethod;
	}	
}