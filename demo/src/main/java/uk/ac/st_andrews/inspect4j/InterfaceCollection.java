package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
/**
 *  This class is responsible for collecting all the interfaces from the AST of a file
 */
public class InterfaceCollection {
    private ArrayList<Interface> interfaceList; // list of interfaces as Interface objects
    private CompilationUnit ast; // AST for the class being analysed

    /**
     *  Constructor
     * @param ast - CompilationUnit object representing the AST of a file
     */
    public InterfaceCollection(CompilationUnit ast){
        this.interfaceList = new ArrayList<>();
        this.ast = ast;
    }

    /**
     * Gets the list of interfaces
     * @return ArrayList<Interface> - list of interfaces
     */
    public ArrayList<Interface> getInterfaces() {
        return interfaceList;
    }

    /**
     *  Sets the list of interfaces
     * @param interfaces - list of interfaces
     */
    public void setInterfaces(ArrayList<Interface> interfaces) {
        this.interfaceList = interfaces;
    }

    /**
     *  Gets the AST for the entity being analysed
     * @return CompilationUnit - AST for the entity being analysed
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     *  Sets the AST for the entity being analysed
     * @param ast - AST for the entity being analysed
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }


    /**
     *  This method extracts all the interfaces from the AST of a file
     */
    public void extractInterfacesFromAST(){
          VoidVisitor<List<Interface>> interfaceDefCollector = new InterfaceDeclarationCollector();
        interfaceDefCollector.visit(ast, interfaceList);

    }

    /**
     *  This method links all the methods to relevant parent interfaces
     * @param methods - MethodCollection object representing the methods in a file
     */
    public void addMethods(MethodCollection methods){
        interfaceList.forEach(x-> x.findMethods(methods));
   }

    /**
     *  This method links all the interfaces to relevant parent interfaces
     * @param interfaces - InterfaceCollection object representing the interfaces in a file
     */
    public void addInterfaces(InterfaceCollection interfaces){
        interfaceList.forEach(x-> x.findInterfaces(interfaces));
   }

   
    /**
     *  This method links all the classes to relevant parent interfaces
     * @param refs - ClassCollection object representing the classes in a file
     */
    public void addClasses(ClassCollection refs) {
        interfaceList.forEach(x -> x.findClasses(refs));
    }
    

    /**
     *  This class is used to collect the interface declarations from the AST.
     */
    private static class InterfaceDeclarationCollector extends VoidVisitorAdapter<List<Interface>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration intDecl, List<Interface> collection) { 
            super.visit(intDecl, collection);
            if(intDecl.isInterface()){
                collection.add(new Interface(intDecl));
            }
        }
    }
}
