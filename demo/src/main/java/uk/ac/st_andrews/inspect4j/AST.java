package uk.ac.st_andrews.inspect4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;

public class AST {
    private CompilationUnit fullTree;
    private ClassCollection classes;
    private MethodCollection methods;
    private InterfaceCollection interfaces;
    private MethodReferenceCollection references;
    private LambdaCollection lambdas;
    private VariableCollection variables;
    private MainInfo main;

    public AST(String path) {
        this.fullTree = parseFile(path);
        this.classes = new ClassCollection(fullTree);
        this.methods = new MethodCollection(fullTree);
        this.interfaces = new InterfaceCollection(fullTree);
        this.references = new MethodReferenceCollection(fullTree);
        this.lambdas = new LambdaCollection(fullTree);
        this.variables = new VariableCollection(fullTree);
        this.main = null;
    }

    public AST(CompilationUnit cu, String path) {
        this.fullTree = cu;
        this.classes = new ClassCollection(fullTree);
        this.methods = new MethodCollection(fullTree);
        this.interfaces = new InterfaceCollection(fullTree);
        this.references = new MethodReferenceCollection(fullTree);
        this.lambdas = new LambdaCollection(fullTree);
        this.variables = new VariableCollection(fullTree);
    }

    private CompilationUnit parseFile(String path) {
        try {
            Path file = Paths.get(path);

            if (Files.exists(file)) {
                return StaticJavaParser.parse(Files.newInputStream(Paths.get(path)));
            }
        } catch (IOException e) {
            System.out.println("Error! Could not read file: " + e);
        } catch (InvalidPathException i) {
            System.out.println("Error! Could not convert path into path object!");
        } catch (ParseProblemException ppe) {
            System.out.println("Error! Could not parse file into AST!");
            ;
        }

        return null;
    }

    public MainInfo findMainMethod() {
        Method mainMd = methods.getMethods()
                .stream()
                .filter(x -> x.isMain() == true)
                .findAny()
                .orElse(null);

        if (mainMd == null) {
            return new MainInfo(false, null);
        } else {
            NodeList<Statement> stmts = mainMd.getDeclaration().getBody().get().getStatements();
            String mainMdCall = null;
            for (Statement stmt : stmts) {
                Expression expr = stmt.asExpressionStmt().getExpression();

                if (expr.isMethodCallExpr()) {
                    mainMdCall = expr.asMethodCallExpr().getNameAsString();
                    break;
                }
            }

            return new MainInfo(true, mainMdCall);
        }
    }

    public void extractMetadata() {
        variables.extractVariablesFromAST();
        lambdas.extractLambdasFromAST();
        methods.extractMethodsFromAST();
        classes.extractClassesFromAST();
        interfaces.extractInterfacesFromAST();
        references.extractReferencesFromAST();
        addMethodMembers();
        addClassMembers();
        addInterfaceMembers();
        //addLambdaMembers();
        main = findMainMethod();
    }

    private void addMethodMembers() {
        methods.addVariables(variables);
        methods.addLambdas(lambdas);
        methods.addClasses(classes);
        methods.addInterfaces(interfaces);
        methods.addReferences(references);
    }

    private void addClassMembers() {
        classes.addVariables(variables);
        classes.addLambdas(lambdas);
        classes.addClasses(classes);
        classes.addInterfaces(interfaces);
        classes.addReferences(references);
        classes.addMethods(methods);
    }

    // private void addLambdaMembers() {
    //    // classes.addVariables(variables);
    //     lambdas.addLambdas(lambdas);
    //     lambdas.addClasses(classes);
    //     //classes.addInterfaces(interfaces);
    //     //classes.addReferences(references);
    //     //classes.addMethods(methods);
    // }

    // private void addVariableMembers(){
    // variables.addVariables(variables);
    // variables.addLambdas(lambdas);
    // variables.addClasses(classes);
    // variables.addInterfaces(interfaces);
    // variables.addReferences(references);
    // variables.addMethods(methods);
    // }

    private void addInterfaceMembers() {
        interfaces.addMethods(methods);
    }

    public void printMetadata() {
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

    }

    public void writeToJson(String path, String directory) {
        FileInfo fileInfo = new FileInfo(path, classes, interfaces, main);
        JSONWriterGson json = new JSONWriterGson(fileInfo);
        json.write(directory);
    }

    public CompilationUnit getFullTree() {
        return fullTree;
    }

    public void setFullTree(CompilationUnit fullTree) {
        this.fullTree = fullTree;
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

    public MainInfo getMain() {
        return main;
    }

    public void setMain(MainInfo main) {
        this.main = main;
    }
}