package uk.ac.st_andrews.inspect4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * The class that collects the dependencies (i.e. imported entities) of a file
 */
public class DependencyCollection {
    private ArrayList<Dependency> dependencies; // list of file dependencies
    private CompilationUnit ast; // the AST of the file
    private String path; // the path of the file
    private String repositoryPath; // the path of the repository
    private static final String SEP = FileSystems.getDefault().getSeparator(); // the file separator

    /**
     * Constructor
     * 
     * @param ast            - the AST of the file
     * @param path           - the path of the file
     * @param repositoryPath - the path of the repository
     */
    public DependencyCollection(CompilationUnit ast, String path, String repositoryPath) {
        this.dependencies = new ArrayList<Dependency>();
        this.ast = ast;
        this.path = path;
        this.repositoryPath = repositoryPath;
    }

    /**
     * Get the file dependencies
     * 
     * @return - the dependencies
     */
    public ArrayList<Dependency> getDependencies() {
        return dependencies;
    }

    /**
     * Set the file dependencies
     * 
     * @param dependencies - the dependencies
     */
    public void setDependencies(ArrayList<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Get the path of the file
     * 
     * @return - the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the path of the file
     * 
     * @param path - the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get the AST of the file
     * 
     * @return - the AST
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     * Set the AST of the file
     * 
     * @param ast - the AST
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    /**
     * Prints file dependencies
     */
    public void printMetadata() {
        dependencies.forEach(x -> System.out.println(x.toString()));

    }

    /**
     * Extract the dependencies from the AST
     */
    public void extractDependenciesFromAST() {
        VoidVisitor<List<Dependency>> depDeclCollector = new ImportDeclarationCollector();
        depDeclCollector.visit(ast, dependencies);
    }

    /**
     * The visitor that collects the import declarations
     */
    private class ImportDeclarationCollector extends VoidVisitorAdapter<List<Dependency>> {
        @Override
        public void visit(ImportDeclaration importDecl, List<Dependency> collection) {
            super.visit(importDecl, collection);
            extractDependencyInfo(importDecl, collection);
        }
    }

    /**
     * Extract the dependency information from the import declaration
     * 
     * @param imp        - the import declaration
     * @param collection - the collection of file dependencies
     */
    public void extractDependencyInfo(ImportDeclaration imp, List<Dependency> collection) {
        String packageName, importName;

        if (!imp.isAsterisk()) {
            importName = imp.getName().getIdentifier();
            packageName = getPackageName(imp);

            if (!imp.isStatic()) {

                if (path.equals(repositoryPath)) {
                    collection.add(new Dependency(packageName, importName, "external", "class/interface"));
                } else {
                    String parentPath = (new File(repositoryPath)).getAbsolutePath();
                    boolean addedAsInternal = addInternalDependency(parentPath, packageName, importName, collection);
                    if (!addedAsInternal) {
                        collection.add(new Dependency(packageName, importName, "external", "class/interface"));
                    }
                }

            } else {

                if (path.equals(repositoryPath)) {
                    collection.add(new Dependency(packageName, importName, "external", "static member"));
                } else {

                    boolean addedAsInternal = addStaticDependencies(repositoryPath, packageName, collection, false,
                            importName);
                    if (!addedAsInternal) {
                        collection.add(new Dependency(packageName, importName, "external", "static member"));
                    }
                }
            }

        } else {
            importName = "*";
            packageName = getPackageName(imp);

            if (!imp.isStatic()) {
                if (path.equals(repositoryPath)) {
                    collection.add(new Dependency(packageName, importName, "external", "class/interface"));
                } else {
                    boolean addedAsInternal = addDependencies(packageName, collection);
                    if (!addedAsInternal) {
                        collection.add(new Dependency(packageName, importName, "external", "classes/interfaces"));
                    }
                }
            } else {

                if (path.equals(repositoryPath)) {
                    collection.add(new Dependency(packageName, importName, "external", "static members"));
                } else {
                    boolean addedAsInternal = addStaticDependencies(repositoryPath, packageName, collection, true,
                            importName);
                    if (!addedAsInternal) {
                        collection.add(new Dependency(packageName, importName, "external", "static members"));
                    }
                }
            }
        }

    }

    /**
     * Get the package name from the import declaration
     * 
     * @param imp - the import declaration
     * @return - the package name
     */
    public String getPackageName(ImportDeclaration imp) {
        if (!imp.isAsterisk()) {
            return imp.getName().getQualifier().get().asString();
        } else {

            String id = imp.getName().getIdentifier();
            if (imp.getName().getQualifier().isPresent()) {
                String qual = imp.getName().getQualifier().get().asString();
                return qual + "." + id;
            } else {
                return id;
            }
        }

    }

    /**
     * Add a internal dependency to the collection of file dependencies
     * 
     * @param packagePath - the path of the package
     * @param packageName - the package name
     * @param importName  - the import name
     * @param collection  - the collection of file dependencies
     */
    public boolean addInternalDependency(String parentPath, String packageName, String importName,
            List<Dependency> collection) {

        boolean found = checkPackageExistence(parentPath, packageName, importName, collection);
        if (found)
            return true;
        String pkgParentPath = new File(parentPath).getParentFile().getAbsolutePath();
        found = checkPackageExistence(pkgParentPath, packageName, importName, collection);
        if (found)
            return true;
        return continueDependencySearch(parentPath, packageName, importName, collection);
    }

    /**
     * Check if the package exists and add the dependency to the collection
     * 
     * @param parentPath  - the path of the parent directory of the package
     * @param packageName - the package name
     * @param importName  - the import name
     * @param collection  - the collection of file dependencies
     * @return - true if the dependency is internal and has been added to collection
     */
    public boolean checkPackageExistence(String parentPath, String packageName, String importName,
            List<Dependency> collection) {

        File pkg = new File(parentPath + SEP + packageName.replace(".", SEP));
        if (pkg.exists()) {
            File file = new File(pkg.getAbsolutePath() + SEP + importName + ".java");
            if (file.exists()) {
                ArrayList<Class> classes = searchFileForClasses(file.getAbsolutePath());
                for (Class c : classes) {
                    if (c.getName().equals(importName)) {
                        collection.add(new Dependency(packageName, importName, "internal", "class"));
                        return true;
                    }
                }

                ArrayList<Interface> intfs = searchFileForInterfaces(file.getAbsolutePath());
                for (Interface i : intfs) {
                    if (i.getName().equals(importName)) {
                        collection.add(new Dependency(packageName, importName, "internal", "interface"));
                        return true;
                    }

                }
            }

        }
        return false;
    }

    /**
     * Continue the search to determine if a dependency is internal or external
     * 
     * @param parentPath  - the path of the parent directory of the package
     * @param packageName - the package name
     * @param importName  - the import name
     * @param collection  - the collection of dependencies
     * @return - true if the dependency is internal and has been added to collection
     */
    public boolean continueDependencySearch(String parentPath, String packageName, String importName,
            List<Dependency> collection) {

        Path dirObj = Paths.get(parentPath);
        List<File> directories = new ArrayList<>();
        try {
            directories = Files.list(dirObj)
                    .map(Path::toFile)
                    .filter(File::isDirectory)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.out.println("Could not get directories! :" + e);
            System.exit(0);
        }
        if (directories.size() > 0) {
            for (int i = 0; i < directories.size(); i++) {
                boolean found = addInternalDependency(directories.get(i).getAbsolutePath(), packageName, importName,
                        collection);
                if (found) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add the dependencies of a file to the collection of file dependencies
     * 
     * @param packageName - the name of the package containing imported entity
     * @param collection  - the collection of file dependencies
     */
    public boolean addDependencies(String packageName, List<Dependency> collection) {

        File file = new File(path);
        File filePackage = file.getParentFile();

        if (inSamePackage(filePackage, packageName)) {
            Path packageObj = filePackage.toPath();
            List<File> files = getFiles(packageObj);
            addImportedClasses(files, packageName, collection);
            addImportedInterfaces(files, packageName, collection);
        } else {
            String parPath = (new File(repositoryPath)).getAbsolutePath();
            boolean found = addInternalDependencies(parPath, packageName, collection);
            if (found)
                return true;
            parPath = new File(parPath).getParentFile().getAbsolutePath();
            found = addInternalDependencies(parPath, packageName, collection);
            return found;
        }
        return false;
    }

    /**
     * Add the dependencies of a file to the collection of file dependencies
     * 
     * @param parentPath  - the path of the parent directory of the package
     * @param packageName - the package name
     * @param collection  - the collection of file dependencies
     * @return - true if the dependency is internal and has been added to collection
     */
    private boolean addInternalDependencies(String parentPath, String packageName, List<Dependency> collection) {

        File pkg = new File(parentPath + SEP + packageName.replace(".", SEP));
        if (pkg.exists()) {
            Path packageObj = pkg.toPath();
            List<File> files = getFiles(packageObj);
            addImportedClasses(files, packageName, collection);
            addImportedInterfaces(files, packageName, collection);
            return true;
        } else {
            Path dirObj = Paths.get(parentPath);
            List<File> directories = new ArrayList<>();
            try {
                directories = Files.list(dirObj)
                        .map(Path::toFile)
                        .filter(File::isDirectory)
                        .collect(Collectors.toList());

            } catch (IOException e) {
                System.out.println("Could not get directories! :" + e);
                System.exit(0);
            }
            if (directories.size() > 0) {
                for (int i = 0; i < directories.size(); i++) {
                    boolean found = addInternalDependencies(directories.get(i).getAbsolutePath(), packageName,
                            collection);
                    if (found) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if the file is in the same package as the import
     * 
     * @param filePackage - the package of the file
     * @param packageName - the package name of the import
     * @return - true if the file is in the same package as the imported entity
     */
    private boolean inSamePackage(File filePackage, String packageName) {
        String fileParentPackage = filePackage.getParentFile().getAbsolutePath();
        String importPackageFullPath = fileParentPackage + SEP + packageName.replace(".", SEP);
        String filePackageAsString = filePackage.getAbsolutePath();
        return filePackageAsString.equals(importPackageFullPath);
    }

    /**
     * Add the static dependencies of a file to the collection of file dependencies
     * 
     * @param parentPath - the path of the parent directory of the package
     * @param classIntf  - the name of the class/interface containing the static
     *                   member
     * @param collection - the collection of file dependencies
     * @param isAst      - true if the import is an asterisk import
     * @param impName    - the name of the imported entity
     * @return - true if the dependency is internal and has been added to collection
     */
    private boolean addStaticDependencies(String parentPath, String classIntf, List<Dependency> collection,
            boolean isAst, String impName) {
                
        if (!classIntf.endsWith(".java"))
            classIntf = classIntf.replace(".", File.separator) + ".java";

        File file = new File(parentPath + SEP + classIntf);
        
        if (file.exists()) {
  
            if(isAst){
                collection.add(new Dependency((classIntf.substring(0, classIntf.lastIndexOf(".java")).replace(File.separator, ".")), impName, "internal", "static members"));
            }else{
                collection.add(new Dependency((classIntf.substring(0, classIntf.lastIndexOf(".java")).replace(File.separator, ".")), impName, "internal", "static member"));
            }
    

            return true;
        } else {
                Path dirObj = Paths.get(parentPath);
                List<File> directories = new ArrayList<>();
                try {
                    directories = Files.list(dirObj)
                            .map(Path::toFile)
                            .filter(File::isDirectory)
                            .collect(Collectors.toList());

                } catch (IOException e) {
                    System.out.println("Could not get directories! :" + e);
                    System.exit(0);
                }
                if (directories.size() > 0) {
                    for (int i = 0; i < directories.size(); i++) {
                        boolean found = addStaticDependencies(directories.get(i).getAbsolutePath(), classIntf,
                                collection,
                                isAst, impName);
                        if (found) {
                            return true;
                        }
                    }
                }
        }

        return false;
    }

    /**
     * Add internal imported classes to the collection of file dependencies
     * 
     * @param files       - the files in the directory containing the file being
     *                    analysed
     * @param packageName - the package name
     * @param collection  - the collection of file dependencies
     */
    private void addImportedClasses(List<File> files, String packageName, List<Dependency> collection) {
        String fileName = path.substring(path.lastIndexOf(SEP) + 1);
        if (files.size() > 0) {
            files.forEach(f -> {
                if (!f.getName().equals(fileName)) {
                    String filePath = f.getAbsolutePath();
                    ArrayList<Class> classes = searchFileForClasses(filePath);
                    if (classes != null) {
                        classes.forEach(
                                a -> collection.add(new Dependency(packageName, a.getName(), "internal", "class")));
                    }
                }

            });
        }
    }

    /**
     * Add internal imported interfaces to the collection of file dependencies
     * 
     * @param files       - the files in the directory containing the file being
     *                    analysed
     * @param packageName - the package name
     * @param collection  - the collection of file dependencies
     */
    private void addImportedInterfaces(List<File> files, String packageName, List<Dependency> collection) {

        String fileName = path.substring(path.lastIndexOf(SEP)).replace(SEP, "");
        if (files.size() > 0) {
            files.forEach(f -> {
                if (!f.getName().equals(fileName)) {
                    String filePath = f.getAbsolutePath();
                    ArrayList<Interface> intfs = searchFileForInterfaces(filePath);
                    if (intfs != null) {
                        intfs.forEach(
                                b -> collection.add(new Dependency(packageName, b.getName(), "internal", "interface")));
                    }

                }
            });
        }

    }

    /**
     * Get the files in a directory
     * 
     * @param packageObj - the path of the directory
     * @return - the files in the directory
     */
    private List<File> getFiles(Path packageObj) {
        List<File> files = null;
        try {
            files = Files.list(packageObj)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(x -> FilenameUtils.getExtension(x.getName()).equals("java"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Could not get files! :" + e);
            System.exit(0);
        }
        return files;

    }

    /**
     * Search a file for classes. These are classes are classes that are not nested
     * or private.
     * 
     * @param filePath - the path of the file
     * @return - the classes in the file as list of Class objects
     */
    public ArrayList<Class> searchFileForClasses(String filePath) {
        if (filePath.length() > 0 && filePath != null) {
            AST ast = new AST(filePath, repositoryPath);
            ast.extractMetadata();
            return (ArrayList<Class>) ast.getClassCollection()
                    .getClasses()
                    .stream()
                    .filter(x -> x.getClassCategory() == ClassInterfaceCategory.STANDARD)
                    .filter(x -> x.getAccessModifer() != AccessModifierType.PRIVATE)
                    .collect(Collectors.toList());
        }

        return null;
    }

    /**
     * Search a file for interfaces. These are interfaces that are not nested or
     * private.
     * 
     * @param filePath - the path of a file
     * @return - the interfaces in the file as list of Interface objects
     */
    public ArrayList<Interface> searchFileForInterfaces(String filePath) {
        if (filePath.length() > 0 && filePath != null) {
            AST ast = new AST(filePath, repositoryPath);
            ast.extractMetadata();
            return (ArrayList<Interface>) ast.getInterfaceCollection()
                    .getInterfaces()
                    .stream()
                    .filter(x -> x.getInterfaceCategory() == ClassInterfaceCategory.STANDARD)
                    .filter(x -> x.getAccessModifer() != AccessModifierType.PRIVATE)
                    .collect(Collectors.toList());
        }
        return null;
    }

}
