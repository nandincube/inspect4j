package uk.ac.st_andrews.inspect4j;

public class Cli {

    private AST ast;
    private ClassCollection classes;
    private MethodCollection methods;
    private InterfaceCollection interfaces;
    private MethodReferenceCollection references;
    private LambdaCollection lambdas;
    private VariableCollection variables;
    private String path;


    public Cli(String path){
        this.ast = new AST(path);
        this.path = path;
        this.classes = new ClassCollection(ast.getFullTree());
        this.interfaces = new InterfaceCollection(ast.getFullTree());
        this.methods = null;
        this.lambdas = null;
        this.variables = null;
        analyse();
       
    }
    /*public void analyse(){
        
        System.out.println("Classes: \n");
        ClassCollection classes = new ClassCollection(ast.getFullTree());
        classes.getMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Interfaces: \n");
        InterfaceCollection interfaces = new InterfaceCollection(ast.getFullTree());
        interfaces.getMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Methods: \n");
        MethodCollection methods = new MethodCollection(ast.getFullTree(), classes, interfaces);
        methods.getMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Stored Variables: \n");
        VariableCollection variables = new VariableCollection(ast.getFullTree(), classes, interfaces, methods);
        variables.getMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Lambdas: \n");
        LambdaCollection lambdas = new LambdaCollection(ast.getFullTree());
        lambdas.getMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Method References: \n");
        MethodReferenceCollection references = new MethodReferenceCollection(ast.getFullTree());
        references.getMetadata();
        System.out.println("--------------------------------------");
        JSONWriter json = new JSONWriter(classes);
        json.write();
    }*/


    public void analyse(){
       
        classes.extractMetadata();
        interfaces.extractMetadata();

        methods = new MethodCollection(ast.getFullTree(), classes, interfaces);
        methods.extractMetadata();
       

        variables = new VariableCollection(ast.getFullTree(), classes, interfaces, methods);
        variables.extractMetadata();
        createMethodHierarchy();

        lambdas = new LambdaCollection(ast.getFullTree());
        lambdas.extractMetadata();

        references = new MethodReferenceCollection(ast.getFullTree());
        references.extractMetadata();
       
        createClassHierarchy();
        interfaces.addMethods(methods);
        System.out.println("Classes: \n");
        classes.printMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Interfaces: \n");
        interfaces.printMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Methods: \n");
        methods.printMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Stored Variables: \n");
        variables.printMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Lambdas: \n");
        lambdas.printMetadata();
        System.out.println("--------------------------------------");
        System.out.println("Method References: \n");
        references.printMetadata();
        System.out.println("--------------------------------------");

        FileInfo fileInfo = new FileInfo(path, classes);

        JSONWriterGson json = new JSONWriterGson(fileInfo);
        
        json.write();
        /*JSONWriterGson json2 = new JSONWriterGson(interfaces);
        json2.writeInterfaces();*/
    }

    private void createClassHierarchy(){
        classes.addOuterClassesOrMethods(classes, methods);
        classes.addInnerOrLocal(classes);
        classes.addMethods(methods);
        classes.getClasses().removeIf(x -> x.isInnerClass() == true || x.isLocalClass() == true);
    }

    private void createMethodHierarchy(){
        methods.addVariables(variables);
    }

    private void createLambdaHierarchy(){
        methods.addVariables(variables);
    }
     
    public AST getAst() {
        return ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }


    public ClassCollection getClasses() {
        return classes;
    }


    public void setClasses(ClassCollection classes) {
        this.classes = classes;
    }


    public MethodCollection getMethods() {
        return methods;
    }


    public void setMethods(MethodCollection methods) {
        this.methods = methods;
    }


    public InterfaceCollection getInterfaces() {
        return interfaces;
    }


    public void setInterfaces(InterfaceCollection interfaces) {
        this.interfaces = interfaces;
    }


    public MethodReferenceCollection getReferences() {
        return references;
    }


    public void setReferences(MethodReferenceCollection references) {
        this.references = references;
    }


    public LambdaCollection getLambdas() {
        return lambdas;
    }


    public void setLambdas(LambdaCollection lambdas) {
        this.lambdas = lambdas;
    }


    public VariableCollection getVariables() {
        return variables;
    }


    public void setVariables(VariableCollection variables) {
        this.variables = variables;
    }
  

}
