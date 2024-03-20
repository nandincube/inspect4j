package uk.ac.st_andrews.inspect4j;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * This class encapsulates all the information extracted about a file. 
 * This object contains all the information of a file including the classes, interfaces, dependencies and main method information.
 * In addition, it contains the file path, file name, file extension and the JavaDoc of the file.
 * 
 */
public class FileInfo {
    private String path; // file path
    private String fileNameBase; // file name without extension
    private String extension; // file extension
    private ClassCollection classes; // collection of classes in the file
    private InterfaceCollection interfaces; // collection of interfaces in the file
    private DependencyCollection dependencies; // collection of dependencies in the file    
    private String javaDoc; // JavaDoc of the file
    private MainInfo main; // main method information

    /**
     *  Constructor
     * @param filePath - path of the file
     * @param classes - collection of classes in the file 
     * @param interfaces - collection of interfaces in the file
     * @param main - main method information
     */
    public FileInfo(String filePath, ClassCollection classes, InterfaceCollection interfaces, MainInfo main, DependencyCollection dependencies) {
        File file = new File(filePath);
        this.path = file.getAbsolutePath();
        this.fileNameBase = extractFileName(file.getName());
        this.extension = extractExtensions(file.getName());
        this.classes = classes;
        this.interfaces = interfaces;
        this.dependencies = dependencies;
        this.javaDoc = getFileDoc();
        this.main = main;

    }

    /**
     *  Extracts the file name without the extension
     * @param filename - file name
     * @return - file name without extension
     */
    public String extractFileName(String filename){

        //adapted from: https://www.baeldung.com/java-filename-without-extension [Accessed on: 08/01/24]
        if (filename == null || filename.isEmpty()) {
            return filename;
        }
    
        String extensions = "(?<!^)[.]" +  ".*";
        return filename.replaceAll(extensions, "");
        
        
    }

    /**
     *  Extracts the JavaDoc of the file
     * @return - JavaDoc of the file
     */
    private String getFileDoc(){
        Class publicClass = classes.getClasses().stream()
                                .filter(x -> x.getClassCategory() == ClassInterfaceCategory.STANDARD &&
                                        (x.getAccessModifer() == AccessModifierType.PUBLIC || 
                                            x.getAccessModifer() == AccessModifierType.PROTECTED))
                                .findAny()
                                .orElse(null);
        if(publicClass == null){
            publicClass = classes.getClasses().stream()
                                .filter(x -> x.getClassCategory() == ClassInterfaceCategory.STANDARD &&
                                        (x.getAccessModifer() == AccessModifierType.DEFAULT))
                                .findAny()
                                .orElse(null);
        }
    

        if(publicClass != null) {
            
            return publicClass.getJavaDoc();
        }else{
            Interface publicInterface = interfaces.getInterfaces().stream()
                                            .findAny()
                                            .orElse(null);
            if(publicInterface == null) System.out.println("No public class or interface found in the file: " + fileNameBase);
            return publicInterface == null? "": publicInterface.getJavaDoc();                                             
        }

    }

    /**
     *  Extracts the file extension
     * @param filename - file name
     * @return - file extension
     */
    public String extractExtensions(String filename){
        return FilenameUtils.getExtension(filename);
    }

    /**
     *  Get the file path
     * @return - file path
     */ 
    public String getPath() {
        return path;
    }

    /**
     *  Set the file path
     * @param path - file path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     *  Get the file name without extension
     * @return - file name without extension
     */
    public String getFileNameBase() {
        return fileNameBase;
    }

    /**
     *  Set the file name without extension
     * @param fileNameBase - file name without extension
     */
    public void setFileNameBase(String fileNameBase) {
        this.fileNameBase = fileNameBase;
    }

    /**
     *  Get the file extension
     * @return - file extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     *  Set the file extension
     * @param extension - file extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     *  Get the collection of classes in the file
     * @return - collection of classes in the file
     */
    public ClassCollection getClasses() {
        return classes;
    }

    /**
     *  Set the collection of classes in the file
     * @param classes - collection of classes in the file
     */
    public void setClasses(ClassCollection classes) {
        this.classes = classes;
    }
    
    /**
     *  Get the string representation of the object
     */
    @Override
    public String toString() {
        return "FileData [path=" + path + ", fileNameBase=" + fileNameBase + ", extension=" + extension + "]";
    }

    /**
     *  Get the JavaDoc of the file
     * @return - JavaDoc of the file
     */
    public String getJavaDoc() {
        return javaDoc;
    }

    /**
     *  Set the JavaDoc of the file
     * @param javaDoc - JavaDoc of the file
     */
    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    /** 
     *  Get the collection of interfaces in the file
     * @return - collection of interfaces in the file
     */
    public InterfaceCollection getInterfaces() {
        return interfaces;
    }

    /**
     *  Set the collection of interfaces in the file
     * @param interfaces - collection of interfaces in the file
     */
    public void setInterfaces(InterfaceCollection interfaces) {
        this.interfaces = interfaces;
    }

    /**
     *  Get the main method information
     * @return - main method information
     */
    public MainInfo getMain() {
        return main;
    }

    /**
     *  Set the main method information
     * @param main - main method information
     */
    public void setMain(MainInfo main) {
        this.main = main;
    }

    /**
     *  Get the collection of dependencies in the file
     * @return - collection of dependencies in the file
     */
    public DependencyCollection getDependencies() {
        return dependencies;
    }

    /**
     * Set the collection of dependencies in the file
     * @param dependencies - collection of dependencies in the file
     */
    public void setDependencies(DependencyCollection dependencies) {
        this.dependencies = dependencies;
    }
  
}
