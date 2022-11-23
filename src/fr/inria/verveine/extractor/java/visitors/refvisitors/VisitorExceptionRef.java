package fr.inria.verveine.extractor.java.visitors.refvisitors;

import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJOptions;
import fr.inria.verveine.extractor.java.utils.NodeTypeChecker;
import org.eclipse.jdt.core.dom.*;
import org.moosetechnology.model.famixjava.famixjavaentities.Class;
import org.moosetechnology.model.famixjava.famixjavaentities.Exception;
import org.moosetechnology.model.famixjava.famixjavaentities.Method;
import org.moosetechnology.model.famixjava.famixjavaentities.Package;
import org.moosetechnology.model.famixjava.famixjavaentities.ParameterType;
import org.moosetechnology.model.famixjava.famixtraits.TType;

import java.util.List;

/** A visitor to record exceptions declared/thrown/caught.<br>
 * It is simpler than the other ref visitors because we only need to worry about methods
 * @author anquetil
 */
public class VisitorExceptionRef extends AbstractRefVisitor {

    public VisitorExceptionRef(JavaDictionary dico, VerveineJOptions options) {
        super(dico, options);
    }


    protected Package visitCompilationUnit(CompilationUnit node) {
        Package fmx = null;
        PackageDeclaration pckg = node.getPackage();
        if (pckg == null) {
            fmx = dico.getFamixPackageDefault();
        } else {
            fmx = (Package) dico.getEntityByKey(pckg.resolveBinding());
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
                TType excepFmx = (TType) this.referedType(excep.resolveBinding(), context.topType(), true);
                if (excepFmx != null) {
					if (! summarizeClasses()) {
                        // not instanceof because we test the exact type and not subclasses
                        if (excepFmx.getClass() == ParameterType.class) { // excepFmx.getClass() == org.moosetechnology.model.famixjava.famixjavaentities.Exception.class) ||  
                            excepFmx = (Exception) dico.asException(excepFmx);
                        }
                        dico.createFamixDeclaredException(fmx, (Exception) excepFmx);
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
            org.moosetechnology.model.famixjava.famixjavaentities.Exception excepFmx = null;
            if ( NodeTypeChecker.isSimpleType(excepClass) || NodeTypeChecker.isQualifiedType(excepClass) ) {
                excepFmx = (org.moosetechnology.model.famixjava.famixjavaentities.Exception) referedType(excepClass, meth, true);
            }
            if (excepFmx != null) {
                if (! summarizeClasses()) {
                    dico.createFamixCaughtException(meth, excepFmx);
                }
            }
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(ThrowStatement node) {
        Method meth = this.context.topMethod();
        org.moosetechnology.model.famixjava.famixjavaentities.Exception excepFmx = (org.moosetechnology.model.famixjava.famixjavaentities.Exception) this
                .referedType(node.getExpression().resolveTypeBinding(), context.topType(), true);
        if (excepFmx != null) {
            if (! summarizeClasses()) {
                dico.createFamixThrownException(meth, excepFmx);
            }
        }
        return super.visit(node);
    }

}
