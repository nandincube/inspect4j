package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassCollection {
    private ArrayList<Class> classes;
    private CompilationUnit ast;

    public ClassCollection(CompilationUnit ast){
        this.classes = new ArrayList<>();
        this.ast = ast;
    }

    public void getMetadata(){
        getDeclarationInfo();
        //getMethodDocumentation();
        //getMethodReturnStatements();
        printMetadata();
    }

    
    private void printMetadata(){
        classes.forEach(x -> System.out.println(x.toString()));
    }

    
    private void getDeclarationInfo(){
        VoidVisitor<List<Class>> classDefCollector = new ClassDefinitionCollector();
        classDefCollector.visit(ast, classes);

    }

    private static class ClassDefinitionCollector extends VoidVisitorAdapter<List<Class>> {
            @Override
            public void visit(ClassOrInterfaceDeclaration cd, List<Class> collection) { 
                super.visit(cd,collection);
                if(!cd.isInterface()){
                    collection.add(new Class(cd.getNameAsString(),cd.getTypeParameters(), cd.getImplementedTypes(), cd.getExtendedTypes(), cd.isInnerClass(), 
                    cd.isLocalClassDeclaration(), cd.getBegin().get().line, cd.getEnd().get().line));
                    //isInnerClass only picks up on non-static nested classes
                }
              
            }

    }
}
