package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

/**
 * 
 */
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
   // private MethodDeclaration declaration;
    private CallableDeclaration<?> declaration;
    private boolean isMain;
    private AccessModifierType accessModifer;
   
    private NonAccessModifierType nonAccessModifer;
 
       
    /**
     * 
     * @param method
     * @param returnStmts
     */
    public Method(MethodDeclaration method, HashSet<String> returnStmts) {
        init(method);
        this.returnType = method.getTypeAsString();
        this.returnStmts = returnStmts;
        this.isMain = checkIfMain(method);
    }
       
    /**
     * 
     * @param method
     * @param returnStmts
     */
    public Method(ConstructorDeclaration method) {
        init(method);
        this.returnType = "void";
        this.returnStmts = new HashSet<String>();
        this.isMain = false;
    }

    public void init(CallableDeclaration<?> method){
        this.name = method.getNameAsString();
        this.declaration = method;
        this.parent = findParentClassInterface(method);
        this.lineMin = method.getBegin().get().line;
        this.lineMax = method.getEnd().get().line;
        this.params = new HashMap<String, String>();
        extractParameterInformation(method);
        this.classes = new ArrayList<Class>();
        this.interfaces = new ArrayList<Interface>();
        this.references = new ArrayList<MethodReference>();
        this.lambdas = new ArrayList<Lambda>();
        this.storedVarCalls = new ArrayList<Variable>();
        this.javaDoc = getJavaDoc(method);
        extractAccessModifier();
        extractNonAccessModifier();

    }

      
    // /**
    //  * 
    //  * @param method
    //  * @param returnStmts
    //  */
    // public Method(MethodDeclaration method, HashSet<String> returnStmts) {
    //     this.name = method.getNameAsString();*
    //     this.params = new HashMap<String, String>();*
    //     this.returnType = method.getTypeAsString();
    //     extractParameterInformation(method);*
    //     this.returnStmts = returnStmts;
    //     this.lineMin = method.getBegin().get().line;*
    //     this.lineMax = method.getEnd().get().line;*
    //     this.parent = findParentClassInterface(method);*
    //     this.declaration = method;*
    //     this.isMain = checkIfMain(method);

    //     this.classes = new ArrayList<Class>();*
    //     this.interfaces = new ArrayList<Interface>();*
    //     this.references = new ArrayList<MethodReference>();*
    //     this.lambdas = new ArrayList<Lambda>();*
    //     this.storedVarCalls = new ArrayList<Variable>();*
    //     this.javaDoc = getJavaDoc(method);*
    // }


        /**
     * 
     */
    private void extractAccessModifier() {
        switch (declaration.getAccessSpecifier()) {
            case PUBLIC:
                accessModifer = AccessModifierType.PUBLIC;
                break;
            case PROTECTED:
                accessModifer = AccessModifierType.PROTECTED;
                break;
            case PRIVATE:
                accessModifer = AccessModifierType.PRIVATE;
                break;
            case NONE:
                accessModifer = AccessModifierType.DEFAULT;
                break;
        }
    }

    /**
     * 
     */
    private void extractNonAccessModifier() {

        for (Modifier mod : declaration.getModifiers()) {
            if (nonAccessModifer == null || nonAccessModifer == NonAccessModifierType.NONE) {
                switch (mod.getKeyword()) {
                    case STATIC:
                        nonAccessModifer = NonAccessModifierType.STATIC;
                        break;
                    case ABSTRACT:
                        nonAccessModifer = NonAccessModifierType.ABSTRACT;
                        break;
                    case FINAL:
                        nonAccessModifer = NonAccessModifierType.FINAL;
                        break;
                    default:
                        nonAccessModifer = NonAccessModifierType.NONE;
                        break;
                }
            }

        }

        if (nonAccessModifer == null) {
            nonAccessModifer = NonAccessModifierType.NONE;
        }

    }
    /**
     * 
     * @param md
     * @return
     */
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

    /**
     * 
     * @param md
     */
    private void extractParameterInformation(CallableDeclaration<?> md) {
        if (md.getParameters() != null) {
            for (Parameter param : md.getParameters()) {
                params.put(param.getNameAsString(), param.getTypeAsString());
            }
        }
    }

    /**
     * 
     * @return
     */
    public List<Lambda> getLambdas() {
        return lambdas;
    }

    /**
     * 
     * @param lambdas
     */
    public void setLambdas(List<Lambda> lambdas) {
        this.lambdas = lambdas;
    }

    /**
     * 
     * @return
     */
    public String getJavaDoc() {
        return javaDoc;
    }

    /**
     * 
     * @param javaDoc
     */
    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    /**
     * 
     * @param md
     * @return
     */
    private String getJavaDoc(CallableDeclaration<?> md) {
        if (md.getJavadoc().isPresent()) {
            return md.getJavadocComment().get().getContent().strip();
        }
        return null;
    }

    /**
     * 
     * @param md
     * @return
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(CallableDeclaration<?> md) {
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

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     */
    public int getLineMin() {
        return lineMin;
    }

    /**
     * 
     * @param lineMin
     */
    public void setLineMin(int lineMin) {
        this.lineMin = lineMin;
    }

    /**
     * 
     * @return
     */
    public int getLineMax() {
        return lineMax;
    }

    /**
     * 
     * @param lineMax
     */
    public void setLineMax(int lineMax) {
        this.lineMax = lineMax;
    }

    /**
     * 
     * @param vars
     */
    public void findVariables(VariableCollection vars){
        for (Variable v : vars.getVariables()) {
            ParentEntity<?> varParent = v.getParent();
            if (varParent != null &&  varParent.getEntityType() == EntityType.METHOD) {
                if(varParent.getDeclaration() == declaration){
                    storedVarCalls.add(v);
                }
                
            }
        }
    }

    /**
     * 
     * @param ls
     */
    public void findLambdas(LambdaCollection ls){
        for (Lambda l : ls.getLambdas()) {
            ParentEntity<?> lambdaParent = l.getParent();
            if (lambdaParent != null &&  lambdaParent.getEntityType() == EntityType.METHOD) {
                if(lambdaParent.getDeclaration() == declaration){
                    lambdas.add(l);
                }
                
            }
        }
    }

    /**
     * 
     * @param cls
     */
    public void findClasses(ClassCollection cls){
        for (Class cl : cls.getClasses()) {
            ParentEntity<?> classParent = cl.getParent();
            if (classParent != null &&  classParent.getEntityType() == EntityType.METHOD) {
                if(classParent.getDeclaration() == declaration){
                    classes.add(cl);
                }
                
            }
        }
    }

    /**
     * 
     * @param intfs
     */
    public void findInterfaces(InterfaceCollection intfs){
        for (Interface intf: intfs.getInterfaces()) {
            ParentEntity<?> interfaceParent = intf.getParent();
            if (interfaceParent != null &&  interfaceParent.getEntityType() == EntityType.METHOD) {
                if(interfaceParent.getDeclaration() == declaration){
                    interfaces.add(intf);
                }
                
            }
        }
    }

    /**
     * 
     * @param refs
     */
    public void findReferences(MethodReferenceCollection refs){
        for (MethodReference ref : refs.getMethodReferences()) {
            ParentEntity<?> referenceParent = ref.getParent();
            if (referenceParent != null &&  referenceParent.getEntityType() == EntityType.METHOD) {
                if(referenceParent.getDeclaration() == declaration){
                    references.add(ref);
                }
            }
        }
    }

    /**
     * 
     */
    @Override
    public String toString() {
        return "Method [javaDoc=" + javaDoc + ", name=" + name + ", params=" + params + ", returnStmts=" + returnStmts
                + ", returnType=" + returnType + ", lineMin=" + lineMin + ", lineMax=" + lineMax + ", storedVarCalls="
                + storedVarCalls + ", lambdas=" + lambdas + ", classes=" + classes + ", interfaces=" + interfaces
                + ", references=" + references + ", parent=" + parent + ", declaration=" + declaration + "]";
    }

    /**
     * 
     * @return
     */
    public List<Variable> getStoredVarCalls() {
        return storedVarCalls;
    }

    /**
     * 
     * @param storedVarCalls
     */
    public void setStoredVarCalls(List<Variable> storedVarCalls) {
        this.storedVarCalls = storedVarCalls;
    }

    /**
     * 
     * @return
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * 
     * @param params
     */
    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    /**
     * 
     * @return
     */
    public HashSet<String> getReturnStmts() {
        return returnStmts;
    }

    /**
     * 
     * @param returnStmts
     */
    public void setReturnStmts(HashSet<String> returnStmts) {
        this.returnStmts = returnStmts;
    }

    /**
     * 
     * @return
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * 
     * @param returnType
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * 
     * @return
     */
    public ParentEntity<?> getParent() {
        return parent;
    }

    /**
     * 
     * @param parent
     */
    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

    /**
     * 
     * @return
     */
    public List<Class> getClasses() {
        return classes;
    }

    /**
     * 
     * @param classes
     */
    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    /**
     * 
     * @return
     */
    public List<Interface> getInterfaces() {
        return interfaces;
    }

    /**
     * 
     * @param interfaces
     */
    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * 
     * @return
     */
    public List<MethodReference> getReferences() {
        return references;
    }

    /**
     * 
     * @param references
     */
    public void setReferences(List<MethodReference> references) {
        this.references = references;
    }

    /**
     * 
     * @return
     */
    public CallableDeclaration<?> getDeclaration() {
        return declaration;
    }

    /**
     * 
     * @param declaration
     */
    public void setDeclaration(MethodDeclaration declaration) {
        this.declaration = declaration;
    }

    /**
     * 
     * @return
     */
    public boolean isMain() {
        return isMain;
    }

    /**
     * 
     * @param isMain
     */
    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    /**
     * 
     * @param declaration
     */
    public void setDeclaration(CallableDeclaration<?> declaration) {
        this.declaration = declaration;
    }

    /**
     * 
     * @return
     */
    public AccessModifierType getAccessModifer() {
        return accessModifer;
    }

    /**
     * 
     * @param accessModifer
     */
    public void setAccessModifer(AccessModifierType accessModifer) {
        this.accessModifer = accessModifer;
    }

    /***
     * 
     * @return
     */
    public NonAccessModifierType getNonAccessModifer() {
        return nonAccessModifer;
    }

    /**
     * 
     * @param nonAccessModifer
     */
    public void setNonAccessModifer(NonAccessModifierType nonAccessModifer) {
        this.nonAccessModifer = nonAccessModifer;
    }


}
