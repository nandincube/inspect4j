package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.ReturnStmt;


public class Method {
    private String declarationAsString;
    private String name;
    private NodeList<Parameter> params;
    private List<ReturnStmt> returnStmts;
    private int lineMin; 
    private int lineMax;
    private List<Variable> storedVarCalls;
    private transient Class parentClass;
    private transient Interface parentInterface;
    
    public Method(MethodDeclaration method,  List<ReturnStmt> returnStmts, ClassCollection classes, InterfaceCollection interfaces){
        this.declarationAsString =  method.getDeclarationAsString(true, true, true);
        this.name =  method.getNameAsString();
        this.params = method.getParameters();
        this.returnStmts = returnStmts;
        this.lineMin = method.getBegin().get().line;
        this.lineMax = method.getEnd().get().line;
        this.parentClass = findParentClass(method, classes);
        this.parentInterface = findParentInterface(method, interfaces);
        this.storedVarCalls = new ArrayList<Variable>();

    }

    private Class findParentClass(MethodDeclaration md, ClassCollection classCol){
        if(md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()){
            String pClass = md.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
            for(Class cl: classCol.getClasses()){
                if(pClass.equals(cl.getName())){
                    return cl;
                }
            }
        }
        return null;
    }

    private Interface findParentInterface(MethodDeclaration md, InterfaceCollection interfaceCol){
        if(md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()){
            String pInterface = md.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
            for(Interface intf: interfaceCol.getInterfaces()){
                if(pInterface.equals(intf.getName())){
                    return intf;
                }
            }
        }
        return null;
    }

    

    public Class getParentClass() {
        return parentClass;
    }

    public void setParentClass(Class parentClass) {
        this.parentClass = parentClass;
    }

    public Interface getParentInterface() {
        return parentInterface;
    }

    public void setParentInterface(Interface parentInterface) {
        this.parentInterface = parentInterface;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeList<Parameter> getParams() {
        return params;
    }

    public void setParams(NodeList<Parameter> params) {
        this.params = params;
    }

    public String getDeclarationAsString() {
        return declarationAsString;
    }

    public void setDeclarationAsString(String declarationAsString) {
        this.declarationAsString = declarationAsString;
    }

    public List<ReturnStmt> getReturnStmts() {
        return returnStmts;
    }

    public void setReturnStmts(List<ReturnStmt> returnStmts) {
        this.returnStmts = returnStmts;
    }

    public int getLineMin() {
        return lineMin;
    }

    public void setLineMin(int lineMin) {
        this.lineMin = lineMin;
    }

    public int getLineMax() {
        return lineMax;
    }

    public void setLineMax(int lineMax) {
        this.lineMax = lineMax;
    }

    public void findVariables(VariableCollection vars){
        for(Variable v: vars.getVariables()){
            if(v.getParentMethod() != null){
                if( v.getParentMethod() == this){
                    storedVarCalls.add(v);
                }
            }
        }
    }
    @Override
    public String toString() {
        String output = "Method [declarationAsString=" + declarationAsString + ", name=" + name + ", params=" + params
                + ", returnStmts=" + returnStmts + ", lineMin=" + lineMin + ", lineMax=" + lineMax + ", parentClass=";
        output = parentClass != null ? output + parentClass.getName() : output + "null";
        output = output + ", storedVarCalls=";
        output = !storedVarCalls.isEmpty() ? output + storedVarCalls : output + "null";
        output = output + ", parentInterface=";
        output = parentInterface != null ? output + parentInterface.getName() + "]": output + "null]";
        return output; 
    }

    public List<Variable> getStoredVarCalls() {
        return storedVarCalls;
    }

    public void setStoredVarCalls(List<Variable> storedVarCalls) {
        this.storedVarCalls = storedVarCalls;
    }
  
}
