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
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.javadoc.JavadocBlockTag;

/**
 * Class to represent a method in the AST.
 */
public class Method {
    private String javaDoc; // the javadoc for the method
    private String name; // the name of the method
    private HashMap<String, String> params; // the parameters of the method
    private HashSet<String> returnStmts; // the return statements of the method
    private String returnType; // the return type of the method
    private int lineMin; // the minimum line number of the method
    private int lineMax; // the maximum line number of the method
    private List<Variable> storedVarCalls; // variables which store the result of method calls
    private List<Lambda> lambdas; // lambdas in the method
    private List<Class> classes; // classes in the method
    private List<Interface> interfaces; // interfaces in the method
    private List<MethodReference> references; // method references in the method
    private ParentEntity<?> parent; // the parent entity of the method
    private CallableDeclaration<?> declaration; // the declaration of the method
    private boolean isMain; // whether the method is the main method
    private AccessModifierType accessModifer; // the access modifier of the method
    private List<String> directCalls; // the direct method calls in the method

    private List<NonAccessModifierType> nonAccessModifer; // the non-access modifiers of the method

    /**
     * Constructor
     * 
     * @param method      - the method declaration
     * @param returnStmts - the return statements of the method
     */
    public Method(MethodDeclaration method, HashSet<String> returnStmts) {
        init(method);
        this.returnType = method.getTypeAsString();
        this.returnStmts = returnStmts;
        this.isMain = checkIfMain(method);
    }

    /**
     * Constructor
     * 
     * @param method      - the method declaration
     * @param returnStmts - the return statements of the method
     */
    public Method(ConstructorDeclaration method) {
        init(method);
        this.returnType = "void"; // constructors do not have a return type
        this.returnStmts = new HashSet<String>();
        this.isMain = false;
    }

    /**
     * Method to initialise the method object
     * 
     * @param method - the method declaration as a callable declaration
     */
    public void init(CallableDeclaration<?> method) {
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
        this.directCalls = new ArrayList<String>();
        this.javaDoc = getJavaDoc(method);
        this.nonAccessModifer = new ArrayList<NonAccessModifierType>();
        extractAccessModifier();
        extractNonAccessModifier();
        extractDirectCalls();

    }

    /**
     * Method to extract the access modifier of the method
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
     * Method to extract the non-access modifiers of the method
     */
    private void extractNonAccessModifier() {
        for (Modifier mod : declaration.getModifiers()) {
            switch (mod.getKeyword()) {
                case STATIC:
                    nonAccessModifer.add(NonAccessModifierType.STATIC);
                    break;
                case ABSTRACT:
                    nonAccessModifer.add(NonAccessModifierType.ABSTRACT);
                    break;
                case FINAL:

                    nonAccessModifer.add(NonAccessModifierType.FINAL);
                    break;
                default:
                    nonAccessModifer.add(NonAccessModifierType.NONE);
                    break;
            }

        }

        if (nonAccessModifer.size() > 1 && nonAccessModifer.contains(NonAccessModifierType.NONE)) { // if there are
                                                                                                    // multiple
                                                                                                    // non-access
                                                                                                    // modifiers and one
                                                                                                    // of them is none
            nonAccessModifer.remove(NonAccessModifierType.NONE);
        } else if (parent.getEntityType() == EntityType.INTERFACE
                && (nonAccessModifer.contains(NonAccessModifierType.NONE) ||
                        nonAccessModifer.size() == 0)) { // if the parent is an interface and the non-access modifier is
                                                         // none or empty
            nonAccessModifer.add(NonAccessModifierType.ABSTRACT);
        } else if (nonAccessModifer.size() == 0) { // if the non-access modifier is empty
            nonAccessModifer.add(NonAccessModifierType.NONE);
        }

    }

    /**
     * Method to check if the method is the main method
     * 
     * @param md - the method declaration
     * @return boolean - whether the method is the main method
     */
    private boolean checkIfMain(MethodDeclaration md) {
        if (md.isPublic() && md.isStatic() && md.getType().isVoidType()) { // if the method is public, static and void
            if (name.equals("main") && params.size() == 1) {
                String paramType = params.values().stream().findFirst().get();
                if (paramType.equals("String[]")) { // if the parameter is a string array
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method to extract the direct method calls in the method
     */
    private void extractDirectCalls() {
        List<MethodCallExpr> calls = declaration.findAll(MethodCallExpr.class);
        List<ObjectCreationExpr> consCalls = declaration.findAll(ObjectCreationExpr.class);
        if (calls != null) {
            for (MethodCallExpr call : calls) {
                if (!(call.getParentNode().get() instanceof AssignExpr|| call.getParentNode().get() instanceof VariableDeclarationExpr
                || call.getParentNode().get() instanceof VariableDeclarator)) {

                    String scope = call.asMethodCallExpr().getScope().isPresent()
                            ? call.asMethodCallExpr().getScope().get().toString() + "."
                            : "";

                    directCalls.add(scope + call.getNameAsString());
                }
            }
        }

        if(consCalls != null){
            for (ObjectCreationExpr call : consCalls) {
                if (!(call.getParentNode().get() instanceof AssignExpr|| call.getParentNode().get() instanceof VariableDeclarationExpr 
                || call.getParentNode().get() instanceof VariableDeclarator)) {

                    String scope = call.asObjectCreationExpr().getType().toString();
                    directCalls.add(scope);
                }
            }
        }
    }

    /**
     * Method to extract the parameters of the method
     * 
     * @param md - the method declaration as a callable declaration
     */
    private void extractParameterInformation(CallableDeclaration<?> md) {
        if (md.getParameters() != null) {
            for (Parameter param : md.getParameters()) {
                params.put(param.getNameAsString(), param.getTypeAsString());
            }
        }
    }

    /**
     * Method to get the lambdas in the method
     * 
     * @return List<Lambda> - the lambdas in the method
     */
    public List<Lambda> getLambdas() {
        return lambdas;
    }

    /**
     * Method to set the lambdas in the method
     * 
     * @param lambdas - the lambdas in the method
     */
    public void setLambdas(List<Lambda> lambdas) {
        this.lambdas = lambdas;
    }

    /**
     * Method to get the javadoc comment for the method
     * 
     * @return String - the javadoc comment for the method
     */
    public String getJavaDoc() {
        return javaDoc;
    }

    /**
     * Method to set the javadoc comment for the method
     * 
     * @param javaDoc - the javadoc comment for the method
     */
    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    /**
     * Method to get the javadoc comment for the method
     * 
     * @param md - the method declaration
     * @return String - the javadoc comment for the method
     */
    private String getJavaDoc(CallableDeclaration<?> md) {
        if (md.getJavadoc().isPresent()) { // if the method has a javadoc
            String doc = md.getJavadoc().get().getDescription().toText().strip(); // get the description of the javadoc
            doc = doc.replaceFirst("^[*]+", "").strip(); // remove the leading asterisks
            if (md.getJavadoc().get().getBlockTags().size() > 0) { // if there are tags in the javadoc

                List<JavadocBlockTag> tags = md.getJavadoc().get().getBlockTags();
                doc = doc + ". [tags = [";
                for (int i = 0; i < tags.size(); i++) {
                    if (i > 0)
                        doc = doc + ", ";

                    // System.out.println(tags.get(i));
                    doc = doc + "@" + tags.get(i).getType().name() + " " + tags.get(i).getTagName() + " " +
                            tags.get(i).getContent().toText().strip(); // get the tag name and content
                }
                doc = doc + " ]";
            }
            return doc.replaceFirst("^[*]+", "").strip();
        }
        return null;
    }

    /**
     * finds the parent class/interface entity of the method. The parent entity is
     * the most recent or deepest class/interface ancestor of the method.
     * 
     * @param md - the method declaration
     * @return ParentEntity<ClassOrInterfaceDeclaration> - the parent
     *         class/interface entity of the method
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(CallableDeclaration<?> md) {
        if (md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) { // if the method is inside a
                                                                              // class/interface
            ClassOrInterfaceDeclaration parentIC = md.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if (parentIC == null)
                System.out.println("Could not find parent for this method");
            if (parentIC.isInterface()) { // if the parent is an interface
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.INTERFACE);
            } else {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.CLASS);
            }
        }
        return null;
    }

    /**
     * Method to get the name of the method
     * 
     * @return String - the name of the method
     */
    public String getName() {
        return name;
    }

    /**
     * Method to set the name of the method
     * 
     * @param name - the name of the method
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method to get the minimum line number of the method
     * 
     * @return int - the minimum line number of the method
     */
    public int getLineMin() {
        return lineMin;
    }

    /**
     * Method to set the minimum line number of the method
     * 
     * @param lineMin - the minimum line number of the method
     */
    public void setLineMin(int lineMin) {
        this.lineMin = lineMin;
    }

    /**
     * Method to get the maximum line number of the method
     * 
     * @return int - the maximum line number of the method
     */
    public int getLineMax() {
        return lineMax;
    }

    /**
     * Method sets the maximum line number of the method
     * 
     * @param lineMax - the maximum line number of the method
     */
    public void setLineMax(int lineMax) {
        this.lineMax = lineMax;
    }

    /**
     * Method to find the variables contained in this method
     * 
     * @param vars - the variable collection
     */
    public void findVariables(VariableCollection vars) {
        for (Variable v : vars.getVariables()) {
            ParentEntity<?> varParent = v.getParent();
            if (varParent != null && varParent.getEntityType() == EntityType.METHOD) { // the parent of the variable is
                                                                                       // a method
                if (varParent.getDeclaration() == declaration) { // if the variable is in this method
                    storedVarCalls.add(v);
                }

            }
        }
    }

    /**
     * Method to find the lambdas contained in this method
     * 
     * @param ls - the lambda collection
     */
    public void findLambdas(LambdaCollection ls) {
        for (Lambda l : ls.getLambdas()) {
            ParentEntity<?> lambdaParent = l.getParent();
            if (lambdaParent != null && lambdaParent.getEntityType() == EntityType.METHOD) { // the parent of the lambda
                                                                                             // is a method
                if (lambdaParent.getDeclaration() == declaration) { // if the lambda is in this method
                    lambdas.add(l);
                }

            }
        }
    }

    /**
     * Method to find the classes contained in this method
     * 
     * @param cls - the class collection
     */
    public void findClasses(ClassCollection cls) {
        for (Class cl : cls.getClasses()) {
            ParentEntity<?> classParent = cl.getParent();
            if (classParent != null && classParent.getEntityType() == EntityType.METHOD) { // the parent of the class is
                                                                                           // a method
                if (classParent.getDeclaration() == declaration) { // if the class is in this method
                    classes.add(cl);
                }

            }
        }
    }

    /**
     * Method to find the interfaces contained in this method
     * 
     * @param intfs - the interface collection
     */
    public void findInterfaces(InterfaceCollection intfs) {
        for (Interface intf : intfs.getInterfaces()) {
            ParentEntity<?> interfaceParent = intf.getParent();
            if (interfaceParent != null && interfaceParent.getEntityType() == EntityType.METHOD) { // the parent of the
                                                                                                   // interface is a
                                                                                                   // method
                if (interfaceParent.getDeclaration() == declaration) { // if the interface is in this method
                    interfaces.add(intf);
                }

            }
        }
    }

    /**
     * Method to find the method references for this method
     * 
     * @param refs - the method reference collection
     */
    public void findReferences(MethodReferenceCollection refs) {
        for (MethodReference ref : refs.getMethodReferences()) {
            ParentEntity<?> referenceParent = ref.getParent();
            if (referenceParent != null && referenceParent.getEntityType() == EntityType.METHOD) { // the parent of the
                                                                                                   // method reference
                                                                                                   // is a method
                if (referenceParent.getDeclaration() == declaration) { // if the reference is in this method
                    references.add(ref);
                }
            }
        }
    }

    /**
     * Method to get the string representation of the method
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
     * Method to set the stored variable calls
     * 
     * @param storedVarCalls - the stored variable calls
     */
    public void setStoredVarCalls(List<Variable> storedVarCalls) {
        this.storedVarCalls = storedVarCalls;
    }

    /**
     * Method to get the parameters of the method
     * 
     * @return HashMap<String, String> - the parameters of the method
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * Method to set the parameters of the method
     * 
     * @param params - the parameters of the method
     */
    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    /**
     * Method to get the return statements of the method
     * 
     * @return HashSet<String> - the return statements of the method
     */
    public HashSet<String> getReturnStmts() {
        return returnStmts;
    }

    /**
     * Method to set the return statements of the method
     * 
     * @param returnStmts - the return statements of the method
     */
    public void setReturnStmts(HashSet<String> returnStmts) {
        this.returnStmts = returnStmts;
    }

    /**
     * Method to get the return type of the method
     * 
     * @return String - the return type of the method
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Method to set the return type of the method
     * 
     * @param returnType - the return type of the method
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Method to get the parent entity of the method
     * 
     * @return ParentEntity<?> - the parent entity of the method
     */
    public ParentEntity<?> getParent() {
        return parent;
    }

    /**
     * Method to set the parent entity of the method
     * 
     * @param parent - the parent entity of the method
     */
    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

    /**
     * Method to get the classes in the method
     * 
     * @return List<Class> - the classes in the method
     */
    public List<Class> getClasses() {
        return classes;
    }

    /**
     * Method to set the classes in the method
     * 
     * @param classes - the classes in the method
     */
    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    /**
     * Method to get the interfaces in the method
     * 
     * @return List<Interface> - the interfaces in the method
     */
    public List<Interface> getInterfaces() {
        return interfaces;
    }

    /**
     * Method to set the interfaces in the method
     * 
     * @param interfaces - the interfaces in the method
     */
    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * Method to get the method references of the method
     * 
     * @return List<MethodReference> - the method references of the method
     */
    public List<MethodReference> getReferences() {
        return references;
    }

    /**
     * Method to set the method references of the method
     * 
     * @param references - the method references of the method
     */
    public void setReferences(List<MethodReference> references) {
        this.references = references;
    }

    /**
     * Method to get the declaration of the method
     * 
     * @return CallableDeclaration<?> - the declaration of the method
     */
    public CallableDeclaration<?> getDeclaration() {
        return declaration;
    }

    /**
     * Method to set the declaration of the method
     * 
     * @param declaration - the declaration of the method as method declaration
     */
    public void setDeclaration(MethodDeclaration declaration) {
        this.declaration = declaration;
    }

    /**
     * Method to check if the method is the main method
     * 
     * @return boolean - whether the method is the main method
     */
    public boolean isMain() {
        return isMain;
    }

    /**
     * Method to set whether the method is the main method
     * 
     * @param isMain
     */
    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    /**
     * Method to set the declaration of the method
     * 
     * @param declaration - the declaration of the method as callable declaration
     */
    public void setDeclaration(CallableDeclaration<?> declaration) {
        this.declaration = declaration;
    }

    /**
     * Method to get the access modifier of the method
     * 
     * @return AccessModifierType - the access modifier of the method
     */
    public AccessModifierType getAccessModifer() {
        return accessModifer;
    }

    /**
     * Method to set the access modifier of the method
     * 
     * @param accessModifer - the access modifier of the method
     */
    public void setAccessModifer(AccessModifierType accessModifer) {
        this.accessModifer = accessModifer;
    }

    /***
     * Method to get the non-access modifiers of the method
     * 
     * @return List<NonAccessModifierType> - the non-access modifiers of the method
     */
    public List<NonAccessModifierType> getNonAccessModifer() {
        return nonAccessModifer;
    }

    /**
     * Method to set the non-access modifiers of the method
     * 
     * @param nonAccessModifer - the non-access modifiers of the method
     */
    public void setNonAccessModifer(List<NonAccessModifierType> nonAccessModifer) {
        this.nonAccessModifer = nonAccessModifer;
    }

    /**
     * Method to get the direct method calls in the method
     * 
     * @return List<String> - the direct method calls in the method
     */
    public List<String> getDirectCalls() {
        return directCalls;
    }

    /**
     * Method to set the direct method calls in the method
     * 
     * @param directCalls - the direct method calls in the method
     */
    public void setDirectCalls(List<String> directCalls) {
        this.directCalls = directCalls;
    }

}
