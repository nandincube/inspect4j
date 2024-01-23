package uk.ac.st_andrews.inspect4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "inspect4j", version = "inspect4j 1.0", mixinStandardHelpOptions = true)

/**
 * 
 */

//public class Cli implements Runnable {
public class Cli {

    //private AST ast;
    @Option(names = { "-i", "--input_path" },type = String.class, required = true, description = "input path of the file or directory to inspect.")
    private String path;
    @Option(names = { "-o", "--output_path" }, type = String.class,  defaultValue = "OutputDir", description = "output directory path to store results. If the directory does not exist, the tool will create it")
    private String outputDir;

    private static final String FILE_PATH = "/home/nmn2/Documents/CS4099/Dissertation/inspect4j/inspect4j/demo/src/main/java/uk/ac/st_andrews/inspect4j/DummyFiles"; 
    private static final String OUTPUTDIR_PATH = "/home/nmn2/Documents/CS4099/Dissertation/inspect4j/inspect4j/OutputDir";
   
    @Option(names = { "--help" }, description = "Show this message and exit.")

    public static void main(String[] args) throws Exception {
        Cli c = new Cli(FILE_PATH, OUTPUTDIR_PATH);
    }


    /**
     * 
     * @param path
     * @param outputDir
     */
    public Cli(String path, String outputDir){
        this.path = path;
        this.outputDir = outputDir;
        analyse();
        System.out.println("Analysis completed! ");
       
    }
 
    /**
     * 
     */
    public void analyse(){
        Path pathObj = Paths.get(path);
        if(Files.exists(pathObj)){
            if(Files.isDirectory(pathObj)){
                analyseDirectory(path, outputDir);
            }else{
                analyseFile(path, outputDir);
            } 
        }else{
            System.out.println("Could not find source file/directory!");
        }
    }

    /**
     * 
     * @param dirPath
     * @param outDir
     */
    public void analyseDirectory(String dirPath, String outDir){
        try{

            Path dirObj =  Paths.get(dirPath);
            List<File> directories = Files.list(dirObj)
                .map(Path::toFile)
                .filter(File::isDirectory)
                .collect(Collectors.toList());

            if(directories.size() > 0){
                directories.forEach(x-> {
                    String outPath = outDir+"/"+x.getName();
                    String dir =  dirPath+"/"+x.getName();
                    
                    analyseDirectory(dir, outPath);
                });
            }
            
            List<File> files = Files.list(dirObj)
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toList());
            if(files.size() > 0){
                files.forEach(x-> {
                    String filePath = x.getAbsolutePath();
                    analyseFile(filePath, outDir);
                });
            }
        }catch(IOException i){
            System.out.println("Couldn't analyse this directory! "+ i);
        }

    }


    /**
     * 
     * @param filePath
     * @param outDir
     */
    public void analyseFile(String filePath, String outDir){
        if(filePath.length() > 0  && filePath != null){
            AST ast = new AST(filePath);
            ast.extractMetadata();
           // ast.printMetadata();
            ast.writeToJson(filePath, outDir);
            System.out.println("Data Extracted for file: " + filePath + "\n");
        } 
        

    }  

}
