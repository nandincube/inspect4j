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

/**
 * This class is used to create and store the Abstract Syntax Tree (AST) representation of a given file.
 * This class is also used to extract metadata from the AST which will get encapsulated in the FileInfo object, that will be written to a JSON file.
 */
public class AST {
    private CompilationUnit fullTree; // The full AST of the file
    private ClassCollection classCollection; // Collection of classes extracted from the tree
    private MethodCollection methodCollection; // Collection of methods extracted from the tree
    private InterfaceCollection interfaceCollection; // Collection of interfaces extracted from the tree
    private MethodReferenceCollection referenceCollection; // Collection of method references extracted from the tree
    private LambdaCollection lambdaCollection; // Collection of lambdas extracted from the tree
    private VariableCollection variableCollection; // Collection of variables extracted from the tree
    private DependencyCollection dependencyCollection; // Collection of dependencies extracted from the tree
    private MainInfo main; // Information about the main method
    private String path; // The path of the file
    private FileInfo fileInfo; // Information about the file


    /**
     *  Constructor for the AST class.
     * @param path - The path of the file
     * @param repoPath - The path of the repository
     */
    public AST(String path, String repoPath) {
        this.path = path; 
        this.fullTree = parseFile(path);
        if(fullTree == null) {
            return;
        }
        this.classCollection = new ClassCollection(fullTree);
        this.methodCollection = new MethodCollection(fullTree);
        this.interfaceCollection = new InterfaceCollection(fullTree);
        this.referenceCollection = new MethodReferenceCollection(fullTree);
        this.lambdaCollection = new LambdaCollection(fullTree);
        this.variableCollection = new VariableCollection(fullTree);
        this.dependencyCollection = new DependencyCollection(fullTree, path, repoPath);
        this.main = null;
    }

    /**
     *  This method is used to parse a file into an AST.
     * @param path - The path of the file
     * @return - The AST of the file
     */
    private CompilationUnit parseFile(String path) {
        try {
            Path file = Paths.get(path);

            if (Files.exists(file)) {
                return StaticJavaParser.parse(Files.newInputStream(Paths.get(path))); //parses the file into an AST
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
     *  This method is used to find the main method in the AST.
     * @return - Information about the main method
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
                    String scope = expr.asMethodCallExpr().getScope().isPresent()
                            ? expr.asMethodCallExpr().getScope().get().toString() + "."
                            : "";
                    mainMdCall = scope+ expr.asMethodCallExpr().getNameAsString();
                    break;
                }
            }

            return new MainInfo(true, mainMdCall);
        }
    }

    /**
     * This method is used to extract metadata from the AST.
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
        main = findMainMethod();
    }

    /**
     * This method is used to add the members to the methods in the collection.
     */
    private void addMethodMembers() {
        methodCollection.addVariables(variableCollection);
        methodCollection.addLambdas(lambdaCollection);
        methodCollection.addClasses(classCollection);
        methodCollection.addInterfaces(interfaceCollection);
        methodCollection.addReferences(referenceCollection);
    }

    /**
     * This method is used to add the members to the classes in the collection.
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
     * The method is used to add the members to the interfaces in the collection.
     */
    private void addInterfaceMembers() {
        interfaceCollection.addMethods(methodCollection);
        interfaceCollection.addInterfaces(interfaceCollection);
        interfaceCollection.addClasses(classCollection);
    }

    /**
     * This method is used to write the metadata to a JSON file.
     * @param path - The path of the file being analysed
     * @param directory - The directory to write the JSON file to
     */
    public void writeToJson(String path, String directory) {
        fileInfo = new FileInfo(path, classCollection, interfaceCollection, main, dependencyCollection);
        OutputWriter json = new OutputWriter(fileInfo);
        json.write(directory);
    }

    /**
     *  The method used to get the full AST of the file.
     * @return - The full AST of the file
     */
    public CompilationUnit getFullTree() {
        return fullTree;
    }

    /**
     *  The method used to set the full AST of the file.
     * @param fullTree - The full AST of the file
     */
    public void setFullTree(CompilationUnit fullTree) {
        this.fullTree = fullTree;
    }

    /**
     *  The method used to get the class collection.
     * @return - The class collection
     */
    public ClassCollection getClassCollection() {
        return classCollection;
    }

    /**
     *  The method used to set the class collection.
     * @param classCollection   - The class collection
     */
    public void setClassCollection(ClassCollection classCollection) {
        this.classCollection = classCollection;
    }

    /**
     *  The method used to get the method collection.
     * @return - The method collection
     */
    public MethodCollection getMethodCollection() {
        return methodCollection;
    }

    /**
     *  The method used to set the method collection.
     * @param methodCollection - The method collection
     */
    public void setMethodCollection(MethodCollection methodCollection) {
        this.methodCollection = methodCollection;
    }

    /**
     *  The method used to get the interface collection.
     * @return - The interface collection
     */
    public InterfaceCollection getInterfaceCollection() {
        return interfaceCollection;
    }

    /**
     *  The method used to set the interface collection.
     * @param interfaceCollection - The interface collection
     */
    public void setInterfaceCollection(InterfaceCollection interfaceCollection) {
        this.interfaceCollection = interfaceCollection;
    }

    /**
     *  The method used to get the method reference collection.
     * @return - The method reference collection
     */
    public MethodReferenceCollection getReferenceCollection() {
        return referenceCollection;
    }

    /**
     *  The method used to set the method reference collection.
     * @param referenceCollection - The method reference collection
     */
    public void setReferenceCollection(MethodReferenceCollection referenceCollection) {
        this.referenceCollection = referenceCollection;
    }

    /**
     *  The method used to get the lambda collection.
     * @return - The lambda collection
     */
    public LambdaCollection getLambdaCollection() {
        return lambdaCollection;
    }

    /**
     *  The method used to set the lambda collection.
     * @param lambdaCollection - The lambda collection
     */
    public void setLambdaCollection(LambdaCollection lambdaCollection) {
        this.lambdaCollection = lambdaCollection;
    }

    /**
     *  The method used to get the variable collection.
     * @return - The variable collection
     */
    public VariableCollection getVariableCollection() {
        return variableCollection;
    }

    /**
     *  The method used to set the variable collection.
     * @param variableCollection    - The variable collection
     */
    public void setVariableCollection(VariableCollection variableCollection) {
        this.variableCollection = variableCollection;
    }

    /**
     *  The method used to get the main method information.
     * @return - The main method information
     */
    public MainInfo getMain() {
        return main;
    }

    /**
     *  The method used to set the main method information.
     * @param main - The main method information
     */
    public void setMain(MainInfo main) {
        this.main = main;
    }

    /**
     *  The method used to get the path of the file.
     * @return - The path of the file
     */
    public String getPath() {
        return path;
    }

    /**
     *  The method used to set the path of the file.
     * @param path - The path of the file
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     *  The method used to get the dependency collection.
     * @return - The dependency collection
     */
    public DependencyCollection getDependencyCollection() {
        return dependencyCollection;
    }

    /**
     * The method used to set the dependency collection.
     * @param dependencyCollection - The dependency collection
     */
    public void setDependencyCollection(DependencyCollection dependencyCollection) {
        this.dependencyCollection = dependencyCollection;
    }
    
    /**
     * The method used to get the file information.
     * @return - The file information
     */
    public FileInfo getFileInfo() {
        return fileInfo;
    }

    /**
     * The method used to set the file information.
     * @param fileInfo - The file information
     */
    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    /**
     * The method used to get the full AST of the file.
     * @return - The full AST of the file
     */
    public CompilationUnit getAst() {
        return fullTree;
    }

}