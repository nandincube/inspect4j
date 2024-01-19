package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class VariableCollection {

    private ArrayList<Variable> variables;
    private CompilationUnit ast;

    public VariableCollection(CompilationUnit ast) {
        this.variables = new ArrayList<>();
        this.ast = ast;
    }

    public void getMetadata() {
        extractVariablesFromAST();
        printMetadata();
    }

    public void printMetadata() {
        variables.forEach(x -> System.out.println(x.toString()));

    }

    public void extractVariablesFromAST() {
        VoidVisitor<List<Variable>> variableCollector = new StoredVariableCallsCollector();
        variableCollector.visit(ast, variables);
    }

    public CompilationUnit getAst() {
        return ast;
    }

    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }

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
