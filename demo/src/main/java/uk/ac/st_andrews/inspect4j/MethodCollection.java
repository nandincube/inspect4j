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

/**
 * 
 */
public class MethodCollection {
    private ArrayList<Method> methods;
    private CompilationUnit ast;
  
    /**
     * 
     * @param ast
     */
    public MethodCollection(CompilationUnit ast){
        this.methods = new ArrayList<>();
        this.ast = ast;
    }

    /**
     * 
     */
    public void extractMethodsFromAST(){
        VoidVisitor<List<Method>> methodDeclCollector = new MethodDeclarationCollector();
        methodDeclCollector.visit(ast, methods);
    }

    /**
     * 
     * @return
     */
    public ArrayList<Method> getMethods() {
        return methods;
    }

    /**
     * 
     * @param methods
     */
    public void setMethods(ArrayList<Method> methods) {
        this.methods = methods;
    }

    /**
     * 
     * @return
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     * 
     * @param ast
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    /**
     * 
     */
    public void printMetadata(){
        methods.forEach(x -> System.out.println(x.toString()));

    }
    

    /**
     * 
     * @param vars
     */
    public void addVariables(VariableCollection vars){
         methods.forEach(x-> x.findVariables(vars));
    }

    /**
     * 
     * @param lbdas
     */
    public void addLambdas(LambdaCollection lbdas){
         methods.forEach(x-> x.findLambdas(lbdas));
     }

     /**
      * 
      * @param classes
      */
    public void addClasses(ClassCollection classes){
         methods.forEach(x-> x.findClasses(classes));
    }

    /**
     * 
     * @param intfs
     */
    public void addInterfaces(InterfaceCollection intfs){
         methods.forEach(x-> x.findInterfaces(intfs));
    }

    /**
     * 
     * @param refs
     */
    public void addReferences(MethodReferenceCollection refs){
         methods.forEach(x-> x.findReferences(refs));
    }

    /**
     * 
     */
    private static class MethodDeclarationCollector extends VoidVisitorAdapter<List<Method>> {
            @Override
            public void visit(MethodDeclaration methodDecl, List<Method> collection) { 
                super.visit(methodDecl, collection);
                collection.add(new Method(methodDecl, getReturnStatements(methodDecl)));
            }
    }

    /**
     * 
     * @param md
     * @return
     */
    private static HashSet<String> getReturnStatements(MethodDeclaration md){
        List<String> rtns = new ArrayList<String>();
        GenericListVisitorAdapter<String, List<String>> returnPrinter = new ReturnStatementCollector();
        return new HashSet<String>(returnPrinter.visit(md, rtns));
    }
    
    /**
     * 
     */
    private static class ReturnStatementCollector extends GenericListVisitorAdapter<String, List<String>> {
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
