package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;

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

    public String getBodyAsString() {
        return bodyAsString;
    }

    private void extractParameterInformation(LambdaExpr md) {
        if (md.getParameters() != null) {
            for (Parameter param : md.getParameters()) {
                params.put(param.getNameAsString(), param.getTypeAsString());
            }
        }
    }

    public void setExpressionAsString(String expressionAsString) {
        this.bodyAsString = expressionAsString;
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

    private ParentEntity<MethodDeclaration> findParentMethod(LambdaExpr expr) {
        if (expr.findAncestor(MethodDeclaration.class).isPresent()) {
            MethodDeclaration parentMethod = expr.findAncestor(MethodDeclaration.class).get();
            if (parentMethod != null) {
                return new ParentEntity<MethodDeclaration>(parentMethod, EntityType.METHOD);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Lambda [expressionAsString=" + bodyAsString + ", params=" + params + ", returnStmts="
                + returnStmts + ", parent=" + parent + ", lineMin=" + lineMin + ", lineMax=" + lineMax + "]";
    }

    public HashSet<String> getReturnStmts() {
        return returnStmts;
    }

    public void setReturnStmts(HashSet<String> returnStmts) {
        this.returnStmts = returnStmts;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public ParentEntity<?> getParent() {
        return parent;
    }

    public void setParent(ParentEntity<?> parent) {
        this.parent = parent;
    }
}
