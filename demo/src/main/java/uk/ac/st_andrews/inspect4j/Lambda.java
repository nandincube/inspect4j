package uk.ac.st_andrews.inspect4j;

import java.util.HashMap;
import java.util.HashSet;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;


public class Lambda {
    private String bodyAsString;
    private HashMap<String, String> params;
    private HashSet<String> returnStmts;
    private ParentEntity<?> parent;
    private int lineMin; 
    private int lineMax;
    //private Method parentMethod;

    public Lambda(LambdaExpr lambda, HashSet<String> returnStmts){
        this.bodyAsString = lambda.getBody().toString();
        this.params = new HashMap<String,String>();
        extractParameterInformation(lambda);
        this.returnStmts = returnStmts;
        this.lineMin = lambda.getBegin().get().line;
        this.lineMax = lambda.getEnd().get().line;
        this.parent = findParent(lambda);
    }

    public String getBodyAsString() {
        return bodyAsString;
    }

    private void extractParameterInformation(LambdaExpr md) {
        if (md.getParameters() != null) {
            for (Parameter param : md.getParameters()) {
                params.put(param.getNameAsString(), param.getTypeAsString());
            }
        }
    }

    public void setExpressionAsString(String expressionAsString) {
        this.bodyAsString = expressionAsString;
    }

    public int getLineMin() {
        return lineMin;
    }

    public void setLineMin(int lineMin) {
        this.lineMin = lineMin;
    }

    public int getLineMax() {
        return lineMax;
    }

    public void setLineMax(int lineMax) {
        this.lineMax = lineMax;
    }

    // private Method findParentMethod(LambdaExpr expr, MethodCollection methodCol){
    //     if(expr.findAncestor(MethodDeclaration.class).isPresent()){
    //         String pMethod = expr.findAncestor(MethodDeclaration.class).get().getNameAsString();
    //         for(Method md: methodCol.getMethods()){
    //             if(pMethod.equals(md.getName())){
    //                 return md;
    //             }
    //         }
    //     }
    //     return null;
    // }


    private ParentEntity<?> findParent(LambdaExpr expr) {

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

    private ParentEntity< ClassOrInterfaceDeclaration> findParentClassInterface(LambdaExpr expr) {
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

    
    private ParentEntity<MethodDeclaration> findParentMethod(LambdaExpr expr){
        if(expr.findAncestor(MethodDeclaration.class).isPresent()){
            MethodDeclaration parentMethod = expr.findAncestor(MethodDeclaration.class).get();
            if(parentMethod != null){   
                return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
            }
        }
        return null;
    }



    // public Method getParentMethod() {
    //     return parentMethod;
    // }

    // public void setParentMethod(Method parentMethod) {
    //     this.parentMethod = parentMethod;
    // }

    @Override
    public String toString() {
        return "Lambda [expressionAsString=" + bodyAsString + ", params=" + params + ", returnStmts="
                + returnStmts + ", parent=" + parent + ", lineMin=" + lineMin + ", lineMax=" + lineMax + "]";
    }

    public HashSet<String> getReturnStmts() {
        return returnStmts;
    }

    public void setReturnStmts(HashSet<String> returnStmts) {
        this.returnStmts = returnStmts;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public ParentEntity<?> getParent() {
        return parent;
    }

    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

    
}
