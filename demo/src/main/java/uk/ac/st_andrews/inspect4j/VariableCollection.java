package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
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
            addVariable(md, collection);

        }

        @Override
        public void visit(ConstructorDeclaration cd, List<Variable> collection) {
            super.visit(cd, collection);
            addVariable(cd, collection);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration cd, List<Variable> collection) {
            super.visit(cd, collection);
            cd.getFields().stream().forEach(x -> {
                x.getVariables().stream().forEach(a -> {
                    if (a.getInitializer().isPresent()) {
                        if (a.getInitializer().get().isMethodCallExpr()
                                || a.getInitializer().get().isObjectCreationExpr()) {

                            ParentEntity<ClassOrInterfaceDeclaration> par;
                            if (cd.isInterface()) {
                                par = new ParentEntity<ClassOrInterfaceDeclaration>(cd, EntityType.INTERFACE);
                            } else {
                                par = new ParentEntity<ClassOrInterfaceDeclaration>(cd, EntityType.CLASS);
                            }
                            collection.add(new Variable(a, par, a.getInitializer().get()));
                        }
                    }
                });
            });
        }

    }

    private static void addVariable(CallableDeclaration<?> md, List<Variable> collection) {

        List<AssignExpr> assignments = md.findAll(AssignExpr.class);

        for (AssignExpr assignment : assignments) {
            if (assignment.getTarget().isNameExpr() ||
                    assignment.getTarget().isFieldAccessExpr()) { // is the thing being assigned a variable or field
                if (assignment.getValue().isMethodCallExpr() || assignment.getValue().isObjectCreationExpr()) {
                    collection.add(new Variable(assignment));
                }
            }
        }

        List<VariableDeclarationExpr> vds = md.findAll(VariableDeclarationExpr.class);

        vds.stream().forEach(vd -> {
            vd.getVariables().stream().forEach(a -> {
                if (a.getInitializer().isPresent()) {
                    if (a.getInitializer().get().isMethodCallExpr()
                            || a.getInitializer().get().isObjectCreationExpr()) {
                                //System.out.println("Variable: " + a.getNameAsString()+ "- type"+a.getInitializer().get()+ " -"+ md.getClass());
                        addToCollection(md, a, collection);
                    }

                }
            });
        });
    }

    private static void addToCollection(CallableDeclaration<?> md, VariableDeclarator a, List<Variable> collection ){
        if (md.isConstructorDeclaration()) {

            collection.add(new Variable(a,
                    new ParentEntity<ConstructorDeclaration>(md.asConstructorDeclaration(), EntityType.METHOD),
                    a.getInitializer().get()));
        } else {
            collection.add(new Variable(a,
                    new ParentEntity<MethodDeclaration>(md.asMethodDeclaration(), EntityType.METHOD),
                    a.getInitializer().get()));
        }


    }
}
       


