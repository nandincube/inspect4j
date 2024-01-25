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

    public DependencyCollection(ArrayList<Dependency> dependencies, CompilationUnit ast, String path) {
        this.dependencies = dependencies;
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
    public void extractDependenciesFromAST(){
        VoidVisitor<List<Dependency>> depDeclCollector = new ImportDeclarationCollector();
        depDeclCollector.visit(ast, dependencies);
    }
    
    /**
     * 
     */
    private static class ImportDeclarationCollector extends VoidVisitorAdapter<List<Dependency>> {
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
    public  static void extractDependencyInfo(ImportDeclaration imp, List<Dependency> collection){
        if (imp.isAsterisk()){
            String fromPackage = imp.getName().getQualifier().get().asString();
            String packagePath = getPackagePath(fromPackage);
            if(packagePath != null ){
                searchPackage(fromPackage, packagePath, collection);
            }else{
               // collection.add();
               
               //TODO:
                String fromPackage = imp.getName().getQualifier().get().asString();
                String importName = imp.getName().getIdentifier();
                //collection.add(new Dependency(fromPackage, importName, "external", "class"));
            }

            //String importName = imp.getName().getIdentifier
            
        }else{
           String fromPackage = (imp.getName().getQualifier().isPresent()) ? 
                                imp.getName().getQualifier().get().asString() : imp.getName().getIdentifier();
            // String importType = isInternal(imp);
            // String importName = imp.getName().getIdentifier();
            // String typeElement = extractType(imp);
        }

    }

    //Adaptation of code from inspect4py: REWRITE THISSSS!!

    public String isInternal(ImportDeclaration imp){
        //TODO:
        return "";
    }

    public static String getParentPackageName(){

        if (imp.getName().getQualifier()){
            
        }
    }

    public static String getPackagePath(String packageName){
        String packageString = packageName.replace(".","/");
        
        if((new File(packageString)).getParentFile() != null) {
            return (new File(packageString)).getParentFile().getAbsolutePath();
        }else{
            return null;
        }
       
    }

    public String importedEntityPath(ImportDeclaration imp){
        //TODO:
        String importString2  = imp.getNameAsString();
        importString2 = importString2.replace(".","/");

        String importString  = imp.getNameAsString();
        importString = importString.replace(".","\\");

        String repo = (new File(path)).getParentFile().getAbsolutePath();
        return repo + "\\"+".java";

    }

    // public boolean findClasses(String importedEntity){
        
      
    //         //we want to extract all public, protected and default classes;
       

    // }


    //used for class/interface that is directly refered to as in import (i.e. not .*)
    public String type_module(String , String importName){
        String fullImportPath = "";

        String repo = (new File(path)).getParentFile().getAbsolutePath();
        
        if(fullPackageName != null){
            packageName = packageName.replace(".","/");
            fullImportPath = repo + 

        }
        if m:
            m = m.replace(".", "/")
            file_module = abs_repo_path + "/" + m + "/" + i + ".py"
        else:
            file_module = abs_repo_path + "/" + i + ".py"

        file_module_path = Path(file_module)
        if file_module_path.is_file():
            return "internal"
        else:
            if m:
                m = m.replace(".", "/")
                file_module = abs_repo_path + "/" + m + ".py"
                file_module_path = Path(file_module)
                if file_module_path.is_file():
                    return "internal"
                else:
                    file_module = abs_repo_path + "/" + m + "/main.py"
                    file_module_path = Path(file_module)
                    if file_module_path.is_file():
                        return "internal"
                    else:
                        return "external"
            else:
                dir_module = abs_repo_path + "/" + i
                if os.path.exists(dir_module):
                    return "internal"
                else:
                    return "external"
    }


     /**
      * 
      * @param packagePath
      */
    public static void searchPackage(String fromPackage, String packagePath, List<Dependency> collection){
        try{

            Path packageObj =  Paths.get(packagePath);
            
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
                            collection.add(new Dependency(fromPackage, a.getName(), "internal", "class"));
                        });
                    }
                
                    ArrayList<Interface> intfs = searchFileForInterfaces(filePath);
                    if(intfs != null){
                        intfs.forEach(b -> {
                            collection.add(new Dependency(fromPackage, b.getName(), "internal", "interface"));
                        });
                    }
                });
            }
        }catch(IOException i){
            System.out.println("Couldn't analyse this package! "+ i);
        }

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
