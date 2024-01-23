package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class DependencyCollection {
    private ArrayList<Dependency> dependences;
    private CompilationUnit ast;

    public DependencyCollection(ArrayList<Dependency> dependency, CompilationUnit ast) {
        this.dependences = dependency;
        this.ast = ast;
    }

    public ArrayList<Dependency> getDependency() {
        return dependences;
    }

    public void setDependency(ArrayList<Dependency> dependences) {
        this.dependences = dependences;
    }

    public CompilationUnit getAst() {
        return ast;
    }

    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }


    public void extractMethodsFromAST(){
        VoidVisitor<List<Dependency>> methodDeclCollector = new ImportDeclarationCollector();
        methodDeclCollector.visit(ast, dependences);
    }
    
    /**
     * 
     */
    private static class ImportDeclarationCollector extends VoidVisitorAdapter<List<Dependency>> {
            @Override
            public void visit(ImportDeclaration importDecl, List<Dependency> collection) { 
                super.visit(importDecl, collection);
                collection.add(new Dependency(importDecl));
            }
    }
    
}
