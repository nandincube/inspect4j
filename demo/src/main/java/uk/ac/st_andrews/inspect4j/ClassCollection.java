package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassCollection {
    private ArrayList<Class> classes;
    private transient CompilationUnit ast;

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

    public void extractMetadata(){
        getDeclarationInfo();

    }
    
    public void printMetadata(){
        classes.forEach(x -> System.out.println(x.toString()));
    }

    
    private void getDeclarationInfo(){
        VoidVisitor<List<Class>> classDefCollector = new ClassDefinitionCollector();
        classDefCollector.visit(ast, classes);

    }

    public void addMethods(MethodCollection md){
        classes.forEach(x-> x.findMethods(md));
    }
  
    public void addOuterClassesOrMethods(ClassCollection cls, MethodCollection mds){
        classes.forEach(x-> x.findOuterClassOrMethod(cls, mds));
    }

     public void addOuterClasses(ClassCollection cls){
        classes.forEach(x-> x.findOuterClass(cls));
    }


    public void addInnerOrLocal(ClassCollection cls){
        classes.forEach(x-> x.findInterOrLocalChildrenClasses(cls));
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public CompilationUnit getAst() {
        return ast;
    }

    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    private static class ClassDefinitionCollector extends VoidVisitorAdapter<List<Class>> {
            @Override
            public void visit(ClassOrInterfaceDeclaration cd, List<Class> collection) { 
                super.visit(cd,collection);
                if(!cd.isInterface()){
                    collection.add(new Class(cd));
                }
              
            }
    }
}
