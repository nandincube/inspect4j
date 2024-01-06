package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;

public class Variable {
    private String name;
    private transient Class varClass;
    private transient Interface varInterface;
    private transient Method parentMethod;
    private String methodCalled; //stored variable call

    public Variable(AssignExpr assignment, ClassCollection classes, InterfaceCollection interfaces, MethodCollection methods){
        this.name = assignment.getTarget().asNameExpr().getNameAsString();
        this.varClass = findParentClass(assignment, classes);
        this.varInterface = findParentInterface(assignment, interfaces);
        this.parentMethod = findParentMethod(assignment,methods);
        this.methodCalled = assignment.getValue().asMethodCallExpr().getName().asString();
    }

    private Class findParentClass(AssignExpr expr, ClassCollection classCol){
        if(expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()){
            String pClass = expr.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
            for(Class cl: classCol.getClasses()){
                if(pClass.equals(cl.getName())){
                    return cl;
                }
            }
        }
        return null;
    }

    private Interface findParentInterface(AssignExpr expr, InterfaceCollection interfaceCol){
        if(expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()){
            String pInterf = expr.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
            for(Interface intf: interfaceCol.getInterfaces()){
                if(pInterf.equals(intf.getName())){
                    return intf;
                }
            }
        }
        return null;
    }

    private Method findParentMethod(AssignExpr expr, MethodCollection methodCol){
        if(expr.findAncestor(MethodDeclaration.class).isPresent()){
            String pMethod = expr.findAncestor(MethodDeclaration.class).get().getNameAsString();
            for(Method md: methodCol.getMethods()){
                if(pMethod.equals(md.getName())){
                    return md;
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

    public Class getVarClass() {
        return varClass;
    }

    public void setVarClass(Class varClass) {
        this.varClass = varClass;
    }

    public Method getParentMethod() {
        return parentMethod;
    }

    public void setMethod(Method method) {
        this.parentMethod = method;
    }

    public String getMethodCalled() {
        return methodCalled;
    }

    public void setMethodCalled(String methodCalled) {
        this.methodCalled = methodCalled;
    }


    @Override
    public String toString() {
        String output  = "Variable [name=" + name + ", varClass=";
        output = varClass != null? output + varClass.getName(): output +"null";
        output = output + ", varInterface=" ;
        output = varInterface != null? output + varInterface.getName(): output +"null";
        output = output + ", method=";
        output = parentMethod != null? output + parentMethod.getName(): output +"null";
        output = output + ", methodCalled=" + methodCalled + "]";

        return output;
    }

    public Interface getVarInterface() {
        return varInterface;
    }

    public void setVarInterface(Interface varInterface) {
        this.varInterface = varInterface;
    }
    
}
