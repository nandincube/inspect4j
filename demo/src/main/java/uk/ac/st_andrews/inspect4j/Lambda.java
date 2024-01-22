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
 * 
 */
public class Lambda {
    private String bodyAsString;
    private HashMap<String, String> params;
    private HashSet<String> returnStmts;
    private ParentEntity<?> parent;
    private int lineMin;
    private int lineMax;
    private LambdaExpr declaration;
    private List<Class> classes;
    private List<MethodReference> references;
    private List<Lambda> lambdas;
    private List<Variable> storedVarCalls;

    /**
     * 
     * @param lambdaDecl
     * @param returnStmts
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
     * 
     * @return
     */
    public String getBodyAsString() {
        return bodyAsString;
    }

    /**
     * 
     * @param md
     */
    private void extractParameterInformation(LambdaExpr md) {
        if (md.getParameters() != null) {
            for (Parameter param : md.getParameters()) {
                params.put(param.getNameAsString(), param.getTypeAsString());
            }
        }
    }

    /**
     * 
     * @param expressionAsString
     */
    public void setExpressionAsString(String expressionAsString) {
        this.bodyAsString = expressionAsString;
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

    /**
     * 
     * @param cls
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
     * 
     * @param lbds
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
     * 
     * @param refs
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
     * 
     * @param vars
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
     * 
     * @param expr
     * @return
     */
    private ParentEntity<?> findParent(LambdaExpr expr) {

        ParentEntity<ClassOrInterfaceDeclaration> parentIC = findParentClassInterface(expr);
        ParentEntity<MethodDeclaration> parentMethod = findParentMethod(expr);

        if (parentIC == null && parentMethod == null)
            return null;
        if (parentIC == null && parentMethod != null)
            return parentMethod;
        if (parentIC != null && parentMethod == null)
            return parentIC;
        if (parentIC.getDeclaration().isAncestorOf(parentMethod.getDeclaration())) {
            return parentMethod;
        }
        return parentIC;
    }

    /**
     * 
     * @param expr
     * @return
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
     * 
     * @param expr
     * @return
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
     * 
     */
    @Override
    public String toString() {
        return "Lambda [expressionAsString=" + bodyAsString + ", params=" + params + ", returnStmts="
                + returnStmts + ", parent=" + parent + ", lineMin=" + lineMin + ", lineMax=" + lineMax + "]";
    }

    /**
     * 
     * @return
     */
    public HashSet<String> getReturnStmts() {
        return returnStmts;
    }

    /**
     * 
     * @param returnStmts
     */
    public void setReturnStmts(HashSet<String> returnStmts) {
        this.returnStmts = returnStmts;
    }

    /**
     * 
     * @return
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * 
     * @param params
     */
    public void setParams(HashMap<String, String> params) {
        this.params = params;
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
}
