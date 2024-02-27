package uk.ac.st_andrews.inspect4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "inspect4j", version = "inspect4j 1.0", mixinStandardHelpOptions = true)

/**
 * 
 */

public class Cli {

    @Option(names = { "-i",
            "--input_path" }, type = String.class, required = true, description = "input path of the file or directory to inspect.")
    private String path;
    @Option(names = { "-o",
            "--output_path" }, type = String.class, defaultValue = "OutputDir", description = "output directory path to store results. If the directory does not exist, the tool will create it")
    private String outputDir;
    private static final String OUTPUTDIR_PATH = "OutputDir";
    public static String fileSeperator = FileSystems.getDefault().getSeparator();

    @Option(names = { "--help" }, description = "Show this message and exit.")

    public static void main(String[] args) throws Exception {
        // Cli c = new Cli("C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 -
        // Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\debugging",
        // OUTPUTDIR_PATH );
        // c.analyse();

        if (args.length > 0) {
            String out = args.length == 1 ? OUTPUTDIR_PATH : args[1];
            Cli c = new Cli(args[0], out); // if more than 2 args are provided the additional args are ignored
            c.analyse();
        } else {
            System.out.println("Usage: java -jar demo" + fileSeperator + "target" + fileSeperator
                    + "inspect4j-1.0-jar-with-dependencies.jar");
        }

    }

    /**
     * 
     * @param path
     * @param outputDir
     */
    public Cli(String path, String outputDir) {
        this.path = path;
        this.outputDir = outputDir;
    }

    /**
     * 
     */
    public void analyse() {
        Path pathObj = Paths.get(path);
        if (Files.exists(pathObj)) {
            if (Files.isDirectory(pathObj)) {
                analyseDirectory(path, outputDir);
            } else {
                analyseFile(path, outputDir);
            }

            System.out.println("Analysis completed! ");
        } else {
            System.out.println("Could not find source file/directory!");
            System.exit(0);
        }

    }

    /**
     * 
     * @param dirPath
     * @param outDir
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
                    String outPath = outDir + fileSeperator + x.getName();
                    String dir = dirPath + fileSeperator + x.getName();
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
     * 
     * @param filePath
     * @param outDir
     */
    public void analyseFile(String filePath, String outDir) {

        if (filePath.length() > 0 && filePath != null) {
            if (FilenameUtils.getExtension(filePath).equals("java")) {
                AST ast = new AST(filePath, path);
                ast.extractMetadata();
                ast.writeToJson(filePath, outDir);
                System.out.println("Data Extracted for file: " + filePath + "\n");
            } else {
                System.out.println("File provided is not a java file");
            }
        }
    }

}
