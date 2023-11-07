package uk.ac.st_andrews.inspect4j;

public class Variable {
    private String name;
    private Class varClass;
    private Method method;
    private String methodCalled; //stored variable call

    public Variable(String name, Class varClass,  Method method, String methodCalled) {
        this.name = name;
        this.varClass = varClass;
        this.method = method;
        this.methodCalled = methodCalled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getVarClass() {
        return varClass;
    }

    public void setVarClass(Class varClass) {
        this.varClass = varClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getMethodCalled() {
        return methodCalled;
    }

    public void setMethodCalled(String methodCalled) {
        this.methodCalled = methodCalled;
    }

    @Override
    public String toString() {
        return "Variable [name=" + name + ", varClass=" + varClass + ", method=" + method + ", methodCalled="
                + methodCalled + "]";
    }
    
}
