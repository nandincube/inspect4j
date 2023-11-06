package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodCollection {
    private ArrayList<Method> methods;
    private CompilationUnit ast;

  
    public MethodCollection(CompilationUnit ast){
        this.methods = new ArrayList<>();
        this.ast = ast;
    }

    public void getMetadata(){
        getDeclarationInfo();
        //getMethodDocumentation();
        //getMethodReturnStatements();
        printMetadata();
    }

    private void getDeclarationInfo(){
        VoidVisitor<List<Method>> methodDeclCollector = new MethodDeclarationCollector();
        methodDeclCollector.visit(ast, methods);
    }
    
 
    private void getMethodDocumentation(){

    }

    public ArrayList<Method> getMethods() {
        return methods;
    }

    public void setMethods(ArrayList<Method> methods) {
        this.methods = methods;
    }

    public CompilationUnit getAst() {
        return ast;
    }

    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    private void printMetadata(){
        methods.forEach(x -> System.out.println(x.toString()));

    }

    private static class MethodDeclarationCollector extends VoidVisitorAdapter<List<Method>> {
            @Override
            public void visit(MethodDeclaration md, List<Method> collection) { 
                super.visit(md,collection);
                collection.add(new Method( md.getDeclarationAsString(true,true,true),
                md.getNameAsString(), md.getParameters(), getReturnStatements(md), md.getBegin().get().line,md.getEnd().get().line));
            }

    }

    private static List<ReturnStmt>  getReturnStatements(MethodDeclaration md){
        List<ReturnStmt> rtns = new ArrayList<ReturnStmt>();
        GenericListVisitorAdapter<ReturnStmt, List<ReturnStmt>> returnPrinter = new ReturnStatementCollector();
        rtns = returnPrinter.visit(md, rtns);
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
