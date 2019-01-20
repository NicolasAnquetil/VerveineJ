package fr.inria.verveine.extractor.java.refvisitors;

import java.util.List;

import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import eu.synectique.verveine.core.gen.famix.BehaviouralEntity;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.Reference;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

public class VisitorTypeRefRef extends AbstractRefVisitor {

	/**
	 * what sourceAnchors to create
	 */
	private String anchors;

	public VisitorTypeRefRef(JavaDictionary dico, boolean classSummary, String anchors) {
		super(dico, classSummary);
		this.anchors = anchors;
	}

	// VISITOR METHODS

	@Override
	public boolean visit(CompilationUnit node) {
		visitCompilationUnit(node);
		return super.visit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		endVisitCompilationUnit(node);
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

	@Override
	public boolean visit(ClassInstanceCreation node) {
		if (node.getAnonymousClassDeclaration() == null) {
			Type clazz = node.getType();
			eu.synectique.verveine.core.gen.famix.Type fmx = referedType(clazz, (ContainerEntity) context.top(), true);
			Reference ref = null;
			if (classSummary) {
				//ref = dico.addFamixReference(findHighestType(context.top()), findHighestType(fmx), /*lastReference*/null);
			} else {
				ref = dico.addFamixReference( (BehaviouralEntity) context.top(), fmx, context.getLastReference());
				context.setLastReference(ref);
			}

			if ( anchors.equals(VerveineJParser.ANCHOR_ASSOC) && (ref != null) ) {
				dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		if (visitAnonymousClassDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		endVisitAnonymousClassDeclaration( node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		if (visitEnumDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		endVisitEnumDeclaration( node);
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		if (visitAnnotationTypeDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		endVisitAnnotationTypeDeclaration(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		if (visitAnnotationTypeMemberDeclaration( node) != null) {
			return super.visit(node);
		} else {
			return false;
		}
	}

	public void endVisit(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		setVariablesDeclaredType((List<VariableDeclaration>)node.fragments(), referedType(node.getType(), context.topType(), false));
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
		Method fmx = visitMethodDeclaration( node);
		if (fmx != null) {
			if (! node.isConstructor()) {
				fmx.setDeclaredType(referedType(node.getReturnType2(), fmx, false));
			}

			for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>) node.parameters()) {
				StructuralEntity fmxParam = (StructuralEntity) dico.getEntityByKey(param.resolveBinding());
				if (fmxParam != null) {
					fmxParam.setDeclaredType( referedType(param.getType(), fmx, false) );
				}
			}

			return super.visit(node);
		}

		return false;
	}


	@Override
	public void endVisit(MethodDeclaration node) {
		endVisitMethodDeclaration(node);
	}

	@Override
	public boolean visit(Initializer node) {
		if (visitInitializer(node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@Override
	public void endVisit(Initializer node) {
		endVisitInitializer(node);
	}

	public boolean visit(InstanceofExpression node) {
		eu.synectique.verveine.core.gen.famix.Type fmx = null;
		Type clazz = node.getRightOperand();
		fmx = referedType(clazz, (ContainerEntity) context.top(), true);

		Reference ref = null;
		if (classSummary) {
			//ref = dico.addFamixReference(findHighestType(context.top()), findHighestType(fmx), /*lastReference*/null);
		}
		else {
			ref = dico.addFamixReference((BehaviouralEntity) context.top(), fmx, context.getLastReference());
			context.setLastReference(ref);
		}
		if (anchors.equals(VerveineJParser.ANCHOR_ASSOC)) {
			dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
		}

		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationExpression node) {
		setVariablesDeclaredType((List<VariableDeclaration>)node.fragments(), referedType(node.getType(), context.topMethod(), false));
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
		setVariablesDeclaredType((List<VariableDeclaration>)node.fragments(), referedType(node.getType(), context.topMethod(), false));
		return false;
	}

	private void setVariablesDeclaredType(List<VariableDeclaration> vars, eu.synectique.verveine.core.gen.famix.Type varTyp) {
		for (VariableDeclaration var : vars) {
			StructuralEntity fmx = (StructuralEntity) dico.getEntityByKey(var.resolveBinding());
			if (fmx != null) {
				fmx.setDeclaredType(varTyp);
			}
		}
	}

	// UTILITY METHODS

}
