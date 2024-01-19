package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class Variable {
    private String name;
    private ParentEntity<?> parent;
    private String methodCalled;
    private String javaDoc;

    public Variable(AssignExpr assignment) {
        this.name = assignment.getTarget().asNameExpr().getNameAsString();
        this.parent = findParent(assignment);
        this.methodCalled = assignment.getValue().asMethodCallExpr().getName().asString();
        this.javaDoc = findJavaDoc(assignment.getTarget().asNameExpr());
    }

    public String getJavaDoc() {
        return javaDoc;
    }

    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    private String findJavaDoc(NameExpr var) {
        if (var.getComment().isPresent()) {
            if (var.getComment().get().isJavadocComment()) {
                return var.getComment().get().getContent().strip();
            }
        }
        return null;
    }

    private ParentEntity<?> findParent(AssignExpr expr) {

        ParentEntity<ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(expr);
        ParentEntity<MethodDeclaration> parentMethod = findParentMethod(expr);

        if(parentIC == null && parentMethod == null) return null;
        if(parentIC == null && parentMethod != null) return parentMethod;
        if(parentIC != null && parentMethod == null) return parentIC;
        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) {
            return parentMethod;
        }
        return parentIC;
    }

    private ParentEntity< ClassOrInterfaceDeclaration> findParentClassInterface(AssignExpr expr) {
        if (expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = expr.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if(parentIC.isInterface()){
                return new ParentEntity<ClassOrInterfaceDeclaration>( parentIC, EntityType.INTERFACE);
            }else{
                return new ParentEntity<ClassOrInterfaceDeclaration>( parentIC, EntityType.CLASS);
            }
            
        }
        return null;
    }

    private ParentEntity<MethodDeclaration> findParentMethod(AssignExpr expr){
        if(expr.findAncestor(MethodDeclaration.class).isPresent()){
            MethodDeclaration parentMethod = expr.findAncestor(MethodDeclaration.class).get();   
            if(parentMethod != null){
                return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethodCalled() {
        return methodCalled;
    }

    public void setMethodCalled(String methodCalled) {
        this.methodCalled = methodCalled;
    }

    public ParentEntity<?> getParent() {
        return parent;
    }

    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

}
