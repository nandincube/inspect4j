package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

public class Class {
    private String name;
    private NodeList<TypeParameter> typeParams;
    private NodeList<ClassOrInterfaceType> implInterfaces;
    private NodeList<ClassOrInterfaceType> superClasses;


    public Class(String name, NodeList<TypeParameter> typeParams, NodeList<ClassOrInterfaceType> implInterfaces, NodeList<ClassOrInterfaceType> superClasses) {
        this.name = name;
        this.typeParams = typeParams;
        this.implInterfaces = implInterfaces;
        this.superClasses = superClasses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeList<TypeParameter> getTypeParams() {
        return typeParams;
    }

    public void setTypeParams(NodeList<TypeParameter> typeParams) {
        this.typeParams = typeParams;
    }

    public NodeList<ClassOrInterfaceType> getImplInterfaces() {
        return implInterfaces;
    }

    public void setImplInterfaces(NodeList<ClassOrInterfaceType> implInterfaces) {
        this.implInterfaces = implInterfaces;
    }

    public NodeList<ClassOrInterfaceType> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(NodeList<ClassOrInterfaceType> superClasses) {
        this.superClasses = superClasses;
    }

    @Override
    public String toString() {
        return "Class [name=" + name + ", typeParams=" + typeParams + ", implInterfaces=" + implInterfaces
                + ", superClasses=" + superClasses + "]";
    }

}
