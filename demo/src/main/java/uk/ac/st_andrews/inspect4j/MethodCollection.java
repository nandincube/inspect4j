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
   // private ClassCollection classes;
    //private InterfaceCollection interfaces;
    //private VariableCollection variables;
   // private LambdaCollection lambdas;

  
    public MethodCollection(CompilationUnit ast){
        this.methods = new ArrayList<>();
        this.ast = ast;
    }

    // public MethodCollection(CompilationUnit ast, VariableCollection vars, LambdaCollection lambdas){
    //     this.methods = new ArrayList<>();
    //     this.ast = ast;
    //     this.variables = vars;
    //     this.lambdas = lambdas;
    // }

    // public MethodCollection(CompilationUnit ast, ClassCollection classes, VariableCollection vars, InterfaceCollection interfaces){
    //     this.methods = new ArrayList<>();
    //     this.ast = ast;
    //     this.classes = classes;
    //     this.interfaces = interfaces;
    //     this.variables = vars;

    // }

    public void getMetadata(){
        extractMethodsFromAST();
        //getMethodDocumentation();
        //getMethodReturnStatements();
        printMetadata();
    }

    public void extractMethodsFromAST(){
        VoidVisitor<List<Method>> methodDeclCollector = new MethodDeclarationCollector();
        methodDeclCollector.visit(ast, methods);
    }

    // public ClassCollection getClasses() {
    //     return classes;
    // }

    // public  void setClasses(ClassCollection classes) {
    //     this.classes = classes;
    // }

    // public InterfaceCollection getInterfaces() {
    //     return interfaces;
    // }

    // public void setInterfaces(InterfaceCollection interfaces) {
    //     this.interfaces = interfaces;
    // }

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
    

    // public void addVariables(VariableCollection vars){
    //     methods.forEach(x-> x.findVariables(vars));
    // }

    public void addVariables(VariableCollection vars){
         methods.forEach(x-> x.findVariables(vars));
    }

    public void addLambdas(LambdaCollection lbdas){
         methods.forEach(x-> x.findLambdas(lbdas));
     }

    public void addClasses(ClassCollection classes){
         methods.forEach(x-> x.findClasses(classes));
    }

    public void addInterfaces(InterfaceCollection intfs){
         methods.forEach(x-> x.findInterfaces(intfs));
    }

    public void addReferences(MethodReferenceCollection refs){
         methods.forEach(x-> x.findReferences(refs));
     }



    // public VariableCollection getVariables() {
    //     return variables;
    // }

    // public void setVariables(VariableCollection variables) {
    //     this.variables = variables;
    // }

    // public LambdaCollection getLambdas() {
    //     return lambdas;
    // }

    // public void setLambdas(LambdaCollection lambdas) {
    //     this.lambdas = lambdas;
    // }



    private static class MethodDeclarationCollector extends VoidVisitorAdapter<List<Method>> {
            @Override
            public void visit(MethodDeclaration methodDecl, List<Method> collection) { 
                super.visit(methodDecl, collection);
                collection.add(new Method(methodDecl, getReturnStatements(methodDecl)));
            }
    }

    private static HashSet<String> getReturnStatements(MethodDeclaration md){
        List<String> rtns = new ArrayList<String>();
        GenericListVisitorAdapter<String, List<String>> returnPrinter = new ReturnStatementCollector();
        return new HashSet<String>(returnPrinter.visit(md, rtns));
    }
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
