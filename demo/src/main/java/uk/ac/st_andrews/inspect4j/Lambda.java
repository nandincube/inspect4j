package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;

/**
 *  This class represents a Lambda in the AST
 */
public class Lambda {
    private String bodyAsString; // body of the lambda as a string
    private HashMap<String, String> params; // parameters of the lambda - key is the name of the parameter, value is the type of the parameter
    private HashSet<String> returnStmts; // return statements in the lambda
    private ParentEntity<?> parent; // parent entity of the lambda
    private int lineMin; // start line of the lambda
    private int lineMax; // end line of the lambda
    private LambdaExpr declaration; // the lambda declaration
    private List<Class> classes; // list of classes in the lambda
    private List<MethodReference> references; // list of method references in the lambda
    private List<Lambda> lambdas; // list of lambdas in the lambda
    private List<Variable> storedVarCalls; // list of assignment variables/stored variable calls in the lambda

    /**
     *  Constructor
     * @param lambdaDecl - the lambda declaration
     * @param returnStmts - the return statements in the lambda
     */
    public Lambda(LambdaExpr lambdaDecl, HashSet<String> returnStmts) {

        this.bodyAsString = lambdaDecl.getBody().toString();
        this.params = new HashMap<String, String>();
        this.lambdas = new ArrayList<Lambda>();
        this.classes = new ArrayList<Class>();
        this.storedVarCalls = new ArrayList<Variable>();
        this.declaration = lambdaDecl;
        extractParameterInformation(lambdaDecl);
        this.returnStmts = returnStmts;
        this.lineMin = lambdaDecl.getBegin().get().line;
        this.lineMax = lambdaDecl.getEnd().get().line;
        this.parent = findParent(lambdaDecl);
    }

    /**
     *  Method to get the body of the lambda as a string
     * @return String - body of the lambda as a string
     */
    public String getBodyAsString() {
        return bodyAsString;
    }

    /**
     *  Extracts the parameter information from the lambda declaration
     * @param md - the lambda declaration
     */
    private void extractParameterInformation(LambdaExpr md) {
        if (md.getParameters() != null) {
            for (Parameter param : md.getParameters()) {
                params.put(param.getNameAsString(), param.getTypeAsString()); // key is the name of the parameter, value is the type of the parameter
            }
        }
    }

    /**
     *  Sets the body of the lambda as a string
     * @param expressionAsString - body of the lambda as a string
     */
    public void setExpressionAsString(String expressionAsString) {
        this.bodyAsString = expressionAsString;
    }

    /**
     *  Gets the start line of the lambda
     * @return int - start line of the lambda
     */
    public int getLineMin() {
        return lineMin;
    }

    /**
     *  Sets the start lineof the lambda
     * @param lineMin - start line of the lambda
     */
    public void setLineMin(int lineMin) {
        this.lineMin = lineMin;
    }


    /**
     *  Gets the end line of the lambda
     * @return int - end line of the lambda
     */
    public int getLineMax() {
        return lineMax;
    }

    /**
     *  Sets the end line of the lambda
     * @param lineMax - end line of the lambda
     */
    public void setLineMax(int lineMax) {
        this.lineMax = lineMax;
    }

    /**
     * Using ClassCollection, finds and stores the classes that belong to this lambda
     * @param cls - ClassCollection object representing the classes in a file
     */
    public void findClasses(ClassCollection cls) {
        for (Class cl : cls.getClasses()) {
            ParentEntity<?> classParent = cl.getParent();
            if (classParent != null && classParent.getEntityType() == EntityType.LAMBDA) {
                if (classParent.getDeclaration() == declaration) {
                    classes.add(cl);
                }
            }
        }
    }

    /**
     *  Using LambdaCollection, finds and stores the lambdas that belong to this lambda
     * @param lbds - LambdaCollection object representing the lambdas in a file
     */
    public void findLambdas(LambdaCollection lbds) {
        for (Lambda lm : lbds.getLambdas()) {
            ParentEntity<?> classParent = lm.getParent();
            if (classParent != null && classParent.getEntityType() == EntityType.LAMBDA) {
                if (classParent.getDeclaration() == declaration) {
                    lambdas.add(lm);
                }
            }
        }
    }

    /**
     *  Using MethodReferenceCollection, finds and stores the method references that belong to this lambda
     * @param refs - MethodReferenceCollection object representing the method references in a file
     */
    public void findReferences(MethodReferenceCollection refs) {
        for (MethodReference ref : refs.getMethodReferences()) {
            ParentEntity<?> classParent = ref.getParent();
            if (classParent != null && classParent.getEntityType() == EntityType.LAMBDA) {
                if (classParent.getDeclaration() == declaration) {
                    references.add(ref);
                }
            }
        }
    }

    /**
     * Using VariableCollection, finds and stores the assigment variables that belong to this lambda
     * @param vars - VariableCollection object representing the variables in a file
     */
    public void findVariables(VariableCollection vars) {
        for (Variable var : vars.getVariables()) {
            ParentEntity<?> classParent = var.getParent();
            if (classParent != null && classParent.getEntityType() == EntityType.LAMBDA) {
                if (classParent.getDeclaration() == declaration) {
                    storedVarCalls.add(var);
                }
            }
        }
    }

     /**
     * finds the parent class/interface/method of the lambda if it exists
     * 
     * @param expr - the lambda declaration
     * @return - the parent class/interface/method if it exists
     */
    private ParentEntity<?> findParent(LambdaExpr expr) {

        ParentEntity<ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(expr);
        ParentEntity<MethodDeclaration> parentMethod = findParentMethod(expr);

        if (parentIC == null && parentMethod == null)
            return null;
        if (parentIC == null && parentMethod != null) // if the lambda is in a method
            return parentMethod;
        if (parentIC != null && parentMethod == null) // if the lambda is in a class/interface (outside a method)
            return parentIC;
        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) { // if the class/interface is the ancestor of the method
            return parentMethod;
        }
        return parentIC;
    }

       /**
     * finds the parent/ancestor class/interface of the lambda if it exists
     * 
     * @param expr - the lambda declaration
     * @return - the parent class/interface of the class if it exists
     */
    private ParentEntity<ClassOrInterfaceDeclaration> findParentClassInterface(LambdaExpr expr) {
        if (expr.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            ClassOrInterfaceDeclaration parentIC = expr.findAncestor(ClassOrInterfaceDeclaration.class).get();
            if (parentIC.isInterface()) {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.INTERFACE);
            } else {
                return new ParentEntity<ClassOrInterfaceDeclaration>(parentIC, EntityType.CLASS);
            }
        }
        return null;

    }

      /**
     * finds the parent/ancestor method of the class if it exists
     * 
     * @param expr - the lambda declaration
     * @return - the parent method of the class if it exists
     */
    private ParentEntity<MethodDeclaration> findParentMethod(LambdaExpr expr) {
        if (expr.findAncestor(MethodDeclaration.class).isPresent()) {
            MethodDeclaration parentMethod = expr.findAncestor(MethodDeclaration.class).get();
            if (parentMethod != null) {
                return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
            }
        }
        return null;
    }

    /**
     *  Gets the string representation of the lambda
     */
    @Override
    public String toString() {
        return "Lambda [expressionAsString=" + bodyAsString + ", params=" + params + ", returnStmts="
                + returnStmts + ", parent=" + parent + ", lineMin=" + lineMin + ", lineMax=" + lineMax + "]";
    }

    /** 
     *  Gets the return statements in the lambda
     * @return  HashSet<String> - return statements in the lambda
     */
    public HashSet<String> getReturnStmts() {
        return returnStmts;
    }

    /**
     *  Sets the return statements in the lambda
     * @param returnStmts - return statements in the lambda
     */
    public void setReturnStmts(HashSet<String> returnStmts) {
        this.returnStmts = returnStmts;
    }

    /**
     *  Gets the parameters of the lambda
     * @return  HashMap<String, String> - parameters of the lambda
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     *  Sets the parameters of the lambda
     * @param params - parameters of the lambda
     */
    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    /**
     *  Gets the parent entity of the lambda
     * @return ParentEntity<?> - parent entity of the lambda
     */
    public ParentEntity<?> getParent() {
        return parent;
    }

    /**
     *  Sets the parent entity of the lambda
     * @param parent - parent entity of the lambda
     */
    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }
}
