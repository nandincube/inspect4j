package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.type.Type;


public class MethodReference {
    private String expressionAsString;
    private Expression containingEntity;
    private String identifier;
    private NodeList<Type> argumentTypes;

    public MethodReference(MethodReferenceExpr methodRef){
        this.expressionAsString = methodRef.toString();
        this.containingEntity = methodRef.getScope();
        this.identifier = methodRef.getIdentifier();
        if (methodRef.getTypeArguments().isPresent()) {
            this.argumentTypes = methodRef.getTypeArguments().get();
        }else{
            this.argumentTypes = null;
        }
        

    }

    public String getExpressionAsString() {
        return expressionAsString;
    }

    public void setExpressionAsString(String expressionAsString) {
        this.expressionAsString = expressionAsString;
    }

    public Expression getContainingEntity() {
        return containingEntity;
    }

    public void setContainingEntity(Expression containingEntity) {
        this.containingEntity = containingEntity;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public NodeList<Type> getArgumentTypes() {
        return argumentTypes;
    }

    public void setArgumentTypes(NodeList<Type> argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    @Override
    public String toString() {
        return "MethodReference [expressionAsString=" + expressionAsString + ", containingEntity=" + containingEntity
                + ", identifier=" + identifier + ", argumentTypes=" + argumentTypes + "]";
    }

    
    
}
