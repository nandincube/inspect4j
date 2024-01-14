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

//public class Cli implements Runnable {
public class Cli {

  
    //private AST ast;
    @Option(names = { "-i", "--input_path" },type = String.class, required = true, description = "input path of the file or directory to inspect.")
    private String path;
    @Option(names = { "-o", "--output_path" }, type = String.class,  defaultValue = "OutputDir", description = "output directory path to store results. If the directory does not exist, the tool will create it")
    private String outputDir;

    private static final String FILE_PATH = "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\main\\java\\uk\\ac\\st_andrews\\inspect4j\\DummyFiles\\DummyDir"; 
    private static final String OUTPUTDIR_PATH = "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\OutputDir";
   
    
    @Option(names = { "--help" }, description = "Show this message and exit.")
  

    // public static void main(String[] args) throws Exception {
    //     int exitCode = new CommandLine(new Cli()).execute(args); 
    //     System.exit(exitCode); 
    // }

    public static void main(String[] args) throws Exception {
        Cli c = new Cli(FILE_PATH, OUTPUTDIR_PATH);
    }

    //  @Override
    //  public void run() { 
    //       analyse();
    //  }

    public Cli(){
    }

    public Cli(String path, String outputDir){
        this.path = path;
        this.outputDir = outputDir;
        analyse();
       
    }
 
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
    public void analyseDirectory(String dirPath, String outDir){
        try{

            System.out.println("Processing... "+dirPath);
            System.out.println("Output Directory: "+outDir);
            Path dirObj =  Paths.get(dirPath);
            List<File> directories = Files.list(dirObj)
                .map(Path::toFile)
                .filter(File::isDirectory)
                .collect(Collectors.toList());

            if(directories.size() > 0){
                directories.forEach(x-> {
                    String outPath = outDir+"\\"+x.getName();
                    String dir =  dirPath+"\\"+x.getName();
                    
                    System.out.println("Output Directory in loop: "+outPath);
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


    public void analyseFile(String filePath, String outDir){
        if(filePath.length() > 0  && filePath != null){
            System.out.println("Processing... "+filePath);
            System.out.println("Output Directory: "+outDir);
            AST ast = new AST(filePath);
            ast.extractMetadata();
            ast.printMetadata();
            ast.writeToJson(filePath, outDir);
        } 
        

    }  

}
