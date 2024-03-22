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
 * Unit test for Basic Class/Interface features.
 */
public class BasicTests {
        private static final String SEP = FileSystems.getDefault().getSeparator();
        private static final String OUTPUTDIR_PATH = "OutputDir";

        /**
         * Analyse the file and write the output to a json file.
         * 
         * @param path   - path of the file to analyse
         * @param outdir - output directory path to store results in json files
         */
        private void analyse(String path, String outdir) {
                AST ast = new AST(path, path);
                ast.extractMetadata();
                ast.writeToJson(path, outdir);

        }

        /**
         * Check if the output json file is similar to the expected json object.
         * @param expectedObject - expected json object
         * @param outputFile - output json file
         */
        private void checkSimilarity(JSONObject expectedObject, String outputFile) {
                JSONObject actualObject = readJson(outputFile);
                assertTrue(expectedObject.similar(actualObject));
        }

        /**
         * Read the json file and return the json object.
         * @param outputFile - output json file
         * @return - json object
         */
        private JSONObject readJson(String outputFile) {

                String json = "";
                try {
                        File f = new File(outputFile);
                        json = new String(Files.readAllBytes(Paths.get(outputFile)));
                } catch (IOException e) {
                        System.out.println("Could not read file: " + e);
                }
                return new JSONObject(json);

        }

        /**
         * Test the basic class with main method.
         */
        @Test
        public void TestBasicClassWithMain() {

                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP + "test_basic" + SEP
                                + "BasicClassWithMain.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithMain.json";

                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path",
                                                "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_basic\\BasicClassWithMain.java")
                                .put("fileNameBase", "BasicClassWithMain")
                                .put("extension", "java"));
                jsonObject.put("classes", new JSONObject()
                                .put("BasicClassWithMain", new JSONObject()
                                                .put("access_modifier", "public")
                                                .put("non_access_modifiers", new JSONArray().put("none"))

                                                .put("min_max_lineno", new JSONObject()
                                                                .put("min_lineno", 3)
                                                                .put("max_lineno", 9))
                                                .put("methods", new JSONArray()
                                                                .put(new JSONObject()
                                                                                .put("main", new JSONObject()
                                                                                                .put("access_modifier",
                                                                                                                "public")
                                                                                                .put("non_access_modifiers",
                                                                                                                new JSONArray().put(
                                                                                                                                "static"))
                                                                                                .put("args", new JSONArray()
                                                                                                                .put("args"))
                                                                                                .put("arg_types",
                                                                                                                new JSONObject().put(
                                                                                                                                "args",
                                                                                                                                "String[]"))
                                                                                                .put("return_type",
                                                                                                                "void")
                                                                                                .put("min_max_lineno",
                                                                                                                new JSONObject()
                                                                                                                                .put("min_lineno",
                                                                                                                                                4)
                                                                                                                                .put("max_lineno",
                                                                                                                                                7))
                                                                                                .put("calls", new JSONArray()
                                                                                                                .put("System.out.println")))))));

                jsonObject.put("main_info", new JSONObject()
                                .put("main_flag", true)
                                .put("main_method", "System.out.println"));

                checkSimilarity(jsonObject, outputFile);

        }

        /**
         * Test the basic class with one method.
         */
        @Test
        public void TestBasicClassWithOneMethod() {

                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP + "test_basic" + SEP
                                + "BasicClassWithOneMethod.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithOneMethod.json";

                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path", "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_basic\\BasicClassWithOneMethod.java")
                                .put("fileNameBase", "BasicClassWithOneMethod")
                                .put("extension", "java"));

                JSONArray dependenciesArray = new JSONArray();
                JSONObject dependencyObject = new JSONObject()
                                .put("from_package", "java.util")
                                .put("import", "List")
                                .put("type", "external")
                                .put("type_element", "class/interface");
                dependenciesArray.put(dependencyObject);
                jsonObject.put("dependencies", dependenciesArray);

                JSONObject classesObject = new JSONObject();
                JSONObject basicClassObject = new JSONObject()
                                .put("access_modifier", "public")
                                .put("non_access_modifiers", new JSONArray().put("none"))
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 5)
                                                .put("max_lineno", 16))
                                .put("methods", new JSONArray()
                                                .put(new JSONObject()
                                                                .put("cat", new JSONObject()
                                                                                .put("access_modifier", "public")
                                                                                .put("non_access_modifiers",
                                                                                                new JSONArray().put(
                                                                                                                "none"))
                                                                                .put("args", new JSONArray()
                                                                                                .put("name")
                                                                                                .put("list")
                                                                                                .put("age")
                                                                                                .put("breed"))
                                                                                .put("arg_types", new JSONObject()
                                                                                                .put("name", "String")
                                                                                                .put("list", "List<String>")
                                                                                                .put("age", "int")
                                                                                                .put("breed", "String"))
                                                                                .put("return_type", "String")
                                                                                .put("returns", new JSONArray()
                                                                                                .put("abdbf"))
                                                                                .put("min_max_lineno", new JSONObject()
                                                                                                .put("min_lineno", 8)
                                                                                                .put("max_lineno",
                                                                                                                15))
                                                                                .put("calls", new JSONArray()
                                                                                                .put("System.out.println")
                                                                                                .put("System.out.println")
                                                                                                .put("System.out.println")))));
                classesObject.put("BasicClassWithOneMethod", basicClassObject);
                jsonObject.put("classes", classesObject);

                checkSimilarity(jsonObject, outputFile);
        }

        /**
         * Test the basic class with multiple methods.
         */
        @Test
        public void TestBasicClassWithMultipleMethods() {

                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP + "test_basic" + SEP
                                + "BasicClassWithMultipleMethods.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithMultipleMethods.json";

                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path", "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_basic\\BasicClassWithMultipleMethods.java")
                                .put("fileNameBase", "BasicClassWithMultipleMethods")
                                .put("extension", "java"));

                JSONArray dependencies = new JSONArray();
                JSONObject dependency = new JSONObject()
                                .put("from_package", "java.util")
                                .put("import", "List")
                                .put("type", "external")
                                .put("type_element", "class/interface");
                dependencies.put(dependency);
                jsonObject.put("dependencies", dependencies);

                JSONObject classes = new JSONObject();
                JSONObject basicClassWithMultipleMethods = new JSONObject()
                                .put("access_modifier", "public")
                                .put("non_access_modifiers", new JSONArray().put("none"))
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 5)
                                                .put("max_lineno", 50))
                                .put("store_vars_calls", new JSONObject()
                                                .put("dogName", "dogNameSetter"));

                JSONArray methods = new JSONArray();
                JSONObject mainMethod = new JSONObject()
                                .put("access_modifier", "public")
                                .put("non_access_modifiers", new JSONArray().put("static"))
                                .put("args", new JSONArray().put("args"))
                                .put("arg_types", new JSONObject().put("args", "String[]"))
                                .put("return_type", "void")
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 10)
                                                .put("max_lineno", 12))
                                .put("calls", new JSONArray()
                                                .put("System.out.println"));
                methods.put(new JSONObject().put("main", mainMethod));

                JSONObject basicClassWithMultipleMethodsMethod = new JSONObject()
                                .put("access_modifier", "public")
                                .put("non_access_modifiers", new JSONArray().put("none"))
                                .put("args", new JSONArray().put("catName"))
                                .put("arg_types", new JSONObject().put("catName", "String"))
                                .put("return_type", "void")
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 14)
                                                .put("max_lineno", 16));
                                        
                methods.put(new JSONObject().put("BasicClassWithMultipleMethods", basicClassWithMultipleMethodsMethod));

                JSONObject catMethod = new JSONObject()
                                .put("access_modifier", "public")
                                .put("non_access_modifiers", new JSONArray().put("none"))
                                .put("args", new JSONArray().put("name").put("list").put("age").put("breed"))
                                .put("arg_types", new JSONObject()
                                                .put("name", "String")
                                                .put("list", "List<String>")
                                                .put("age", "int")
                                                .put("breed", "String"))
                                .put("return_type", "String")
                                .put("returns", new JSONArray().put("abdbf"))
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 18)
                                                .put("max_lineno", 28))
                                .put("calls", new JSONArray()
                                                .put("System.out.println")
                                                .put("System.out.println")
                                                .put("System.out.println"))
                                              
                                .put("store_vars_calls", new JSONObject()
                                                .put("dog", "dogNameSetter")
                                                .put("dog2", "dogNameSetter"));
                methods.put(new JSONObject().put("cat", catMethod));

                JSONObject batMethod = new JSONObject()
                                .put("access_modifier", "public")
                                .put("non_access_modifiers", new JSONArray().put("static"))
                                .put("args", new JSONArray().put("name").put("age").put("breed"))
                                .put("arg_types", new JSONObject()
                                                .put("name", "String")
                                                .put("age", "int")
                                                .put("breed", "String"))
                                .put("return_type", "String")
                                .put("returns", new JSONArray().put("no").put("yes"))
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 30)
                                                .put("max_lineno", 40))
                                .put("calls", new JSONArray()
                                                .put("System.out.println")
                                                .put("System.out.println"));
                methods.put(new JSONObject().put("bat", batMethod));

                JSONObject matMethod = new JSONObject()
                                .put("access_modifier", "private")
                                .put("non_access_modifiers", new JSONArray().put("none"))
                                .put("args", new JSONArray().put("size").put("name"))
                                .put("arg_types", new JSONObject()
                                                .put("size", "int")
                                                .put("name", "String"))
                                .put("return_type", "void")
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 42)
                                                .put("max_lineno", 45))
                                .put("calls", new JSONArray()
                                                .put("System.out.println")
                                                .put("System.out.println"));
                methods.put(new JSONObject().put("mat", matMethod));

                JSONObject dogNameSetterMethod = new JSONObject()
                                .put("access_modifier", "private")
                                .put("non_access_modifiers", new JSONArray().put("none"))
                                .put("return_type", "String")
                                .put("returns", new JSONArray().put("Sam"))
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 47)
                                                .put("max_lineno", 49));
                methods.put(new JSONObject().put("dogNameSetter", dogNameSetterMethod));

                basicClassWithMultipleMethods.put("methods", methods);
                classes.put("BasicClassWithMultipleMethods", basicClassWithMultipleMethods);
                jsonObject.put("classes", classes);

                JSONObject mainInfo = new JSONObject()
                                .put("main_flag", true)
                                .put("main_method", "System.out.println");
                jsonObject.put("main_info", mainInfo);

                checkSimilarity(jsonObject, outputFile);

        }

        /**
         * Test the basic interface with methods.
         */
        @Test
        public void TestBasicInterfaceWithMethods() {

                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP + "test_basic" + SEP
                                + "BasicInterfaceWithMethods.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicInterfaceWithMethods.json";

                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path", "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_basic\\BasicInterfaceWithMethods.java")
                                .put("fileNameBase", "BasicInterfaceWithMethods")
                                .put("extension", "java"));

                jsonObject.put("dependencies", new JSONArray()
                                .put(new JSONObject()
                                                .put("from_package", "java.util")
                                                .put("import", "List")
                                                .put("type", "external")
                                                .put("type_element", "class/interface")));
                jsonObject.put("interfaces", new JSONObject()
                                .put("BasicInterfaceWithMethods", new JSONObject()
                                                .put("access_modifier", "public")
                                                .put("methods", new JSONArray()
                                                                .put(new JSONObject()
                                                                                .put("bat", new JSONObject()
                                                                                                .put("access_modifier",
                                                                                                                "default")
                                                                                                .put("non_access_modifiers",
                                                                                                                new JSONArray().put(
                                                                                                                                "abstract"))
                                                                                                .put("args", new JSONArray()
                                                                                                                .put("name")
                                                                                                                .put("list")
                                                                                                                .put("age")
                                                                                                                .put("breed"))
                                                                                                .put("arg_types",
                                                                                                                new JSONObject()
                                                                                                                                .put("name", "String")
                                                                                                                                .put("list", "List<String>")
                                                                                                                                .put("age", "int")
                                                                                                                                .put("breed", "String"))
                                                                                                .put("return_type",
                                                                                                                "String")
                                                                                                .put("min_max_lineno",
                                                                                                                new JSONObject()
                                                                                                                                .put("min_lineno",
                                                                                                                                                6)
                                                                                                                                .put("max_lineno",
                                                                                                                                                6))))
                                                                .put(new JSONObject()
                                                                                .put("printAddress", new JSONObject()
                                                                                                .put("access_modifier",
                                                                                                                "default")
                                                                                                .put("non_access_modifiers",
                                                                                                                new JSONArray().put(
                                                                                                                                "abstract"))
                                                                                                .put("args", new JSONArray()
                                                                                                                .put("address"))
                                                                                                .put("arg_types",
                                                                                                                new JSONObject()
                                                                                                                                .put("address", "String"))
                                                                                                .put("return_type",
                                                                                                                "String")
                                                                                                .put("min_max_lineno",
                                                                                                                new JSONObject()
                                                                                                                                .put("min_lineno",
                                                                                                                                                7)
                                                                                                                                .put("max_lineno",
                                                                                                                                                7))

                                                                                )))

                                                .put("min_max_lineno", new JSONObject()
                                                                .put("min_lineno", 5)
                                                                .put("max_lineno", 8))));

                checkSimilarity(jsonObject, outputFile);
        }

}
