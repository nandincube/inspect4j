package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Class to collect all the method references in a class and store them in a
 * list.
 */
public class MethodReferenceCollection {

    private ArrayList<MethodReference> methodRefs; // list of method references
    private CompilationUnit ast; // AST for the class being analysed

    /**
     * Constructor.
     * 
     * @param ast - AST for the Class being analysed
     */
    public MethodReferenceCollection(CompilationUnit ast) {
        this.methodRefs = new ArrayList<>();
        this.ast = ast;
    }

    /**
     * Method to extract all the method references from the AST and store them in a
     * list.
     */
    public void extractReferencesFromAST() {
        VoidVisitor<List<MethodReference>> methodRefDeclCollector = new MethodReferenceExprCollector();
        methodRefDeclCollector.visit(ast, methodRefs);
    }

    /**
     * Method to get the list of method references.
     * 
     * @return ArrayList<MethodReference> - list of method references
     */
    public ArrayList<MethodReference> getMethodReferences() {
        return methodRefs;
    }

    /**
     * Method to set the list of method references.
     * 
     * @param methodRefs - list of method references
     */
    public void setMethodReferences(ArrayList<MethodReference> methodRefs) {
        this.methodRefs = methodRefs;
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
     * Class to collect all the method references in an ast and store them in a
     * list.
     */
    private static class MethodReferenceExprCollector extends VoidVisitorAdapter<List<MethodReference>> {
        @Override
        public void visit(MethodReferenceExpr methodRef, List<MethodReference> collection) {
            super.visit(methodRef, collection);
            collection.add(new MethodReference(methodRef));
        }

    }
}
