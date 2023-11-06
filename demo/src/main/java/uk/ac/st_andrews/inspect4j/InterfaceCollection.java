package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class InterfaceCollection {
    private ArrayList<Class> classes;
    private CompilationUnit ast;

    public InterfaceCollection(CompilationUnit ast){
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
                //collection.add(new Interface(cd.getNameAsString(),cd.getTypeParameters(), cd.getImplementedTypes(), cd.getExtendedTypes()));
            }

    }
}
