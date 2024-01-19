package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
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
    private List<Class> classes;
    private List<Interface> interfaces;
    private List<MethodReference> references;
    private List<Lambda> lambdas;
    private ParentEntity<?> parent;
    private ClassOrInterfaceDeclaration declaration;
    private List<Variable> storedVarCalls;
    private String javaDoc;
    
    public Class(ClassOrInterfaceDeclaration classDecl) {
        NodeList<TypeParameter> typeParams = classDecl.getTypeParameters();
        NodeList<ClassOrInterfaceType> implementedInterfaces = classDecl.getImplementedTypes();
        NodeList<ClassOrInterfaceType> superClasses = classDecl.getExtendedTypes();

        this.name = classDecl.getNameAsString();
        this.isInnerClass = classDecl.isInnerClass();    //isInnerClass only picks up on non-static nested classes
        this.isLocalClass = classDecl.isLocalClassDeclaration();
        this.lineMin = classDecl.getBegin().get().line;
        this.lineMax = classDecl.getEnd().get().line;
        this.declaration = classDecl;
     
        this.typeParams = new ArrayList<String>();
        this.implementedInterfaces = new ArrayList<String>();
        this.superClasses = new ArrayList<String>();
    
        this.javaDoc = getJavaDoc(classDecl);
        this.parent = findParent(classDecl);

        this.methods = new ArrayList<Method>();
        this.classes = new ArrayList<Class>();
        this.interfaces = new ArrayList<Interface>();
        this.references = new ArrayList<MethodReference>();
        this.lambdas = new ArrayList<Lambda>();

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


    private ParentEntity<?> findParent(ClassOrInterfaceDeclaration decl) {

        ParentEntity<ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(decl);
        ParentEntity<MethodDeclaration> parentMethod = findParentMethod(decl);

        if(parentIC == null && parentMethod == null) return null;
        if(parentIC == null && parentMethod != null) return parentMethod;
        if(parentIC != null && parentMethod == null) return parentIC;
        
        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) {
            return parentMethod;
        }
        return parentIC;
    }

    private ParentEntity< ClassOrInterfaceDeclaration> findParentClassInterface( ClassOrInterfaceDeclaration decl) {
        if (decl.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = decl.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if(parentIC.isInterface()){
                return new ParentEntity<ClassOrInterfaceDeclaration>( parentIC, EntityType.INTERFACE);
            }else{
                return new ParentEntity<ClassOrInterfaceDeclaration>( parentIC, EntityType.CLASS);
            }
     
        }
        return null;
    }



    private ParentEntity<MethodDeclaration> findParentMethod(ClassOrInterfaceDeclaration decl){
        if(decl.findAncestor(MethodDeclaration.class).isPresent()){
            MethodDeclaration parentMethod = decl.findAncestor(MethodDeclaration.class).get();  
            if(parentMethod != null){
                return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
            }
           
        }
        return null;
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

    public void findMethods(MethodCollection mds){
        for (Method md : mds.getMethods()) {
            ParentEntity<?> methodParent = md.getParent();
            if (methodParent != null &&  methodParent.getEntityType() == EntityType.CLASS) {
                if(methodParent.getDeclaration() == declaration){
                    methods.add(md);
                }
                
            }
        }
    }

    public void findVariables(VariableCollection vars){
        for (Variable v : vars.getVariables()) {
            ParentEntity<?> varParent = v.getParent();
            if (varParent != null &&  varParent.getEntityType() == EntityType.CLASS) {
                if(varParent.getDeclaration() == declaration){
                    storedVarCalls.add(v);
                }
                
            }
        }
    }

    public void findLambdas(LambdaCollection ls){
        for (Lambda l : ls.getLambdas()) {
            ParentEntity<?> lambdaParent = l.getParent();
            if (lambdaParent != null &&  lambdaParent.getEntityType() == EntityType.CLASS) {
                if(lambdaParent.getDeclaration() == declaration){
                    lambdas.add(l);
                }
                
            }
        }
    }

    public void findClasses(ClassCollection cls){
        for (Class cl : cls.getClasses()) {
            ParentEntity<?> classParent = cl.getParent();
            if (classParent != null && 
                classParent.getEntityType() == EntityType.CLASS && cl.isInnerClass()) {
                    if(classParent.getDeclaration() == declaration){
                        classes.add(cl);
                    }
            }
        }
    }

    public void findInterfaces(InterfaceCollection intfs){
        for (Interface intf: intfs.getInterfaces()) {
            ParentEntity<?> interfaceParent = intf.getParent();
            if (interfaceParent != null &&  interfaceParent.getEntityType() == EntityType.CLASS) {
                if(interfaceParent.getDeclaration() == declaration){
                    interfaces.add(intf);
                }
                
            }
        }
    }


    public void findReferences(MethodReferenceCollection refs){
        for (MethodReference ref : refs.getMethodReferences()) {
            ParentEntity<?> referenceParent = ref.getParent();
            if (referenceParent != null &&  referenceParent.getEntityType() == EntityType.CLASS) {
                if(referenceParent.getDeclaration() == declaration){
                    references.add(ref);
                }
                
            }
        }
    }



    public Class returnInstance(){
        return this;
    }

    

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }

    public List<MethodReference> getReferences() {
        return references;
    }

    public void setReferences(List<MethodReference> references) {
        this.references = references;
    }

    public ParentEntity<?> getParent() {
        return parent;
    }

    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

  

    public List<Lambda> getLambdas() {
        return lambdas;
    }

    public void setLambdas(List<Lambda> lambdas) {
        this.lambdas = lambdas;
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
    }

    public List<Variable> getStoredVarCalls() {
        return storedVarCalls;
    }

    public void setStoredVarCalls(List<Variable> storedVarCalls) {
        this.storedVarCalls = storedVarCalls;
    }

    @Override
    public String toString() {
        return "Class [name=" + name + ", isInnerClass=" + isInnerClass + ", isLocalClass=" + isLocalClass
                + ", lineMin=" + lineMin + ", lineMax=" + lineMax + ", methods=" + methods + ", typeParams="
                + typeParams + ", implementedInterfaces=" + implementedInterfaces + ", superClasses=" + superClasses
                + ", classes=" + classes + ", interfaces=" + interfaces + ", references=" + references + ", parent="
                + parent + ", declaration=" + declaration + ", javaDoc=" + javaDoc + "]";
    }

}
