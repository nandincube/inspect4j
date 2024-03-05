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
 * Class to represent a clas
 */
public class Class {
    private String name; // !
    private ClassInterfaceCategory classCategory;// !
    private AccessModifierType accessModifer; // !
    private List<NonAccessModifierType> nonAccessModifer; // !
    private int lineMin;// !
    private int lineMax;// !
    private List<Method> methods;// !
    private List<String> typeParams;// !
    private List<String> implementedInterfaces;// !
    private List<String> superClasses; // !
    private List<Class> classes; // !
    private List<Interface> interfaces; // !
    private List<MethodReference> references;
    private List<Lambda> lambdas;
    private ParentEntity<?> parent;// !
    private ClassOrInterfaceDeclaration declaration; // !
    private List<Variable> storedVarCalls; // !
    private String javaDoc; // !

    /**
     * 
     * @param classDecl
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
                switch (mod.getKeyword()) {
                    case STATIC:
                        nonAccessModifer.add(NonAccessModifierType.STATIC);
                       // nonAccessModifer = NonAccessModifierType.STATIC;
                        break;
                    case ABSTRACT:
                    nonAccessModifer.add(NonAccessModifierType.ABSTRACT);
                       // nonAccessModifer = NonAccessModifierType.ABSTRACT;
                        break;
                    case FINAL:
                    nonAccessModifer.add(NonAccessModifierType.FINAL);
                       // nonAccessModifer = NonAccessModifierType.FINAL;
                        break;
                    default:
                    nonAccessModifer.add(NonAccessModifierType.NONE);
                      //  nonAccessModifer = NonAccessModifierType.NONE;
                        break;
                }
            }

        
        if(nonAccessModifer.size()  > 1 && nonAccessModifer.contains(NonAccessModifierType.NONE)){
            nonAccessModifer.remove(NonAccessModifierType.NONE);
        }else if(nonAccessModifer.size()==0){
            nonAccessModifer.add(NonAccessModifierType.NONE);
        }
    }

    /**
     * 
     */
    private void extractClassCategory() {
        if (declaration.isInnerClass()) {
            classCategory = ClassInterfaceCategory.INNER;
            return;
        }else if (declaration.isLocalClassDeclaration()) {
            classCategory = ClassInterfaceCategory.LOCAL;
            return;
        }else if (declaration.isNestedType() && declaration.isStatic()) {
            classCategory = ClassInterfaceCategory.STATIC_NESTED;
            return;
        }else {
            classCategory = ClassInterfaceCategory.STANDARD;
        }

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
     * @param cl
     * @return
     */
    private String getJavaDoc(ClassOrInterfaceDeclaration cl) {
        if (cl.getJavadoc().isPresent()) {
            String doc = cl.getJavadocComment().get().getContent().strip().toString();
            return doc.replaceFirst("^[*]+", "").strip();
        }
        return null;
    }

    /**
     * 
     * @param t
     */
    public void typesToString(NodeList<TypeParameter> t) {
        t.forEach(x -> typeParams.add(x.getNameAsString().trim()));
    }

    /**
     * 
     * @param i
     */
    public void interfacesToString(NodeList<ClassOrInterfaceType> i) {

        i.forEach(x -> implementedInterfaces.add(NameWithType(x)));

    }

    /**
     *  Returns the name of the interface/class with the type parameters if it exists
     * @param ci     The interface/Class
     * @return    The name of the interface with the types parameters if it exists
     */
    public String NameWithType(ClassOrInterfaceType ci ){
        NodeList<Type> types = ci.getTypeArguments().isPresent() ? ci.getTypeArguments().get() : new NodeList<>();
        String typesString = "";
        if(types.size() > 0){
            typesString += "<";
                for(Type t : types){
                  typesString += t.asString() + ",";
                }
                typesString = typesString.substring(0, typesString.length() - 1);
                typesString += ">";
        }
        String ciName =   ci.getNameWithScope().trim()+ typesString;

        return ciName;
    }

    /**
     * 
     * @param s
     */
    public void superClassesToString(NodeList<ClassOrInterfaceType> s) {

        s.forEach(x -> superClasses.add(NameWithType(x)));

    }

    /**
     * 
     * @param decl
     * @return
     */
    private ParentEntity<?> findParent(ClassOrInterfaceDeclaration decl) {

        ParentEntity<ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(decl);
        ParentEntity<MethodDeclaration> parentMethod = findParentMethod(decl);

        if (parentIC == null && parentMethod == null)
            return null;
        if (parentIC == null && parentMethod != null)
            return parentMethod;
        if (parentIC != null && parentMethod == null)
            return parentIC;

        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) {
            return parentMethod;
        }
        return parentIC;
    }

    /**
     * 
     * @param decl
     * @return
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(ClassOrInterfaceDeclaration decl) {
        if (decl.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = decl.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if (parentIC.isInterface()) {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.INTERFACE);
            } else {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.CLASS);
            }

        }
        return null;
    }

    /**
     * 
     * @param decl
     * @return
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
    public List<String> getTypeParams() {
        return typeParams;
    }

    /**
     * 
     * @param typeParams
     */
    public void setTypeParams(List<String> typeParams) {
        this.typeParams = typeParams;
    }

    /**
     * 
     * @return
     */
    public List<String> getimplementedInterfaces() {
        return implementedInterfaces;
    }

    /**
     * 
     * @param implementedInterfaces
     */
    public void setimplementedInterfaces(List<String> implementedInterfaces) {
        this.implementedInterfaces = implementedInterfaces;
    }

    /**
     * 
     * @return
     */
    public List<String> getSuperClasses() {
        return superClasses;
    }

    /**
     * 
     * @param superClasses
     */
    public void setSuperClasses(List<String> superClasses) {
        this.superClasses = superClasses;
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
     * @return
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * 
     * @param methods
     */
    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    /**
     * 
     * @return
     */
    public List<String> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    /**
     * 
     * @param implementedInterfaces
     */
    public void setImplementedInterfaces(List<String> implementedInterfaces) {
        this.implementedInterfaces = implementedInterfaces;
    }

    /**
     * 
     * @param mds
     */
    public void findMethods(MethodCollection mds) {
        for (Method md : mds.getMethods()) {
            ParentEntity<?> methodParent = md.getParent();
            if (methodParent != null && methodParent.getEntityType() == EntityType.CLASS) {
                if (methodParent.getDeclaration() == declaration) {
                    methods.add(md);
                }

            }
        }
    }

    /**
     * 
     * @param vars
     */
    public void findVariables(VariableCollection vars) {
        for (Variable v : vars.getVariables()) {
            ParentEntity<?> varParent = v.getParent();
            if (varParent != null && varParent.getEntityType() == EntityType.CLASS) {
                if (varParent.getDeclaration() == declaration) {
                    storedVarCalls.add(v);
                }

            }
        }
    }

    /**
     * 
     * @param ls
     */
    public void findLambdas(LambdaCollection ls) {
        for (Lambda l : ls.getLambdas()) {
            ParentEntity<?> lambdaParent = l.getParent();
            if (lambdaParent != null && lambdaParent.getEntityType() == EntityType.CLASS) {
                if (lambdaParent.getDeclaration() == declaration) {
                    lambdas.add(l);
                }

            }
        }
    }

    /**
     * 
     * @param cls
     */
    public void findClasses(ClassCollection cls) {
        for (Class cl : cls.getClasses()) {
            ParentEntity<?> classParent = cl.getParent();
            if (classParent != null && classParent.getEntityType() == EntityType.CLASS &&
             (cl.getClassCategory() == ClassInterfaceCategory.INNER ||  cl.getClassCategory() == ClassInterfaceCategory.STATIC_NESTED )) {
                if (classParent.getDeclaration() == declaration) {
                    classes.add(cl);
                }
            }
        }
    }

    /**
     * 
     * @param intfs
     */
    public void findInterfaces(InterfaceCollection intfs) {
        for (Interface intf : intfs.getInterfaces()) {
            ParentEntity<?> interfaceParent = intf.getParent();
            if (interfaceParent != null && intf.getInterfaceCategory() == ClassInterfaceCategory.NESTED &&
             interfaceParent.getEntityType() == EntityType.CLASS) {
                if (interfaceParent.getDeclaration() == declaration) {
                    interfaces.add(intf);
                }

            }
        }
    }

    /**
     * 
     * @param refs
     */
    public void findReferences(MethodReferenceCollection refs) {
        for (MethodReference ref : refs.getMethodReferences()) {
            ParentEntity<?> referenceParent = ref.getParent();
            if (referenceParent != null && referenceParent.getEntityType() == EntityType.CLASS) {
                if (referenceParent.getDeclaration() == declaration) {
                    references.add(ref);
                }

            }
        }
    }

    /**
     * 
     * @return
     */
    public Class returnInstance() {
        return this;
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
    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    /**
     * 
     * @param declaration
     */
    public void setDeclaration(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
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

    /**
     * 
     * @return
     */
    public ClassInterfaceCategory getClassCategory() {
        return classCategory;
    }

    /**
     * 
     * @param classCategory
     */
    public void setClassCategory(ClassInterfaceCategory classCategory) {
        this.classCategory = classCategory;
    }

    /**
     * 
     * @return
     */
    public List<NonAccessModifierType> getNonAccessModifer() {
        return nonAccessModifer;
    }

    /**
     * 
     * @param nonAccessModifer
     */
    public void setNonAccessModifer(List<NonAccessModifierType> nonAccessModifer) {
        this.nonAccessModifer = nonAccessModifer;
    }


    /**
     * 
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
