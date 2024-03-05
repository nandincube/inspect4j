package junit;

import static org.junit.Assert.assertEquals;
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
 * Unit test for basic class/interface with methods.
 */
public class DepDocTests {
        private static final String SEP = FileSystems.getDefault().getSeparator();
        private static final String OUTPUTDIR_PATH = "OutputDir";

        private void analyse(String path, String outdir) {
                AST ast = new AST(path, path);
                ast.extractMetadata();
                ast.writeToJson(path, outdir);

        }

        private void checkSimilarity(JSONObject expectedObject, String outputFile) {
                JSONObject actualObject = readJson(outputFile);
                assertTrue(expectedObject.similar(actualObject));
        }

        private JSONObject readJson(String outputFile) {

                String json = "";
                try {
                        File f = new File(outputFile);
                        if (f.exists())
                                System.out.println("File exists");
                        json = new String(Files.readAllBytes(Paths.get(outputFile)));
                } catch (IOException e) {
                        System.out.println("Could not read file: " + e);
                }
                return new JSONObject(json);

        }

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
                basicClassWithDependenciesObject.put("extend", new JSONArray());
                basicClassWithDependenciesObject.put("implement", new JSONArray());
                basicClassWithDependenciesObject.put("type_params", new JSONArray());
                basicClassWithDependenciesObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 7)
                                .put("max_lineno", 25));
                basicClassWithDependenciesObject.put("store_vars_calls", new JSONObject());

                JSONArray methodsArray = new JSONArray();
                JSONObject mainMethodObject = new JSONObject();
                JSONObject main = new JSONObject();
                mainMethodObject.put("access_modifier", "public");
                mainMethodObject.put("non_access_modifiers", new JSONArray().put("static"));
                mainMethodObject.put("args", new JSONArray().put("args"));
                mainMethodObject.put("arg_types", new JSONObject().put("args", "String[]"));
                mainMethodObject.put("return_type", "void");
                mainMethodObject.put("returns", new JSONArray());
                mainMethodObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 9)
                                .put("max_lineno", 12));
                mainMethodObject.put("store_vars_calls", new JSONObject());
                mainMethodObject.put("lambdas", new JSONArray());
                mainMethodObject.put("method_references", new JSONArray());
                mainMethodObject.put("local_classes", new JSONArray());
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
                batMethodObject.put("store_vars_calls", new JSONObject());
                batMethodObject.put("lambdas", new JSONArray());
                batMethodObject.put("method_references", new JSONArray());
                batMethodObject.put("local_classes", new JSONArray());
                bat.put("bat", batMethodObject);
                methodsArray.put(main);
                methodsArray.put(bat);

                basicClassWithDependenciesObject.put("methods", methodsArray);
                basicClassWithDependenciesObject.put("nested_interfaces", new JSONArray());
                basicClassWithDependenciesObject.put("inner_classes", new JSONArray());
                basicClassWithDependenciesObject.put("static_nested_classes", new JSONArray());

                classesObject.put("BasicClassWithDependencies", basicClassWithDependenciesObject);
                jsonObject.put("classes", classesObject);
                jsonObject.put("interfaces", new JSONObject());
                jsonObject.put("main_info", new JSONObject()
                                .put("main_flag", true)
                                .put("main_method", "System.out.println"));
                // System.out.println(jsonObject);

                // assertEquals(jsonObject.toString(), readJson(outputFile).toString());
                checkSimilarity(jsonObject, outputFile);

        }

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
                basicClassObject.put("extend", new JSONArray());
                basicClassObject.put("implement", new JSONArray());
                basicClassObject.put("type_params", new JSONArray());
                basicClassObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 7)
                                .put("max_lineno", 13));
                basicClassObject.put("store_vars_calls", new JSONObject());

                JSONArray methodsArray = new JSONArray();
                JSONObject mainMethodObject = new JSONObject();
                mainMethodObject.put("access_modifier", "public");
                mainMethodObject.put("non_access_modifiers", new JSONArray().put("static"));
                mainMethodObject.put("args", new JSONArray().put("args"));
                mainMethodObject.put("arg_types", new JSONObject().put("args", "String[]"));
                mainMethodObject.put("return_type", "void");
                mainMethodObject.put("returns", new JSONArray());
                mainMethodObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 8)
                                .put("max_lineno", 11));
                mainMethodObject.put("store_vars_calls", new JSONObject());
                mainMethodObject.put("lambdas", new JSONArray());
                mainMethodObject.put("method_references", new JSONArray());
                mainMethodObject.put("local_classes", new JSONArray());

                methodsArray.put(new JSONObject().put("main", mainMethodObject));

                basicClassObject.put("methods", methodsArray);
                basicClassObject.put("nested_interfaces", new JSONArray());
                basicClassObject.put("inner_classes", new JSONArray());
                basicClassObject.put("static_nested_classes", new JSONArray());

                classesObject.put("BasicClassWithDependenciesAsterisk", basicClassObject);
                jsonObject.put("classes", classesObject);
                jsonObject.put("interfaces", new JSONObject());
                jsonObject.put("main_info", new JSONObject()
                                .put("main_flag", true)
                                .put("main_method", "System.out.println"));

                checkSimilarity(jsonObject, outputFile);

        }

        @Test
        public void testBasicClassWithInternalDependencies() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                + "test_doc_and_dependencies" + SEP
                + "BasicClassWithInternalDependencies.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithInternalDependencies.json";
                String repoPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files";

                System.out.println("repoPath: " + repoPath);
                AST ast = new AST(inputPath,repoPath);
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
                        .put("import", "InterfaceNestedInClass")
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
                basicClassWithInternalDependenciesObject.put("extend", new JSONArray());
                basicClassWithInternalDependenciesObject.put("implement", new JSONArray());
                basicClassWithInternalDependenciesObject.put("type_params", new JSONArray());
                basicClassWithInternalDependenciesObject.put("min_max_lineno", new JSONObject()
                        .put("min_lineno", 6)
                        .put("max_lineno", 8));
                basicClassWithInternalDependenciesObject.put("store_vars_calls", new JSONObject());
                basicClassWithInternalDependenciesObject.put("methods", new JSONArray());
                basicClassWithInternalDependenciesObject.put("nested_interfaces", new JSONArray());
                basicClassWithInternalDependenciesObject.put("inner_classes", new JSONArray());
                basicClassWithInternalDependenciesObject.put("static_nested_classes", new JSONArray());

                classesObject.put("BasicClassWithInternalDependencies", basicClassWithInternalDependenciesObject);

                jsonObject.put("classes", classesObject);
                jsonObject.put("interfaces", new JSONObject());

                checkSimilarity(jsonObject, outputFile);

        }

        @Test
        public void testBasicClassWithJavaDoc() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                + "test_doc_and_dependencies" + SEP + "BasicClassWithJavaDoc.java";

                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithJavaDoc.json";
             
                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();


                checkSimilarity(jsonObject, outputFile);


        }

}
