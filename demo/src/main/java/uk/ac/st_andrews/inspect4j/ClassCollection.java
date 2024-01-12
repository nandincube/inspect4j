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
    //private MethodCollection methods;

    public ClassCollection(CompilationUnit ast){
        this.classes = new ArrayList<>();
        this.ast = ast;
        //this.methods = methods;
    }

    public void getMetadata(){
        extractClassesFromAST();
        //getMethodDocumentation();
        //getMethodReturnStatements();
        printMetadata();
    }

    public void extractClassesFromAST(){
         VoidVisitor<List<Class>> classDefCollector = new ClassDefinitionCollector();
        classDefCollector.visit(ast, classes);

    }
    
    public void printMetadata(){
        classes.forEach(x -> System.out.println(x.toString()));
    }

    

    // public void addMethods(MethodCollection md){
    //     classes.forEach(x-> x.findMethods(md));
    // }
  
    // public void addOuterClassesOrMethods(ClassCollection cls, MethodCollection mds){
    //     classes.forEach(x-> x.findOuterClassOrMethod(cls, mds));
    // }

    //  public void addOuterClasses(ClassCollection cls){
    //     classes.forEach(x-> x.findOuterClass(cls));
    // }


    // public void addInnerOrLocal(ClassCollection cls){
    //     classes.forEach(x-> x.findInterOrLocalChildrenClasses(cls));
    // }

    public void addVariables(VariableCollection vars){
        classes.forEach(x-> x.findVariables(vars));
   }

   public void addLambdas(LambdaCollection lbdas){
        classes.forEach(x-> x.findLambdas(lbdas));
    }

   public void addMethods(MethodCollection methods){
        classes.forEach(x-> x.findMethods(methods));
   }

   public void addInterfaces(InterfaceCollection intfs){
        classes.forEach(x-> x.findInterfaces(intfs));
   }

    public void addReferences(MethodReferenceCollection refs){
        classes.forEach(x-> x.findReferences(refs));
    }

    public void addClasses(ClassCollection refs){
        classes.forEach(x-> x.findClasses(refs));
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
