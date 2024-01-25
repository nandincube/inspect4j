package uk.ac.st_andrews.inspect4j;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 */
public class FileInfo {
    private String path;
    private String fileNameBase;
    private String extension;
    private ClassCollection classes;
    private InterfaceCollection interfaces;
    private String javaDoc;
    private MainInfo main;

    /**
     * 
     * @param filePath
     * @param classes
     * @param interfaces
     * @param main
     */
    public FileInfo(String filePath, ClassCollection classes, InterfaceCollection interfaces, MainInfo main) {
        File file = new File(filePath);
        this.path = file.getAbsolutePath();
        this.fileNameBase = extractFileName(file.getName());
        this.extension = extractExtensions(file.getName());
        this.classes = classes;
        this.interfaces = interfaces;
        this.javaDoc = getFileDoc();
        this.main = main;

    }

    /**
     * 
     * @param filename
     * @return
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
     * 
     * @return
     */
    private String getFileDoc(){
        Class publicClass = classes.getClasses().stream()
                                .filter(x -> x.getClassCategory() == ClassInterfaceCategory.STANDARD &&
                                        (x.getAccessModifer() == AccessModifierType.PUBLIC || 
                                            x.getAccessModifer() == AccessModifierType.PROTECTED))
                                .findAny()
                                .orElse(null);
    

        if(publicClass != null) {
            return publicClass.getJavaDoc();
        }else{
            Interface publicInterface = interfaces.getInterfaces().stream()
                                            .findAny()
                                            .orElse(null);
            
            return publicInterface.getJavaDoc();                                             
        }

    }

    /**
     * 
     * @param filename
     * @return
     */
    public String extractExtensions(String filename){
        return FilenameUtils.getExtension(filename);
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
     * @return
     */
    public String getFileNameBase() {
        return fileNameBase;
    }

    /**
     * 
     * @param fileNameBase
     */
    public void setFileNameBase(String fileNameBase) {
        this.fileNameBase = fileNameBase;
    }

    /**
     * 
     * @return
     */
    public String getExtension() {
        return extension;
    }

    /**
     * 
     * @param extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * 
     * @return
     */
    public ClassCollection getClasses() {
        return classes;
    }

    /**
     * 
     * @param classes
     */
    public void setClasses(ClassCollection classes) {
        this.classes = classes;
    }
    
    /**
     * 
     */
    @Override
    public String toString() {
        return "FileData [path=" + path + ", fileNameBase=" + fileNameBase + ", extension=" + extension + "]";
    }

    /**
     * 
     * @return
     */
    public String getJavaDoc() {
        return javaDoc;
    }

    /**
     * 
     * @param javaDoc
     */
    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    /**
     * 
     * @return
     */
    public InterfaceCollection getInterfaces() {
        return interfaces;
    }

    /**
     * 
     * @param interfaces
     */
    public void setInterfaces(InterfaceCollection interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * 
     * @return
     */
    public MainInfo getMain() {
        return main;
    }

    /**
     * ss
     * @param main
     */
    public void setMain(MainInfo main) {
        this.main = main;
    }
  
}
