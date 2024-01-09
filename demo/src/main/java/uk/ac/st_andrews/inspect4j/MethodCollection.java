package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashSet;
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
    private ClassCollection classes;
    private InterfaceCollection interfaces;

  
    public MethodCollection(CompilationUnit ast){
        this.methods = new ArrayList<>();
        this.ast = ast;
    }

    public MethodCollection(CompilationUnit ast, ClassCollection classes, InterfaceCollection interfaces){
        this.methods = new ArrayList<>();
        this.ast = ast;
        this.classes = classes;
        this.interfaces = interfaces;

    }

    public void getMetadata(){
        getDeclarationInfo();
        //getMethodDocumentation();
        //getMethodReturnStatements();
        printMetadata();
    }

    public void extractMetadata(){
        getDeclarationInfo();

    }

    private void getDeclarationInfo(){
        VoidVisitor<List<Method>> methodDeclCollector = new MethodDeclarationCollector();
        methodDeclCollector.visit(ast, methods);
    }

    public ClassCollection getClasses() {
        return classes;
    }

    public  void setClasses(ClassCollection classes) {
        this.classes = classes;
    }

    public InterfaceCollection getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(InterfaceCollection interfaces) {
        this.interfaces = interfaces;
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

    public void printMetadata(){
        methods.forEach(x -> System.out.println(x.toString()));

    }

    public void addVariables(VariableCollection vars){
        methods.forEach(x-> x.findVariables(vars));
    }


    private class MethodDeclarationCollector extends VoidVisitorAdapter<List<Method>> {
            @Override
            public void visit(MethodDeclaration methodDecl, List<Method> collection) { 
                super.visit(methodDecl, collection);
                collection.add(new Method(methodDecl, getReturnStatements(methodDecl), classes, interfaces));
            }
    }

    private HashSet<String> getReturnStatements(MethodDeclaration md){
        List<String> rtns = new ArrayList<String>();
        GenericListVisitorAdapter<String, List<String>> returnPrinter = new ReturnStatementCollector();
        return new HashSet<String>(returnPrinter.visit(md, rtns));
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
