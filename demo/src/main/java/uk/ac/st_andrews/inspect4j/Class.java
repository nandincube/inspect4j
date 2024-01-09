package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.Node.DirectChildrenIterator;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

public class Class {
    private String name;
    private boolean isInnerClass;
    private boolean isLocalClass;
    private int lineMin;
    private int lineMax;
    private List<Method> methods;
    private List<String> typeParams;
    private List<String> implementedInterfaces;
    private List<String> superClasses;
    private transient Class outerClass;    
    private transient Method outerMethod;
    private transient String ancestor = null;
    private  List<Class> innerOrLocalChildrenClasses;
    private String javaDoc;
    
    public Class(ClassOrInterfaceDeclaration classDecl) {
        
        NodeList<TypeParameter> typeParams = classDecl.getTypeParameters();
        NodeList<ClassOrInterfaceType> implementedInterfaces = classDecl.getImplementedTypes();
        NodeList<ClassOrInterfaceType> superClasses = classDecl.getExtendedTypes();
        ancestor = findAncestorToDecl(classDecl);
        this.name = classDecl.getNameAsString();
        this.isInnerClass = classDecl.isInnerClass();    //isInnerClass only picks up on non-static nested classes
        this.isLocalClass = classDecl.isLocalClassDeclaration();
        this.lineMin = classDecl.getBegin().get().line;
        this.lineMax = classDecl.getEnd().get().line;
        this.methods = new ArrayList<Method>();
        this.typeParams = new ArrayList<String>();
        this.implementedInterfaces = new ArrayList<String>();
        this.superClasses = new ArrayList<String>();
        this.outerClass = null;
        this.innerOrLocalChildrenClasses = new ArrayList<Class>();
        this.javaDoc = getJavaDoc(classDecl);

        findMyMethods(classDecl);

        typesToString(typeParams);
        interfacesToString(implementedInterfaces);
        superClassesToString(superClasses);

    }

    public String getJavaDoc() {
        return javaDoc;
    }

    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }


    private String getJavaDoc(ClassOrInterfaceDeclaration cl){
        if (cl.getJavadoc().isPresent()){
            return cl.getJavadocComment().get().getContent().strip();   
        }
        return null;
    }


    public void typesToString(NodeList<TypeParameter> t){
        t.forEach(x -> typeParams.add(x.getNameAsString().trim()));
    }

    public void interfacesToString(NodeList<ClassOrInterfaceType> i){
      
        i.forEach(x -> implementedInterfaces.add(x.getNameAsString().trim()));
        
    }

    public void superClassesToString(NodeList<ClassOrInterfaceType> s){
        
        s.forEach(x -> superClasses.add(x.getNameAsString().trim()));
        
    }

    private String findAncestorToDecl(ClassOrInterfaceDeclaration classDecl){
         if(classDecl.findAncestor(MethodDeclaration.class).isPresent()){
                return classDecl.findAncestor(MethodDeclaration.class).get().getNameAsString();
        }
        if(classDecl.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()){
                return classDecl.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
        }
        
        return null;
    }


    public void findMethods(MethodCollection mdCol){
        for(Method md: mdCol.getMethods()){
            if(md.getParentClass() != null){
                if( md.getParentClass() == this){
                    methods.add(md);
                }
            }
        }
    }

    public void findMethods(ClassOrInterfaceDeclaration c){
        List<Node> md = c.stream(Node.TreeTraversal.DIRECT_CHILDREN).filter(x -> x instanceof MethodDeclaration).toList();
        md.forEach(x -> x.);
        System.out.println("METHODS for "+ name+" : " );


    }
    
    public void findOuterClassOrMethod(ClassCollection classCol, MethodCollection methodCol){

        if(this.isInnerClass == true || this.isLocalClass == true ){
            for(Method md: methodCol.getMethods()){
                if(ancestor.equals(md.getName())){
                    System.out.println("FOUND!!! "+ md.getName());
                    this.outerMethod = md;
                    return;
                }
            }
            for(Class cl: classCol.getClasses()){
                if(ancestor.equals(cl.getName())){
                    System.out.println("FOUND!!! "+ cl.getName());
                    this.outerClass = cl;
                }
            }

        }
    }

    public void findOuterClass(ClassCollection classCol){

        if(this.isInnerClass == true || this.isLocalClass == true ){
        
            for(Class cl: classCol.getClasses()){
                if(ancestor.equals(cl.getName())){
                    System.out.println("FOUND!!! "+ cl.getName());
                    this.outerClass = cl;
                }
            }

        }
    }

    public Class findInterOrLocalChildrenClasses(ClassCollection classCol){
         if(this.isInnerClass == false || this.isLocalClass == false ){
                for(Class cl: classCol.getClasses()){
                    if(cl.isInnerClass() == true || cl.isLocalClass() == true ){
                        if(cl.getOuterClass() == this){
                            innerOrLocalChildrenClasses.add(cl);
                        }
                    }
                }
        }
        return null;
    }


    public Method getOuterMethod() {
        return outerMethod;
    }

    public void setOuterMethod(Method outerMethod) {
        this.outerMethod = outerMethod;
    }

    public String getAncestor() {
        return ancestor;
    }

    public void setAncestor(String ancestor) {
        this.ancestor = ancestor;
    }

    public List<Class> getInnerOrLocalChildrenClasses() {
        return innerOrLocalChildrenClasses;
    }

    public void setInnerOrLocalChildrenClasses(List<Class> innerOrLocalChildrenClasses) {
        this.innerOrLocalChildrenClasses = innerOrLocalChildrenClasses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypeParams() {
        return typeParams;
    }

    public void setTypeParams(List<String> typeParams) {
        this.typeParams = typeParams;
    }

    public List<String> getimplementedInterfaces() {
        return implementedInterfaces;
    }

    public void setimplementedInterfaces(List<String> implementedInterfaces) {
        this.implementedInterfaces = implementedInterfaces;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(List<String> superClasses) {
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


    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public List<String> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    public void setImplementedInterfaces(List<String> implementedInterfaces) {
        this.implementedInterfaces = implementedInterfaces;
    }

    public Class getOuterClass() {
        return outerClass;
    }

    public void setOuterClass(Class outerClass) {
        this.outerClass = outerClass;
    }

    public Class returnInstance(){
        return this;
    }

    @Override
    public String toString() {
        String output =  "Class [name=" + name + ", isInnerClass=" + isInnerClass + ", isLocalClass=" + isLocalClass
                + ", lineMin=" + lineMin + ", lineMax=" + lineMax + ", methods=" + methods + ", typeParams="
                + typeParams + ", implementedInterfaces=" + implementedInterfaces + ", superClasses=" + superClasses;
                if(outerClass != null){
                    output = output + ", OuterClasses=" + outerClass.getName();
                }
                
               return output + "]";
    }

}
