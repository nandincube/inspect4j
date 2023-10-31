package uk.ac.st_andrews.inspect4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class AST {
    private CompilationUnit fullTree;

    public AST(String path){
        this.fullTree = parseFile(path);
    }

    private CompilationUnit parseFile(String path){
        try{
            return StaticJavaParser.parse(Files.newInputStream(Paths.get(path)));
        }
        catch(IOException e){
            System.out.println("Error! Could not read file: "+ e);
        }
        catch(InvalidPathException i){
            System.out.println("Error! Could not convert path into path object!");
        }
        catch(ParseProblemException ppe){
            System.out.println("Error! Could not parse file into AST!");;
        }

        return null;
    }

    public CompilationUnit getFullTree() {
        return fullTree;
    }

    public void setFullTree(CompilationUnit fullTree) {
        this.fullTree = fullTree;
    }
}