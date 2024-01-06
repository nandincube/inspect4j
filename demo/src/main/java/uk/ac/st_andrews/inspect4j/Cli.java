package uk.ac.st_andrews.inspect4j;

public class Cli {

    private String path;
    private AST ast;
    private ClassCollection classes;
    private MethodCollection methods;
    private InterfaceCollection interfaces;
    private MethodReferenceCollection references;
    private LambdaCollection lambdas;
    private VariableCollection variables;

    public Cli(String path){
        this.path = path;
        this.ast = new AST(path);
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
        
        
        classes = new ClassCollection(ast.getFullTree());
        classes.extractMetadata();

        interfaces = new InterfaceCollection(ast.getFullTree());
        interfaces.extractMetadata();
        

        methods = new MethodCollection(ast.getFullTree(), classes, interfaces);
        methods.extractMetadata();
       

        variables = new VariableCollection(ast.getFullTree(), classes, interfaces, methods);
        variables.extractMetadata();
        methods.addVariables(variables);

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
        /*JSONWriterGson json = new JSONWriterGson(classes);
        json.write();*/
         JSONWriterGson json2 = new JSONWriterGson(interfaces);
        json2.writeInterfaces();
    }

    private void createClassHierarchy(){
        classes.addOuterClasses(classes);
        classes.addInnerOrLocal(classes);
        classes.addMethods(methods);
        classes.getClasses().removeIf(x -> x.isInnerClass() == true || x.isLocalClass() == true);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

     
    public AST getAst() {
        return ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }

}
