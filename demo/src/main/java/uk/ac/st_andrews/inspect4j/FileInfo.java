package uk.ac.st_andrews.inspect4j;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
public class FileInfo {
    private String path;
    private String fileNameBase;
    private String extension;
    private ClassCollection classes;
    private InterfaceCollection interfaces;
    private String javaDoc;

    public FileInfo(String filePath, ClassCollection classes, InterfaceCollection interfaces) {
        File file = new File(filePath);
        this.path = file.getAbsolutePath();
        this.fileNameBase = extractFileName(file.getName());
        this.extension = extractExtensions(file.getName());
        this.classes = classes;
        this.interfaces = interfaces;
        this.javaDoc = getFileDoc();
    }

    public String extractFileName(String filename){

        //adapted from: https://www.baeldung.com/java-filename-without-extension [Accessed on: 08/01/24]
        if (filename == null || filename.isEmpty()) {
            return filename;
        }
    
        String extensions = "(?<!^)[.]" +  ".*";
        return filename.replaceAll(extensions, "");
        
        
    }

    private String getFileDoc(){
        Class publicClass = classes.getClasses().stream()
                                .filter(x -> !x.isInnerClass() && !x.isLocalClass())
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

    public String extractExtensions(String filename){
        return FilenameUtils.getExtension(filename);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileNameBase() {
        return fileNameBase;
    }

    public void setFileNameBase(String fileNameBase) {
        this.fileNameBase = fileNameBase;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public ClassCollection getClasses() {
        return classes;
    }

    public void setClasses(ClassCollection classes) {
        this.classes = classes;
    }
    
    @Override
    public String toString() {
        return "FileData [path=" + path + ", fileNameBase=" + fileNameBase + ", extension=" + extension + "]";
    }

    public String getJavaDoc() {
        return javaDoc;
    }

    public void setJavaDoc(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    public InterfaceCollection getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(InterfaceCollection interfaces) {
        this.interfaces = interfaces;
    }
  
}
