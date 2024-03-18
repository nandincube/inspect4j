package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodReferenceExpr;

/**
 * Class to represent a method reference in the AST.
 */
public class MethodReference {
    private String containingEntity; // scope of the method reference
    private String identifier; // the identifier of the method reference
    private List<String> argumentTypes; // the types of the arguments of the method reference
    private ParentEntity<?> parent; // the parent entity of the method reference

    /**
     * Constructor
     * 
     * @param methodRef - the method reference
     */
    public MethodReference(MethodReferenceExpr methodRef) {
        this.containingEntity = methodRef.getScope().toString();
        this.identifier = methodRef.getIdentifier();
        this.parent = findParent(methodRef);
        this.argumentTypes = new ArrayList<String>();
        addArgumentTypes(methodRef);
    }

    /**
     * Method to add the types of the arguments of the method reference to the list
     * of argument types.
     * 
     * @param methodRef
     */
    private void addArgumentTypes(MethodReferenceExpr methodRef) {
        if (methodRef.getTypeArguments().isPresent()) {
            methodRef.getTypeArguments().get().stream().forEach(x -> argumentTypes.add(x.asString()));
        }
    }

    /**
     * Method that gets the scope of the method reference.
     * 
     * @return
     */
    public String getContainingEntity() {
        return containingEntity;
    }

    /**
     * Method that sets the scope of the method reference.
     * 
     * @param containingEntity - the scope of the method reference
     */
    public void setContainingEntity(String containingEntity) {
        this.containingEntity = containingEntity;
    }

    /**
     * Method that gets the identifier of the method reference.
     * 
     * @return String - the identifier of the method reference
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Method that sets the identifier of the method reference.
     * 
     * @param identifier - the identifier of the method reference
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Method that gets the types of the arguments of the method reference.
     * 
     * @return List<String> - the types of the arguments of the method reference
     */
    public List<String> getArgumentTypes() {
        return argumentTypes;
    }

    /**
     * Method that sets the types of the arguments of the method reference.
     * 
     * @param argumentTypes - the types of the arguments of the method reference
     */
    public void setArgumentTypes(List<String> argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    /**
     * Method to find the parent entity (most recent/deepest class,interface or
     * method ancestor) of the method reference.
     * 
     * @param expr - the method reference
     * @return ParentEntity<?> - the parent entity of the method reference
     */
    private ParentEntity<?> findParent(MethodReferenceExpr expr) {

        ParentEntity<ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(expr);
        ParentEntity<MethodDeclaration> parentMethod = findParentMethod(expr);

        if (parentIC == null && parentMethod == null)
            return null;
        if (parentIC == null && parentMethod != null)
            return parentMethod;
        if (parentIC != null && parentMethod == null)
            return parentIC;

        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) { // if the class/interface is the
                                                                                     // ancestor of the method
            return parentMethod;
        }
        return parentIC;
    }

    /**
     * Method to find the parent class/interface of the method reference.
     * 
     * @param expr - the method reference
     * @return ParentEntity<ClassOrInterfaceDeclaration> - the parent
     *         class/interface of the method reference
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(MethodReferenceExpr expr) {
        if (expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) { // if the method reference is inside a
                                                                                // class/interface
            ClassOrInterfaceDeclaration parentIC = expr.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if (parentIC.isInterface()) { // if the parent is an interface
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.INTERFACE);
            } else {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.CLASS);
            }
        }
        return null;
    }

    /**
     * Method to find the parent method of the method reference.
     * 
     * @param expr - the method reference
     * @return ParentEntity<MethodDeclaration> - the parent method of the method
     *         reference
     */
    private ParentEntity<MethodDeclaration> findParentMethod(MethodReferenceExpr expr) {
        if (expr.findAncestor(MethodDeclaration.class).isPresent()) { // if the method reference is inside a method
            MethodDeclaration parentMethod = expr.findAncestor(MethodDeclaration.class).get();
            if (parentMethod != null) { // if the parent is a method
                return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
            }
        }
        return null;
    }

    /**
     * Method to get the string representation of the method reference.
     */
    @Override
    public String toString() {
        return "MethodReference [containingEntity=" + containingEntity + ", identifier=" + identifier
                + ", argumentTypes=" + argumentTypes + ", parent=" + parent + "]";
    }

    /**
     * Method to get the parent entity of the method reference.
     * 
     * @return ParentEntity<?> - the parent entity of the method reference
     */
    public ParentEntity<?> getParent() {
        return parent;
    }

    /**
     * Method to set the parent entity of the method reference.
     * 
     * @param parent - the parent entity of the method reference
     */
    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }

}
