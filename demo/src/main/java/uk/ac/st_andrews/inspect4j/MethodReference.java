package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.type.Type;


public class MethodReference {
    //private String expressionAsString;
    private String containingEntity;
    private String identifier;
    //private NodeList<String> argumentTypes;
    private List<String> argumentTypes;
    private ParentEntity<?> parent;

    public MethodReference(MethodReferenceExpr methodRef){
        //this.expressionAsString = methodRef.toString();
        this.containingEntity = methodRef.getScope().toString();
        this.identifier = methodRef.getIdentifier();
        this.parent = findParent(methodRef);
        this.argumentTypes = new ArrayList<String>();
        addArgumentTypes(methodRef);
    }

    private void addArgumentTypes(MethodReferenceExpr methodRef){
        if (methodRef.getTypeArguments().isPresent()) {
            methodRef.getTypeArguments().get().stream().forEach(x -> argumentTypes.add(x.asString()));
        }
    }

    // public String getExpressionAsString() {
    //     return expressionAsString;
    // }

    // public void setExpressionAsString(String expressionAsString) {
    //     this.expressionAsString = expressionAsString;
    // }

    public String getContainingEntity() {
        return containingEntity;
    }

    public void setContainingEntity(String containingEntity) {
        this.containingEntity = containingEntity;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getArgumentTypes() {
        return argumentTypes;
    }

    public void setArgumentTypes(List<String> argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    private ParentEntity<?> findParent(MethodReferenceExpr expr) {

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

    private ParentEntity< ClassOrInterfaceDeclaration> findParentClassInterface(MethodReferenceExpr expr) {
        if (expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = expr.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if(parentIC.isInterface()){
                return new ParentEntity<ClassOrInterfaceDeclaration>( parentIC, EntityType.INTERFACE);
            }else{
                return new ParentEntity<ClassOrInterfaceDeclaration>( parentIC, EntityType.CLASS);
            }
            
            //String parentICAString = parentIC.getNameAsString();
            // for (Class cl : classCol.getClasses()) {
            //     if (parentICAString.equals(cl.getName())) {
            //         parentIC.isAncestorOf(parentIC);
            //         return new ParentEntity<Class, ClassOrInterfaceDeclaration>(cl, parentIC, EntityType.CLASS);

            //     }
            // }

            // for (Interface intf : interfCol.getInterfaces()) {
            //     if (parentICAString.equals(intf.getName())) {
            //         return new ParentEntity<Interface, ClassOrInterfaceDeclaration>(intf, parentIC,
            //                 EntityType.INTERFACE);
            //     }
            // }
        }
        return null;
    }

    private ParentEntity<MethodDeclaration> findParentMethod(MethodReferenceExpr expr){
        if(expr.findAncestor(MethodDeclaration.class).isPresent()){
            MethodDeclaration parentMethod = expr.findAncestor(MethodDeclaration.class).get();
            //String parentMethodString = parentMethod.getNameAsString();
           // for(Method md: methodCol.getMethods()){
              //  if(parentMethodString.equals(md.getName())){
                if(parentMethod != null){   
                    return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
                }
            //     }
            // }
           
        }
        return null;
    }



    @Override
    public String toString() {
        return "MethodReference [containingEntity=" + containingEntity + ", identifier=" + identifier
                + ", argumentTypes=" + argumentTypes + ", parent=" + parent + "]";
    }

    public ParentEntity<?> getParent() {
        return parent;
    }

    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

    
    
}
