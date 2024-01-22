package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

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
    private String javaDoc;
    private ParentEntity<?> parent;
    private List<String> typeParams;
    private List<String> extendedInterfaces;
    private ClassOrInterfaceDeclaration declaration;
    private int lineMin;
    private int lineMax;

    /**
     * 
     * @param interfaceDecl
     */
    public Interface(ClassOrInterfaceDeclaration interfaceDecl) {
        this.name = interfaceDecl.getNameAsString();
        this.extendedInterfaces = new ArrayList<String>();
        this.typeParams = new ArrayList<String>();
        this.methods = new ArrayList<Method>();
        this.javaDoc = getJavaDoc(interfaceDecl);
        this.parent = findParentClassInterface(interfaceDecl);
        this.declaration = interfaceDecl;
        this.lineMin = interfaceDecl.getBegin().get().line;
        this.lineMax = interfaceDecl.getEnd().get().line;

        typeParamsToString(interfaceDecl.getTypeParameters());
        extendedInterfacesToString(interfaceDecl.getExtendedTypes());
    }

    /**
     * 
     * @param cl
     * @return
     */
    private String getJavaDoc(ClassOrInterfaceDeclaration cl){
        if (cl.getJavadoc().isPresent()){
            return cl.getJavadocComment().get().getContent().strip();   
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
}
