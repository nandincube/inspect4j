package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

/**
 * 
 */
public class Interface {
    private String name;
    private List<Method> methods;
    private List<Interface> interfaces;
    private String javaDoc;
    private ParentEntity<?> parent;
    private List<String> typeParams;
    private List<String> extendedInterfaces;
    private ClassOrInterfaceDeclaration declaration;
    private int lineMin;
    private int lineMax;
    private ClassInterfaceCategory interfaceCategory;
   // private NonAccessModifierType nonAccessModifer; - all interfaces are implicitly abstract
   private AccessModifierType accessModifer;


    // private List<String> implementedInterfaces;
    // private List<String> superClasses;
    // private List<Class> classes;
    // private List<Interface> interfaces;
    // private List<MethodReference> references;
    // private List<Lambda> lambdas;
    // private List<Variable> storedVarCalls;

    /**
     * 
     * @param interfaceDecl
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

        extractInterfaceCategory();
        extractAccessModifier();

        typeParamsToString(interfaceDecl.getTypeParameters());
        extendedInterfacesToString(interfaceDecl.getExtendedTypes());
    }

    /**
     * 
     */
    private void extractAccessModifier() {
        
        System.out.println("ACCESS SPECIFIER FOR INTERFACE: "+ name +" - " + declaration.getAccessSpecifier());
        if(declaration.getAccessSpecifier() == AccessSpecifier.PUBLIC){
            accessModifer = AccessModifierType.PUBLIC;
        }else{
            accessModifer = AccessModifierType.DEFAULT;
        }
    }


    /**
     * 
     */
    private void extractInterfaceCategory() {
        if (declaration.isNestedType()) {
            interfaceCategory = ClassInterfaceCategory.NESTED;
        }else {
            interfaceCategory = ClassInterfaceCategory.STANDARD;
        }

    }

        /**
     * 
     * @param intfs
     */
    public void findInterfaces(InterfaceCollection intfs) {
        for (Interface intf : intfs.getInterfaces()) {
            ParentEntity<?> interfaceParent = intf.getParent();
            if (interfaceParent != null && intf.getInterfaceCategory() == ClassInterfaceCategory.NESTED
                && interfaceParent.getEntityType() == EntityType.INTERFACE) {
                if (interfaceParent.getDeclaration() == declaration) {
                    interfaces.add(intf);
                }

            }
        }
    }

    /**
     * 
     * @param intf
     * @return
     */
    private String getJavaDoc(ClassOrInterfaceDeclaration intf){
        if (intf.getJavadoc().isPresent()){
            return intf.getJavadocComment().get().getContent().strip();   
        }
        return null;
    }

    /**
     * 
     * @param expr
     * @return
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(ClassOrInterfaceDeclaration expr) {
        if (expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = expr.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if(parentIC.isInterface()){
                return new ParentEntity<ClassOrInterfaceDeclaration>( parentIC, EntityType.INTERFACE);
            }else{
                return new ParentEntity<ClassOrInterfaceDeclaration>( parentIC, EntityType.CLASS);
            }
        }
        return null;
    }

    /**
     * 
     * @param types
     */
    private void typeParamsToString(NodeList<TypeParameter> types){
        types.forEach(x -> typeParams.add(x.getNameAsString().trim()));
    }

    /**
     * 
     * @param interfaces
     */
    private void extendedInterfacesToString(NodeList<ClassOrInterfaceType> interfaces){
        interfaces.forEach(x -> extendedInterfaces.add(x.getNameAsString().trim()));
    }


    /**
     * 
     * @param mds
     */
    public void findMethods(MethodCollection mds){
        for (Method md : mds.getMethods()) {
            ParentEntity<?> methodParent = md.getParent();
            if (methodParent != null &&  methodParent.getEntityType() == EntityType.INTERFACE) {
                if(methodParent.getDeclaration() == declaration){
                    methods.add(md);
                }
                
            }
        }
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
    public List<String> getExtendedInterfaces() {
        return extendedInterfaces;
    }

    /**
     * 
     * @param extendedInterfaces
     */
    public void setExtendedInterfaces(List<String> extendedInterfaces) {
        this.extendedInterfaces = extendedInterfaces;
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
     */
    @Override
    public String toString() {
        return "Interface [name=" + name + ", extendedInterfaces=" + extendedInterfaces + ", typeParams=" + typeParams
                + ", methods=" + methods+ "]";
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

    public ClassInterfaceCategory getInterfaceCategory() {
        return interfaceCategory;
    }
    
    public void setInterfaceCategory(ClassInterfaceCategory interfaceCategory) {
        this.interfaceCategory = interfaceCategory;
    }
    
    public AccessModifierType getAccessModifer() {
        return accessModifer;
    }
    
    public void setAccessModifer(AccessModifierType accessModifer) {
        this.accessModifer = accessModifer;
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }
}
