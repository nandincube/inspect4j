package uk.ac.st_andrews.inspect4j;

/**
 * Class to represent a dependency in a class or interface. A dependency is an import statement in a class/interface.
 */
public class Dependency {
    private String fromPackage; //name of package containing the class/interface/static member being imported
    private String importName; //name of the class/interface/static member being imported
    private String importType; //whether the imported entity is internal or external to the repository
    private String typeElement; //whether the imported entity is a class, interface or static member

    /**
     * Constructor
     * @param fromPackage - name of package containing the class/interface/static member being imported
     * @param importName - name of the class/interface/member being imported
     * @param importType - whether the imported entity is internal or external to the repository
     * @param typeElement - whether the imported entity is a class, interface or static member
     */
    public Dependency(String fromPackage, String importName, String importType, String typeElement) {
        this.fromPackage = fromPackage;
        this.importName = importName;
        this.importType = importType;
        this.typeElement = typeElement;
    }

    /**
     * Gets the name of the package containing the class/interface/static member being imported
     * @return String - name of package containing the class/interface/static member being imported
     */
    public String getFromPackage() {
        return fromPackage;
    }

    /**
     * Sets the name of the package containing the class/interface/static member being imported
     * @param fromPackage - name of package containing the class/interface/static member being imported
     */ 
    public void setFromPackage(String fromPackage) {
        this.fromPackage = fromPackage;
    }

    /*
     * Gets the name of the class/interface/static member being imported
     * @return String - name of the class/interface/member being imported
     */
    public String getImportName() {
        return importName;
    }

    /**
     * Sets the name of the class/interface/static member being imported
     * @param importName - name of the class/interface/static member being imported
     */
    public void setImportName(String importName) {
        this.importName = importName;
    }

    /**
     * Gets whether the imported entity is internal or external to the repository
     * @return  String -  return "internal" if the imported entity is internal to the repository, "external" otherwise
     */
    public String getImportType() {
        return importType;
    }

    /**
     * Sets whether the imported entity is internal or external to the repository
     * @param importType - return "internal" if the imported entity is internal to the repository, "external" otherwise
     */
    public void setImportType(String importType) {
        this.importType = importType;
    }

    /**
     * Gets whether the imported entity is a class, interface or static member
     * @return
     */
    public String getTypeElement() {
        return typeElement;
    }

    /**
     *   Sets whether the imported entity is a class, interface or static member
     * @param typeElement - return "class" if the imported entity is a class, "interface if interface, "static" if static member
     */
    public void setTypeElement(String typeElement) {
        this.typeElement = typeElement;
    }
}
