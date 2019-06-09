package fr.inria.verveine.extractor.java.utils;

import org.eclipse.jdt.core.dom.*;

/**
 * Checks the type of ASTNodes without having to use instanceof<br>
 * Uses double dispatch (ASTVisitor infrastructure)<br>
 * The testing methods are static and use a singleton visitor
 */
public class NodeTypeChecker extends ASTVisitor {

    protected enum ENodeTypes {
        ARRAYACCESS , ARRAYCREATION , ASSIGNMENT , CASTEXPRESSION , CLASSINSTANCECREATION , CONDITIONALEXPRESSION , FIELDACCESS , INFIXEXPRESSION , METHODINVOCATION , PARENTHESIZEDEXPRESSION , QUALIFIEDTYPE , SIMPLENAME , NAME , SIMPLETYPE , STRINGLITERAL , SUPERFIELDACCESS , SUPERMETHODINVOCATION , THISEXPRESSION , TYPELITERAL }

    protected static NodeTypeChecker instance = null;

    protected static NodeTypeChecker getInstance() {
        if (instance == null) {
            instance = new NodeTypeChecker();
        }
        return instance;
    }

    public static boolean isArrayAccess(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.ARRAYACCESS);
    }
    public static boolean isArrayCreation(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.ARRAYCREATION);
    }
    public static boolean isAssignment(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.ASSIGNMENT);
    }
    public static boolean isCastExpression(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.CASTEXPRESSION);
    }
    public static boolean isClassInstanceCreation(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.CLASSINSTANCECREATION);
    }
    public static boolean isConditionalExpression(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.CONDITIONALEXPRESSION);
    }
    public static boolean isFieldAccess(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.FIELDACCESS);
    }
    public static boolean isInfixExpression(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.INFIXEXPRESSION);
    }
    public static boolean isMethodInvocation(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.METHODINVOCATION);
    }
    public static boolean isParenthesizedExpression(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.PARENTHESIZEDEXPRESSION);
    }
    public static boolean isQualifiedType(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.QUALIFIEDTYPE);
    }
    public static boolean isSimpleName(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.SIMPLENAME);
    }
    public static boolean isName(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.NAME);
    }
    public static boolean isSimpleType(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.SIMPLETYPE);
    }
    public static boolean isStringLiteral(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.STRINGLITERAL);
    }
    public static boolean isSuperFieldAccess(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.SUPERFIELDACCESS);
    }
    public static boolean isSuperMethodInvocation(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.SUPERMETHODINVOCATION);
    }
    public static boolean isThisExpression(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.THISEXPRESSION);
    }
    public static boolean isTypeLiteral(ASTNode node) {
        return getInstance().checkNodeType(node, ENodeTypes.TYPELITERAL);
    }


    protected boolean[] checkResults;
    
    protected NodeTypeChecker() {
	checkResults = new boolean[ENodeTypes.values().length];
    }

    private boolean checkNodeType(ASTNode node, ENodeTypes typeToCheck) {
        if (node == null) {
            return false;
        }
        checkResults[typeToCheck.ordinal()] = false;
        node.accept(this);
        return checkResults[typeToCheck.ordinal()];
    }

    @Override
    public boolean visit(ArrayAccess node) {
        checkResults[ENodeTypes.ARRAYACCESS.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(ArrayCreation node) {
        checkResults[ENodeTypes.ARRAYCREATION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(Assignment node) {
        checkResults[ENodeTypes.ASSIGNMENT.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(CastExpression node) {
        checkResults[ENodeTypes.CASTEXPRESSION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(ClassInstanceCreation node) {
        checkResults[ENodeTypes.CLASSINSTANCECREATION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(ConditionalExpression node) {
        checkResults[ENodeTypes.CONDITIONALEXPRESSION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(FieldAccess node) {
        checkResults[ENodeTypes.FIELDACCESS.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(InfixExpression node) {
        checkResults[ENodeTypes.INFIXEXPRESSION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(MethodInvocation node) {
        checkResults[ENodeTypes.METHODINVOCATION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(ParenthesizedExpression node) {
        checkResults[ENodeTypes.PARENTHESIZEDEXPRESSION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(SimpleType node) {
        checkResults[ENodeTypes.SIMPLETYPE.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(QualifiedType node) {
        checkResults[ENodeTypes.QUALIFIEDTYPE.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(SimpleName node) {
        checkResults[ENodeTypes.NAME.ordinal()] = true;
        checkResults[ENodeTypes.SIMPLENAME.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(QualifiedName node) {
        checkResults[ENodeTypes.NAME.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(StringLiteral node) {
        checkResults[ENodeTypes.STRINGLITERAL.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(SuperFieldAccess node) {
        checkResults[ENodeTypes.SUPERFIELDACCESS.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(SuperMethodInvocation node) {
        checkResults[ENodeTypes.SUPERMETHODINVOCATION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(ThisExpression node) {
        checkResults[ENodeTypes.THISEXPRESSION.ordinal()] = true;
        return false;
    }
    @Override
    public boolean visit(TypeLiteral node) {
        checkResults[ENodeTypes.TYPELITERAL.ordinal()] = true;
        return false;
    }

}
