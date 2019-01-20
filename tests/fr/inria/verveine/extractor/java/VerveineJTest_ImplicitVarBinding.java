package fr.inria.verveine.extractor.java;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.junit.Before;
import org.junit.Test;

import eu.synectique.verveine.core.Dictionary;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.extractor.java.utils.ImplicitVarBinding;

public class VerveineJTest_ImplicitVarBinding {

	private Method mth1;
	private Method mth2;

	@Before
	public void setup() {
		mth1 = new Method();
		mth2 = new Method();
	}

	@Test
	public void testUniqForMethod() {
		assertEquals(ImplicitVarBinding.getInstance(mth1, Dictionary.SELF_NAME), ImplicitVarBinding.getInstance(mth1, Dictionary.SELF_NAME));
		assertEquals(ImplicitVarBinding.getInstance(mth1, Dictionary.SUPER_NAME), ImplicitVarBinding.getInstance(mth1, Dictionary.SUPER_NAME));
	}
	
	@Test
	public void testDiffForMethods() {
		assertNotEquals(ImplicitVarBinding.getInstance(mth1, Dictionary.SELF_NAME), ImplicitVarBinding.getInstance(mth2, Dictionary.SELF_NAME));
		assertNotEquals(ImplicitVarBinding.getInstance(mth1, Dictionary.SUPER_NAME), ImplicitVarBinding.getInstance(mth2, Dictionary.SUPER_NAME));
	}

	@Test
	public void testSefDiffSuper() {
		assertNotEquals(ImplicitVarBinding.getInstance(mth1, Dictionary.SELF_NAME), ImplicitVarBinding.getInstance(mth1, Dictionary.SUPER_NAME));
	}
	
}
