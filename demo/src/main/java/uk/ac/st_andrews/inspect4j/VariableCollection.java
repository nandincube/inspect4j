package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class VariableCollection {

    private ArrayList<Variable> variables;
    private CompilationUnit ast;
    private ClassCollection classes;
    private InterfaceCollection interfaces;
    private MethodCollection methods;

  
    public VariableCollection(CompilationUnit ast, ClassCollection classes, InterfaceCollection interfaces, MethodCollection methods){
        this.variables = new ArrayList<>();
        this.ast = ast;
        this.methods = methods;
        this.classes = classes;
        this.interfaces = interfaces;
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
        variables.forEach(x -> System.out.println(x.toString()));

    }


    private void getDeclarationInfo(){
        VoidVisitor<List<Variable>> variableCollector = new StoredVariableCallsCollector();
        variableCollector.visit(ast, variables);
    }
    
  

    public CompilationUnit getAst() {
        return ast;
    }

    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }


    public ClassCollection getClasses() {
        return classes;
    }

    public void setClasses(ClassCollection classes) {
        this.classes = classes;
    }

    public InterfaceCollection getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(InterfaceCollection interfaces) {
        this.interfaces = interfaces;
    }

    public MethodCollection getMethods() {
        return methods;
    }

    public void setMethods(MethodCollection methods) {
        this.methods = methods;
    }

    private class StoredVariableCallsCollector extends VoidVisitorAdapter<List<Variable> >{
                    
        @Override
        public void visit(MethodDeclaration md, List<Variable> collection) { 
            super.visit(md,collection);

            List<AssignExpr> assignments = md.findAll(AssignExpr.class);
            
            for(AssignExpr assignment: assignments ){

                if(assignment.getTarget().isNameExpr() || assignment.getTarget().isFieldAccessExpr()){ // is the thing being assigned a variable or field

                    if(assignment.getValue().isMethodCallExpr()){

                       // String idAsString = assignment.getTarget().asNameExpr().getNameAsString();
                       // String methodNameAsString = assignment.getValue().asMethodCallExpr().getName().asString();
                        collection.add(new Variable(assignment,classes,interfaces,methods));
                        //collection.add(new Variable(idAsString,null,null, methodNameAsString));
                    }

                } 
            }
        }

    }


}
