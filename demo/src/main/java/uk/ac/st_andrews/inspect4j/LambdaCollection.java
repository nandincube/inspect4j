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

/**
 *  This class is responsible for collecting all the lambdas from the AST
 */
public class LambdaCollection {
    private ArrayList<Lambda> lambdas; // list of lambdas as Lambda objects
    private CompilationUnit ast; // AST for the class being analysed

    /**
     *  Constructor
     * @param ast - CompilationUnit object representing the AST of a file
     */
    public LambdaCollection(CompilationUnit ast) {
        this.lambdas = new ArrayList<>();
        this.ast = ast;
    }

    /**
     *  This method extracts all the lambdas from the AST of a file
     */
    public void extractLambdasFromAST() {
        VoidVisitor<List<Lambda>> lambdaDeclCollector = new LambdaExprCollector();
        lambdaDeclCollector.visit(ast, lambdas);
    }

    /**
     *  This method links all the classes to relevant parent lambdas
    * @param classes
    */
    public void addClasses(ClassCollection classes) {
        lambdas.forEach(x -> x.findClasses(classes));
    }

    /**
     *  This method links all the lambdas to relevant parent lambdas
     * @param lmbds
     */
    public void addLambdas(LambdaCollection lmbds) {
        lambdas.forEach(x -> x.findLambdas(lmbds));
    }

    /**
     *  This method links all the method references to relevant parent lambdas
     * @param refs - MethodReferenceCollection object representing the method references in a file
     */
    public void addReferences(MethodReferenceCollection refs) {
        lambdas.forEach(x -> x.findReferences(refs));
    }

    /**
     *  This method links all the assignment methods/stored variable calls to relevant parent lambdas
     * @param vars - VariableCollection object representing the variables in a file
     */
    public void addVariable(VariableCollection vars) {
        lambdas.forEach(x -> x.findVariables(vars));
    }

    /**
     *  Gets the list of lambdas
     * @return ArrayList<Lambda> - list of lambdas
     */
    public ArrayList<Lambda> getLambdas() {
        return lambdas;
    }

    /**
     *  Sets the list of lambdas
     * @param lambda - list of lambdas
     */
    public void setLambdas(ArrayList<Lambda> lambdas) {
        this.lambdas = lambdas;
    }

    /**
     * Gets the AST for the entity being analysed
     * @return CompilationUnit - AST for the entity being analysed
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     *  Sets the AST for the entity being analysed
     * @param ast - AST for the entity being analysed
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }


    /**
     * Visitor Class to collect all the lambdas in an ast and store them in a
     * list.
     */
    private class LambdaExprCollector extends VoidVisitorAdapter<List<Lambda>> {
        @Override
        public void visit(LambdaExpr le, List<Lambda> collection) {
            super.visit(le, collection);
            collection.add(new Lambda(le, getReturnStatements(le)));
        }

    }
 /**
     * This method is used to get the return statements for a given lambda.
     * 
     * @param le - The lambda expression declaration
     * @return - The set of return statements
     */
    private HashSet<String> getReturnStatements(LambdaExpr le) {
        List<String> rtns = new ArrayList<String>();
        GenericListVisitorAdapter<String, List<String>> returnPrinter = new ReturnStatementCollector();
        return new HashSet<String>(returnPrinter.visit(le, rtns));
    }

      /**
     * This visitor class is used to collect all the return statements for in a given lambda
     * subtree of the original AST and stores them in a list.
     */
    private class ReturnStatementCollector extends GenericListVisitorAdapter<String, List<String>> {
        @Override
        public List<String> visit(ReturnStmt rs, List<String> arg) {
            super.visit(rs, arg);
            if (rs.getExpression().isPresent()) {
                String expr = rs.getExpression().get().toString();
                arg.add(expr);
            }

            return arg;
        }
    }
}
