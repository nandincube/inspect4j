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

public class DependencyCollection {
    private ArrayList<Dependency> dependencies;
    private CompilationUnit ast;
    private String path;
    private static String repositoryPath;
    private static String fileSeperator = FileSystems.getDefault().getSeparator();

    public DependencyCollection(CompilationUnit ast, String path, String repositoryPath) {
        this.dependencies = new ArrayList<Dependency>();
        this.ast = ast;
        this.path = path;
        this.repositoryPath = repositoryPath;
    }

    /**
     * 
     * @return
     */
    public ArrayList<Dependency> getDependencies() {
        return dependencies;
    }

    /**
     * 
     * @param dependencies
     */
    public void setDependences(ArrayList<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * 
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * 
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 
     * @param dependencies
     */
    public void setDependency(ArrayList<Dependency> dependencies) {
        this.dependencies = dependencies;
        
    }

    /**
     * 
     * @return
     */
    public CompilationUnit getAst() {
        return ast;
    }

    /**
     * 
     * @param ast
     */
    public void setAst(CompilationUnit ast) {
        this.ast = ast;
    }

    /**
    * 
    */
    public void printMetadata() {
        dependencies.forEach(x -> System.out.println(x.toString()));

    }

    /**
     * 
     */
    public void extractDependenciesFromAST() {
        VoidVisitor<List<Dependency>> depDeclCollector = new ImportDeclarationCollector();
        depDeclCollector.visit(ast, dependencies);
    }

    /**
     * 
     */
    private class ImportDeclarationCollector extends VoidVisitorAdapter<List<Dependency>> {
        @Override
        public void visit(ImportDeclaration importDecl, List<Dependency> collection) {
            super.visit(importDecl, collection);
            extractDependencyInfo(importDecl, collection);
        }
    }

    public void extractDependencyInfo(ImportDeclaration imp, List<Dependency> collection) {
        String packageName, importName;

        if (!imp.isAsterisk()) {
            importName = imp.getName().getIdentifier();
            packageName = getPackageName(imp);
                  
            if(!imp.isStatic()){
                String parentPath = (new File(repositoryPath)).getAbsolutePath();
                boolean addedInternal = addDependency(parentPath, packageName, importName, collection);
                if(!addedInternal){
                     collection.add(new Dependency(packageName, importName, "external",  "class/interface"));
                 }
            }else{
                boolean addedInternal = addStaticDependencies(repositoryPath, packageName, collection,false, importName);
                if(!addedInternal){
                    collection.add(new Dependency(packageName, importName, "external",  "static member"));
                }
            }

        } else {
            importName = "*";
            packageName = getPackageName(imp);
            
            if(!imp.isStatic()){
                boolean addedInternal = addDependencies(packageName, collection);
                if(!addedInternal){
                    collection.add(new Dependency(packageName, importName, "external",  "classes/interfaces"));
                }
            }else{
                boolean addedInternal = addStaticDependencies(repositoryPath, packageName, collection,true,importName);
                if(!addedInternal){
                    collection.add(new Dependency(packageName, importName, "external",  "static members"));
                }
            }
        }    

    }



    // Adaptation of code from inspect4py: REWRITE THISSSS!!

    // same as module = node.module
    public String getPackageName(ImportDeclaration imp) {
        if(!imp.isAsterisk()){
            return imp.getName().getQualifier().get().asString();
        }else{
            
            String id = imp.getName().getIdentifier();
            if (imp.getName().getQualifier().isPresent()) {
                String qual = imp.getName().getQualifier().get().asString();
                return qual + fileSeperator + id;
            } else {
                return id;
            }
        }
        
    }

    
    /**
     * 
     * @param packagePath
     */
    public boolean addDependency(String parentPath, String packageName, String importName, List<Dependency> collection) {

        File pkg = new File(parentPath + fileSeperator + packageName);
        
        if (pkg.exists()) {
            File file = new File(pkg.getAbsolutePath()+fileSeperator+importName+".java");
            if(file.exists()){
                ArrayList<Class> classes = searchFileForClasses(file.getAbsolutePath());
                for(Class c: classes){
                    if(c.getName().equals(importName)){
                        collection.add(new Dependency(packageName, importName, "internal",  "class"));
                        return true;
                    } 
                }

                ArrayList<Interface> intfs = searchFileForInterfaces(file.getAbsolutePath());
                for(Interface i: intfs){
                    if(i.getName().equals(importName)){
                        collection.add(new Dependency(packageName, importName, "internal",  "interface"));
                        return true;
                    }
                    
                }
            }
            
        }
        return continueDependencySearch(parentPath, packageName, importName, collection);
    }

    public boolean continueDependencySearch(String parentPath, String packageName, String importName, List<Dependency> collection){
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
                boolean found = addDependency(directories.get(i).getAbsolutePath(), packageName, importName,collection);
                if (found) {return true;}
            } 
        }
        return false;
    }


    /**
     * 
     * @param packagePath
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
            boolean found = addDependencies(parPath, packageName, collection);
            return found;
        }
        return false;
    }


    private boolean addDependencies(String parentPath, String packageName, List<Dependency> collection) {

        File pkg = new File(parentPath + fileSeperator + packageName);
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
                    boolean found = addDependencies(directories.get(i).getAbsolutePath(), packageName, collection);
                    if (found) {return true;}
                }
            }
        }
        return false;
    }

    private boolean inSamePackage(File filePackage, String packageName) {
        String fileParentPackage = filePackage.getParentFile().getAbsolutePath();
        String importPackageFullPath = fileParentPackage + fileSeperator + packageName;
        String filePackageAsString = filePackage.getAbsolutePath();
        return filePackageAsString.equals(importPackageFullPath);
    }

 

    private boolean addStaticDependencies(String parentPath, String classIntf, List<Dependency> collection, boolean isAst, String impName){
        if(!classIntf.endsWith(".java")) classIntf = classIntf+".java";

        File file = new File(parentPath + fileSeperator + classIntf);
        if (file.exists()) {
            if(isAst){
                collection.add(new Dependency(classIntf, impName, "internal",  "static members"));
            }else{
                collection.add(new Dependency(classIntf, impName, "internal",  "static member"));
            }
           
            return true;
        }else{
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
                    boolean found = addStaticDependencies(directories.get(i).getAbsolutePath(), classIntf, collection, isAst, impName);
                    if (found) {return true;}
                }
            }
        }
        return false;
    }

    private void addImportedClasses(List<File> files, String packageName, List<Dependency> collection) {
        String fileName = path.substring(path.lastIndexOf(fileSeperator)+1);
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

    private void addImportedInterfaces(List<File> files, String packageName, List<Dependency> collection) {

        String fileName = path.substring(path.lastIndexOf(fileSeperator)).replace(fileSeperator, "");
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
     * 
     * @param filePath
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
     * 
     * @param filePath
     * @return
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
