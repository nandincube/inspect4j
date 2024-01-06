package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
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
        getDeclarationInfo();
        printMetadata();
    }

    public void extractMetadata(){
        getDeclarationInfo();

    }
    private void getDeclarationInfo(){
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

    private static class LambdaExprCollector extends VoidVisitorAdapter<List<Lambda>> {
            @Override
            public void visit(LambdaExpr le, List<Lambda> collection) { 
                super.visit(le, collection);
                collection.add(new Lambda( le, getReturnStatements(le)));
            }

    }

    private static List<ReturnStmt>  getReturnStatements(LambdaExpr le){
        List<ReturnStmt> rtns = new ArrayList<ReturnStmt>();
        GenericListVisitorAdapter<ReturnStmt, List<ReturnStmt>> returnPrinter = new ReturnStatementCollector();
        rtns = returnPrinter.visit(le, rtns);
        List<ReturnStmt>  rtnsWithoutDuplicates = new ArrayList<ReturnStmt>();
        for(ReturnStmt rs: rtns){
            if(!rtnsWithoutDuplicates.contains(rs)){
                rtnsWithoutDuplicates.add(rs);
            }
        }
        return rtnsWithoutDuplicates;
    }
    private static class ReturnStatementCollector extends GenericListVisitorAdapter<ReturnStmt, List<ReturnStmt>> {
            @Override
            public List<ReturnStmt> visit(ReturnStmt rs, List<ReturnStmt> arg) { 
                super.visit(rs,arg);
                arg.add(rs);
                return arg;
            }
            
    }
}
