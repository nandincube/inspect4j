package uk.ac.st_andrews.inspect4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;


/**
 *  Command line interface for inspect4j
 *
 */
public class Cli {
    private String repositoryPath; // path of the file or repository to inspect
    private String outputDir; // output directory path to store results in json files
    private static final String OUTPUTDIR_PATH = "OutputDir"; // default output directory name
    public static final String SEP = FileSystems.getDefault().getSeparator(); // file separator

    /**
     * Main method
     * @param args - command line arguments
     * @throws Exception  - exception
     */
    public static void main(String[] args) throws Exception {

        if(args.length == 2){
            if (printRepositoryHierarchy(args)){
                return;
            }
        }
     
        if (args.length > 0) {
             String out = args.length == 1 ? OUTPUTDIR_PATH : args[1];
             Cli c = new Cli(args[0], out); // if more than 2 args are provided the additional args are ignored
             c.analyse();
        } else {
             System.out.println("Usage: java -jar demo" + SEP + "target" + SEP
                    + "inspect4j-1.0-jar-with-dependencies.jar <FILE.java | DIRECTORY> [OUTPUT_DIRECTORY]");
        }

    }

    /**
     *  Constructor
     * @param repositoryPath - input path of the file or directory to inspect
     * @param outputDir - output directory path to store results in json files
     */
    public Cli(String repositoryPath, String outputDir) {
        this.repositoryPath = repositoryPath;
        this.outputDir = outputDir;
    }

    /**
     * Print the hierarchy of the repository
     * @param repo - path of the repository
     */
    public static boolean printRepositoryHierarchy(String[] args){
        if(args[0].equals("-t") || args[0].equals("--tree")){
            File file = new File(args[1]);
            if (file.isDirectory()) {
                RepoHierarchy repoHierarchy = new RepoHierarchy(args[1]);
                repoHierarchy.buildDirectoryTree();
                repoHierarchy.printHierarchy();
            } else {
                System.out.println("Path provided is not a directory! ");
                System.exit(0);
            }
            return true;
        }else if(args[1].equals("-t") || args[1].equals("--tree")){

            File file = new File(args[0]);
            if (file.isDirectory()) {
                RepoHierarchy repoHierarchy = new RepoHierarchy(args[0]);
                repoHierarchy.buildDirectoryTree();
                repoHierarchy.printHierarchy();
            } else {
                System.out.println("Path provided is not a directory! ");
                System.exit(0);
            }
            return true;
        }
        return false;
        
    }

    /**
     *  Analyse the file or directory
     */
    public void analyse() {
        Path pathObj = null;
        try{
            pathObj = Paths.get(repositoryPath);
            
            if (Files.exists(pathObj)) {
                if (Files.isDirectory(pathObj)) {
                    analyseDirectory(repositoryPath, outputDir);
                } else {
                    String file = repositoryPath;
                    analyseFile(file, outputDir);
                }
                Requirements requirements = new Requirements(repositoryPath, outputDir);
                requirements.extractRequirements();

                System.out.println("Analysis completed! ");
            } else {
                System.out.println("Could not find source file/directory!");
                System.exit(0);
            }

        }catch(InvalidPathException e) {
            System.out.println("Invalid path provided! ");
            System.exit(0);
        }

    }

    /**
     *  Analyse the directory
     * @param dirPath - path of the directory that is to be analysed
     * @param outDir - output directory path of the json results
     */
    public void analyseDirectory(String dirPath, String outDir) {
        try {

            Path dirObj = Paths.get(dirPath);
            List<File> directories = Files.list(dirObj)
                    .map(Path::toFile)
                    .filter(File::isDirectory)
                    .collect(Collectors.toList());

            if (directories.size() > 0) {
                directories.forEach(x -> {
                    String outPath = outDir + SEP + x.getName();
                    String dir = dirPath + SEP + x.getName();
                    analyseDirectory(dir, outPath);
                });
            }

            List<File> files = Files.list(dirObj)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(x -> FilenameUtils.getExtension(x.getName()).equals("java"))
                    .collect(Collectors.toList());

            if (files.size() > 0) {
                files.forEach(x -> {
                    String filePath = x.getAbsolutePath();
                    analyseFile(filePath, outDir);
                });
            }
        } catch (IOException i) {
            System.out.println("Couldn't analyse this directory! " + i);
        }

    }

    /**
     *  Runs inspect4j on the file and writes the results in json file to the output directory
     * @param filePath - path of the file that is to be analysed
     * @param outDir - output directory path of the results
     */
    public void analyseFile(String filePath, String outDir) {

        if (filePath.length() > 0 && filePath != null) {
            if (FilenameUtils.getExtension(filePath).equals("java")) {
                AST ast = new AST(filePath, repositoryPath);
                if(ast.getAst() == null) {
                    System.out.println("Error! Could not parse file into AST!");
                    return;
                }
                ast.extractMetadata();
                ast.writeToJson(filePath, outDir);
                System.out.println("Data Extracted for file: " + filePath + ""+SEP);
            } else {
                System.out.println("File provided is not a java file");
            }
        }
    }

}
