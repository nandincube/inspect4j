package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *  This class is used to collect all the methods from the AST and store them in a list.
 */
public class MethodCollection {
    private ArrayList<Method> methods; // List of methods
    private CompilationUnit ast; // The AST of the file as a CompilationUnit object
  
    /**
     *  Constructor for the MethodCollection class.
     * @param ast
     */
    public MethodCollection(CompilationUnit ast){
        this.methods = new ArrayList<>();
        this.ast = ast;
    }

    /**
     *  This method is used to extract all the methods from the AST and store them in a list.
     */
    public void extractMethodsFromAST(){
        VoidVisitor<List<Method>> methodDeclCollector = new MethodDeclarationCollector();
        methodDeclCollector.visit(ast, methods);
    }

    /**
     *  This method is used to get the list of methods.
     * @return - List of methods
     */
    public ArrayList<Method> getMethods() {
        return methods;
    }

    /**
     *  This method is used to set the list of methods.
     * @param methods - List of methods
     */
    public void setMethods(ArrayList<Method> methods) {
        this.methods = methods;
    }

    /**
     *  This method is used to get the AST of the file.
     * @return - The AST of the file
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     *  This method is used to set the AST of the file.
     * @param ast - The AST of the file
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }


    /**
     *  This method is used to add the variables to their corresponding parent methods.
     * @param vars - The VariableCollection object
     */
    public void addVariables(VariableCollection vars){
         methods.forEach(x-> x.findVariables(vars));
    }

    /**
     *  This method is used to add the lambdas to their corresponding parent methods.
     * @param lbdas - The LambdaCollection object
     */
    public void addLambdas(LambdaCollection lbdas){
         methods.forEach(x-> x.findLambdas(lbdas));
     }

     /**
      *  This method is used to add local classes to their corresponding parent methods.
      * @param classes
      */
    public void addClasses(ClassCollection classes){
         methods.forEach(x-> x.findClasses(classes));
    }

    /**
     *  This method is used to add the interfaces to their corresponding parent methods.
     * @param intfs - The InterfaceCollection object
     */
    public void addInterfaces(InterfaceCollection intfs){
         methods.forEach(x-> x.findInterfaces(intfs));
    }

    /**
     *  This method is used to add method references to their corresponding parent methods.
     * @param refs - The MethodReferenceCollection object
     */
    public void addReferences(MethodReferenceCollection refs){
         methods.forEach(x-> x.findReferences(refs));
    }

    /**
     *  This static class is used to collect all the method declarations from the AST and store them in a list.
     */
    private static class MethodDeclarationCollector extends VoidVisitorAdapter<List<Method>> {
            /**
             * This method is used to visit the method declarations and store them as method objects in the arraylist contained in the MethodCollection object.
             * @param methodDecl - The method declaration
             * @param collection - The list of methods contained in the MethodCollection object
             */
            @Override
            public void visit(MethodDeclaration methodDecl, List<Method> collection) { 
                super.visit(methodDecl, collection);
                collection.add(new Method(methodDecl, getReturnStatements(methodDecl))); // Add the method to the list
            }

            /**
             * This method is used to visit the constructor declarations and store them as method objects in the arraylist contained in the MethodCollection object.
             * @param methodDecl - The method declaration
             * @param collection - The list of methods contained in the MethodCollection object
             */
            @Override
            public void visit(ConstructorDeclaration constructorDecl, List<Method> collection) { 
                super.visit(constructorDecl, collection);
                collection.add(new Method(constructorDecl));
            }
    }

    /**
     *  This method is used to get the return statements for a given method.
     * @param md - The method declaration
     * @return - The set of return statements
     */
    private static HashSet<String> getReturnStatements(MethodDeclaration md){
        List<String> rtns = new ArrayList<String>();
        GenericListVisitorAdapter<String, List<String>> returnPrinter = new ReturnStatementCollector();
        return new HashSet<String>(returnPrinter.visit(md, rtns)); // here the subtree for the method declaration is visited to collect the return statements 
    }
    
    /**
     * This class is used to collect all the return statements for in a given Method subtree of an AST and stores them in a list.
     */
    private static class ReturnStatementCollector extends GenericListVisitorAdapter<String, List<String>> {

        /**
         * This method is used to visit the return statements and store them in a list.
         * @param rs - The return statement node
         * @param arg - The list of return statements for the given method
         */
        @Override
        public List<String> visit(ReturnStmt rs, List<String> arg) { 
            super.visit(rs,arg);
            if(rs.getExpression().isPresent()){ // if the return statement has an expression
                String expr = rs.getExpression().get().toString().replace("\"", "");
                arg.add(expr);
            }
            return arg;
        }     
    }
}
