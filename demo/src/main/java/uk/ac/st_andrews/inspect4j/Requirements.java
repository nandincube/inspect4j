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

import javax.management.modelmbean.XMLParseException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Requirements {
    private String repoPath;
    private String outpath;
    private StringBuilder requirements;
    private StringBuilder javaInfo;

    public Requirements(String repoPath, String outpath) {
        this.repoPath = repoPath;
        this.outpath = outpath;
        this.requirements = new StringBuilder();
        this.javaInfo = new StringBuilder();
    }

    public String getRepoPath() {
        return repoPath;
    }

    public String getOutpath() {
        return outpath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

    public void setOutpath(String outpath) {
        this.outpath = outpath;
    }

    public void extractRequirements(){
        try{
            List<File> pomFiles = Files.walk(Paths.get(repoPath))
            .filter(Files::isRegularFile)
            .filter(path -> path.getFileName().toString().equals("pom.xml"))
            .map(Path::toFile)
            .collect(Collectors.toList());
            if(pomFiles.size() > 0){
                for (File file : pomFiles) {
                    String filePath = file.getAbsoluteFile().toString();
                    System.out.println("Reading pom file: " + filePath);
                    readPom(filePath);
                }
                writeToRequirementsFile();
            }
        }catch(IOException e){
            System.out.println("Error while finding pom.xml files!");
        }
      
        

    }

    public void readPom(String filePath){
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
        
            model = reader.read(new FileReader(filePath));
            readPomJavaInfo(model);
            List<Dependency> dependencies = model.getDependencies();
            for (Dependency dependency : dependencies) {
                requirements.append(dependency.getGroupId() + ":" + dependency.getArtifactId() + "==" + dependency.getVersion() + "\n");
            }
            
        } catch (FileNotFoundException e) {
           System.out.println("pom.xml file not found!");
        }  catch (IOException e) {
            System.out.println("Issue reading pom file!");
        } catch(XmlPullParserException x){
            System.out.println("Error parsing pom file!");
        }
    }

    public void readPomJavaInfo(Model model){
        if(model.getProperties().getProperty("maven.compiler.source") != null ){
            javaInfo.append("Java Version: " + model.getProperties().getProperty("maven.compiler.source") + "\n");
           
        }

        if(model.getProperties().getProperty("maven.compiler.target") != null){
            javaInfo.append("Java Target Version: " + model.getProperties().getProperty("maven.compiler.target") + "\n");
        }
    }

    public void writeToRequirementsFile(){
        if(outpath != null && requirements != null){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outpath+"/requirements.txt"))) {
                writer.append(javaInfo);
                writer.append("\n");
                writer.append(requirements);
                writer.flush();
            } catch (IOException e) {
                System.out.println("Unable to write to requirements.txt file!");
            }
        }

    }

    public String getRequirements() {
        return requirements.toString();
    }
}
