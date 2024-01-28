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
    private ClassCollection classCollection;
    private MethodCollection methodCollection;
    private InterfaceCollection interfaceCollection;
    private MethodReferenceCollection referenceCollection;
    private LambdaCollection lambdaCollection;
    private VariableCollection variableCollection;
    private DependencyCollection dependencyCollection;
    private MainInfo main;
    private String path;

    /**
     * 
     * @param path
     */
    public AST(String path) {
        this.path = path;
        this.fullTree = parseFile(path);
        this.classCollection = new ClassCollection(fullTree);
        this.methodCollection = new MethodCollection(fullTree);
        this.interfaceCollection = new InterfaceCollection(fullTree);
        this.referenceCollection = new MethodReferenceCollection(fullTree);
        this.lambdaCollection = new LambdaCollection(fullTree);
        this.variableCollection = new VariableCollection(fullTree);
        this.dependencyCollection = new DependencyCollection(fullTree, path);
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
        Method mainMd = methodCollection.getMethods()
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
        variableCollection.extractVariablesFromAST();
        lambdaCollection.extractLambdasFromAST();
        methodCollection.extractMethodsFromAST();
        classCollection.extractClassesFromAST();
        interfaceCollection.extractInterfacesFromAST();
        referenceCollection.extractReferencesFromAST();
        dependencyCollection.extractDependenciesFromAST();
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
        methodCollection.addVariables(variableCollection);
        methodCollection.addLambdas(lambdaCollection);
        methodCollection.addClasses(classCollection);
        methodCollection.addInterfaces(interfaceCollection);
        methodCollection.addReferences(referenceCollection);
    }

    /**
     * 
     */
    private void addClassMembers() {
        classCollection.addVariables(variableCollection);
        classCollection.addLambdas(lambdaCollection);
        classCollection.addClasses(classCollection);
        classCollection.addInterfaces(interfaceCollection);
        classCollection.addReferences(referenceCollection);
        classCollection.addMethods(methodCollection);
    }

    /**
     * 
     */
    private void addInterfaceMembers() {
        interfaceCollection.addMethods(methodCollection);
        interfaceCollection.addInterfaces(interfaceCollection);
        interfaceCollection.addClasses(classCollection);
    }

    /**
     * 
     */
    public void printMetadata() {
        System.out.println("Extracting classes...\n");
        classCollection.printMetadata();
        System.out.println("Extracting interfaces...\n");
        interfaceCollection.printMetadata();
        System.out.println("Extracting Methods... \n");
        methodCollection.printMetadata();
        System.out.println("Extracting Variables...\n");
        variableCollection.printMetadata();
        System.out.println("Extracting Lambdas...\n");
        lambdaCollection.printMetadata();
        System.out.println("Extracting Method References...\n");
        referenceCollection.printMetadata();
        System.out.println("Extracting Dependencies References...\n");
        dependencyCollection.printMetadata();


    }

    /**
     * 
     * @param path
     * @param directory
     */
    public void writeToJson(String path, String directory) {
        FileInfo fileInfo = new FileInfo(path, classCollection, interfaceCollection, main, dependencyCollection);
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
    public ClassCollection getClassCollection() {
        return classCollection;
    }

    /**
     * 
     * @param classCollection
     */
    public void setClassCollection(ClassCollection classCollection) {
        this.classCollection = classCollection;
    }

    /**
     * 
     * @return
     */
    public MethodCollection getMethodCollection() {
        return methodCollection;
    }

    /**
     * 
     * @param methodCollection
     */
    public void setMethodCollection(MethodCollection methodCollection) {
        this.methodCollection = methodCollection;
    }

    /**
     * 
     * @return
     */
    public InterfaceCollection getInterfaceCollection() {
        return interfaceCollection;
    }

    /**
     * 
     * @param interfaceCollection
     */
    public void setInterfaceCollection(InterfaceCollection interfaceCollection) {
        this.interfaceCollection = interfaceCollection;
    }

    /**
     * 
     * @return
     */
    public MethodReferenceCollection getReferenceCollection() {
        return referenceCollection;
    }

    /**
     * 
     * @param referenceCollection
     */
    public void setReferenceCollection(MethodReferenceCollection referenceCollection) {
        this.referenceCollection = referenceCollection;
    }

    /**
     * 
     * @return
     */
    public LambdaCollection getLambdaCollection() {
        return lambdaCollection;
    }

    /**
     * 
     * @param lambdaCollection
     */
    public void setLambdaCollection(LambdaCollection lambdaCollection) {
        this.lambdaCollection = lambdaCollection;
    }

    /**
     * 
     * @return
     */
    public VariableCollection getVariableCollection() {
        return variableCollection;
    }

    /**
     * 
     * @param variableCollection
     */
    public void setVariableCollection(VariableCollection variableCollection) {
        this.variableCollection = variableCollection;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DependencyCollection getDependencyCollection() {
        return dependencyCollection;
    }

    public void setDependencyCollection(DependencyCollection dependencyCollection) {
        this.dependencyCollection = dependencyCollection;
    }

}