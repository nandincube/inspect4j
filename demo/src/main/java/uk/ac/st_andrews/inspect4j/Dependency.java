package uk.ac.st_andrews.inspect4j;

import com.github.javaparser.ast.ImportDeclaration;

public class Dependency {
    private String fromPackage;
    private String importName;
    private String importType;
    private String typeElement;

    public Dependency(String fromPackage, String importName, String importType, String typeElement) {
        this.fromPackage = fromPackage;
        this.importName = importName;
        this.importType = importType;
        this.typeElement = typeElement;
    }

    public Dependency(ImportDeclaration imp) {
        extractDependencyInfo(imp);
    }

    public void extractDependencyInfo(ImportDeclaration imp){
        if (imp.isAsterisk()){
            fromPackage = imp.getName().getQualifier().get().asString();
            
        }else{
            fromPackage = (imp.getName().getQualifier().isPresent()) ? 
                                imp.getName().getQualifier().get().asString() : imp.getName().getIdentifier();
            importType = isInternal(imp);
            importName = imp.getName().getIdentifier();
            typeElement = extractType(imp);
        }

    }

    //Adaptation of code from inspect4py: REWRITE THISSSS!!

    public String isInternal(ImportDeclaration imp){
        //TODO:
        return "";
    }

    public String extractType(ImportDeclaration imp){
        //TODO:
        return "";
    }

    public String getFromPackage() {
        return fromPackage;
    }

    public void setFromPackage(String fromPackage) {
        this.fromPackage = fromPackage;
    }

    public String getImportName() {
        return importName;
    }

    public void setImportName(String importName) {
        this.importName = importName;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(String typeElement) {
        this.typeElement = typeElement;
    }

    


}
