package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.google.gson.JsonObject;

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
    private List<Class> classes;
    private List<Interface> interfaces;
    private List<MethodReference> references;
    private ParentEntity<?> parent;
    private MethodDeclaration declaration;
    private boolean isMain;
    //private Class parentClass;
   // private Interface parentInterface;
    //private MethodDeclaration declaration;
   
    public Method(MethodDeclaration method, HashSet<String> returnStmts) {
        this.name = method.getNameAsString();
        this.params = new HashMap<String, String>();
        this.returnType = method.getTypeAsString();
        extractParameterInformation(method);
        this.returnStmts = returnStmts;
        this.lineMin = method.getBegin().get().line;
        this.lineMax = method.getEnd().get().line;
        this.parent = findParentClassInterface(method);
        this.declaration = method;
        this.isMain = checkIfMain(method);

        this.classes = new ArrayList<Class>();
        this.interfaces = new ArrayList<Interface>();
        this.references = new ArrayList<MethodReference>();
        this.lambdas = new ArrayList<Lambda>();

        //this.parentClass = findParentClass(method, classes);
        //this.parentInterface = findParentInterface(method, interfaces);
        this.storedVarCalls = new ArrayList<Variable>();
        this.javaDoc = getJavaDoc(method);
        // findInnerOrLocalChildrenClasses(classes);
    }

    public JsonObject lineRange() {
        JsonObject range = new JsonObject();
        range.addProperty("min_lineno", lineMin);
        range.addProperty("max_lineno", lineMax);
        return range;
    }

    private boolean checkIfMain(MethodDeclaration md){
        if(md.isPublic() && md.isStatic() && md.getType().isVoidType()){
            if(name.equals("main") && params.size() == 1){
                String paramType = params.values().stream().findFirst().get();
                if(paramType.equals("String[]")){
                    return true;
                }
            }
        }
        return false;
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

    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(MethodDeclaration md) {
        if (md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = md.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if(parentIC == null) System.out.println("Could not find parent for this method");
            if(parentIC.isInterface()){
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.INTERFACE);
            }else{
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.CLASS);
            }
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

    // private Class findParentClass(MethodDeclaration md, ClassCollection classCol) {
    //     if (md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
    //         String pClass = md.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
    //         for (Class cl : classCol.getClasses()) {
    //             if (pClass.equals(cl.getName())) {
    //                 return cl;
    //             }
    //         }
    //     }
    //     return null;
    // }

    // private Interface findParentInterface(MethodDeclaration md, InterfaceCollection interfaceCol) {
    //     if (md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
    //         String pInterface = md.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
    //         for (Interface intf : interfaceCol.getInterfaces()) {
    //             if (pInterface.equals(intf.getName())) {
    //                 return intf;
    //             }
    //         }
    //     }
    //     return null;
    // }

    // public Class getParentClass() {
    //     return parentClass;
    // }

    // public void setParentClass(Class parentClass) {
    //     this.parentClass = parentClass;
    // }

    // public Interface getParentInterface() {
    //     return parentInterface;
    // }

    // public void setParentInterface(Interface parentInterface) {
    //     this.parentInterface = parentInterface;
    // }

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

    // public void findVariables(VariableCollection vars) {
    //     // for (Variable v : vars.getVariables()) {
    //     //     if (v.getParentMethod() != null) {
    //     //         if (v.getParentMethod() == this) {
    //     //             storedVarCalls.add(v);
    //     //         }
    //     //     }
    //     // }

    //     for (Variable v : vars.getVariables()) {
    //         ParentEntity<?,?> par = v.getParent();
    //         if (par != null && par.getEntityType() == EntityType.METHOD) {
                
    //         }
    //     }

    // }

    public void findVariables(VariableCollection vars){
        for (Variable v : vars.getVariables()) {
            ParentEntity<?> varParent = v.getParent();
            if (varParent != null &&  varParent.getEntityType() == EntityType.METHOD) {
                if(varParent.getDeclaration() == declaration){
                    System.out.println("Found Child");
                    storedVarCalls.add(v);
                }
                
            }
        }
    }

    public void findLambdas(LambdaCollection ls){
        for (Lambda l : ls.getLambdas()) {
            ParentEntity<?> lambdaParent = l.getParent();
            if (lambdaParent != null &&  lambdaParent.getEntityType() == EntityType.METHOD) {
                if(lambdaParent.getDeclaration() == declaration){
                    System.out.println("Found Child");
                    lambdas.add(l);
                }
                
            }
        }
    }

    public void findClasses(ClassCollection cls){
        for (Class cl : cls.getClasses()) {
            ParentEntity<?> classParent = cl.getParent();
            if (classParent != null &&  classParent.getEntityType() == EntityType.METHOD) {
                if(classParent.getDeclaration() == declaration){
                    System.out.println("Found Child");
                    classes.add(cl);
                }
                
            }
        }
    }

    public void findInterfaces(InterfaceCollection intfs){
        for (Interface intf: intfs.getInterfaces()) {
            ParentEntity<?> interfaceParent = intf.getParent();
            if (interfaceParent != null &&  interfaceParent.getEntityType() == EntityType.METHOD) {
                if(interfaceParent.getDeclaration() == declaration){
                    System.out.println("Found Child");
                    interfaces.add(intf);
                }
                
            }
        }
    }


    public void findReferences(MethodReferenceCollection refs){
        for (MethodReference ref : refs.getMethodReferences()) {
            ParentEntity<?> referenceParent = ref.getParent();
            if (referenceParent != null &&  referenceParent.getEntityType() == EntityType.METHOD) {
                if(referenceParent.getDeclaration() == declaration){
                    System.out.println("Found Child");
                    references.add(ref);
                }
                
            }
        }
    }



    @Override
    public String toString() {
        return "Method [javaDoc=" + javaDoc + ", name=" + name + ", params=" + params + ", returnStmts=" + returnStmts
                + ", returnType=" + returnType + ", lineMin=" + lineMin + ", lineMax=" + lineMax + ", storedVarCalls="
                + storedVarCalls + ", lambdas=" + lambdas + ", classes=" + classes + ", interfaces=" + interfaces
                + ", references=" + references + ", parent=" + parent + ", declaration=" + declaration + "]";
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

    public ParentEntity<?> getParent() {
        return parent;
    }

    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }

    public List<MethodReference> getReferences() {
        return references;
    }

    public void setReferences(List<MethodReference> references) {
        this.references = references;
    }

    public MethodDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(MethodDeclaration declaration) {
        this.declaration = declaration;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }


}
