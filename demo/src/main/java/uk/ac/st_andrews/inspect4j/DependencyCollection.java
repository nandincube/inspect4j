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

    public DependencyCollection( CompilationUnit ast, String path, String repositoryPath) {
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
    public void printMetadata(){
        dependencies.forEach(x -> System.out.println(x.toString()));

    }

    /**
     * 
     */
    public void extractDependenciesFromAST(){
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


    /**
     * 
     * @param imp
     * @param collection
     */
    public void extractDependencyInfo(ImportDeclaration imp, List<Dependency> collection){
        String fromPackageName, importName,importType, typeElement;
        fromPackageName =  getParentPackageName(imp);

        if (imp.isAsterisk()){
            boolean foundPackage = searchPackage(fromPackageName, collection,path);
            if(!foundPackage){
                importName = "";
                importType = "external";
                typeElement =  "unknown";
                collection.add(new Dependency(fromPackageName, importName, importType, typeElement));
            }
           
        }else{
            importName = imp.getName().getIdentifier();
            importType = importOrigin(fromPackageName,importName,path);
            typeElement =  importTypeElement(importType, imp, fromPackageName, importName,path);
            collection.add(new Dependency(fromPackageName, importName, importType, typeElement));
        }

    }

    //Adaptation of code from inspect4py: REWRITE THISSSS!!

  
    //same as module = node.module
    public String getParentPackageName(ImportDeclaration imp){
        return imp.getName().getQualifier().get().asString();
    }

    public String getPackageAbsolutePath(String packageName, String path2){
         if((new File(path2)).getParentFile() != null) {
            String repoPath = (new File(path2)).getParentFile().getAbsolutePath();
            String packageRelativePath =  packageName.replace(".",fileSeperator);
            return repoPath + fileSeperator + packageRelativePath;
          }else{
              return null;
         }
       
    }

    public String getImportAbsolutePath(String absPackagePath, String importName){
        return absPackagePath+ fileSeperator + importName +".java";

    }

    public String importOrigin(String packageName , String importName,String path2){

        if(findOrigin(packageName , importName,path2).equals("internal")){ //check if import is within same package
            return "internal";
        }else{ //check if important is with the repository
            return findOrigin(packageName.substring(packageName.indexOf(".")+1) , importName, repositoryPath);
        }
    }


// "C:\Users\nandi\OneDrive\Documents\4th year\CS4099 - Dissertation\Dissertation\inspect4j\demo\src\test\java\test_files\test_doc_and_dependencies\test_files\test_basic\BasicClassWithMultipleMethods.java"
//C:\Users\nandi\OneDrive\Documents\4th year\CS4099 - Dissertation\Dissertation\inspect4j\demo\src\test\java\test_files\test_doc_and_dependencies\BasicClassWithDependenciesAsterisk.java
    //used for class/interface that is directly refered to as in import (i.e. not .*)
    public String findOrigin(String packageName , String importName, String path2){
        String absPackagePath = getPackageAbsolutePath(packageName, path2);      
        if(absPackagePath == null) {
            System.out.println("Could not extract parent package/directory for :"+packageName);
            return null;
        }
        String fullImportPath = getImportAbsolutePath(absPackagePath,importName);
 
        if(fullImportPath == null) {
            System.out.println("Could not extract import path for :"+packageName);
            return null;
        }

        File importFile = new File(fullImportPath);

        if (importFile.exists()){
            return "internal";
        }else if (packageName.length() > 0 ){
            //should be able to deal with static imports/ importing of static members
            String p = absPackagePath + ".java";
            if ((new File(p)).exists()){
                return "internal";
            }else{
                return "external";
            }
        }else{
            return "external";
        }
    }


    
    public String importTypeElement(String importType, ImportDeclaration imp, String packageName, String importName, String path2){
        String typeElement = findTypeElement(importType, imp, packageName, importName, path2);
        if( typeElement != null){ //check if import is within same package
            return typeElement;
        }else{ //check if important is with the repository
            return findTypeElement(importType, imp, packageName.substring(packageName.indexOf(".")+1) , importName, repositoryPath);
        }
    }

    /**
     * 
     * @param importType
     * @param imp
     * @param packageName
     * @param importName
     * @return
     */
    public String findTypeElement(String importType, ImportDeclaration imp, String packageName, String importName, String path2){
        // if internal - try to match import name with non private classes or interfaces
        //if internal - check import declation is static or is importing static member ()

        if(importType.equals("internal")){
            if(imp.isStatic()) return "static member";

            String fullPackagePath = getPackageAbsolutePath(packageName, path2);

            if(fullPackagePath == null) {
                System.out.println("Could not extract package path for :"+path2);
                return null;
            }
    
            File packageDir = new File(fullPackagePath);
            if (packageDir.exists()){
                Path packageObj =  Paths.get(fullPackagePath);
            
                List<File> files = new ArrayList<File>();
                try {
                    files = Files.list(packageObj)
                        .map(Path::toFile)
                        .filter(File::isFile)
                        .filter(f -> f.toString().endsWith(".java"))
                        .collect(Collectors.toList());
                } catch (IOException e) {
                    System.out.println("Unable to retrieve files from package "+packageName);
                }
    
                if(files.size() > 0){
                   for(File f: files ){
                        String filePath = f.getAbsolutePath();
                        ArrayList<Class> classes = searchFileForClasses(filePath);
                        if(classes != null){
                           for(Class cl: classes){
                                if(cl.getName().equals(importName)) return "class";
                           }
                        }
                    
                        ArrayList<Interface> intfs = searchFileForInterfaces(filePath);
                        if(intfs != null){
                            for(Interface intf: intfs){
                                if(intf.getName().equals(importName)) return "interface";
                           }
                        }
                    
                   }
                }
                
            }

        }else{
            if(imp.isStatic()) return "static member";
            return "unknown";
        }

        return null;

    }




     /**
      * 
      * @param packagePath
      */
    public boolean searchPackage(String fromPackageName, List<Dependency> collection, String path2){
        try{
            String fromPackagePath =  getPackageAbsolutePath(fromPackageName, path2);
            if(fromPackagePath == null) return false;
            Path packageObj =  Paths.get(fromPackagePath);
            
            List<File> files = Files.list(packageObj)
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toList());

            if(files.size() > 0){
                files.forEach(x-> {
                    String filePath = x.getAbsolutePath();
                    ArrayList<Class> classes = searchFileForClasses(filePath);
                    if(classes != null){
                        classes.forEach(a -> {
                            collection.add(new Dependency(fromPackageName, a.getName(), "internal", "class"));
                        });
                    }
                
                    ArrayList<Interface> intfs = searchFileForInterfaces(filePath);
                    if(intfs != null){
                        intfs.forEach(b -> {
                            collection.add(new Dependency(fromPackageName, b.getName(), "internal", "interface"));
                        });
                    }
                });
            }else{
                return false;
            }

            return true;
        }catch(IOException i){
            System.out.println("Couldn't analyse this package! "+ i);
            
        }
        return false;
    }


   /**
    * 
    * @param filePath
    */
    public  ArrayList<Class> searchFileForClasses(String filePath){
        if(filePath.length() > 0  && filePath != null){
            AST ast = new AST(filePath,repositoryPath);
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
    public ArrayList<Interface> searchFileForInterfaces(String filePath){
        if(filePath.length() > 0  && filePath != null){
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
