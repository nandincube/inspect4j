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
 * Class to collect all the variables in a class/method and store them in a
 * list.
 */
public class VariableCollection {

    private ArrayList<Variable> variables; // list of variables
    private CompilationUnit ast; // AST for the class being analysed

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
     * Method to extract all the variables from the AST and store them in a list.
     */
    public void extractVariablesFromAST() {
        VoidVisitor<List<Variable>> variableCollector = new StoredVariableCallsCollector();
        variableCollector.visit(ast, variables);
    }

    /**
     * Method to get the AST for the entity being analysed.
     * 
     * @return CompilationUnit - AST for the entity being analysed
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     * Method to set the AST for the entity being analysed.
     * 
     * @param ast - AST for the entity being analysed
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    /**
     * Method to get the list of variables.
     * 
     * @return ArrayList<Variable> - list of variables
     */
    public ArrayList<Variable> getVariables() {
        return variables;
    }

    /**
     * Method to set the list of variables.
     * 
     * @param variables
     */
    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }

    /**
     * This Visitor class is used to collect all the variables in a class/method and store
     * them in a list.
     */
    private static class StoredVariableCallsCollector extends VoidVisitorAdapter<List<Variable>> {

        /**
         * Method to visit a method declaration and collect all the variables in the
         * method.
         * 
         * @param md         - method declaration
         * @param collection - list of variables
         */
        @Override
        public void visit(MethodDeclaration md, List<Variable> collection) {
            super.visit(md, collection);
            addVariable(md, collection);

        }

        /**
         * Method to visit a constructor declaration and collect all the variables in
         * the constructor.
         * 
         * @param cd         - constructor declaration
         * @param collection - list of variables
         */
        @Override
        public void visit(ConstructorDeclaration cd, List<Variable> collection) {
            super.visit(cd, collection);
            addVariable(cd, collection);
        }

        /**
         * Method to visit a class/interface declaration and collect all the variables
         * in the class/interface.
         * 
         * @param cd         - class/interface declaration
         * @param collection - list of variables
         */
        @Override
        public void visit(ClassOrInterfaceDeclaration cd, List<Variable> collection) {
            super.visit(cd, collection);
            cd.getFields().stream().forEach(x -> {
                x.getVariables().stream().forEach(a -> {
                    if (a.getInitializer().isPresent()) { // if the variable has an initializer
                        if (a.getInitializer().get().isMethodCallExpr()
                                || a.getInitializer().get().isObjectCreationExpr()) { // if the initializer is a method
                                                                                      // call or object creation

                            ParentEntity<ClassOrInterfaceDeclaration> par;
                            if (cd.isInterface()) { // if the class is an interface
                                par = new ParentEntity<ClassOrInterfaceDeclaration>(cd, EntityType.INTERFACE);
                            } else {
                                par = new ParentEntity<ClassOrInterfaceDeclaration>(cd, EntityType.CLASS);
                            }
                            collection.add(new Variable(a, par, a.getInitializer().get())); // add the variable to the
                                                                                            // collection
                        }
                    }
                });
            });
        }

    }

    /**
     * Method to add the variables in a method/constructor to the list of variables.
     * 
     * @param md         - method/constructor declaration
     * @param collection - list of variables
     */
    private static void addVariable(CallableDeclaration<?> md, List<Variable> collection) {

        List<AssignExpr> assignments = md.findAll(AssignExpr.class); // get all the assignments in the
                                                                     // method/constructor

        for (AssignExpr assignment : assignments) {
            if (assignment.getTarget().isNameExpr() ||
                    assignment.getTarget().isFieldAccessExpr()) { // if the target of the assignment is a variable
                if (assignment.getValue().isMethodCallExpr() || assignment.getValue().isObjectCreationExpr()) { // if
                                                                                                                // the
                                                                                                                // value
                                                                                                                // of
                                                                                                                // the
                                                                                                                // assignment
                                                                                                                // is a
                                                                                                                // method
                                                                                                                // call
                                                                                                                // or
                                                                                                                // object
                                                                                                                // creation
                    collection.add(new Variable(assignment)); // add the variable to the collection
                }
            }
        }

        List<VariableDeclarationExpr> vds = md.findAll(VariableDeclarationExpr.class); // get all the variable
                                                                                       // declaration expressions in the
                                                                                       // method/constructor

        vds.stream().forEach(vd -> {
            vd.getVariables().stream().forEach(a -> {
                if (a.getInitializer().isPresent()) {
                    if (a.getInitializer().get().isMethodCallExpr()
                            || a.getInitializer().get().isObjectCreationExpr()) { // if the initializer is a method call
                                                                                  // or object creation

                        addToCollection(md, a, collection);
                    }

                }
            });
        });
    }

    /**
     * Method to add a variable to the list of variables.
     * 
     * @param md         - method/constructor declaration
     * @param a          - variable
     * @param collection - list of variables
     */
    private static void addToCollection(CallableDeclaration<?> md, VariableDeclarator a, List<Variable> collection) {
        if (md.isConstructorDeclaration()) { // if the method is a constructor

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
