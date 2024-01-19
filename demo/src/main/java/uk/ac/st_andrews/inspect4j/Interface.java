package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

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

    private String getJavaDoc(ClassOrInterfaceDeclaration cl){
        if (cl.getJavadoc().isPresent()){
            return cl.getJavadocComment().get().getContent().strip();   
        }
        return null;
    }

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

    private void typeParamsToString(NodeList<TypeParameter> types){
        types.forEach(x -> typeParams.add(x.getNameAsString().trim()));
    }

    private void extendedInterfacesToString(NodeList<ClassOrInterfaceType> interfaces){
        interfaces.forEach(x -> extendedInterfaces.add(x.getNameAsString().trim()));
    }


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getExtendedInterfaces() {
        return extendedInterfaces;
    }

    public void setExtendedInterfaces(List<String> extendedInterfaces) {
        this.extendedInterfaces = extendedInterfaces;
    }

    public List<String> getTypeParams() {
        return typeParams;
    }

    public void setTypeParams(List<String> typeParams) {
        this.typeParams = typeParams;
    }

    @Override
    public String toString() {
        return "Interface [name=" + name + ", extendedInterfaces=" + extendedInterfaces + ", typeParams=" + typeParams
                + ", methods=" + methods+ "]";
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }


    public String getJavaDoc() {
        return javaDoc;
    }


    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    public ParentEntity<?> getParent() {
        return parent;
    }

    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
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

  

    
}
