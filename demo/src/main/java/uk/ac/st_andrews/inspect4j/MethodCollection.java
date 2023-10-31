package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodCollection {
    private ArrayList<Method> methods;
    private CompilationUnit ast;

    public MethodCollection(CompilationUnit ast){
        this.methods = new ArrayList<>();
        this.ast = ast;
    }

    public void getMetadata(){
        getDeclarationInfo();
        //getMethodDocumentation();
        //getMethodReturnStatements();
        printMetadata();
    }

    private void getDeclarationInfo(){
        VoidVisitor<List<Method>> methodDeclCollector = new MethodDeclarationCollector();
        methodDeclCollector.visit(ast, methods);

    }
    
    private void getMethodReturnStatements(){
    

    }

    private void getMethodDocumentation(){

    }

    private void printMetadata(){
        methods.forEach(x -> System.out.println(x.toString()));

    }

    private static class MethodDeclarationCollector extends VoidVisitorAdapter<List<Method>> {
            @Override
            public void visit(MethodDeclaration md, List<Method> collection) { 
                super.visit(md,collection);
                collection.add(new Method(md.getDeclarationAsString(true,true,true),
                md.getNameAsString(), md.getParameters(), md.getBody().get().getStatements()));
            }

    }


}
