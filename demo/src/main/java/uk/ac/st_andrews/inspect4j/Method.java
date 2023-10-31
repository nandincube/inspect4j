package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class Method {
    private String declarationAsString;
    private String name;
    private NodeList params;
    private NodeList<Statement> body;
    private NodeList returnStmts;
    
    public Method(String decl, String name, NodeList params, NodeList<Statement> body){
        this.declarationAsString = decl;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeList getParams() {
        return params;
    }

    public void setParams(NodeList params) {
        this.params = params;
    }

    public NodeList<Statement> getBody() {
        return body;
    }

    public void setBody(NodeList<Statement> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Method [declarationAsString=" + declarationAsString + ", name=" + name + ", params=" + params
                + ", body=" + body + ", returnStmts=" + returnStmts + "]";
    }

    

}
