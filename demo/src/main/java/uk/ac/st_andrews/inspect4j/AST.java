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

    /**
     * 
     * @param path
     */
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

    /**
     * 
     * @param path
     * @return
     */
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

    /**
     * 
     * @return
     */
    public MainInfo findMainMethod() {
        Method mainMd = methods.getMethods()
                .stream()
                .filter(x -> x.isMain() == true)
                .findAny()
                .orElse(null);

        if (mainMd == null) {
            return new MainInfo(false, null);
        } else {
            NodeList<Statement> stmts = mainMd.getDeclaration().asMethodDeclaration().getBody().get().getStatements();
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

    /**
     * 
     */
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

    /**
     * 
     */
    private void addMethodMembers() {
        methods.addVariables(variables);
        methods.addLambdas(lambdas);
        methods.addClasses(classes);
        methods.addInterfaces(interfaces);
        methods.addReferences(references);
    }

    /**
     * 
     */
    private void addClassMembers() {
        classes.addVariables(variables);
        classes.addLambdas(lambdas);
        classes.addClasses(classes);
        classes.addInterfaces(interfaces);
        classes.addReferences(references);
        classes.addMethods(methods);
    }

    /**
     * 
     */
    private void addInterfaceMembers() {
        interfaces.addMethods(methods);
    }

    /**
     * 
     */
    public void printMetadata() {
        System.out.println("Extracting Classes...\n");
        classes.printMetadata();
        System.out.println("Extracting Interfaces...\n");
        interfaces.printMetadata();
        System.out.println("Extracting Methods... \n");
        methods.printMetadata();
        System.out.println("Extracting Variables...\n");
        variables.printMetadata();
        System.out.println("Extracting Lambdas...\n");
        lambdas.printMetadata();
        System.out.println("Extracting Method References...\n");
        references.printMetadata();

    }

    /**
     * 
     * @param path
     * @param directory
     */
    public void writeToJson(String path, String directory) {
        FileInfo fileInfo = new FileInfo(path, classes, interfaces, main);
        JSONWriterGson json = new JSONWriterGson(fileInfo);
        json.write(directory);
    }

    /**
     * 
     * @return
     */
    public CompilationUnit getFullTree() {
        return fullTree;
    }

    /**
     * 
     * @param fullTree
     */
    public void setFullTree(CompilationUnit fullTree) {
        this.fullTree = fullTree;
    }

    /**
     * 
     * @return
     */
    public ClassCollection getClasses() {
        return classes;
    }

    /**
     * 
     * @param classes
     */
    public void setClasses(ClassCollection classes) {
        this.classes = classes;
    }

    /**
     * 
     * @return
     */
    public MethodCollection getMethods() {
        return methods;
    }

    /**
     * 
     * @param methods
     */
    public void setMethods(MethodCollection methods) {
        this.methods = methods;
    }

    /**
     * 
     * @return
     */
    public InterfaceCollection getInterfaces() {
        return interfaces;
    }

    /**
     * 
     * @param interfaces
     */
    public void setInterfaces(InterfaceCollection interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * 
     * @return
     */
    public MethodReferenceCollection getReferences() {
        return references;
    }

    /**
     * 
     * @param references
     */
    public void setReferences(MethodReferenceCollection references) {
        this.references = references;
    }

    /**
     * 
     * @return
     */
    public LambdaCollection getLambdas() {
        return lambdas;
    }

    /**
     * 
     * @param lambdas
     */
    public void setLambdas(LambdaCollection lambdas) {
        this.lambdas = lambdas;
    }

    /**
     * 
     * @return
     */
    public VariableCollection getVariables() {
        return variables;
    }

    /**
     * 
     * @param variables
     */
    public void setVariables(VariableCollection variables) {
        this.variables = variables;
    }

    /**
     * 
     * @return
     */
    public MainInfo getMain() {
        return main;
    }

    /**
     * 
     * @param main
     */
    public void setMain(MainInfo main) {
        this.main = main;
    }
}