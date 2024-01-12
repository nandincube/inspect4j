package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class LambdaCollection {
    private ArrayList<Lambda> lambdas;
    private CompilationUnit ast;

  
    public LambdaCollection(CompilationUnit ast){
        this.lambdas = new ArrayList<>();
        this.ast = ast;
    }

    public void getMetadata(){
        extractLambdasFromAST();
        printMetadata();
    }

    public void extractLambdasFromAST(){
        VoidVisitor<List<Lambda>> lambdaDeclCollector = new LambdaExprCollector();
        lambdaDeclCollector.visit(ast, lambdas);
    }
    
    public ArrayList<Lambda> getLambdas() {
        return lambdas;
    }

    public void setLambdas(ArrayList<Lambda> lambdas) {
        this.lambdas = lambdas;
    }

    public CompilationUnit getAst() {
        return ast;
    }

    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    public void printMetadata(){
        lambdas.forEach(x -> System.out.println(x.toString()));

    }

    private class LambdaExprCollector extends VoidVisitorAdapter<List<Lambda>> {
            @Override
            public void visit(LambdaExpr le, List<Lambda> collection) { 
                super.visit(le, collection);
                collection.add(new Lambda( le, getReturnStatements(le)));
            }

    }

    private HashSet<String> getReturnStatements(LambdaExpr le){
        List<String> rtns = new ArrayList<String>();
        GenericListVisitorAdapter<String, List<String>> returnPrinter = new ReturnStatementCollector();
        return new HashSet<String>(returnPrinter.visit(le, rtns));
    }

    private class ReturnStatementCollector extends GenericListVisitorAdapter<String, List<String>> {
        @Override
        public List<String> visit(ReturnStmt rs, List<String> arg) { 
            super.visit(rs,arg);
            if(rs.getExpression().isPresent()){
                String expr = rs.getExpression().get().toString();
                arg.add(expr);
            }
            
            return arg;
        }     
}
}
