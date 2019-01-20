package eu.synectique.verveine.extractor.java.utils;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;

import eu.synectique.verveine.core.Dictionary;
import eu.synectique.verveine.core.gen.famix.Method;



/**
 * This is a JDT {@link IBinding} implementor to serve as key for implicit variables (this, super)
 * Rational: The Famix dictionary needs an IBinding as entity key.
 * But implicit variables don't have associated JDT binding
 * So we create this class that will implement a fake IBinding for each implicit variables in each behaviouralEntity it appears in.
 * The actual key will composed from the binding of its owner behaviouralEntity and its name (this, super)
 * @author Anquetil
 */
public class ImplicitVarBinding implements IBinding {
	protected Method owner;
	protected String name;
	
	static Map<Method,ImplicitVars> allImplicitVarBnd = new Hashtable<>();

	/**
	 * Used to keep the two possible ImplicitVariable for a given class
	 * @author anquetil
	 */
	protected static class ImplicitVars {
		public ImplicitVarBinding self_iv;
		public ImplicitVarBinding super_iv;
	}
	
	public static ImplicitVarBinding getInstance(Method owner, String name) {
		ImplicitVarBinding bnd = null;
		ImplicitVars vars = allImplicitVarBnd.get(owner);
		if (vars == null) {
			vars = new ImplicitVars();
			allImplicitVarBnd.put(owner, vars);
		}
		else {
			bnd = (name.equals(Dictionary.SELF_NAME) ? vars.self_iv : vars.super_iv);
		}

		if (bnd == null) {
			bnd = new ImplicitVarBinding(owner,name);
			if (name.equals(Dictionary.SELF_NAME)) {
				vars.self_iv = bnd;
			}
			else {
				vars.super_iv = bnd;
			}
		}
		
		return bnd;
	}

	protected ImplicitVarBinding(Method owner, String name) {
		this.owner = owner;
		this.name = name;
	}

	@Override
	public IAnnotationBinding[] getAnnotations() {
		return null;
	}

	@Override
	public IJavaElement getJavaElement() {
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getKind() {
		return 0;
	}

	@Override
	public int getModifiers() {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}

	@Override
	public boolean isEqualTo(IBinding arg0) {
		return this.equals(arg0);
	}

	@Override
	public boolean isRecovered() {
		return false;
	}

	@Override
	public boolean isSynthetic() {
		return false;
	}

}
