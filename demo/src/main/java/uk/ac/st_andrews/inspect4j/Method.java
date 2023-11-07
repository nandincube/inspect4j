package uk.ac.st_andrews.inspect4j;

import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.ReturnStmt;


public class Method {
    private String declarationAsString;
    private String name;
    private NodeList<Parameter> params;
    private List<ReturnStmt> returnStmts = null;
    private  int lineMin; 
    private  int lineMax;

    public Method( String decl, String name, NodeList<Parameter> params,  List<ReturnStmt> returnStmts, int lineMin, int lineMax){
        this.declarationAsString = decl;
        this.name = name;
        this.params = params;
        this.returnStmts = returnStmts;
        this.lineMin = lineMin;
        this.lineMax = lineMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeList<Parameter> getParams() {
        return params;
    }

    public void setParams(NodeList<Parameter> params) {
        this.params = params;
    }

    public String getDeclarationAsString() {
        return declarationAsString;
    }

    public void setDeclarationAsString(String declarationAsString) {
        this.declarationAsString = declarationAsString;
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
        return "Method [declarationAsString=" + declarationAsString + ", name=" + name + ", params=" + params
                + ", returnStmts=" + returnStmts + ", lineMin=" + lineMin + ", lineMax=" + lineMax + "]\n";
    }
    
}
