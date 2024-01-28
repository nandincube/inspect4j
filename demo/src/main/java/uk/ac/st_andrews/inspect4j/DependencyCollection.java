package uk.ac.st_andrews.inspect4j;

import java.io.File;
import java.io.IOException;
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

    public DependencyCollection( CompilationUnit ast, String path) {
        this.dependencies = new ArrayList<Dependency>();
        this.ast = ast;
        this.path = path;
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
        //VoidVisitor<List<Dependency>> depDeclCollector = new ImportDeclarationCollector();
       // depDeclCollector.visit(ast, dependencies);
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
            boolean foundPackage = searchPackage(fromPackageName, collection);
            if(!foundPackage){
                importName = "";
                importType = "external";
                typeElement =  "unknown";
                collection.add(new Dependency(fromPackageName, importName, importType, typeElement));
            }
           
        }else{
            importName = imp.getName().getIdentifier();
            importType = importOrigin(fromPackageName,importName);
            typeElement =  importTypeElement(importType, imp, fromPackageName, importName);
            collection.add(new Dependency(fromPackageName, importName, importType, typeElement));
        }

    }

    //Adaptation of code from inspect4py: REWRITE THISSSS!!

  
    //same as module = node.module
    public String getParentPackageName(ImportDeclaration imp){
        return imp.getName().getQualifier().get().asString();
    }

    public String getPackageAbsolutePath(String packageName){
         if((new File(path)).getParentFile() != null) {
            String repoPath = (new File(path)).getParentFile().getAbsolutePath();
            String packageRelativePath =  packageName.replace(".","/");
            return repoPath + "/" + packageRelativePath;
          }else{
              return null;
         }
       
    }

    public String getImportAbsolutePath(String packageName, String importName){
        String absPackagePath = getPackageAbsolutePath(packageName);
        if(absPackagePath == null) return null;
        return absPackagePath+ "/" + importName +".java";

    }


    //used for class/interface that is directly refered to as in import (i.e. not .*)
    public String importOrigin(String packageName , String importName){
        String absPackagePath = getPackageAbsolutePath(packageName);
        if(absPackagePath == null) {
            System.out.println("Could not extract parent package/directory for :"+packageName);
            return null;
        }
        String fullImportPath = getImportAbsolutePath(packageName, importName);

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

    /**
     * 
     * @param importType
     * @param imp
     * @param packageName
     * @param importName
     * @return
     */
    public String importTypeElement(String importType, ImportDeclaration imp, String packageName, String importName){
        // if internal - try to match import name with non private classes or interfaces
        //if internale - check import declation is static or is importing static member ()

        if(importType.equals("internal")){
            if(imp.isStatic()) return "static member";

            String fullPackagePath = getPackageAbsolutePath(packageName);

            if(fullPackagePath == null) {
                System.out.println("Could not extract package path for :"+path);
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
    public boolean searchPackage(String fromPackageName, List<Dependency> collection){
        try{
            String fromPackagePath =  getPackageAbsolutePath(fromPackageName);
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
    public static ArrayList<Class> searchFileForClasses(String filePath){
        if(filePath.length() > 0  && filePath != null){
            AST ast = new AST(filePath);
            ast.extractMetadata();
            return (ArrayList<Class>) ast.getClassCollection()
                                            .getClasses()
                                            .stream()
                                            .filter(x -> x.getClassCategory() == ClassInterfaceCategory.STANDARD)
                                            .filter(x -> x.getAccessModifer() != AccessModifierType.PRIVATE)
                                            .toList();
        } 

        return null;
    }  

     /**
      * 
      * @param filePath
      * @return
      */
    public static ArrayList<Interface> searchFileForInterfaces(String filePath){
        if(filePath.length() > 0  && filePath != null){
            AST ast = new AST(filePath);
            ast.extractMetadata();
            return (ArrayList<Interface>) ast.getInterfaceCollection()
                                        .getInterfaces()
                                            .stream()
                                            .filter(x -> x.getInterfaceCategory() == ClassInterfaceCategory.STANDARD)
                                            .filter(x -> x.getAccessModifer() != AccessModifierType.PRIVATE)
                                            .toList();
        } 
        return null;
    } 
    
}
