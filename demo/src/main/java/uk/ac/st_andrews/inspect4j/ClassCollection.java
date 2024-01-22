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
    private ArrayList<Class> classes;
    private CompilationUnit ast;

    /**
     * 
     * @param ast
     */
    public ClassCollection(CompilationUnit ast) {
        this.classes = new ArrayList<>();
        this.ast = ast;
    }

    /**
     * 
     */
    public void extractClassesFromAST() {
        VoidVisitor<List<Class>> classDefCollector = new ClassDefinitionCollector();
        classDefCollector.visit(ast, classes);

    }

    /**
     * 
     */
    public void printMetadata() {
        classes.forEach(x -> System.out.println(x.toString()));
    }

    /**
     * 
     * @param vars
     */
    public void addVariables(VariableCollection vars) {
        classes.forEach(x -> x.findVariables(vars));
    }

    /**
     * 
     * @param lbdas
     */
    public void addLambdas(LambdaCollection lbdas) {
        classes.forEach(x -> x.findLambdas(lbdas));
    }

    /**
     * 
     * @param methods
     */
    public void addMethods(MethodCollection methods) {
        classes.forEach(x -> x.findMethods(methods));
    }

    /**
     * 
     * @param intfs
     */
    public void addInterfaces(InterfaceCollection intfs) {
        classes.forEach(x -> x.findInterfaces(intfs));
    }

    /**
     * 
     * @param refs
     */
    public void addReferences(MethodReferenceCollection refs) {
        classes.forEach(x -> x.findReferences(refs));
    }

    /**
     * 
     * @param refs
     */
    public void addClasses(ClassCollection refs) {
        classes.forEach(x -> x.findClasses(refs));
    }

    /**
     * 
     * @return
     */
    public ArrayList<Class> getClasses() {
        return classes;
    }

    /**
     * 
     * @param classes
     */
    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
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
