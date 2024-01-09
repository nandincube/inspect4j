package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Method {
    private String javaDoc;
    private String name;
    private HashMap<String, String> params;
    private HashSet<String> returnStmts;
    private String returnType;
    private int lineMin;
    private int lineMax;
    private List<Variable> storedVarCalls;
    private List<Lambda> lambdas;
    private Class parentClass;
    private Interface parentInterface;
   
    public Method(MethodDeclaration method, HashSet<String> returnStmts, ClassCollection classes,
            InterfaceCollection interfaces) {
        this.name = method.getNameAsString();
        this.params = new HashMap<String, String>();
        this.returnType = method.getTypeAsString();
        extractParameterInformation(method);
        this.returnStmts = returnStmts;
        this.lineMin = method.getBegin().get().line;
        this.lineMax = method.getEnd().get().line;
        this.parentClass = findParentClass(method, classes);
        this.parentInterface = findParentInterface(method, interfaces);
        this.storedVarCalls = new ArrayList<Variable>();
        this.javaDoc = getJavaDoc(method);
        // findInnerOrLocalChildrenClasses(classes);
    }

    // @SerializedName("min_max_lineno")
    @SerializedName("min_max_lineno")
    public JsonObject lineRange() {
        JsonObject range = new JsonObject();
        range.addProperty("min_lineno", lineMin);
        range.addProperty("max_lineno", lineMax);
        return range;
    }

    private void extractParameterInformation(MethodDeclaration md) {
        if (md.getParameters() != null) {
            for (Parameter param : md.getParameters()) {
                params.put(param.getNameAsString(), param.getTypeAsString());
            }
        }
    }

    public List<Lambda> getLambdas() {
        return lambdas;
    }

    public void setLambdas(List<Lambda> lambdas) {
        this.lambdas = lambdas;
    }

    public String getJavaDoc() {
        return javaDoc;
    }

    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    private String getJavaDoc(MethodDeclaration md) {
        if (md.getJavadoc().isPresent()) {
            return md.getJavadocComment().get().getContent().strip();
        }
        return null;
    }

    // private void findInnerOrLocalChildrenClasses(ClassCollection classCol){
    // for(Class cl: classCol.getClasses()){
    // if(cl.isInnerClass() == true || cl.isLocalClass() == true ){
    // if(cl.getAncestor().equals(declarationAsString)){
    // innerOrLocalChildrenClasses.add(cl);
    // }
    // }
    // }
    // }

    private Class findParentClass(MethodDeclaration md, ClassCollection classCol) {
        if (md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            String pClass = md.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
            for (Class cl : classCol.getClasses()) {
                if (pClass.equals(cl.getName())) {
                    return cl;
                }
            }
        }
        return null;
    }

    private Interface findParentInterface(MethodDeclaration md, InterfaceCollection interfaceCol) {
        if (md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            String pInterface = md.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
            for (Interface intf : interfaceCol.getInterfaces()) {
                if (pInterface.equals(intf.getName())) {
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

    // public String getDeclarationAsString() {
    // return declarationAsString;
    // }

    // public void setDeclarationAsString(String declarationAsString) {
    // this.declarationAsString = declarationAsString;
    // }

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

    public void findVariables(VariableCollection vars) {
        // for (Variable v : vars.getVariables()) {
        //     if (v.getParentMethod() != null) {
        //         if (v.getParentMethod() == this) {
        //             storedVarCalls.add(v);
        //         }
        //     }
        // }

        for (Variable v : vars.getVariables()) {
            ParentEntity<?,?> par = v.getParent();
            if (par != null && par.getEntityType() == EntityType.METHOD) {
                

            }
        }

    }

    public void findLambdas(LambdaCollection lams) {
        for (Lambda l : lams.getLambdas()) {
            if (l.getParentMethod() != null) {
                if (l.getParentMethod() == this) {
                    lambdas.add(l);
                }
            }
        }
    }

    @Override
    public String toString() {
        String output = "Method [name=" + name + ", params=" + params
                + ", returnStmts=" + returnStmts + ", lineMin=" + lineMin + ", lineMax=" + lineMax + ", parentClass=";
        output = parentClass != null ? output + parentClass.getName() : output + "null";
        output = output + ", storedVarCalls=";
        output = !storedVarCalls.isEmpty() ? output + storedVarCalls : output + "null";
        output = output + ", parentInterface=";
        output = parentInterface != null ? output + parentInterface.getName() + "]" : output + "null]";
        return output;
    }

    public List<Variable> getStoredVarCalls() {
        return storedVarCalls;
    }

    public void setStoredVarCalls(List<Variable> storedVarCalls) {
        this.storedVarCalls = storedVarCalls;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public HashSet<String> getReturnStmts() {
        return returnStmts;
    }

    public void setReturnStmts(HashSet<String> returnStmts) {
        this.returnStmts = returnStmts;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }


}
