package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodReferenceCollection {
    private ArrayList<MethodReference> methodRefs;
    private CompilationUnit ast;

  
    public MethodReferenceCollection(CompilationUnit ast){
        this.methodRefs = new ArrayList<>();
        this.ast = ast;
    }

    public void getMetadata(){
        getDeclarationInfo();
        printMetadata();
    }

    public void extractMetadata(){
        getDeclarationInfo();

    }

    private void getDeclarationInfo(){
        VoidVisitor<List<MethodReference>> methodRefDeclCollector = new MethodReferenceExprCollector();
        methodRefDeclCollector.visit(ast, methodRefs);
    }
    
    public ArrayList<MethodReference> getMethodReferences() {
        return methodRefs;
    }

    public void setMethodReferences(ArrayList<MethodReference> methodRefs) {
        this.methodRefs = methodRefs;
    }

    public CompilationUnit getAst() {
        return ast;
    }

    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    public void printMetadata(){
        methodRefs.forEach(x -> System.out.println(x.toString()));

    }

    private static class MethodReferenceExprCollector extends VoidVisitorAdapter<List<MethodReference>> {
            @Override
            public void visit(MethodReferenceExpr methodRef, List<MethodReference> collection) { 
                super.visit(methodRef, collection);
                collection.add(new MethodReference(methodRef));
            }

    }
}
