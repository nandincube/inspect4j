package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

public class Interface {
    private String name;
    private NodeList<ClassOrInterfaceType> extendedInterfaces;
    private NodeList<TypeParameter> typeParams;


    public Interface(String name, NodeList<ClassOrInterfaceType> extendedInterfaces, NodeList<TypeParameter> typeParams) {
        this.name = name;
        this.extendedInterfaces = extendedInterfaces;
        this.typeParams = typeParams;
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
        return "Interface [name=" + name + ", extendedInterfaces=" + extendedInterfaces + ", typeParams=" + typeParams + "]";
    }

    
}
