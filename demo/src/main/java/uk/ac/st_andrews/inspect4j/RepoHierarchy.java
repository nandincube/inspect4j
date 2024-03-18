package uk.ac.st_andrews.inspect4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Class representing the directory hierarchy of the repository
 */
public class RepoHierarchy {
    private Path repoPath; // path to the repository
    private static StringBuilder hierarchy; // the directory tree as a String

    public RepoHierarchy(String path) {
        this.repoPath = Path.of(path);
        hierarchy = new StringBuilder();
    }

    /**
     * Inner class representing a directory in the repository
     */
    private static class Directory {
        public int filesVisted; // number of files visited in the directory
        public int numberOfFiles; // number of files in the directory

        /**
         * Constructor
         * 
         * @param numFiles - number of files in the directory
         */
        public Directory(int numFiles) {
            this.filesVisted = 0;
            this.numberOfFiles = numFiles;
        }

        /***
         * Increment the number of files visited
         */
        public void incrementFilesVisted() {
            this.filesVisted++;
        }

        /**
         * Check if the current file is the last file in the directory
         * 
         * @return true if the file is the last file in the directory
         */
        public boolean isLastFile() {
            return this.filesVisted == this.numberOfFiles - 1;
        }
    }

    /**
     * Builds a string containing the directory tree
     * Code adapted from:
     * https://docs.oracle.com/javase/tutorial/essential/io/walk.html [Accessed on:
     * 26/02/2024]
     */
    public void buildDirectoryTree() {
        HashMap<String, Directory> directoryMap = new HashMap<>(); // map of directories in the repository
        try {
            Files.walkFileTree(repoPath, new SimpleFileVisitor<Path>() {
                int spaces = 0; // number of spaces to add to the hierarchy string

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    addSpaces();
                    int numberOfFiles = 0; // number of files in the directory
                    try {
                        numberOfFiles = Files.list(dir)
                                .filter(Files::isRegularFile)
                                .collect(Collectors.toList()).size();
                    } catch (IOException i) {
                        System.out.println("Couldn't print the directory tree! " + i);
                    }

                    directoryMap.put(dir.getFileName().toString(), new Directory(numberOfFiles)); // add the directory
                                                                                                  // to the map

                    if (spaces == 0) {
                        hierarchy.append(dir.getFileName() + "/" + "\n");
                    } else {
                        hierarchy.append("└──" + dir.getFileName() + "/" + "\n");

                    }

                    spaces += 2;
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    addSpaces();
                    String parentName = file.getParent().getFileName().toString();
                    if (directoryMap.get(parentName).isLastFile()) { // if the file is the last file in the directory
                        hierarchy.append("└──" + file.getFileName() + "\n");
                    } else {
                        hierarchy.append("├──" + file.getFileName() + "\n");
                        Directory directory = directoryMap.get(parentName);
                        directory.incrementFilesVisted();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    spaces -= 2;
                    return FileVisitResult.CONTINUE;
                }

                private void addSpaces() { // add spaces to the hierarchy string
                    for (int i = 0; i < spaces; i++) {
                        hierarchy.append("  ");
                    }
                }
            });
        } catch (IOException i) {
            System.out.println("Couldn't print the directory tree! " + i);
        }
    }

    /**
     * Print the directory hierarchy
     */
    public void printHierarchy() {
        System.out.println(hierarchy);
    }
}
