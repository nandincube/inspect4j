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

public class Requirements {
    private String repoPath;
    private String outDir;
    private static final String SEP = FileSystems.getDefault().getSeparator();

    public Requirements(String repo, String outputDir){
        this.repoPath = repo;
        this.outDir = outputDir;

    }
    
    public void writeRequirementsTxt(){
        boolean foundPom = findPom(repoPath);
        System.out.println(foundPom);
        if(foundPom){
            generateRequirementsTxt();
        }
    }

    private boolean findPom(String dir){
        try {

            Path dirObj = Paths.get(dir);
        
            List<File> files = Files.list(dirObj)
            .map(Path::toFile)
            .filter(File::isFile)
            .filter(x -> FilenameUtils.getExtension(x.getName()).equals("xml"))
            .collect(Collectors.toList());
           
            if (files.size() > 0) {
                for(int i=0; i< files.size();i++){
                    System.out.println(files.get(i).getName());
                    if(files.get(i).getName().equals("pom.xml")){
                        return true;
                    }
                }
            }
            List<File> directories = Files.list(dirObj)
                            .map(Path::toFile)
                            .filter(File::isDirectory)
                            .collect(Collectors.toList());
            if (directories.size() > 0) {
                for(int i=0; i < directories.size();i++){
                   
                    boolean found = findPom(directories.get(i).getAbsolutePath());
                    if(found) return true;
                };
            }

           
        } catch (IOException i) {
            System.out.println("Couldn't analyse this directory! " + i);
        }
        return false;

    }

    private void generateRequirementsTxt(){
        try {
            String[] command = {"C:\\Program Files\\apache-maven-3.9.6\\bin", "dependency:list"};

            ProcessBuilder processBuilder = new ProcessBuilder(command);

            processBuilder.directory(new File(repoPath));
            processBuilder.redirectErrorStream(true);

            File out = new File(outDir);
            File outputFile = new File(outDir+SEP+"dependency-list.txt");

            if (!out.exists()) {
                if (!out.mkdirs()) {
                    System.out.println("Could not create output directories!");
                    return;
                }
            }

            if (!outputFile.exists()) {

                if (!outputFile.createNewFile()) {
                    System.out.println("Could not create output file!");
                    return;
                }
            }

            processBuilder.redirectOutput(outputFile);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.out.println("Could not get requirements of the project!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
