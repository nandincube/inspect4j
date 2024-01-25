package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * 
 */
public class ClassCollection {
    private ArrayList<Class> classList;
    private CompilationUnit ast;

    /**
     * 
     * @param ast
     */
    public ClassCollection(CompilationUnit ast) {
        this.classList = new ArrayList<>();
        this.ast = ast;
    }

    /**
     * 
     */
    public void extractClassesFromAST() {
        VoidVisitor<List<Class>> classDefCollector = new ClassDefinitionCollector();
        classDefCollector.visit(ast, classList);

    }

    /**
     * 
     */
    public void printMetadata() {
        classList.forEach(x -> System.out.println(x.toString()));
    }

    /**
     * 
     * @param vars
     */
    public void addVariables(VariableCollection vars) {
        classList.forEach(x -> x.findVariables(vars));
    }

    /**
     * 
     * @param lbdas
     */
    public void addLambdas(LambdaCollection lbdas) {
        classList.forEach(x -> x.findLambdas(lbdas));
    }

    /**
     * 
     * @param methods
     */
    public void addMethods(MethodCollection methods) {
        classList.forEach(x -> x.findMethods(methods));
    }

    /**
     * 
     * @param intfs
     */
    public void addInterfaces(InterfaceCollection intfs) {
        classList.forEach(x -> x.findInterfaces(intfs));
    }

    /**
     * 
     * @param refs
     */
    public void addReferences(MethodReferenceCollection refs) {
        classList.forEach(x -> x.findReferences(refs));
    }

    /**
     * 
     * @param refs
     */
    public void addClasses(ClassCollection refs) {
        classList.forEach(x -> x.findClasses(refs));
    }

    /**
     * 
     * @return
     */
    public ArrayList<Class> getClasses() {
        return classList;
    }

    /**
     * 
     * @param classList
     */
    public void setClasses(ArrayList<Class> classList) {
        this.classList = classList;
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
    private static class ClassDefinitionCollector extends VoidVisitorAdapter<List<Class>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration cd, List<Class> collection) {
            super.visit(cd, collection);
            if (!cd.isInterface()) {
                collection.add(new Class(cd));
            }

        }
    }
}
