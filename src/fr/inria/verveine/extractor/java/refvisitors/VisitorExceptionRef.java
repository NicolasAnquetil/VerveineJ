package fr.inria.verveine.extractor.java.refvisitors;

import eu.synectique.verveine.core.gen.famix.*;
import eu.synectique.verveine.core.gen.famix.Class;
import fr.inria.verveine.extractor.java.GetVisitedEntityAbstractVisitor;
import fr.inria.verveine.extractor.java.JavaDictionary;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Type;

import java.util.List;

/** A visitor to record exceptions declared/thrown/caught.<br>
 * It is simpler than the other ref visitors because we only need to worry about methods
 * @author anquetil
 */
public class VisitorExceptionRef extends AbstractRefVisitor {

	public VisitorExceptionRef(JavaDictionary dico, boolean classSummary) {
		super(dico, classSummary);
	}


    protected Namespace visitCompilationUnit(CompilationUnit node) {
        Namespace fmx = null;
        PackageDeclaration pckg = node.getPackage();
        if (pckg == null) {
            fmx = dico.getFamixNamespaceDefault();
        } else {
            fmx = (Namespace) dico.getEntityByKey(pckg.resolveBinding());
        }
        this.context.pushPckg(fmx);

        return fmx;
    }

    protected void endVisitCompilationUnit(CompilationUnit node) {
        this.context.popPckg();
        super.endVisit(node);
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (visitTypeDeclaration( node) != null) {
            return super.visit(node);
        } else {
            return false;
        }
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        endVisitTypeDeclaration(node);
    }

	public boolean visit(MethodDeclaration node) {
		Method fmx = visitMethodDeclaration( node);
		if (fmx != null) {
		    for (Type excep : (List<Type>)node.thrownExceptionTypes()) {
				Class excepFmx = (Class) this.referedType(excep.resolveBinding(), context.topType(), true);
				if (excepFmx != null) {
					if (! classSummary) {
						dico.createFamixDeclaredException(fmx, excepFmx);
					}
				}
			}
			return super.visit(node);
		} else {
			return false;
		}
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		endVisitMethodDeclaration(node);
	}

    @Override
    public boolean visit(CatchClause node) {
        Method meth = this.context.topMethod();
        Type excepClass = node.getException().getType();
        if (meth != null) {
            eu.synectique.verveine.core.gen.famix.Class excepFmx = null;
            if ((excepClass instanceof SimpleType) || (excepClass instanceof QualifiedType)) {
                excepFmx = (Class) referedType(excepClass, meth, true);
            }
            if (excepFmx != null) {
                if (! classSummary) {
                    dico.createFamixCaughtException(meth, excepFmx);
                }
            }
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(ThrowStatement node) {
        Method meth = this.context.topMethod();
        eu.synectique.verveine.core.gen.famix.Class excepFmx = (Class) this
                .referedType(node.getExpression().resolveTypeBinding(), context.topType(), true);
        if (excepFmx != null) {
            if (! classSummary) {
                dico.createFamixThrownException(meth, excepFmx);
            }
        }
        return super.visit(node);
    }

}
