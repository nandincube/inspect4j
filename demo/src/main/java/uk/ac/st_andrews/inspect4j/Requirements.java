package uk.ac.st_andrews.inspect4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *  This class extracts the required libraries from the pom.xml files of a maven project
 */
public class Requirements {
    private String repoPath; // Path to the repository
    private String outpath; // Path to the output directory
    private StringBuilder requirements; // Requirements of the project
    private StringBuilder javaInfo; // Java version information

    /**
     * Constructor
     * 
     * @param repoPath - Path to the repository
     * @param outpath  - Path to the output directory
     */
    public Requirements(String repoPath, String outpath) {
        this.repoPath = repoPath;
        this.outpath = outpath;
        this.requirements = new StringBuilder();
        this.javaInfo = new StringBuilder();
    }

    /**
     * Get the path to the repository
     * 
     * @return - Path to the repository
     */
    public String getRepoPath() {
        return repoPath;
    }

    /**
     * Get the path to the output directory
     * 
     * @return - Path to the output directory
     */
    public String getOutpath() {
        return outpath;
    }

    /**
     * Set the path to the repository
     * 
     * @param repoPath - Path to the repository
     */
    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

    /**
     * Set the path to the output directory
     * 
     * @param outpath - Path to the output directory
     */
    public void setOutpath(String outpath) {
        this.outpath = outpath;
    }

    /**
     * Extract the requirements from the pom.xml files
     */
    public void extractRequirements() {
        try {
            List<File> pomFiles = Files.walk(Paths.get(repoPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals("pom.xml")) // Filter only pom.xml files
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            if (pomFiles.size() > 0) {
                for (File file : pomFiles) {
                    String filePath = file.getAbsoluteFile().toString();
                    readPom(filePath);
                }
                writeToRequirementsFile();
            }
        } catch (IOException e) {
            System.out.println("Error while finding pom.xml files!");
        }

    }

    /**
     * Read the pom.xml file and extract the dependencies and java information
     * @param filePath - Path to the pom.xml file
     */
    public void readPom(String filePath) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {

            model = reader.read(new FileReader(filePath));
            readPomJavaInfo(model);
            List<Dependency> dependencies = model.getDependencies(); // Get the maven dependencies
            for (Dependency dependency : dependencies) {
                requirements.append(dependency.getGroupId() + ":" + dependency.getArtifactId() + "=="
                        + dependency.getVersion() + "\n");
            }

        } catch (FileNotFoundException e) {
            System.out.println("pom.xml file not found!");
        } catch (IOException e) {
            System.out.println("Issue reading pom.xml file!");
        } catch (XmlPullParserException x) {
            System.out.println("Error parsing pom.xml file!");
        }
    }

    /**
     * Reads the java information from the pom.xml file
     * @param model - The model object of the pom.xml file
     */
    public void readPomJavaInfo(Model model) {
        if (model.getProperties().getProperty("maven.compiler.source") != null) { // Get the java source version
            javaInfo.append("Java Version: " + model.getProperties().getProperty("maven.compiler.source") + "\n");

        }

        if (model.getProperties().getProperty("maven.compiler.target") != null) { // Get the java target version
            javaInfo.append(
                    "Java Target Version: " + model.getProperties().getProperty("maven.compiler.target") + "\n");
        }
    }

    /**
     * Write the requirements and java information to a requirements.txt file
     */
    public void writeToRequirementsFile() {
        if (outpath != null && requirements != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outpath + "/requirements.txt"))) {
                writer.append(javaInfo);
                writer.append("\n");
                writer.append(requirements);
                writer.flush();
            } catch (IOException e) {
                System.out.println("Unable to write to requirements.txt file!");
            }
        }

    }
}
