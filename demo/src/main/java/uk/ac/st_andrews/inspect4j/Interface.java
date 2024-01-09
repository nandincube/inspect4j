package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.google.gson.annotations.SerializedName;

public class Interface {
    private  String name;
    private transient NodeList<ClassOrInterfaceType> extendedInterfaces;
    private transient NodeList<TypeParameter> typeParams;
    private List<Method> methods;
    private String javaDoc;

    public Interface(ClassOrInterfaceDeclaration interfaceDecl) {
        this.name = interfaceDecl.getNameAsString();
        this.extendedInterfaces = interfaceDecl.getExtendedTypes();
        this.typeParams = interfaceDecl.getTypeParameters();
        this.methods = new ArrayList<Method>();
        this.javaDoc = getJavaDoc(interfaceDecl);
    }

    private String getJavaDoc(ClassOrInterfaceDeclaration cl){
        if (cl.getJavadoc().isPresent()){
            return cl.getJavadocComment().get().getContent().strip();   
        }
        return null;
    }
    
    public void findMethods(MethodCollection mdCol){
        for(Method md: mdCol.getMethods()){
            if(md.getParentInterface() != null){
                if( md.getParentInterface() == this){
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

    public NodeList<ClassOrInterfaceType> getExtendedInterfaces() {
        return extendedInterfaces;
    }

    public void setExtendedInterfaces(NodeList<ClassOrInterfaceType> extendedInterfaces) {
        this.extendedInterfaces = extendedInterfaces;
    }

    public NodeList<TypeParameter> getTypeParams() {
        return typeParams;
    }

    public void setTypeParams(NodeList<TypeParameter> typeParams) {
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

  

    
}
