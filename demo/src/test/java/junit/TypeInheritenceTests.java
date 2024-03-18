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

public class TypeInheritenceTests {

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
        public void testGenericClass() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_inheritence_type_params_and_superclasses" + SEP + "GenericClass.java";

                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "GenericClass.json";
                analyse(inputPath, outputDir);

                JSONObject expectedObject = new JSONObject();
                expectedObject.put("file", new JSONObject()
                                .put("path",
                                                "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_inheritence_type_params_and_superclasses\\GenericClass.java")
                                .put("fileNameBase", "GenericClass")
                                .put("extension", "java"));
                expectedObject.put("dependencies", new JSONArray()
                                .put(new JSONObject()
                                                .put("from_package", "java.util")
                                                .put("import", "List")
                                                .put("type", "external")
                                                .put("type_element", "class/interface")));
                expectedObject.put("classes", new JSONObject()
                                .put("GenericClass", new JSONObject()
                                                .put("access_modifier", "public")
                                                .put("non_access_modifiers", new JSONArray().put("none"))
                                                .put("extend", new JSONArray().put("GenericClassParent<T,S>"))
                                                .put("implement", new JSONArray().put("GenericTypeInterface<T,S,A>"))
                                                .put("type_params", new JSONArray().put("T").put("S").put("A"))
                                                .put("min_max_lineno", new JSONObject()
                                                                .put("min_lineno", 5)
                                                                .put("max_lineno", 34))
                                                .put("methods", new JSONArray()
                                                                .put(new JSONObject()
                                                                                .put("GenericClass", new JSONObject()
                                                                                                .put("access_modifier",
                                                                                                                "public")
                                                                                                .put("non_access_modifiers",
                                                                                                                new JSONArray().put(
                                                                                                                                "none"))
                                                                                                .put("args", new JSONArray()
                                                                                                                .put("name")
                                                                                                                .put("age"))
                                                                                                .put("arg_types",
                                                                                                                new JSONObject()
                                                                                                                                .put("name", "String")
                                                                                                                                .put("age", "int"))
                                                                                                .put("return_type",
                                                                                                                "void")
                                                                                                .put("min_max_lineno",
                                                                                                                new JSONObject()
                                                                                                                                .put("min_lineno",
                                                                                                                                                9)
                                                                                                                                .put("max_lineno",
                                                                                                                                                12))))
                                                                .put(new JSONObject()
                                                                                .put("printAddress", new JSONObject()
                                                                                                .put("access_modifier",
                                                                                                                "public")
                                                                                                .put("non_access_modifiers",
                                                                                                                new JSONArray().put(
                                                                                                                                "none"))
                                                                                                .put("args", new JSONArray()
                                                                                                                .put("address"))
                                                                                                .put("arg_types",
                                                                                                                new JSONObject()
                                                                                                                                .put("address", "String"))
                                                                                                .put("return_type", "T")
                                                                                                .put("returns", new JSONArray()
                                                                                                                .put("t"))
                                                                                                .put("min_max_lineno",
                                                                                                                new JSONObject()
                                                                                                                                .put("min_lineno",
                                                                                                                                                14)
                                                                                                                                .put("max_lineno",
                                                                                                                                                18))))
                                                                .put(new JSONObject()
                                                                                .put("bat", new JSONObject()
                                                                                                .put("access_modifier",
                                                                                                                "public")
                                                                                                .put("non_access_modifiers",
                                                                                                                new JSONArray().put(
                                                                                                                                "none"))
                                                                                                .put("args",
                                                                                                                new JSONArray().put(
                                                                                                                                "name")
                                                                                                                                .put("list")
                                                                                                                                .put("age")
                                                                                                                                .put("breed"))
                                                                                                .put("arg_types",
                                                                                                                new JSONObject()
                                                                                                                                .put("name", "String")
                                                                                                                                .put("list", "List<String>")
                                                                                                                                .put("age", "int")
                                                                                                                                .put("breed", "String"))
                                                                                                .put("return_type", "T")
                                                                                                .put("returns", new JSONArray()
                                                                                                                .put("null"))
                                                                                                .put("min_max_lineno",
                                                                                                                new JSONObject()
                                                                                                                                .put("min_lineno",
                                                                                                                                                20)
                                                                                                                                .put("max_lineno",
                                                                                                                                                32)))))));

                checkSimilarity(expectedObject, outputFile);

        }

        @Test
        public void testGenericClassParent() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_inheritence_type_params_and_superclasses" + SEP + "GenericClassParent.java";

                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "GenericClassParent.json";
                analyse(inputPath, outputDir);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path",
                                                "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_inheritence_type_params_and_superclasses\\GenericClassParent.java")
                                .put("fileNameBase", "GenericClassParent")
                                .put("extension", "java"))
                                .put("dependencies", new JSONArray()
                                                .put(new JSONObject()
                                                                .put("from_package", "java.util")
                                                                .put("import", "List")
                                                                .put("type", "external")
                                                                .put("type_element", "class/interface")))
                                .put("classes", new JSONObject()
                                                .put("GenericClassParent", new JSONObject()
                                                                .put("access_modifier", "public")
                                                                .put("non_access_modifiers",
                                                                                new JSONArray().put("none"))

                                                                .put("type_params", new JSONArray().put("T").put("A"))
                                                                .put("min_max_lineno", new JSONObject()
                                                                                .put("min_lineno", 5)
                                                                                .put("max_lineno", 20))
                                                                .put("methods", new JSONArray()
                                                                                .put(new JSONObject()
                                                                                                .put("GenericClassParent",
                                                                                                                new JSONObject()
                                                                                                                                .put("access_modifier",
                                                                                                                                                "public")
                                                                                                                                .put("non_access_modifiers",
                                                                                                                                                new JSONArray().put(
                                                                                                                                                                "none"))
                                                                                                                                .put("args", new JSONArray()
                                                                                                                                                .put("name"))
                                                                                                                                .put("arg_types",
                                                                                                                                                new JSONObject().put(
                                                                                                                                                                "name",
                                                                                                                                                                "String"))
                                                                                                                                .put("return_type",
                                                                                                                                                "void")
                                                                                                                                .put("min_max_lineno",
                                                                                                                                                new JSONObject()
                                                                                                                                                                .put("min_lineno",
                                                                                                                                                                                9)
                                                                                                                                                                .put("max_lineno",
                                                                                                                                                                                11))))
                                                                                .put(new JSONObject()
                                                                                                .put("cat", new JSONObject()
                                                                                                                .put("access_modifier",
                                                                                                                                "public")
                                                                                                                .put("non_access_modifiers",
                                                                                                                                new JSONArray().put(
                                                                                                                                                "none"))
                                                                                                                .put("args",
                                                                                                                                new JSONArray().put(
                                                                                                                                                "name")
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
                                                                                                                                "void")
                                                                                                                .put("min_max_lineno",
                                                                                                                                new JSONObject()
                                                                                                                                                .put("min_lineno",
                                                                                                                                                                13)
                                                                                                                                                .put("max_lineno",
                                                                                                                                                                17)))))));

                checkSimilarity(jsonObject, outputFile);

        }

        @Test
        public void testGenericTypeInterface() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_inheritence_type_params_and_superclasses" + SEP + "GenericTypeInterface.java";

                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "GenericTypeInterface.json";
                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();

                JSONObject fileObject = new JSONObject();
                fileObject.put("path",
                                "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_inheritence_type_params_and_superclasses\\GenericTypeInterface.java");
                fileObject.put("fileNameBase", "GenericTypeInterface");
                fileObject.put("extension", "java");
                jsonObject.put("file", fileObject);

                JSONArray dependenciesArray = new JSONArray();
                JSONObject dependencyObject = new JSONObject();
                dependencyObject.put("from_package", "java.util");
                dependencyObject.put("import", "List");
                dependencyObject.put("type", "external");
                dependencyObject.put("type_element", "class/interface");

                dependenciesArray.put(dependencyObject);
                jsonObject.put("dependencies", dependenciesArray);

        
                JSONObject interfacesObject = new JSONObject();
                JSONObject genericTypeInterfaceObject = new JSONObject();
                genericTypeInterfaceObject.put("access_modifier", "public");
                genericTypeInterfaceObject.put("type_params", new JSONArray().put("T").put("S").put("A"));
           

                JSONArray methodsArray = new JSONArray();
                JSONObject batObject = new JSONObject();

                batObject.put("access_modifier", "default");
                batObject.put("non_access_modifiers", new JSONArray().put("abstract"));
                batObject.put("args", new JSONArray().put("name").put("list").put("age").put("breed"));
                JSONObject argTypesObject = new JSONObject();
                argTypesObject.put("name", "String");
                argTypesObject.put("list", "List<String>");
                argTypesObject.put("age", "int");
                argTypesObject.put("breed", "String");
                batObject.put("arg_types", argTypesObject);
                batObject.put("return_type", "T");
                JSONObject minMaxLinenoObject = new JSONObject();
                minMaxLinenoObject.put("min_lineno", 7);
                minMaxLinenoObject.put("max_lineno", 7);
                batObject.put("min_max_lineno", minMaxLinenoObject);
                JSONObject bat = new JSONObject();
                bat.put("bat", batObject);
                methodsArray.put(bat);
                JSONObject printAddressObject = new JSONObject();
                printAddressObject.put("access_modifier", "default");
                printAddressObject.put("non_access_modifiers", new JSONArray().put("abstract"));
                printAddressObject.put("args", new JSONArray().put("address"));
                JSONObject argTypesObject2 = new JSONObject();
                argTypesObject2.put("address", "String");
                printAddressObject.put("arg_types", argTypesObject2);
                printAddressObject.put("return_type", "T");
                JSONObject minMaxLinenoObject2 = new JSONObject();
                minMaxLinenoObject2.put("min_lineno", 8);
                minMaxLinenoObject2.put("max_lineno", 8);
                printAddressObject.put("min_max_lineno", minMaxLinenoObject2);
                JSONObject addr = new JSONObject();
                addr.put("printAddress", printAddressObject);
                methodsArray.put(addr);
                genericTypeInterfaceObject.put("methods", methodsArray);
                JSONObject minMaxLinenoObject3 = new JSONObject();
                minMaxLinenoObject3.put("min_lineno", 5);
                minMaxLinenoObject3.put("max_lineno", 9);
                genericTypeInterfaceObject.put("min_max_lineno", minMaxLinenoObject3);
                interfacesObject.put("GenericTypeInterface", genericTypeInterfaceObject);
                jsonObject.put("interfaces", interfacesObject);

                checkSimilarity(jsonObject, outputFile);

        }

        @Test
        public void testGenericTypeInterfaceExtendsInterface() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_inheritence_type_params_and_superclasses" + SEP
                                + "GenericTypeInterfaceExtendsInterface.java";

                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "GenericTypeInterfaceExtendsInterface.json";
                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                JSONObject fileObject = new JSONObject();
                fileObject.put("path",
                                "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_inheritence_type_params_and_superclasses\\GenericTypeInterfaceExtendsInterface.java");
                fileObject.put("fileNameBase", "GenericTypeInterfaceExtendsInterface");
                fileObject.put("extension", "java");
                jsonObject.put("file", fileObject);

                JSONArray dependenciesArray = new JSONArray();
                JSONObject dependencyObject = new JSONObject();
                dependencyObject.put("from_package", "java.util");
                dependencyObject.put("import", "List");
                dependencyObject.put("type", "external");
                dependencyObject.put("type_element", "class/interface");
                dependenciesArray.put(dependencyObject);
                jsonObject.put("dependencies", dependenciesArray);

                JSONObject interfacesObject = new JSONObject();
                JSONObject genericTypeInterfaceExtendsInterfaceObject = new JSONObject();
                genericTypeInterfaceExtendsInterfaceObject.put("access_modifier", "public");
                genericTypeInterfaceExtendsInterfaceObject.put("type_params",
                                new JSONArray().put("T").put("S").put("A"));
                genericTypeInterfaceExtendsInterfaceObject.put("extend", new JSONArray().put("GenericTypeInterface"));
                JSONArray methodsArray = new JSONArray();
                JSONObject methodObject = new JSONObject();
                methodObject.put("bat", new JSONObject()
                                .put("access_modifier", "default")
                                .put("non_access_modifiers", new JSONArray().put("abstract"))
                                .put("args", new JSONArray().put("name").put("list").put("age").put("breed"))
                                .put("arg_types", new JSONObject()
                                                .put("name", "String")
                                                .put("list", "List<String>")
                                                .put("age", "int")
                                                .put("breed", "String"))
                                .put("return_type", "T")
                                .put("min_max_lineno", new JSONObject()
                                                .put("min_lineno", 7)
                                                .put("max_lineno", 7))
                             );
                methodsArray.put(methodObject);
                genericTypeInterfaceExtendsInterfaceObject.put("methods", methodsArray);
                genericTypeInterfaceExtendsInterfaceObject.put("min_max_lineno", new JSONObject()
                                .put("min_lineno", 5)
                                .put("max_lineno", 8));
                interfacesObject.put("GenericTypeInterfaceExtendsInterface",
                                genericTypeInterfaceExtendsInterfaceObject);
                jsonObject.put("interfaces", interfacesObject);

                 checkSimilarity(jsonObject, outputFile);

        }

        @Test
        public void testInterfaceNestedInClass() {
                String inputPath = "src" + SEP + "test" + SEP + "java" + SEP + "test_files" + SEP
                                + "test_inheritence_type_params_and_superclasses" + SEP + "InterfaceNestedInClass.java";

                String outputDir = ((new File(inputPath)).getParentFile().getAbsolutePath()) + SEP + OUTPUTDIR_PATH;
                String outputFile = outputDir + SEP + "json_files" + SEP + "InterfaceNestedInClass.json";
                analyse(inputPath, outputDir);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file", new JSONObject()
                                .put("path",
                                                "C:\\Users\\nandi\\OneDrive\\Documents\\4th year\\CS4099 - Dissertation\\Dissertation\\inspect4j\\demo\\src\\test\\java\\test_files\\test_inheritence_type_params_and_superclasses\\InterfaceNestedInClass.java")
                                .put("fileNameBase", "InterfaceNestedInClass")
                                .put("extension", "java"))
                                .put("classes", new JSONObject()
                                                .put("InterfaceNestedInClass", new JSONObject()
                                                                .put("access_modifier", "public")
                                                                .put("non_access_modifiers",
                                                                                new JSONArray().put("none"))
                                                           
                                                                .put("min_max_lineno", new JSONObject()
                                                                                .put("min_lineno", 3)
                                                                                .put("max_lineno", 22))
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
                                                                                                                                                                15)
                                                                                                                                                .put("max_lineno",
                                                                                                                                                                19))
                                                                                                                .put("store_vars_calls",
                                                                                                                                new JSONObject()
                                                                                                                                                .put("nestObj", "InterfaceNestedInClass")
                                                                                                                                                .put("innerObj", "nestObj.InnerClass"))
                                                                                                               )))
                                                                .put("nested_interfaces", new JSONArray()
                                                                                .put(new JSONObject()
                                                                                                .put("nestedInterface",
                                                                                                                new JSONObject()
                                                                                                                                .put("access_modifier",
                                                                                                                                                "public")
                                                                                                                              
                                                                                                                                .put("methods", new JSONArray()
                                                                                                                                                .put(new JSONObject()
                                                                                                                                                                .put("innerMethod",
                                                                                                                                                                                new JSONObject()
                                                                                                                                                                                                .put("access_modifier",
                                                                                                                                                                                                                "default")
                                                                                                                                                                                                .put("non_access_modifiers",
                                                                                                                                                                                                                new JSONArray().put(
                                                                                                                                                                                                                                "abstract"))
                                                                                                                                                        
                                                                                                                                                                                                .put("return_type",
                                                                                                                                                                                                                "void")
                                                                                                                                                                                                .put("min_max_lineno",
                                                                                                                                                                                                                new JSONObject()
                                                                                                                                                                                                                                .put("min_lineno",
                                                                                                                                                                                                                                                6)
                                                                                                                                                                                                                                .put("max_lineno",
                                                                                                                                                                                                                                                6))
                                                                                                                                                                                       )))
                                                                                                                              
                                                                                                                                .put("min_max_lineno",
                                                                                                                                                new JSONObject()
                                                                                                                                                                .put("min_lineno",
                                                                                                                                                                                5)
                                                                                                                                                                .put("max_lineno",
                                                                                                                                                                                7)))))
                                                                .put("inner_classes", new JSONArray()
                                                                                .put(new JSONObject()
                                                                                                .put("InnerClass",
                                                                                                                new JSONObject()
                                                                                                                                .put("access_modifier",
                                                                                                                                                "public")
                                                                                                                                .put("non_access_modifiers",
                                                                                                                                                new JSONArray().put(
                                                                                                                                                                "none"))
                                                                                                                             
                                                                                                                                .put("implement",
                                                                                                                                                new JSONArray().put(
                                                                                                                                                                "nestedInterface"))
                                                                                                                              
                                                                                                                                .put("min_max_lineno",
                                                                                                                                                new JSONObject()
                                                                                                                                                                .put("min_lineno",
                                                                                                                                                                                9)
                                                                                                                                                                .put("max_lineno",
                                                                                                                                                                                14))
                                                                                                                           
                                                                                                                                .put("methods", new JSONArray()
                                                                                                                                                .put(new JSONObject()
                                                                                                                                                                .put("innerMethod",
                                                                                                                                                                                new JSONObject()
                                                                                                                                                                                                .put("access_modifier",
                                                                                                                                                                                                                "public")
                                                                                                                                                                                                .put("non_access_modifiers",
                                                                                                                                                                                                                new JSONArray().put(
                                                                                                                                                                                                                                "none"))
                                                                                                                                                                                             
                                                                                                                                                                                                .put("return_type",
                                                                                                                                                                                                                "void")
                                                                                                                                                                                                .put("min_max_lineno",
                                                                                                                                                                                                                new JSONObject()
                                                                                                                                                                                                                                .put("min_lineno",
                                                                                                                                                                                                                                                10)
                                                                                                                                                                                                                                .put("max_lineno",
                                                                                                                                                                                                                                                13))
                                                                                                                                                                                       )))
                                                                                                                               )))

                                                                ))
                                .put("interfaces", new JSONObject())
                                .put("main_info", new JSONObject()
                                                .put("main_flag", true)
                                                .put("main_method", "innerObj.innerMethod"));

                checkSimilarity(jsonObject, outputFile);

        }

}
