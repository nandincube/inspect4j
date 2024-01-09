package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class Variable {
    private String name;
    private ParentEntity<?, ?> parent;
    private AssignExpr nodeRef;
    // private transient Class varClass;
    // private transient Interface varInterface;
    // private transient Method parentMethod;
    private String methodCalled;
    private String javaDoc;

    public String getJavaDoc() {
        return javaDoc;
    }

    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    public Variable(AssignExpr assignment, ClassCollection classes, InterfaceCollection interfaces,
            MethodCollection methods) {
        this.name = assignment.getTarget().asNameExpr().getNameAsString();
        this.nodeRef = assignment;
        // this.varClass = findParentClass(assignment, classes);
        // this.varInterface = findParentInterface(assignment, interfaces);
        // this.parentMethod = findParentMethod(assignment,methods);

        this.parent = findParent(assignment, classes, interfaces, methods);
        this.methodCalled = assignment.getValue().asMethodCallExpr().getName().asString();
        this.javaDoc = getJavaDoc(assignment.getTarget().asNameExpr());
    }

    private String getJavaDoc(NameExpr var) {
        if (var.getComment().isPresent()) {
            if (var.getComment().get().isJavadocComment()) {
                return var.getComment().get().getContent().strip();
            }
        }
        return null;
    }

    private ParentEntity<?, ?> findParent(AssignExpr expr, ClassCollection classes, InterfaceCollection interfaces,
            MethodCollection methods) {

        ParentEntity<?, ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(expr, classes, interfaces);
        ParentEntity<Method, MethodDeclaration> parentMethod = findParentMethod(expr, methods);

        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) {
            return parentMethod;
        }
        return parentIC;
    }

    private ParentEntity<?, ClassOrInterfaceDeclaration> findParentClassInterface(AssignExpr expr,
            ClassCollection classCol, InterfaceCollection interfCol) {
        if (expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = expr.findAncestor(ClassOrInterfaceDeclaration.class).get();
            String parentICAString = parentIC.getNameAsString();
            for (Class cl : classCol.getClasses()) {
                if (parentICAString.equals(cl.getName())) {
                    parentIC.isAncestorOf(parentIC);
                    return new ParentEntity<Class, ClassOrInterfaceDeclaration>(cl, parentIC, EntityType.CLASS);

                }
            }

            for (Interface intf : interfCol.getInterfaces()) {
                if (parentICAString.equals(intf.getName())) {
                    return new ParentEntity<Interface, ClassOrInterfaceDeclaration>(intf, parentIC,
                            EntityType.INTERFACE);
                }
            }
        }
        return null;
    }

    // private Interface findParentInterface(AssignExpr expr, InterfaceCollection
    // interfaceCol){
    // if(expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()){
    // String pInterf =
    // expr.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
    // for(Interface intf: interfaceCol.getInterfaces()){
    // if(pInterf.equals(intf.getName())){
    // return intf;
    // }
    // }
    // }
    // return null;
    // }

    // private Method findParentMethod(AssignExpr expr, MethodCollection methodCol){
    // if(expr.findAncestor(MethodDeclaration.class).isPresent()){
    // String pMethod =
    // expr.findAncestor(MethodDeclaration.class).get().getNameAsString();
    // for(Method md: methodCol.getMethods()){
    // if(pMethod.equals(md.getName())){
    // return md;
    // }
    // }
    // }
    // return null;
    // }

    private ParentEntity<Method, MethodDeclaration> findParentMethod(AssignExpr expr, MethodCollection methodCol){
        if(expr.findAncestor(MethodDeclaration.class).isPresent()){
            MethodDeclaration parentMethod = expr.findAncestor(MethodDeclaration.class).get();
            String parentMethodString = parentMethod.getNameAsString();
            for(Method md: methodCol.getMethods()){
                if(parentMethodString.equals(md.getName())){
                    return new ParentEntity<Method, MethodDeclaration>(md, parentMethod, EntityType.METHOD);
                }
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

    // public Class getVarClass() {
    // return varClass;
    // }

    // public void setVarClass(Class varClass) {
    // this.varClass = varClass;
    // }

    // public Method getParentMethod() {
    // return parentMethod;
    // }

    // public void setMethod(Method method) {
    // this.parentMethod = method;
    // }

    // public void setParentMethod(Method parentMethod) {
    // this.parentMethod = parentMethod;
    // }
    public String getMethodCalled() {
        return methodCalled;
    }

    public void setMethodCalled(String methodCalled) {
        this.methodCalled = methodCalled;
    }

    public ParentEntity<?,?> getParent() {
        return parent;
    }

    public void setParent(ParentEntity<?,?> parent) {
        this.parent = parent;
    }

    // @Override
    // public String toString() {
    // String output = "Variable [name=" + name + ", varClass=";
    // output = varClass != null? output + varClass.getName(): output +"null";
    // output = output + ", varInterface=" ;
    // output = varInterface != null? output + varInterface.getName(): output
    // +"null";
    // output = output + ", method=";
    // output = parentMethod != null? output + parentMethod.getName(): output
    // +"null";
    // output = output + ", methodCalled=" + methodCalled + "]";

    // return output;
    // }

    // public Interface getVarInterface() {
    // return varInterface;
    // }

    // public void setVarInterface(Interface varInterface) {
    // this.varInterface = varInterface;
    // }



}
