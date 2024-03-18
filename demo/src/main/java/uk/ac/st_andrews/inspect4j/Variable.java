package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

/**
 * Class to represent a variable that is assigned a method call or object
 * creation (i.e stored_variable_calls)
 */
public class Variable {
    private String name; // name of the variable
    private ParentEntity<?> parent; // parent entity of the variable - i.e. method or class/interface
    private String methodCalled; // method call assigned to the variable
    private String javaDoc; // javadoc of the variable

    /**
     * Constructor
     * 
     * @param assignment - the assignment expression
     */
    public Variable(AssignExpr assignment) {
        Expression identifier = assignment.getTarget();
        if (identifier.isNameExpr()) {
            this.name = identifier.asNameExpr().getNameAsString();
        }
        if (identifier.isFieldAccessExpr()) {
            this.name = identifier.asFieldAccessExpr().getScope() + "."
                    + identifier.asFieldAccessExpr().getNameAsString();
        }

        this.parent = findParent(assignment);
        String scope; // scope of the method call
        if (assignment.getValue().isMethodCallExpr()) { // if the value is a method call
            scope = assignment.getValue().asMethodCallExpr().getScope().isPresent()
                    ? assignment.getValue().asMethodCallExpr().getScope().get().toString() + "."
                    : "";
            this.methodCalled = scope + assignment.getValue().asMethodCallExpr().getName().asString();
        } else { // if the value is an object creation
            scope = assignment.getValue().asObjectCreationExpr().getScope().isPresent()
                    ? assignment.getValue().asObjectCreationExpr().getScope().get().toString() + "."
                    : "";
            this.methodCalled = scope + assignment.getValue().asObjectCreationExpr().getTypeAsString();

        }

        if (identifier.isNameExpr()) { // if the identifier is a name expression
            this.javaDoc = findJavaDoc(identifier.asNameExpr());
        }
        if (identifier.isFieldAccessExpr()) { // if the identifier is a field access expression
            this.javaDoc = findJavaDoc(identifier.asFieldAccessExpr());
        }
    }

    /**
     * Constructor
     * 
     * @param var      - the variable declarator
     * @param parentIC - the parent entity
     * @param mc       - the method call expression
     */
    public Variable(VariableDeclarator var, ParentEntity<?> parentIC, Expression mc) {
        this.name = var.getNameAsString();
        this.parent = parentIC;
        if (mc.isMethodCallExpr()) { // if the expression is a method call
            String scope = mc.asMethodCallExpr().getScope().isPresent()
                    ? mc.asMethodCallExpr().getScope().get().toString() + "."
                    : "";
            this.methodCalled = scope + mc.asMethodCallExpr().getName().asString();
        } else { // if the expression is an object creation
            String scope = mc.asObjectCreationExpr().getScope().isPresent()
                    ? mc.asObjectCreationExpr().getScope().get().toString() + "."
                    : "";
            this.methodCalled = scope + mc.asObjectCreationExpr().getTypeAsString();
        }

        this.javaDoc = findJavaDoc(var);
    }

    /**
     * gets JavaDoc comment
     * 
     * @return - the JavaDoc comment
     */
    public String getJavaDoc() {
        return javaDoc;
    }

    /**
     * sets JavaDoc comment
     * 
     * @param javaDoc - the JavaDoc comment
     */
    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    /**
     * finds the javadoc comment for the variable (NameExpr)
     * 
     * @param var - the variable
     * @return - the javadoc comment
     */
    private String findJavaDoc(NameExpr var) {
        if (var.getComment().isPresent()) {
            if (var.getComment().get().isJavadocComment()) {
                return var.getComment().get().getContent().strip();
            }
        }
        return null;
    }

    /**
     * finds the javadoc comment for the variable
     * 
     * @param var - the variable
     * @return - the javadoc comment
     */
    private String findJavaDoc(FieldAccessExpr var) {
        if (var.getComment().isPresent()) {
            if (var.getComment().get().isJavadocComment()) {
                return var.getComment().get().getContent().strip();
            }
        }
        return null;
    }

    /**
     * finds the javadoc comment for the variable
     * 
     * @param var - the variable
     * @return - the javadoc comment
     */
    private String findJavaDoc(VariableDeclarator var) {
        if (var.getComment().isPresent()) {
            if (var.getComment().get().isJavadocComment()) {
                return var.getComment().get().getContent().strip();
            }
        }
        return null;
    }

    /**
     * finds the parent entity of the variable. The parent entity can be a
     * class/interface or a method that the variable is declared/assigned in.
     * 
     * @param expr - the assignment expression
     * @return - the parent entity
     */
    private ParentEntity<?> findParent(AssignExpr expr) {

        ParentEntity<ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(expr);
        ParentEntity<MethodDeclaration> parentMethod = findParentMethod(expr);

        if (parentIC == null && parentMethod == null)
            return null;
        if (parentIC == null && parentMethod != null)
            return parentMethod;
        if (parentIC != null && parentMethod == null)
            return parentIC;
        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) {
            return parentMethod;
        }
        return parentIC;
    }

    /**
     * finds the parent class/interface of the variable if it exists
     * 
     * @param expr - the assignment expression - the variable
     * @return - the parent class/interface if it exists
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(AssignExpr expr) {
        if (expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = expr.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if (parentIC.isInterface()) {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.INTERFACE);
            } else {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.CLASS);
            }

        }
        return null;
    }

    /**
     * finds the parent method of the variable if it exists
     * 
     * @param expr - the assignment expression - the variable
     * @return - the parent method if it exists
     */
    private ParentEntity<MethodDeclaration> findParentMethod(AssignExpr expr) {
        if (expr.findAncestor(MethodDeclaration.class).isPresent()) {
            MethodDeclaration parentMethod = expr.findAncestor(MethodDeclaration.class).get();
            if (parentMethod != null) {
                return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
            }
        }
        return null;
    }

    /**
     * gets the name of the variable
     * 
     * @return - the name of the variable
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the variable
     * 
     * @param name - the name of the variable
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets the method call assigned to the variable
     * 
     * @return - the method call assigned to the variable
     */
    public String getMethodCalled() {
        return methodCalled;
    }

    /**
     * sets the method call assigned to the variable
     * 
     * @param methodCalled - the method call assigned to the variable
     */
    public void setMethodCalled(String methodCalled) {
        this.methodCalled = methodCalled;
    }

    /**
     * gets the parent entity of the variable
     * 
     * @return - the parent entity of the variable
     */
    public ParentEntity<?> getParent() {
        return parent;
    }

    /**
     * sets the parent entity of the variable
     * 
     * @param parent - the parent entity of the variable
     */
    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

}
