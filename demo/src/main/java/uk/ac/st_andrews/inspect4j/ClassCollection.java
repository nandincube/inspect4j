package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *  This class is responsible for collecting all the classes from the AST
 */
public class ClassCollection {
    private ArrayList<Class> classList; // list of classes as Class objects
    private CompilationUnit ast; // AST for the class being analysed
 
    /**
     *  Constructor
     * @param ast - CompilationUnit object representing the AST of a file
     */
    public ClassCollection(CompilationUnit ast) {
        this.classList = new ArrayList<>();
        this.ast = ast;
    }

    /**
     *  This method extracts all the classes from the AST of a file
     */
    public void extractClassesFromAST() {
        VoidVisitor<List<Class>> classDefCollector = new ClassDeclarationCollector();
        classDefCollector.visit(ast, classList);

    }

   
    /**
     *  This method links all the variables to relevant parent classes
     * @param vars - VariableCollection object representing the variables in a file
     */
    public void addVariables(VariableCollection vars) {
        classList.forEach(x -> x.findVariables(vars));
    }

    /**
     *  This method links all the lambdas to relevant parent classes
     * @param lbdas - LambdaCollection object representing the lambdas in a file
     */
    public void addLambdas(LambdaCollection lbdas) {
        classList.forEach(x -> x.findLambdas(lbdas));
    }

    /**
     *  This method links all the methods to relevant parent classes
     * @param methods - MethodCollection object representing the methods in a file
     */
    public void addMethods(MethodCollection methods) {
        classList.forEach(x -> x.findMethods(methods));
    }

    /**
     * This method links all  nested interfaces to relevant parent classes.
     * @param intfs - InterfaceCollection object representing the interfaces in a file
     */
    public void addInterfaces(InterfaceCollection intfs) {
        classList.forEach(x -> x.findInterfaces(intfs));
    }

    /**
     *  This method links all method references to relevant parent classes
     * @param refs - MethodReferenceCollection object representing the method references in a file
     */
    public void addReferences(MethodReferenceCollection refs) {
        classList.forEach(x -> x.findReferences(refs));
    }

    /**
     *  This method links all classes to their relevant parent classes if they are nested
     * @param refs - ClassCollection object representing the classes in a file
     */ 
    public void addClasses(ClassCollection refs) {
        classList.forEach(x -> x.findClasses(refs)); // links all classes to their parent class
    }

    /**
     *  This method returns the classes
     * @return ArrayList of Class objects representing the classes in a file
     */
    public ArrayList<Class> getClasses() {
        return classList;
    }

    /**
     *  This method sets the classes
     * @param classList - ArrayList of Class objects representing the classes in a file
     */
    public void setClasses(ArrayList<Class> classList) {
        this.classList = classList;
    }

    /**
     *  This method returns the AST
     * @return CompilationUnit object representing the AST of a file
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     *  This method sets the AST
     * @param ast - CompilationUnit object representing the AST of a file
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    /**
     *  This class is responsible for collecting all the classes from the AST of a file
     */
    private static class ClassDeclarationCollector extends VoidVisitorAdapter<List<Class>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration cd, List<Class> collection) {
            super.visit(cd, collection);
     
            if (!cd.isInterface() && !cd.isAnnotationDeclaration() && !cd.isEnumDeclaration()){
                collection.add(new Class(cd));
            }

        }
    }
}
