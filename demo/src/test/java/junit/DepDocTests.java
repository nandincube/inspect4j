package junit;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import uk.ac.st_andrews.inspect4j.AST;

/**
 * Unit test for dependencies and documentation extraction.
 */
public class DepDocTests {
        private static final String SEP = FileSystems.getDefault().getSeparator();
        private static final String OUTPUTDIR_PATH = "OutputDir";

        /**
         * Analyse the file and write the output to a json file
         * @param path - path of the file to analyse
         * @param outdir - output directory path to store results in json files
         */
        private void analyse(String path, String outdir) {
                AST ast = new AST(path, path);
                ast.extractMetadata();
                ast.writeToJson(path, outdir);

        }

        /**
         * Check if the expected json object is similar to the actual json object
         * @param expectedObject - expected json object
         * @param outputFile - path of the output json file
         */
        private void checkSimilarity(JSONObject expectedObject, String outputFile) {
                JSONObject actualObject = readJson(outputFile);
                assertTrue(expectedObject.similar(actualObject));
        }

        /**
         * Read the json file and return the json object
         * @param outputFile - path of the output json file
         * @return - json object
         */
        private JSONObject readJson(String outputFile) {

                String json = "";
                try {
                        json = new String(Files.readAllBytes(Paths.get(outputFile)));
                } catch (IOException e) {
                        System.out.println("Could not read file: " + e);
                }
                return new JSONObject(json);

        }

        /**
         * Test a basic class with with only external dependencies
         */
        @Test
        public void testBasicClassWithExternalDependencies() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_doc_and_dependencies" + SEP
                                + "BasicClassWithDependencies.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithDependencies.json";
                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path", "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_doc_and_dependencies\\BasicClassWithDependencies.java")
                                .put("fileNameBase", "BasicClassWithDependencies")
                                .put("extension", "java"));

                JSONArray dependenciesArray = new JSONArray();
                dependenciesArray.put(new JSONObject()
                                .put("from_package", "java.util")
                                .put("import", "List")
                                .put("type", "external")
                                .put("type_element", "class/interface"));
                dependenciesArray.put(new JSONObject()
                                .put("from_package", "java.security.interfaces")
                                .put("import", "DSAKey")
                                .put("type", "external")
                                .put("type_element", "class/interface"));
                dependenciesArray.put(new JSONObject()
                                .put("from_package", "java.lang.Math")
                                .put("import", "abs")
                                .put("type", "external")
                                .put("type_element", "static member"));

                jsonObject.put("dependencies", dependenciesArray);
                JSONObject classesObject = new JSONObject();
                JSONObject basicClassWithDependenciesObject = new JSONObject();
                basicClassWithDependenciesObject.put("access_modifier", "public");
                basicClassWithDependenciesObject.put("non_access_modifiers", new JSONArray().put("none"));
                basicClassWithDependenciesObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 7)
                                .put("max_lineno", 25));

                JSONArray methodsArray = new JSONArray();
                JSONObject mainMethodObject = new JSONObject();
                JSONObject main = new JSONObject();
                mainMethodObject.put("access_modifier", "public");
                mainMethodObject.put("non_access_modifiers", new JSONArray().put("static"));
                mainMethodObject.put("args", new JSONArray().put("args"));
                mainMethodObject.put("arg_types", new JSONObject().put("args", "String[]"));
                mainMethodObject.put("return_type", "void");

                mainMethodObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 9)
                                .put("max_lineno", 12));
                mainMethodObject.put("calls", new JSONArray().put("System.out.println"));

                main.put("main", mainMethodObject);

                JSONObject batMethodObject = new JSONObject();
                JSONObject bat = new JSONObject();
                batMethodObject.put("access_modifier", "public");
                batMethodObject.put("non_access_modifiers", new JSONArray().put("static").put("final"));
                batMethodObject.put("args", new JSONArray().put("name").put("age").put("breed"));
                batMethodObject.put("arg_types", new JSONObject()
                                .put("name", "String")
                                .put("age", "int")
                                .put("breed", "String"));
                batMethodObject.put("return_type", "String");
                batMethodObject.put("returns", new JSONArray().put("no").put("yes"));
                batMethodObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 14)
                                .put("max_lineno", 24));
                batMethodObject.put("calls", new JSONArray().put("System.out.println")
                                                .put("System.out.println"));

                bat.put("bat", batMethodObject);
                methodsArray.put(main);
                methodsArray.put(bat);

                basicClassWithDependenciesObject.put("methods", methodsArray);
                classesObject.put("BasicClassWithDependencies", basicClassWithDependenciesObject);
                jsonObject.put("classes", classesObject);
                jsonObject.put("main_info", new JSONObject()
                                .put("main_flag", true)
                                .put("main_method", "System.out.println"));

                checkSimilarity(jsonObject, outputFile);

        }

        /**
         * Test a basic class with wit external,  asterisks dependencies 
         */
        @Test
        public void testBasicClassWithExternalDependenciesAsterisks() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_doc_and_dependencies" + SEP
                                + "BasicClassWithDependenciesAsterisk.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithDependenciesAsterisk.json";
                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path", "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_doc_and_dependencies\\BasicClassWithDependenciesAsterisk.java")
                                .put("fileNameBase", "BasicClassWithDependenciesAsterisk")
                                .put("extension", "java"));

                JSONArray dependenciesArray = new JSONArray();
                dependenciesArray.put(new JSONObject()
                                .put("from_package", "org.junit.Assert")
                                .put("import", "*")
                                .put("type", "external")
                                .put("type_element", "static members"));

                dependenciesArray.put(new JSONObject()
                                .put("from_package", "java.util")
                                .put("import", "*")
                                .put("type", "external")
                                .put("type_element", "class/interface"));

                jsonObject.put("dependencies", dependenciesArray);

                JSONObject classesObject = new JSONObject();
                JSONObject basicClassObject = new JSONObject();
                basicClassObject.put("access_modifier", "public");
                basicClassObject.put("non_access_modifiers", new JSONArray().put("none"));

                basicClassObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 7)
                                .put("max_lineno", 13));

                JSONArray methodsArray = new JSONArray();
                JSONObject mainMethodObject = new JSONObject();
                mainMethodObject.put("access_modifier", "public");
                mainMethodObject.put("non_access_modifiers", new JSONArray().put("static"));
                mainMethodObject.put("args", new JSONArray().put("args"));
                mainMethodObject.put("arg_types", new JSONObject().put("args", "String[]"));
                mainMethodObject.put("return_type", "void");
                mainMethodObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 8)
                                .put("max_lineno", 11));
                mainMethodObject.put("calls", new JSONArray().put("System.out.println"));

                methodsArray.put(new JSONObject().put("main", mainMethodObject));

                basicClassObject.put("methods", methodsArray);

                classesObject.put("BasicClassWithDependenciesAsterisk", basicClassObject);
                jsonObject.put("classes", classesObject);
                jsonObject.put("main_info", new JSONObject()
                                .put("main_flag", true)
                                .put("main_method", "System.out.println"));

                checkSimilarity(jsonObject, outputFile);

        }

        /**
         * Test a basic class with internal dependencies
         */
        @Test
        public void testBasicClassWithInternalDependencies() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_doc_and_dependencies" + SEP
                                + "BasicClassWithInternalDependencies.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithInternalDependencies.json";
                String repoPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files";

                AST ast = new AST(inputPath, repoPath);
                ast.extractMetadata();
                ast.writeToJson(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path", "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_doc_and_dependencies\\BasicClassWithInternalDependencies.java")
                                .put("fileNameBase", "BasicClassWithInternalDependencies")
                                .put("extension", "java"));

                JSONArray dependenciesArray = new JSONArray();
                dependenciesArray.put(new JSONObject()
                                .put("from_package", "test_files.test_inheritence_type_params_and_superclasses")
                                .put("import", "GenericClass")
                                .put("type", "internal")
                                .put("type_element", "class"));
                dependenciesArray.put(new JSONObject()
                                .put("from_package", "test_files.test_inheritence_type_params_and_superclasses")
                                .put("import", "GenericClassParent")
                                .put("type", "internal")
                                .put("type_element", "class"));

                dependenciesArray.put(new JSONObject()
                                .put("from_package", "test_files.test_inheritence_type_params_and_superclasses")
                                .put("import", "GenericTypeInterface")
                                .put("type", "internal")
                                .put("type_element", "interface"));
                dependenciesArray.put(new JSONObject()
                                .put("from_package", "test_files.test_inheritence_type_params_and_superclasses")
                                .put("import", "GenericTypeInterfaceExtendsInterface")
                                .put("type", "internal")
                                .put("type_element", "interface"));
                dependenciesArray.put(new JSONObject()
                                .put("from_package", "test_files.test_nested_classes_interfaces")
                                .put("import", "BasicClassWithNestedClasses")
                                .put("type", "internal")
                                .put("type_element", "class"));

                jsonObject.put("dependencies", dependenciesArray);

                JSONObject classesObject = new JSONObject();
                JSONObject basicClassWithInternalDependenciesObject = new JSONObject();
                basicClassWithInternalDependenciesObject.put("access_modifier", "public");
                basicClassWithInternalDependenciesObject.put("non_access_modifiers", new JSONArray().put("none"));

                basicClassWithInternalDependenciesObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 6)
                                .put("max_lineno", 8));

                classesObject.put("BasicClassWithInternalDependencies", basicClassWithInternalDependenciesObject);

                jsonObject.put("classes", classesObject);

                checkSimilarity(jsonObject, outputFile);

        }

        /**
         * Test a basic class with java doc comments
         */
        @Test
        public void testBasicClassWithJavaDoc() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_doc_and_dependencies" + SEP + "BasicClassWithJavaDoc.java";

                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithJavaDoc.json";

                analyse(inputPath, outputDir);

 
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path", "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_doc_and_dependencies\\BasicClassWithJavaDoc.java")
                                .put("fileNameBase", "BasicClassWithJavaDoc")
                                .put("extension", "java")
                                .put("doc", "Class with java doc comments"));

                JSONArray dependenciesArray = new JSONArray();
                JSONObject dependency1 = new JSONObject()
                                .put("from_package", "java.lang.reflect")
                                .put("import", "Array")
                                .put("type", "external")
                                .put("type_element", "class/interface");
                dependenciesArray.put(dependency1);

                JSONObject dependency2 = new JSONObject()
                                .put("from_package", "java.util")
                                .put("import", "ArrayList")
                                .put("type", "external")
                                .put("type_element", "class/interface");
                dependenciesArray.put(dependency2);

                JSONObject dependency3 = new JSONObject()
                                .put("from_package", "java.util")
                                .put("import", "HashMap")
                                .put("type", "external")
                                .put("type_element", "class/interface");
                dependenciesArray.put(dependency3);

                jsonObject.put("dependencies", dependenciesArray);

                JSONObject classesObject = new JSONObject();
                JSONObject basicClassWithJavaDocObject = new JSONObject()
                                .put("doc", "Class with java doc comments")
                                .put("access_modifier", "public")
                                .put("non_access_modifiers", new JSONArray().put("none"))
                                .put("min_max_lineno", new JSONObject().put("min_lineno", 10).put("max_lineno", 56));
  
                JSONArray methodsArray = new JSONArray();
                JSONObject constructorObject = new JSONObject()
                                .put("BasicClassWithJavaDoc", new JSONObject()
                                                .put("doc", "Constructor. [tags = [@PARAM param  ]")
                                                .put("access_modifier", "public")
                                                .put("non_access_modifiers", new JSONArray().put("none"))
                                                .put("args", new JSONArray().put("catName"))
                                                .put("arg_types", new JSONObject().put("catName", "String"))
                                                .put("return_type", "void")
                                                .put("min_max_lineno",
                                                                new JSONObject().put("min_lineno", 17).put("max_lineno",
                                                                                19)));
                methodsArray.put(constructorObject);

                JSONObject catObject = new JSONObject()
                                .put("cat", new JSONObject()
                                                .put("doc", "Prints cat details. [tags = [@PARAM param - (int) cat name, @PARAM param - (int) cat age, @PARAM param - (String) cat breed, @PARAM param , @RETURN return  ]")
                                                .put("access_modifier", "public")
                                                .put("non_access_modifiers", new JSONArray().put("none"))
                                                .put("args", new JSONArray().put("name").put("age").put("breed"))
                                                .put("arg_types", new JSONObject()
                                                                .put("name", "String")
                                                                .put("age", "int")
                                                                .put("breed", "String"))
                                                .put("return_type", "String")
                                                .put("returns", new JSONArray().put("printed cat details"))
                                                .put("min_max_lineno",
                                                                new JSONObject().put("min_lineno", 29).put("max_lineno",
                                                                                35))
                                                .put("calls", new JSONArray().put("System.out.println")
                                                                                        .put("System.out.println")
                                                                                        .put("System.out.println")));
                methodsArray.put(catObject);

                JSONObject matObject = new JSONObject()
                                .put("mat", new JSONObject()
                                                .put("doc", "Prints mat details. [tags = [@PARAM param -  (String) mat product name, @PARAM param - (int) size of mat ]")
                                                .put("access_modifier", "private")
                                                .put("non_access_modifiers", new JSONArray().put("none"))
                                                .put("args", new JSONArray().put("size").put("name"))
                                                .put("arg_types", new JSONObject()
                                                                .put("size", "int")
                                                                .put("name", "String"))
                                                .put("return_type", "void")
                                                .put("min_max_lineno",
                                                                new JSONObject().put("min_lineno", 42).put("max_lineno",
                                                                                55))
                                                .put("calls", new JSONArray().put("System.out.println")
                                                        .put("System.out.println")
                                                        .put("list.add")
                                                        .put("list.forEach")
                                                        .put("System.out.println")
                                                        .put("list.forEach")
                                                        .put("System.out.println")
                                                )
                                                .put("store_vars_calls",
                                                                new JSONObject().put("list", "ArrayList<String>"))
                                                .put("lambdas", new JSONArray()
                                                                .put(new JSONObject()
                                                                        .put("args", new JSONArray().put("x"))
                                                                        .put("arg_types", new JSONObject().put("x", ""))
                                                                        .put("body","{\r\n    System.out.println(x);\r\n}")
                                                                     
                                                                        .put("min_max_lineno",
                                                                new JSONObject().put("min_lineno", 48).put("max_lineno",
                                                                                50)))
                                                                .put(new JSONObject()
                                                                        .put("args", new JSONArray().put("x"))
                                                                        .put("arg_types", new JSONObject().put("x", "Object"))
                                                                        .put("body","{\r\n    System.out.println(\"foo: \" + x);\r\n}")
                                                                      
                                                                        .put("min_max_lineno",
                                                                        new JSONObject().put("min_lineno", 52).put("max_lineno",
                                                                                        54)))
                                                                                
                                                                                
                                                                                ));
                methodsArray.put(matObject);

                basicClassWithJavaDocObject.put("methods", methodsArray);
                classesObject.put("BasicClassWithJavaDoc", basicClassWithJavaDocObject);
                jsonObject.put("classes", classesObject);


                checkSimilarity(jsonObject, outputFile);

        }

}
