package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

/**
 * Class to represent a class in the AST.
 */
public class Class {
    private String name; // the name of the class
    private ClassInterfaceCategory classCategory;// the category of the class
    private AccessModifierType accessModifer; // the access modifier of the class
    private List<NonAccessModifierType> nonAccessModifer; // the non-access modifiers of the class
    private int lineMin;// the starting line of the class
    private int lineMax;// the ending line of the class
    private List<Method> methods;// the methods in the class
    private List<String> typeParams;// the type parameters of the class
    private List<String> implementedInterfaces;// the interfaces implemented by the class
    private List<String> superClasses; // the super classes of the class
    private List<Class> classes; // the inner/nested classes of the class
    private List<Interface> interfaces; // the inner/nested interfaces of the class
    private List<MethodReference> references; // the method references in the class
    private List<Lambda> lambdas; // the lambdas in the class
    private ParentEntity<?> parent;// the parent entity of the class
    private ClassOrInterfaceDeclaration declaration; // the declaration of the class
    private List<Variable> storedVarCalls; // the variables in the class
    private String javaDoc; // the JavaDoc of the class

    /**
     * Constructor
     * 
     * @param classDecl - the class declaration
     */
    public Class(ClassOrInterfaceDeclaration classDecl) {
        NodeList<TypeParameter> typeParams = classDecl.getTypeParameters();
        NodeList<ClassOrInterfaceType> implementedInterfaces = classDecl.getImplementedTypes();
        NodeList<ClassOrInterfaceType> superClasses = classDecl.getExtendedTypes();

        this.name = classDecl.getNameAsString();
        this.lineMin = classDecl.getBegin().get().line;
        this.lineMax = classDecl.getEnd().get().line;
        this.declaration = classDecl;
        this.nonAccessModifer = new ArrayList<NonAccessModifierType>();
        extractAccessModifier();
        extractNonAccessModifier();
        extractClassCategory();

        this.typeParams = new ArrayList<String>();
        this.implementedInterfaces = new ArrayList<String>();
        this.superClasses = new ArrayList<String>();

        this.javaDoc = getJavaDoc(classDecl);
        this.parent = findParent(classDecl);

        this.storedVarCalls = new ArrayList<Variable>();
        this.methods = new ArrayList<Method>();
        this.classes = new ArrayList<Class>();
        this.interfaces = new ArrayList<Interface>();
        this.references = new ArrayList<MethodReference>();
        this.lambdas = new ArrayList<Lambda>();

        typesToString(typeParams);
        interfacesToString(implementedInterfaces);
        superClassesToString(superClasses);
    }

    /**
     * Extracts the access modifier of the class
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
     * Extracts the non-access modifiers of the class
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

        if (nonAccessModifer.size() > 1 && nonAccessModifer.contains(NonAccessModifierType.NONE)) {
            nonAccessModifer.remove(NonAccessModifierType.NONE);
        } else if (nonAccessModifer.size() == 0) {
            nonAccessModifer.add(NonAccessModifierType.NONE);
        }
    }

    /**
     * Extracts the category of the class
     */
    private void extractClassCategory() {
        if (declaration.isInnerClass()) {
            classCategory = ClassInterfaceCategory.INNER;
            return;
        } else if (declaration.isLocalClassDeclaration()) {
            classCategory = ClassInterfaceCategory.LOCAL;
            return;
        } else if (declaration.isNestedType() && declaration.isStatic()) {
            classCategory = ClassInterfaceCategory.STATIC_NESTED;
            return;
        } else {
            classCategory = ClassInterfaceCategory.STANDARD;
        }

    }

    /**
     * Gets the JavaDoc of the class
     * 
     * @return The JavaDoc of the class
     */
    public String getJavaDoc() {

        return javaDoc;
    }

    /**
     * Sets the JavaDoc of the class
     * 
     * @param javaDoc The JavaDoc of the class
     */
    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    /**
     * Gets the JavaDoc of the class from declaration
     * 
     * @param cl The class declaration
     * @return The JavaDoc of the class
     */
    private String getJavaDoc(ClassOrInterfaceDeclaration cl) {
        if (cl.getJavadoc().isPresent()) {
            String doc = cl.getJavadocComment().get().getContent().strip().toString();
            return doc.replaceFirst("^[*]+", "").strip();
        }
        return null;
    }

    /**
     * Converts the type parameters to string
     * 
     * @param t The type parameters as TypeParameter objects
     */
    public void typesToString(NodeList<TypeParameter> t) {
        t.forEach(x -> typeParams.add(x.getNameAsString().trim()));
    }

    /**
     * Converts the interfaces to string
     * 
     * @param i The interfaces as ClassOrInterfaceType objects
     */
    public void interfacesToString(NodeList<ClassOrInterfaceType> i) {

        i.forEach(x -> implementedInterfaces.add(NameWithType(x)));

    }

    /**
     * Returns the name of the interface/class with the type parameters if it exists in the form of a string
     * 
     * @param ci The interface/Class as a ClassOrInterfaceType object
     * @return The name of the interface with the types parameters if it exists
     */
    public String NameWithType(ClassOrInterfaceType ci) {
        NodeList<Type> types = ci.getTypeArguments().isPresent() ? ci.getTypeArguments().get() : new NodeList<>();
        String typesString = "";
        if (types.size() > 0) {
            typesString += "<";
            for (Type t : types) {
                typesString += t.asString() + ",";
            }
            typesString = typesString.substring(0, typesString.length() - 1);
            typesString += ">";
        }
        String ciName = ci.getNameWithScope().trim() + typesString;

        return ciName;
    }

    /**
     * Converts the super classes to string
     * 
     * @param s The list of super classes as ClassOrInterfaceType objects
     */
    public void superClassesToString(NodeList<ClassOrInterfaceType> s) {

        s.forEach(x -> superClasses.add(NameWithType(x)));

    }

    /**
     * finds the parent class/interface/method of the class if it exists
     * 
     * @param decl - the class declaration
     * @return - the parent class/interface/method if it exists
     */
    private ParentEntity<?> findParent(ClassOrInterfaceDeclaration decl) {

        ParentEntity<ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(decl);
        ParentEntity<MethodDeclaration> parentMethod = findParentMethod(decl);

        // findParentClassInterface(decl) and findParentMethod(decl) may both return a
        // non-null value so we need to check if the class is nested/inner or local

        if (parentIC == null && parentMethod == null) // if the class is not inside a class/interface or method - i.e.
                                                      // it is a top level class
            return null;
        if (parentIC == null && parentMethod != null) // if the method is the parent of the class - i.e. class is a
                                                      // local class
            return parentMethod;
        if (parentIC != null && parentMethod == null) // if the class/interface is the parent of the class - i.e. class
                                                      // is a nested/inner class
            return parentIC;

        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) { // if the class/interface is the
                                                                                     // ancestor of the method
            return parentMethod; // return the method
        }
        return parentIC;
    }

    /**
     * finds the parent/ancestor class/interface of the class if it exists
     * 
     * @param decl - the class declaration
     * @return - the parent class/interface of the class if it exists
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(ClassOrInterfaceDeclaration decl) {
        if (decl.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = decl.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if (parentIC.isInterface()) { // if the parent is an interface
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.INTERFACE);
            } else {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.CLASS);
            }

        }
        return null;
    }

    /**
     * finds the parent/ancestor method of the class if it exists
     * 
     * @param decl - the class declaration
     * @return - the parent method of the class if it exists
     */
    private ParentEntity<MethodDeclaration> findParentMethod(ClassOrInterfaceDeclaration decl) {
        if (decl.findAncestor(MethodDeclaration.class).isPresent()) {
            MethodDeclaration parentMethod = decl.findAncestor(MethodDeclaration.class).get();
            if (parentMethod != null) {
                return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
            }

        }
        return null;
    }

    /**
     * Gets the name of the class
     * 
     * @return The name of the class
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the class
     * 
     * @param name The name of the class
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type parameters of the class
     * 
     * @return The type parameters of the class as a list of strings
     */
    public List<String> getTypeParams() {
        return typeParams;
    }

    /**
     * Sets the type parameters of the class
     * 
     * @param typeParams The type parameters of the class as a list of strings
     */
    public void setTypeParams(List<String> typeParams) {
        this.typeParams = typeParams;
    }

    /**
     * Gets the implemented interfaces of the class
     * 
     * @return The implemented interfaces of the class as a list of strings
     */
    public List<String> getimplementedInterfaces() {
        return implementedInterfaces;
    }

    /**
     * Sets the implemented interfaces of the class
     * 
     * @param implementedInterfaces The implemented interfaces of the class
     */
    public void setimplementedInterfaces(List<String> implementedInterfaces) {
        this.implementedInterfaces = implementedInterfaces;
    }

    /**
     * Gets the super classes of the class
     * 
     * @return The super classes of the class as a list of strings
     */
    public List<String> getSuperClasses() {
        return superClasses;
    }

    /**
     * Sets the super classes of the class
     * 
     * @param superClasses The super classes of the class as a list of strings
     */
    public void setSuperClasses(List<String> superClasses) {
        this.superClasses = superClasses;
    }

    /**
     * Gets the starting line of the class
     * 
     * @return The starting line of the class
     */
    public int getLineMin() {
        return lineMin;
    }

    /**
     * Sets the starting line of the class
     * 
     * @param lineMin The starting line of the class
     */
    public void setLineMin(int lineMin) {
        this.lineMin = lineMin;
    }

    /**
     * Gets the ending line of the class
     * 
     * @return The ending line of the class
     */
    public int getLineMax() {
        return lineMax;
    }

    /**
     * Sets the ending line of the class
     * 
     * @param lineMax The ending line of the class
     */
    public void setLineMax(int lineMax) {
        this.lineMax = lineMax;
    }

    /**
     * Gets the methods of the class
     * 
     * @return The methods of the class as a list of Method objects
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * Sets the methods of the class
     * 
     * @param methods The methods of the class as a list of Method objects
     */
    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    /**
     * Using MethodCollection, Finds and stores the methods that belong to this class 
     * It is responsible for (where appropriate) linking the methods to their parent class
     * @param mds The MethodCollection object
     */
    public void findMethods(MethodCollection mds) {
        for (Method md : mds.getMethods()) {
            ParentEntity<?> methodParent = md.getParent();
            if (methodParent != null && methodParent.getEntityType() == EntityType.CLASS) { // if the method is inside a
                                                                                            // class
                if (methodParent.getDeclaration() == declaration) { // if this class is the parent of the method
                    methods.add(md);
                }

            }
        }
    }

    /**
     * Using VariableCollection, Find and stores the variables that belong to this class.
     * It is responsible for (where appropriate) linking variables to their parent class
     * @param vars The VariableCollection object
     */
    public void findVariables(VariableCollection vars) {
        for (Variable v : vars.getVariables()) {
            ParentEntity<?> varParent = v.getParent();
            if (varParent != null && varParent.getEntityType() == EntityType.CLASS) { // if the variable is inside a
                                                                                      // class
                if (varParent.getDeclaration() == declaration) { // if this class is the parent of the variable
                    storedVarCalls.add(v);
                }

            }
        }
    }

    /**
     * USing LambdaCollection, Finds and stores the lambdas that belong to this class
     * It is responsible for (where appropriate) linking lambdas to their parent class
     * @param ls The LambdaCollection object
     */
    public void findLambdas(LambdaCollection ls) {
        for (Lambda l : ls.getLambdas()) {
            ParentEntity<?> lambdaParent = l.getParent();
            if (lambdaParent != null && lambdaParent.getEntityType() == EntityType.CLASS) { // if the lambda is inside a
                                                                                            // class
                if (lambdaParent.getDeclaration() == declaration) { // if this class is the parent of the lambda
                    lambdas.add(l);
                }

            }
        }
    }

    /**
     * Using ClassCollection, Finds and stores the classes that belong to this class 
     * It is responsible  for  (where appropriate) linking nested/inner classes to their parent class.
     * @param cls The ClassCollection object
     */
    public void findClasses(ClassCollection cls) {
        for (Class cl : cls.getClasses()) {
            ParentEntity<?> classParent = cl.getParent();
            if (classParent != null && classParent.getEntityType() == EntityType.CLASS && // if the class (cl) is inside
                                                                                          // a class
                    (cl.getClassCategory() == ClassInterfaceCategory.INNER
                            || cl.getClassCategory() == ClassInterfaceCategory.STATIC_NESTED)) { // if the class (cl) is
                                                                                                 // an inner or static
                                                                                                 // nested class
                if (classParent.getDeclaration() == declaration) { // if this class is the parent of the class (cl)
                    classes.add(cl);
                }
            }
        }
    }

    /**
     * Using InterfaceCollection, Finds and stores  the interfaces that belong to this class 
     * It is responsible for linking the interfaces to their parent class.
     * @param intfs The InterfaceCollection object
     */
    public void findInterfaces(InterfaceCollection intfs) {
        for (Interface intf : intfs.getInterfaces()) {
            ParentEntity<?> interfaceParent = intf.getParent();
            if (interfaceParent != null && intf.getInterfaceCategory() == ClassInterfaceCategory.NESTED &&
                    interfaceParent.getEntityType() == EntityType.CLASS) { // if the interface (intf) is inside a class
                if (interfaceParent.getDeclaration() == declaration) { // if this class is the parent of the interface
                                                                       // (intf)
                    interfaces.add(intf);
                }

            }
        }
    }

    /**
     *  Using MethodReferenceCollection, Finds and stores the method references that belong to this class.
     * It is responsible for (where appropriate) linking the method references to their parent class.
     * @param refs The MethodReferenceCollection object
     */
    public void findReferences(MethodReferenceCollection refs) {
        for (MethodReference ref : refs.getMethodReferences()) {
            ParentEntity<?> referenceParent = ref.getParent();
            if (referenceParent != null && referenceParent.getEntityType() == EntityType.CLASS) { // if the method
                                                                                                  // reference is inside
                                                                                                  // a class
                if (referenceParent.getDeclaration() == declaration) { // if this class is the parent of the method
                                                                       // reference
                    references.add(ref);
                }

            }
        }
    }

    /**
     * Gets the implemented interfaces of the class
     * 
     * @return The implemented interfaces of the class as a list of strings
     */
    public List<String> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    /**
     *  Gets the classess of the class
     * @return The classes of the class as a list of Class objects
     */
    public List<Class> getClasses() {
        return classes;
    }

    /**
     *   Sets the classes of the class
     * @param classes The classes of the class as a list of Class objects
     */
    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    /**
     *  Gets the interfaces of the class
     * @return The interfaces of the class as a list of Interface objects
     */
    public List<Interface> getInterfaces() {
        return interfaces;
    }

    /**
     *  Sets the interfaces of the class
     * @param interfaces
     */
    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * Gets the method references of the class
     * @return  The method references of the class as a list of MethodReference objects
     */
    public List<MethodReference> getReferences() {
        return references;
    }

    /**
     *  Sets the method references of the class
     * @param references The method references of the class as a list of MethodReference objects
     */
    public void setReferences(List<MethodReference> references) {
        this.references = references;
    }

    /**
     *  Gets the parent entity of the class
     * @return The parent entity of the class
     */
    public ParentEntity<?> getParent() {
        return parent;
    }

    /**
     *  Sets the parent entity of the class
     * @param parent The parent entity of the class
     */
    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

    /**
     *  Gets the lambdas of the class
     * @return The lambdas of the class as a list of Lambda objects
     */
    public List<Lambda> getLambdas() {
        return lambdas;
    }

    /** 
     *  Sets the lambdas of the class
     * @param lambdas The lambdas of the class as a list of Lambda objects
     */
    public void setLambdas(List<Lambda> lambdas) {
        this.lambdas = lambdas;
    }

    /**
     *  Gets the declaration of the class
     * @return The declaration of the class
     */
    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    /**
     *  Sets the declaration of the class
     * @param declaration The declaration of the class
     */
    public void setDeclaration(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
    }

    /**
     *  Gets the stored variable calls of the class
     * @return The stored variable calls of the class as a list of Variable objects
     */
    public List<Variable> getStoredVarCalls() {
        return storedVarCalls;
    }

    /**
     *  Sets the stored variable calls of the class
     * @param storedVarCalls The stored variable calls of the class as a list of Variable objects
     */
    public void setStoredVarCalls(List<Variable> storedVarCalls) {
        this.storedVarCalls = storedVarCalls;
    }

    /**
     *  Gets the access  modifier of the class
     * @return The access modifier of the class
     */
    public AccessModifierType getAccessModifer() {
        return accessModifer;
    }

    /**
     *  Sets the access modifier of the class
     * @param accessModifer The access modifier of the class
     */
    public void setAccessModifer(AccessModifierType accessModifer) {
        this.accessModifer = accessModifer;
    }

    /**
     *  Gets the category of the class
     * @return The category of the class as a ClassInterfaceCategory object
     */
    public ClassInterfaceCategory getClassCategory() {
        return classCategory;
    }

    /**
     *   Sets the category of the class
     * @param classCategory The category of the class as a ClassInterfaceCategory object
     */
    public void setClassCategory(ClassInterfaceCategory classCategory) {
        this.classCategory = classCategory;
    }

    /**
     *  Gets the non-access modifiers of the class
     * @return The non-access modifiers of the class as a list of NonAccessModifierType objects
     */
    public List<NonAccessModifierType> getNonAccessModifer() {
        return nonAccessModifer;
    }

    /**
     *  Sets the non-access modifiers of the class
     * @param nonAccessModifer The non-access modifiers of the class as a list of NonAccessModifierType objects
     */
    public void setNonAccessModifer(List<NonAccessModifierType> nonAccessModifer) {
        this.nonAccessModifer = nonAccessModifer;
    }

    /**
     * Return the string representation of the class
     */
    @Override
    public String toString() {
        return "Class [name=" + name + ", classCategory=" + classCategory + ", accessModifer=" + accessModifer
                + ", nonAccessModifer=" + nonAccessModifer + ", lineMin=" + lineMin + ", lineMax=" + lineMax
                + ", methods=" + methods + ", typeParams=" + typeParams + ", implementedInterfaces="
                + implementedInterfaces + ", superClasses=" + superClasses + ", classes=" + classes + ", interfaces="
                + interfaces + ", references=" + references + ", lambdas=" + lambdas + ", parent=" + parent
                + ", declaration=" + declaration + ", storedVarCalls=" + storedVarCalls + ", javaDoc=" + javaDoc + "]";
    }
}
