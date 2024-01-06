package uk.ac.st_andrews.inspect4j;

import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;


public class Lambda {
    private String expressionAsString;
    private NodeList<Parameter> params;
    private List<ReturnStmt> returnStmts;
    private int lineMin; 
    private int lineMax;

    public Lambda(LambdaExpr lambda, List<ReturnStmt> returnStmts){
        this.expressionAsString = lambda.toString();
        this.params = lambda.getParameters();
        this.returnStmts = returnStmts;
        this.lineMin = lambda.getBegin().get().line;
        this.lineMax = lambda.getEnd().get().line;
    }

    

    public String getExpressionAsString() {
        return expressionAsString;
    }

    public void setExpressionAsString(String expressionAsString) {
        this.expressionAsString = expressionAsString;
    }

    public NodeList<Parameter> getParams() {
        return params;
    }

    public void setParams(NodeList<Parameter> params) {
        this.params = params;
    }

    public List<ReturnStmt> getReturnStmts() {
        return returnStmts;
    }

    public void setReturnStmts(List<ReturnStmt> returnStmts) {
        this.returnStmts = returnStmts;
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

    @Override
    public String toString() {
        return "Lambda [expressionAsString=" + expressionAsString + ", params=" + params + ", returnStmts="
                + returnStmts + ", lineMin=" + lineMin + ", lineMax=" + lineMax + "]";
    }

    
}
