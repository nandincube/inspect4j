package uk.ac.st_andrews.inspect4j;

public class Cli {

    private String path;
    private AST ast;

    public Cli(String path){
        this.path = path;
        this.ast = new AST(path);
        analyse();
       
    }

    public void analyse(){
        MethodCollection methods = new MethodCollection(ast.getFullTree());
        methods.getMetadata();
        ClassCollection classes = new ClassCollection(ast.getFullTree());
        classes.getMetadata();
        InterfaceCollection interfaces = new InterfaceCollection(ast.getFullTree());
        interfaces.getMetadata();
        VariableCollection variables = new VariableCollection(ast.getFullTree());
        variables.getMetadata();
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
