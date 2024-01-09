package uk.ac.st_andrews.inspect4j;

import java.util.HashMap;
import java.util.HashSet;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;


public class Lambda {
    private String expressionAsString;
    private HashMap<String, String> params;
    private HashSet<String> returnStmts;
    private int lineMin; 
    private int lineMax;
    private Method parentMethod;

    public Lambda(LambdaExpr lambda, HashSet<String> returnStmts){
        this.expressionAsString = lambda.toString();
        this.params = new HashMap<String,String>();
        extractParameterInformation(lambda);
        this.returnStmts = returnStmts;
        this.lineMin = lambda.getBegin().get().line;
        this.lineMax = lambda.getEnd().get().line;
        this.parentMethod = null;
    }

    public String getExpressionAsString() {
        return expressionAsString;
    }

    private void extractParameterInformation(LambdaExpr md) {
        if (md.getParameters() != null) {
            for (Parameter param : md.getParameters()) {
                params.put(param.getNameAsString(), param.getTypeAsString());
            }
        }
    }


    public void setExpressionAsString(String expressionAsString) {
        this.expressionAsString = expressionAsString;
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

    private Method findParentMethod(LambdaExpr expr, MethodCollection methodCol){
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

    @Override
    public String toString() {
        return "Lambda [expressionAsString=" + expressionAsString + ", params=" + params + ", returnStmts="
                + returnStmts + ", lineMin=" + lineMin + ", lineMax=" + lineMax + ", parentMethod=" + parentMethod
                + "]";
    }

    public Method getParentMethod() {
        return parentMethod;
    }

    public void setParentMethod(Method parentMethod) {
        this.parentMethod = parentMethod;
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

    
}
