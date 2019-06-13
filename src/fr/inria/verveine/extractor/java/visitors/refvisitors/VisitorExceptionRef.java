package fr.inria.verveine.extractor.java.visitors.refvisitors;

import eu.synectique.verveine.core.gen.famix.Class;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.Namespace;
import eu.synectique.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.utils.NodeTypeChecker;
import org.eclipse.jdt.core.dom.*;


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
				eu.synectique.verveine.core.gen.famix.Type excepFmx = this.referedType(excep.resolveBinding(), context.topType(), true);
				if (excepFmx != null) {
					if (! classSummary) {
					    // not instanceof because we test the exact type and not subclasses
					    if ( (excepFmx.getClass() == eu.synectique.verveine.core.gen.famix.Type.class) || (excepFmx.getClass() == ParameterType.class) ) {
					        excepFmx = dico.asClass(excepFmx);
                        }
						dico.createFamixDeclaredException(fmx, (Class) excepFmx);
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
            if ( NodeTypeChecker.isSimpleType(excepClass) || NodeTypeChecker.isQualifiedType(excepClass) ) {
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
