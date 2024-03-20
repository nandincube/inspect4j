package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

/**
 *  This class is responsible for representing an interface in a file
 */
public class Interface {
    private String name; // name of the interface
    private ClassInterfaceCategory interfaceCategory; // category of the interface
    private AccessModifierType accessModifer;//  access modifier of the interface
    private int lineMin;// start line of the interface
    private int lineMax;// end line of the interface
    private List<Method> methods; // list of methods in the interface
    private List<Interface> interfaces;// list of interfaces in the interface
    private List<Class> classes;// list of classes in the interface
    private String javaDoc;// javadoc of the interface
    private ParentEntity<?> parent; // parent entity of the interface
    private List<String> typeParams; // type parameters of the interface
    private List<String> extendedInterfaces; // extended interfaces of the interface
    private ClassOrInterfaceDeclaration declaration; // declaration of the interface

    /**
     *  Constructor
     * @param interfaceDecl - the interface declaration
     */
    public Interface(ClassOrInterfaceDeclaration interfaceDecl) {
        this.name = interfaceDecl.getNameAsString();
        this.extendedInterfaces = new ArrayList<String>();
        this.typeParams = new ArrayList<String>();
        this.methods = new ArrayList<Method>();
        this.interfaces = new ArrayList<Interface>();
        this.javaDoc = getJavaDoc(interfaceDecl);
        this.parent = findParentClassInterface(interfaceDecl);
        this.declaration = interfaceDecl;
        this.lineMin = interfaceDecl.getBegin().get().line;
        this.lineMax = interfaceDecl.getEnd().get().line;
        this.classes = new ArrayList<Class>();

        extractInterfaceCategory();
        extractAccessModifier();

        typeParamsToString(interfaceDecl.getTypeParameters());
        extendedInterfacesToString(interfaceDecl.getExtendedTypes());
    }

    /**
     *  Method to extract the access modifier of the interface
     */
    private void extractAccessModifier() {
        if (declaration.getAccessSpecifier() == AccessSpecifier.PUBLIC) {
            accessModifer = AccessModifierType.PUBLIC;
        } else {
            accessModifer = AccessModifierType.DEFAULT;
        }
    }

    /**
     *  Method to extract the category of the interface
     */
    private void extractInterfaceCategory() {
        if (declaration.isNestedType()) {
            interfaceCategory = ClassInterfaceCategory.NESTED;
        } else {
            interfaceCategory = ClassInterfaceCategory.STANDARD;
        }

    }

    /**
     * Finds and stores the classes that belong to this interface from the ClassCollection
     * It is responsible  for  (where appropriate) linking nested/inner classes to their parent interface.
     * @param cls The ClassCollection object
     */
    public void findClasses(ClassCollection cls) {
        for (Class cl : cls.getClasses()) {
            ParentEntity<?> classParent = cl.getParent();
            if (classParent != null && classParent.getEntityType() == EntityType.INTERFACE &&
                    (cl.getClassCategory() == ClassInterfaceCategory.INNER)) {

                if (classParent.getDeclaration() == declaration) {
                    classes.add(cl);
                }
            }
        }
    }

    /**
     *  Finds and stores the interfaces that belong to this interface from the InterfaceCollection
     * It is responsible  for  (where appropriate) linking nested interfaces to their parent interface.
     * @param intfs The InterfaceCollection object
     */
    public void findInterfaces(InterfaceCollection intfs) {
        for (Interface intf : intfs.getInterfaces()) {
            ParentEntity<?> interfaceParent = intf.getParent();
            if (interfaceParent != null && intf.getInterfaceCategory() == ClassInterfaceCategory.NESTED
                    && interfaceParent.getEntityType() == EntityType.INTERFACE) { // if the interface is nested
                if (interfaceParent.getDeclaration() == declaration) { // if the interface is nested in this interface
                    interfaces.add(intf);
                }

            }
        }
    }

    /**
     * Gets the JavaDoc of the interface
     * @param intf The interface declaration as a ClassOrInterfaceDeclaration object
     * @return The JavaDoc of the interface
     */
    private String getJavaDoc(ClassOrInterfaceDeclaration intf) {
        if (intf.getJavadoc().isPresent()) {
            String doc = intf.getJavadocComment().get().getContent().strip();
            return doc.replaceFirst("^[*]+", "").strip();
        }
        return null;
    }

    /**
     * finds the parent class/interface of the interface if it exists
     * 
     * @param i - the interface as a ClassOrInterfaceDeclaration object
     * @return - the parent class/interface if it exists
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(ClassOrInterfaceDeclaration i) {
        if (i.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = i.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if (parentIC.isInterface()) {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.INTERFACE);
            } else {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.CLASS);
            }
        }
        return null;
    }

   
    /**
     * Converts the type parameters to string
     * 
     * @param types The type parameters as TypeParameter objects
     */
    private void typeParamsToString(NodeList<TypeParameter> types) {
        types.forEach(x -> typeParams.add(x.getNameAsString().trim()));
    }

    /**
     *  Converts the extended interfaces to string
     * @param interfaces The extended interfaces as ClassOrInterfaceType objects
     */
    private void extendedInterfacesToString(NodeList<ClassOrInterfaceType> interfaces) {
        interfaces.forEach(x -> extendedInterfaces.add(x.getNameAsString().trim()));
    }

    /**
     *  Using MethodCollection, Finds and stores the methods that belong to this interface
     * @param mds - The MethodCollection object
     */
    public void findMethods(MethodCollection mds) {
        for (Method md : mds.getMethods()) {
            ParentEntity<?> methodParent = md.getParent();
            if (methodParent != null && methodParent.getEntityType() == EntityType.INTERFACE) {
                if (methodParent.getDeclaration() == declaration) {
                    methods.add(md);
                }

            }
        }
    }

    /**
     *  Gets the name of the interface
     * @return The name of the interface
     */
    public String getName() {
        return name;
    }

    /**
     *  Sets the name of the interface
     * @param name The name of the interface
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *  Gets the extended interfaces of the interface
     * @return The extended interfaces of the interface
     */
    public List<String> getExtendedInterfaces() {
        return extendedInterfaces;
    }

    /**
     *  Sets the extended interfaces of the interface
     * @param extendedInterfaces The extended interfaces of the interface
     */
    public void setExtendedInterfaces(List<String> extendedInterfaces) {
        this.extendedInterfaces = extendedInterfaces;
    }

    /**
     *  Gets the type parameters of the interface
     * @return The type parameters of the interface
     */
    public List<String> getTypeParams() {
        return typeParams;
    }

    /** 
     *  Sets the type parameters of the interface
     * @param typeParams The type parameters of the interface
     */
    public void setTypeParams(List<String> typeParams) {
        this.typeParams = typeParams;
    }

    /**
     *  Gets the string representation of the interface
     */
    @Override
    public String toString() {
        return "Interface [name=" + name + ", extendedInterfaces=" + extendedInterfaces + ", typeParams=" + typeParams
                + ", methods=" + methods + "]";
    }

    /**
     *  Gets the methods of the interface
     * @return The methods of the interface as a List of Method objects
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     *  Sets the methods of the interface
     * @param methods The methods of the interface as  a List of Method objects
     */
    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    /**
     *  Gets the JavaDoc of the interface
     * @return The JavaDoc of the interface 
     */
    public String getJavaDoc() {
        return javaDoc;
    }

    /**
     *  Sets the JavaDoc of the interface
     * @param javaDoc The JavaDoc of the interface
     */
    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    /**
     *  Gets the parent entity of the interface
     * @return The parent entity of the interface
     */
    public ParentEntity<?> getParent() {
        return parent;
    }

    /**
     *  Sets the parent entity of the interface
     * @param parent The parent entity of the interface
     */
    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

    /**
     *  Gets the declaration of the interface
     * @return The declaration of the interface as a ClassOrInterfaceDeclaration object
     */
    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    /**
     *  Sets the declaration of the interface
     * @param declaration The declaration of the interface as a ClassOrInterfaceDeclaration object
     */
    public void setDeclaration(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
    }

    /**
     *  Gets the start line of the interface
     * @return The start line of the interface 
     */
    public int getLineMin() {
        return lineMin;
    }

    /**
     *  Sets the start line of the interface
     * @param lineMin The start line of the interface
     */
    public void setLineMin(int lineMin) {
        this.lineMin = lineMin;
    }

    /**
     *  Gets the end line of the interface
     * @return The end line of the interface
     */
    public int getLineMax() {
        return lineMax;
    }

    /**
     *  Sets the end line of the interface
     * @param lineMax The end line of the interface
     */
    public void setLineMax(int lineMax) {
        this.lineMax = lineMax;
    }
    /**
     *  Gets the category of the interface
     * @return The category of the interface
     */
    public ClassInterfaceCategory getInterfaceCategory() {
        return interfaceCategory;
    }

    /**
     *  Sets the category of the interface
     * @param interfaceCategory The category of the interface
     */
    public void setInterfaceCategory(ClassInterfaceCategory interfaceCategory) {
        this.interfaceCategory = interfaceCategory;
    }

    /**
     *  Gets the access modifier of the interface
     * @return The access modifier of the interface
     */
    public AccessModifierType getAccessModifer() {
        return accessModifer;
    }

    /**
     *  Sets the access modifier of the interface
     * @param accessModifer The access modifier of the interface
     */
    public void setAccessModifer(AccessModifierType accessModifer) {
        this.accessModifer = accessModifer;
    }

    /**
     *  Gets the interfaces in the interface
     * @return The interfaces in the interface
     */
    public List<Interface> getInterfaces() {
        return interfaces;
    }

    /**
     *  Sets the interfaces in the interface
     * @param interfaces The interfaces in the interface
     */
    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     *  Gets the classes in the interface
     * @return The classes in the interface as a List of Class objects
     */
    public List<Class> getClasses() {
        return classes;
    }

    /**
     *  Sets the classes in the interface
     * @param classes The classes in the interface as a List of Class objects
     */
    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }
}
