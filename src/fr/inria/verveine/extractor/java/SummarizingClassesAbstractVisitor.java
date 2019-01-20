package fr.inria.verveine.extractor.java;

import org.eclipse.jdt.core.dom.ITypeBinding;

import eu.synectique.verveine.core.gen.famix.NamedEntity;
import eu.synectique.verveine.core.gen.famix.ParameterType;

/**
 * A conveniency class to introduce the {@link #classSummary} flag and a method that uses it.
 * @author anquetil
 */
public abstract class SummarizingClassesAbstractVisitor extends GetVisitedEntityAbstractVisitor {

	/**
	 * Whether to summarize collected information at the level of classes or produce everything
	 * (see {@link VerveineJParser#classSummary}).
	 */
	protected boolean classSummary = false;

	public SummarizingClassesAbstractVisitor(JavaDictionary dico, boolean classSummary) {
		super(dico);
		this.classSummary = classSummary;
	}

	/**
	 * if {@link #classSummary} is true, we persist only classes that are not defined in methods.
	 * @param bnd -- ITypeBinding for the class that we are checking, might be null and in this case, we check whether there is no method at the top of the context
	 * @return whether to persist the class or its members
	 */
	protected boolean persistClass(ITypeBinding bnd) {
		if (bnd != null) {
			if (bnd.isParameterizedType()) {
				// parameterized types seem to never belong to a method even when they are created within one
				// so we kind of "force" persistClass to consider only context by passing a null binding to it
				return persistClass(null);
			} else {
				// let see if it is a type parameter
				NamedEntity t = dico.getEntityByKey(bnd);
				if ((t != null) && (t instanceof ParameterType)) {
					return false;
				}
				// finally, the "normal" case
				return (!classSummary) || (bnd.getDeclaringMethod() == null);
			}
		} else {
			return (!classSummary) || (context.topMethod() == null);
		}
	
	}

}
