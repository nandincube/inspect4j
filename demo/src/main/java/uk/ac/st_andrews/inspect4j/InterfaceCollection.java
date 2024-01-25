package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class InterfaceCollection {
    private ArrayList<Interface> interfaceList;
    private CompilationUnit ast;

    /**
     * 
     * @param ast
     */
    public InterfaceCollection(CompilationUnit ast){
        this.interfaceList = new ArrayList<>();
        this.ast = ast;
    }

    /**
     * 
     * @return
     */
    public ArrayList<Interface> getInterfaces() {
        return interfaceList;
    }

    /**
     * 
     * @param interfaces
     */
    public void setInterfaces(ArrayList<Interface> interfaces) {
        this.interfaceList = interfaces;
    }

    /**
     * 
     * @return
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     * 
     * @param ast
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }


    /**
     * 
     */
    public void extractInterfacesFromAST(){
          VoidVisitor<List<Interface>> interfaceDefCollector = new InterfaceDefinitionCollector();
        interfaceDefCollector.visit(ast, interfaceList);

    }

    /**
     * 
     * @param methods
     */
    public void addMethods(MethodCollection methods){
        interfaceList.forEach(x-> x.findMethods(methods));
   }

    /**
     * 
     * @param interfaces
     */
    public void addInterfaces(InterfaceCollection interfaces){
        interfaceList.forEach(x-> x.findInterfaces(interfaces));
   }
    
    
   /**
    * 
    */
    public void printMetadata(){
        interfaceList.forEach(x -> System.out.println(x.toString()));
    }

    /**
     * 
     */
    private static class InterfaceDefinitionCollector extends VoidVisitorAdapter<List<Interface>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration intDecl, List<Interface> collection) { 
            super.visit(intDecl, collection);
            if(intDecl.isInterface()){
                System.out.println("intName: "+intDecl.getNameAsString());
                collection.add(new Interface(intDecl));
            }
        }

    }
}
