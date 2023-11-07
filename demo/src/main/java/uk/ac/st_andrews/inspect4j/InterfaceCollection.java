package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class InterfaceCollection {
    private ArrayList<Interface> interfaces;
    private CompilationUnit ast;

    public InterfaceCollection(CompilationUnit ast){
        this.interfaces = new ArrayList<>();
        this.ast = ast;
    }

    public void getMetadata(){
        getDeclarationInfo();
        //getMethodDocumentation();
        //getMethodReturnStatements();
        printMetadata();
    }

    
    private void printMetadata(){
        interfaces.forEach(x -> System.out.println(x.toString()));
    }

    
    private void getDeclarationInfo(){
        VoidVisitor<List<Interface>> interfaceDefCollector = new InterfaceDefinitionCollector();
        interfaceDefCollector.visit(ast, interfaces);

    }

    private static class InterfaceDefinitionCollector extends VoidVisitorAdapter<List<Interface>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration id, List<Interface> collection) { 
            super.visit(id,collection);
            if(id.isInterface()){
                collection.add(new Interface(id.getNameAsString(), id.getExtendedTypes(), id.getTypeParameters()));
            }
        }

    }
}
