package fr.inria.verveine.extractor.java.visitors;

import fr.inria.verveine.extractor.java.EntityDictionary;
import fr.inria.verveine.extractor.java.VerveineJOptions;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.moosetechnology.model.famix.famixjavaentities.ParameterType;
import org.moosetechnology.model.famix.famixtraits.TNamedEntity;

/**
 * A conveniency class to introduce the {@link #classSummary} flag and a method that uses it.
 * @author anquetil
 */
public abstract class SummarizingClassesAbstractVisitor extends GetVisitedEntityAbstractVisitor {

	public SummarizingClassesAbstractVisitor(EntityDictionary dico, VerveineJOptions options) {
		super(dico, options);
	}

	/**
	 * if we are not summarizing the model, we persist only classes that are not defined in methods and stub types.
	 * @param bnd -- ITypeBinding for the class that we are checking, might be null and in this case, we check whether there is no method at the top of the context
	 * @return whether to persist the class or its members
	 */
	protected boolean persistClass(ITypeBinding bnd) {
		if (! summarizeModel()) {
			return true;
		}

		if (bnd != null) {
			if (bnd.isParameterizedType()) {
				// parameterized types seem to never belong to a method even when they are created within one
				// so we kind of "force" persistClass to consider only context by passing a null binding to it
				return persistClass(null);
			} else {
				// let see if it is a type parameter
				TNamedEntity t = (TNamedEntity) dico.getEntityByKey(bnd);
				if ((t != null) && (t instanceof ParameterType)) {
					return false;
				}
				// finally, the "normal" case
				return (bnd.getDeclaringMethod() == null);
			}
		} else {
			return (context.topMethod() == null);
		}
	
	}

	/**
	 * Syntactic sugar: whether to summarize classes or not
	 */
	protected boolean summarizeModel() {
		return options.summarizeModel();
	}
}
