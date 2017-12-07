package eu.synectique.verveine.extractor.java;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;

import eu.synectique.verveine.core.EntityStack;

public class Util {


	/**
	 * Little helper to "normalize" an anonymous type name
	 */
	public static String stringForAnonymousName(String anonymousSuperTypeName, EntityStack context) {
		String anonSuperTypeName = (anonymousSuperTypeName != null) ? anonymousSuperTypeName : context.topType().getName();
		return "anonymous("+anonSuperTypeName+")";
	}

	/**
	 * Helper to compute the name of a JDT Type
	 */
	public static String jdtTypeName(org.eclipse.jdt.core.dom.Type jdtType) {
		if (jdtType == null) {
			return null;
		}

		if (jdtType.isPrimitiveType()) {
			return jdtType.toString();
		} else if (jdtType.isSimpleType()) {
			return ((SimpleType) jdtType).getName().getFullyQualifiedName();
		} else if (jdtType.isQualifiedType()) {
			return ((QualifiedType) jdtType).getName().getIdentifier();
		} else if (jdtType.isArrayType()) {
			return jdtTypeName(((ArrayType) jdtType).getElementType());
		} else if (jdtType.isParameterizedType()) {
			return jdtTypeName(((org.eclipse.jdt.core.dom.ParameterizedType) jdtType).getType());
		} else { // it is a WildCardType
			if (((org.eclipse.jdt.core.dom.WildcardType) jdtType).isUpperBound()) {
				return jdtTypeName(((org.eclipse.jdt.core.dom.WildcardType) jdtType).getBound());
			} else {
				return JavaDictionary.OBJECT_NAME;
			}
		}
	}

}
