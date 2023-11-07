package uk.ac.st_andrews.inspect4j;


import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

public class Class {
    private String name;
    private NodeList<TypeParameter> typeParams;
    private NodeList<ClassOrInterfaceType> implementedInterfaces;
    private NodeList<ClassOrInterfaceType> superClasses;
    private boolean isInnerClass;
    private boolean isLocalClass;
    private int lineMin;
    private int lineMax;

 

    public Class(String name, NodeList<TypeParameter> typeParams, NodeList<ClassOrInterfaceType> implementedInterfaces, NodeList<ClassOrInterfaceType> superClasses, boolean isInnerClass,
     boolean isLocalClass, int lineMin, int lineMax ) {
        this.name = name;
        this.typeParams = typeParams;
        this.implementedInterfaces = implementedInterfaces;
        this.superClasses = superClasses;
        this.isInnerClass = isInnerClass;
        this.isLocalClass =  isLocalClass;
        this.lineMin = lineMin;
        this.lineMax = lineMax;
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

    public NodeList<ClassOrInterfaceType> getimplementedInterfaces() {
        return implementedInterfaces;
    }

    public void setimplementedInterfaces(NodeList<ClassOrInterfaceType> implementedInterfaces) {
        this.implementedInterfaces = implementedInterfaces;
    }

    public NodeList<ClassOrInterfaceType> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(NodeList<ClassOrInterfaceType> superClasses) {
        this.superClasses = superClasses;
    }

    public boolean isInnerClass() {
        return isInnerClass;
    }

    public void setInnerClass(boolean isInnerClass) {
        this.isInnerClass = isInnerClass;
    }

    public boolean isLocalClass() {
        return isLocalClass;
    }

    public void setLocalClass(boolean isLocalClass) {
        this.isLocalClass = isLocalClass;
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

    @Override
    public String toString() {
        return "Class [name=" + name + ", typeParams=" + typeParams + ", implementedInterfaces=" + implementedInterfaces
                + ", superClasses=" + superClasses + ", isInnerClass=" + isInnerClass + ", isLocalClass=" + isLocalClass
                + ", lineMin=" + lineMin + ", lineMax=" + lineMax + "]\n";
    }

}
