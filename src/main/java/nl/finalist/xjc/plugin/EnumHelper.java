package nl.finalist.xjc.plugin;

import java.util.Collection;
import java.util.Map;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class EnumHelper {

	private static final String ENUM_VALUE_GETTER="GetValue";
	
	public static void addEnumerationToClass(JDefinedClass _class, Collection<String> enumValues) throws JClassAlreadyExistsException {
		
		if (!enumValues.isEmpty()) {
			JInlineEnum inlineEnum = new JInlineEnum(_class, "EnumValue", enumValues);			
			addEnumGetter(_class, inlineEnum.getEnumClass(), "getEnumValue","value");
			addEnumSetter(_class, inlineEnum.getEnumClass(), "setEnumValue", "value");
			overrideSetter(_class, inlineEnum, "setValue", "value");
		}
	}
		
	/**
	 * Adds a method which does a lookup of a member field in an enum and returned the found enum.
	 * @param _class
	 * @param _enum
	 * @param methodName
	 * @param fieldName
	 */
	public static void addEnumGetter(JDefinedClass _class, JDefinedClass _enum, String methodName, String fieldName) {
		JMethod method = _class.method(JMod.PUBLIC, _enum, methodName);
		JBlock body = method.body();
		
		JFieldVar fieldVar = findFieldRecursively(_class, fieldName);
		if (fieldVar != null) {
			JInvocation arg = JExpr.ref(_enum.name()).invoke("valueOf").arg(fieldVar);
			body._return(arg);
		} else {
			throw new IllegalStateException("Cannot find field " + fieldName);
		}
	}

	/**
	 * Adds a method which does a lookup of a member field in an enum and returned the found enum.
	 * @param _class
	 * @param _enum
	 * @param methodName
	 * @param fieldName
	 */
	public static void addEnumSetter(JDefinedClass _class, JDefinedClass _enum, String methodName, String fieldName) {
		//Method signature with parameters
		JMethod method = _class.method(JMod.PUBLIC, _class.owner().VOID, methodName);
		JVar param = method.param(_enum, fieldName);
				
		JBlock body = method.body();
		JFieldVar fieldVar = findFieldRecursively(_class, fieldName);
		if (fieldVar != null) {
			JConditional _if = body._if(param.eq(JExpr._null()));
			_if._then().assign(JExpr._this().ref(fieldVar), JExpr._null());
			_if._else().assign(JExpr._this().ref(fieldVar), param.invoke(ENUM_VALUE_GETTER));
		} else {
			throw new IllegalStateException("Cannot find field " + fieldName);
		}
	}
	
	public static void overrideSetter(JDefinedClass _class, JInlineEnum inlineEnum, String methodName, String fieldName) {
		JMethod method = _class.method(JMod.PUBLIC, _class.owner().VOID, methodName);
		JVar param = method.param(String.class, fieldName);
		method.annotate(Override.class);
		JBlock body = method.body();
		JFieldVar fieldVar = findFieldRecursively(_class, fieldName);
		if (fieldVar != null) {
			JConditional _if = body._if(param.eq(JExpr._null()));
			_if._then().assign(JExpr._this().ref(fieldVar), param);
			JBlock elseBlock = _if._else();
			JConditional elseIfBlock = elseBlock._if(
					JExpr.ref(inlineEnum.getEnumClass().name()).invoke(inlineEnum.getFindMethod()).arg(param).ne(JExpr._null()));
			elseIfBlock._then().assign(JExpr._this().ref(fieldVar), param);
			elseIfBlock._else()._throw(JExpr._new(_class.owner().ref(IllegalArgumentException.class)).arg("Only allowed to set to an existing enum value"));
		} else {
			throw new IllegalStateException("Cannot find field " + fieldName);
		}
		
	}
	
	
	/**
	 * Recursively find a member field of a class by searching the class and all super classes.
	 * @param _class the class to start the search
	 * @param name the name of the field
	 * @return the field or <code>null</code> if not found.
	 */
	public static JFieldVar findFieldRecursively(JDefinedClass _class, String name) {
		
		Map<String, JFieldVar> fields = _class.fields();
		
		if (fields.containsKey(name)) {
			return fields.get(name);
		} else {
			JClass _super = _class._extends();
			if (_super == null || !(_super instanceof JDefinedClass)) {
				return null; //Reached the bottom of the recursion. Field not found.
			} else {
				return findFieldRecursively((JDefinedClass) _super, name);
			}
		}
	}
	
}
