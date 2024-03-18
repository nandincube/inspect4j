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

public class NestedTests {
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
                     
                        json = new String(Files.readAllBytes(Paths.get(outputFile)));
                } catch (IOException e) {
                        System.out.println("Could not read file: " + e);
                }
                return new JSONObject(json);

        }

        @Test
        public void testBasicClassWithLocalAndDefaultClasses() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_nested_classes_interfaces" + SEP
                                + "BasicClassWithLocalDefaultClasses.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithLocalDefaultClasses.json";
                analyse(inputPath, outputDir);

                JSONObject expectedObject = new JSONObject();
                expectedObject.put("file", new JSONObject()
                                .put("path",
                                                "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_nested_classes_interfaces\\BasicClassWithLocalDefaultClasses.java")
                                .put("fileNameBase", "BasicClassWithLocalDefaultClasses")
                                .put("extension", "java"));
                expectedObject.put("classes", new JSONObject()
                                .put("BasicClassWithLocalDefaultClasses", new JSONObject()
                                                .put("access_modifier", "public")
                                                .put("non_access_modifiers", new JSONArray().put("none"))

                                                .put("min_max_lineno", new JSONObject()
                                                                .put("min_lineno", 3)
                                                                .put("max_lineno", 13))
                                                .put("methods", new JSONArray()
                                                                .put(new JSONObject()
                                                                                .put("main", new JSONObject()
                                                                                                .put("access_modifier",
                                                                                                                "protected")
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
                                                                                                                                                6)
                                                                                                                                .put("max_lineno",
                                                                                                                                                12))

                                                                                                .put("local_classes",
                                                                                                                new JSONArray()
                                                                                                                                .put(new JSONObject()
                                                                                                                                                .put("LocalClass",
                                                                                                                                                                new JSONObject()
                                                                                                                                                                                .put("access_modifier",
                                                                                                                                                                                                "default")
                                                                                                                                                                                .put("non_access_modifiers",
                                                                                                                                                                                                new JSONArray().put(
                                                                                                                                                                                                                "none"))

                                                                                                                                                                                .put("min_max_lineno",
                                                                                                                                                                                                new JSONObject()
                                                                                                                                                                                                                .put("min_lineno",
                                                                                                                                                                                                                                9)
                                                                                                                                                                                                                .put("max_lineno",
                                                                                                                                                                                                                                11)))))))))

                                .put("DefaultClass", new JSONObject()
                                                .put("access_modifier", "default")
                                                .put("non_access_modifiers", new JSONArray().put("none"))

                                                .put("min_max_lineno", new JSONObject()
                                                                .put("min_lineno", 15)
                                                                .put("max_lineno", 22))
                                                .put("methods", new JSONArray()
                                                                .put(new JSONObject()
                                                                                .put("hi", new JSONObject()
                                                                                                .put("access_modifier",
                                                                                                                "public")
                                                                                                .put("non_access_modifiers",
                                                                                                                new JSONArray().put(
                                                                                                                                "none"))
                                                                                                .put("args", new JSONArray()
                                                                                                                .put("arg"))
                                                                                                .put("arg_types",
                                                                                                                new JSONObject().put(
                                                                                                                                "arg",
                                                                                                                                "String"))
                                                                                                .put("return_type",
                                                                                                                "void")
                                                                                                .put("min_max_lineno",
                                                                                                                new JSONObject()
                                                                                                                                .put("min_lineno",
                                                                                                                                                18)
                                                                                                                                .put("max_lineno",
                                                                                                                                                20)))))));

                checkSimilarity(expectedObject, outputFile);
        }

        @Test
        public void testBasicClassWithNestedClasses() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_nested_classes_interfaces" + SEP
                                + "BasicClassWithNestedClasses.java";
                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "BasicClassWithNestedClasses.json";
                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                JSONObject fileObject = new JSONObject();
                fileObject.put("path",
                                "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_nested_classes_interfaces\\BasicClassWithNestedClasses.java");
                fileObject.put("fileNameBase", "BasicClassWithNestedClasses");
                fileObject.put("extension", "java");
                jsonObject.put("file", fileObject);

                JSONObject classesObject = new JSONObject();
                JSONObject basicClassObject = new JSONObject();
                basicClassObject.put("access_modifier", "public");
                basicClassObject.put("non_access_modifiers", new JSONArray().put("none"));
                basicClassObject.put("min_max_lineno", new JSONObject().put("min_lineno", 3).put("max_lineno", 18));

                JSONArray innerClassesArray = new JSONArray();
                JSONObject innerClassObject = new JSONObject();
                innerClassObject.put("access_modifier", "private");
                innerClassObject.put("non_access_modifiers", new JSONArray().put("none"));
                innerClassObject.put("min_max_lineno", new JSONObject().put("min_lineno", 14).put("max_lineno", 16));
                innerClassesArray.put(new JSONObject().put("InnerClass", innerClassObject));
                basicClassObject.put("inner_classes", innerClassesArray);

                JSONArray staticNestedClassesArray = new JSONArray();
                JSONObject nestedClassObject = new JSONObject();
                nestedClassObject.put("access_modifier", "private");
                nestedClassObject.put("non_access_modifiers", new JSONArray().put("static"));
                nestedClassObject.put("min_max_lineno", new JSONObject().put("min_lineno", 6).put("max_lineno", 12));

                JSONArray methodsArray = new JSONArray();
                JSONObject methodObject = new JSONObject();
                methodObject.put("hi", new JSONObject().put("access_modifier", "public")
                                .put("non_access_modifiers", new JSONArray().put("none"))
                                .put("args", new JSONArray().put("arg"))
                                .put("arg_types", new JSONObject().put("arg", "String")).put("return_type", "void")
                                .put("min_max_lineno", new JSONObject().put("min_lineno", 9).put("max_lineno", 11)));
                methodsArray.put(methodObject);

                nestedClassObject.put("methods", methodsArray);
                staticNestedClassesArray.put(new JSONObject().put("NestedClass", nestedClassObject));
                basicClassObject.put("static_nested_classes", staticNestedClassesArray);

                classesObject.put("BasicClassWithNestedClasses", basicClassObject);
                jsonObject.put("classes", classesObject);

          

                checkSimilarity(jsonObject, outputFile);

        }


}
