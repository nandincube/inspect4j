package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * 
 */
public class VariableCollection {

    private ArrayList<Variable> variables;
    private CompilationUnit ast;

    /**
     * Constructor.
     * 
     * @param ast - AST for the Class being analysed
     */
    public VariableCollection(CompilationUnit ast) {
        this.variables = new ArrayList<>();
        this.ast = ast;
    }

    /**
     * 
     */
    public void printMetadata() {
        variables.forEach(x -> System.out.println(x.toString()));
    }

    /**
     * 
     */
    public void extractVariablesFromAST() {
        VoidVisitor<List<Variable>> variableCollector = new StoredVariableCallsCollector();
        variableCollector.visit(ast, variables);
    }

    /**
     * @return CompilationUnit
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     * @param ast
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    /**
     * @return ArrayList<Variable>
     */
    public ArrayList<Variable> getVariables() {
        return variables;
    }

    /**
     * @param variables
     */
    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }

    /**
     * 
     */
    private static class StoredVariableCallsCollector extends VoidVisitorAdapter<List<Variable>> {

        @Override
        public void visit(MethodDeclaration md, List<Variable> collection) {
            super.visit(md, collection);

            List<AssignExpr> assignments = md.findAll(AssignExpr.class);

            for (AssignExpr assignment : assignments) {
                if (assignment.getTarget().isNameExpr() ||
                        assignment.getTarget().isFieldAccessExpr()) { // is the thing being assigned a variable or field
                    if (assignment.getValue().isMethodCallExpr()) {
                        
                        collection.add(new Variable(assignment));

                    }

                }
            }
        }

    }

}
