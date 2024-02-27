package junit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import uk.ac.st_andrews.inspect4j.AST;

/**
 * Unit test for simple App.
 */
public class BasicTests{
    
    private static final String SEP = FileSystems.getDefault().getSeparator();
    private static final String OUTPUTDIR_PATH = "OutputDir";

    private JsonObject jsonFileToObject(String path){


        JsonObject obj = new JsonObject();
        Gson gson = new Gson();
        
        try {
            JsonReader reader = new JsonReader(new FileReader(path));
            return gson.fromJson(reader, JsonObject.class);
        } catch (FileNotFoundException e) {
           System.out.println("File not found");
        } catch (IOException i){
            System.out.println("Could not read in json file: "+i);
        }
        return null;

   }

    private void analyse(String path){
        AST ast = new AST(path,path);
        ast.extractMetadata();
        ast.writeToJson(path, OUTPUTDIR_PATH);
    }

    private String addQuotations(String str){
        return "\""+str+"\"";
    }

    private void testClass(JsonObject cl, String am,String nam, String[] exts,String[] tps, String[] impls, int min, int max){
        assertEquals(cl.get("access_modifier").toString(), addQuotations(am));
        assertEquals(cl.get("non_access_modifier").toString(), addQuotations(nam));

        JsonArray extend = cl.get("extend").getAsJsonArray();
        JsonArray impl = cl.get("implement").getAsJsonArray();
        JsonArray tParams = cl.get("type_params").getAsJsonArray();
        JsonObject lineRange = cl.get("min_max_lineno").getAsJsonObject();

        assertEquals(extend.size(), exts.length);
        for(int i = 0; i < exts.length; i++){
            assertEquals(addQuotations(exts[i]),extend.get(i).getAsString());
        }

        assertEquals(impl.size(),impls.length);
        for(int i = 0; i < impls.length; i++){
            assertEquals(addQuotations(impls[i]),impl.get(i).getAsString());
        }
        assertEquals(tParams.size(), tps.length);
        for(int i = 0; i < tps.length; i++){
            assertEquals(addQuotations(tps[i]),tParams.get(i).getAsString());
        }
        assertEquals(min, lineRange.get("min_lineno").getAsInt());
        assertEquals(max, lineRange.get("max_lineno").getAsInt());
    }

    private void testDependencies(JsonObject dep, String pkg, String imp, String type, String typeEl){
        assertEquals(pkg,dep.get("from_package").getAsString());
        assertEquals(imp,dep.get("import").getAsString());
        assertEquals(type, dep.get("type").getAsString());
        assertEquals(typeEl, dep.get("type_element").getAsString());
    }

    
    private void testMethod(JsonObject m, String am,String nam, String[] argsArr, HashMap<String,String> argsType, String returnType, String[] returns, int min, int max){
        assertEquals(m.get("access_modifier").toString(), addQuotations(am));
        assertEquals(m.get("non_access_modifier").toString(), addQuotations(nam));

        JsonArray args = m.get("args").getAsJsonArray();
        for(int i = 0; i < argsArr.length; i++){
            assertEquals(argsArr[i], args.get(i).getAsString());
        }
        JsonObject aTypes = m.get("arg_types").getAsJsonObject();
        for(Entry<String,JsonElement> at: aTypes.entrySet()){
            String k = at.getKey().replace("\"","");
            String v = at.getValue().getAsString().replace("\"","");
            assertTrue(argsType.containsKey(k));
            assertEquals(argsType.get(k),v );
        }

        assertEquals(returnType, m.get("return_type").getAsString());

        JsonArray rets = m.get("returns").getAsJsonArray();
        assertEquals(rets.size(), returns.length);
        for(int i = 0; i < returns.length; i++){
            assertEquals(addQuotations(returns[i]), rets.get(i).getAsString());
        }
        JsonObject lineRange = m.get("min_max_lineno").getAsJsonObject();
        assertEquals(min, lineRange.get("min_lineno").getAsInt());
        assertEquals(max,lineRange.get("max_lineno").getAsInt());
    }

    @Test
    public void TestBasicClassWithMain() {
        String inputPath = "src"+SEP+"test"+SEP+"java"+SEP+"test_files"+SEP+"test_basic"+SEP+"BasicClassWithMain.java";
        String outputFile = "OutputDir"+SEP+"json_files"+SEP+"BasicClassWithMain.json";
        analyse(inputPath);

        File input = new File(inputPath);

        JsonObject file = jsonFileToObject(outputFile);
        JsonObject fileInfo = file.get("file").getAsJsonObject();
        JsonObject classes = file.get("classes").getAsJsonObject();
        JsonArray dependencies = file.get("dependencies").getAsJsonArray();
        JsonObject intfs = file.get("interfaces").getAsJsonObject();
        JsonObject main = file.get("main_info").getAsJsonObject();

        assertEquals(addQuotations(input.getAbsolutePath()), fileInfo.get("path").toString().replace("\\\\","\\"));
        assertEquals(addQuotations("BasicClassWithMain"),fileInfo.get("fileNameBase").toString() );
        assertEquals( addQuotations("java"),fileInfo.get("extension").toString());

        assertTrue(dependencies.size() == 1);


        assertTrue(classes.size() == 1);
        assertTrue(classes.get("BasicClassWithMain") != null);

        JsonObject cl = classes.get("BasicClassWithMain").getAsJsonObject();
        String[] arr = new String[0];
        testClass(cl, "public","none", arr, arr, arr, 3, 9);

        JsonArray methods = cl.get("methods").getAsJsonArray();

        JsonObject mainMeth = methods.get(0).getAsJsonObject().get("main").getAsJsonObject();
        assertTrue(mainMeth != null);

        String[] args = new String[]{"args"};
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("args", "String[]");
        
        testMethod(mainMeth,"public", "static", args,map,  
        "void", arr, 4,7);

        assertTrue(intfs.isEmpty());

        assertTrue(main.get("main_flag").getAsBoolean() == true);
        assertEquals(addQuotations("println"),main.get("main_method").toString());

    }


    /**
     * Not finished need to check other methods in this file
     */
    @Test
    public void TestBasicClassWithMultipleMethods() {
        String inputPath = "src"+SEP+"test"+SEP+"java"+SEP+"test_files"+SEP+"test_basic"+SEP+"BasicClassWithMultipleMethods.java";
        String outputFile = "OutputDir"+SEP+"json_files"+SEP+"BasicClassWithMultipleMethods.json";
        analyse(inputPath);

        File input = new File(inputPath);

        JsonObject file = jsonFileToObject(outputFile);
        JsonObject fileInfo = file.get("file").getAsJsonObject();
        JsonObject classes = file.get("classes").getAsJsonObject();
        JsonArray dependencies = file.get("dependencies").getAsJsonArray();
        JsonObject intfs = file.get("interfaces").getAsJsonObject();
        JsonObject main = file.get("main_info").getAsJsonObject();

        assertEquals(addQuotations(input.getAbsolutePath()), fileInfo.get("path").toString().replace("\\\\","\\"));
        assertEquals(addQuotations("BasicClassWithMultipleMethods"),fileInfo.get("fileNameBase").toString() );
        assertEquals( addQuotations("java"),fileInfo.get("extension").toString());

        assertTrue(dependencies.size() == 1);
        testDependencies(dependencies.get(0).getAsJsonObject(),"java.util","List", "external","unknown");

        assertTrue(classes.size() == 1);
        assertTrue(classes.get("BasicClassWithMultipleMethods") != null);

        JsonObject cl = classes.get("BasicClassWithMultipleMethods").getAsJsonObject();
        String[] arr = new String[0];
        testClass(cl, "public","none", arr, arr, arr, 5, 42);

        JsonArray methods = cl.get("methods").getAsJsonArray();

        JsonObject meth = methods.get(0).getAsJsonObject().get("main").getAsJsonObject();
        assertTrue(meth != null);

        String[] args = new String[]{"args"};
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("args", "String[]");
        
        testMethod(meth,"public", "static", args,map,  
        "void", arr, 8,11);

        
        meth = methods.get(1).getAsJsonObject().get("BasicClassWithMultipleMethods").getAsJsonObject();
        assertTrue(meth != null);

        args = new String[]{"catName"};
        map = new HashMap<String,String>();
        map.put("catName", "String");
        
        testMethod(meth,"public", "none", args,map,  
        "void", arr, 13,15);

        meth = methods.get(2).getAsJsonObject().get("cat").getAsJsonObject();
        assertTrue(meth != null);

        args = new String[]{"name",
        "list",
        "age",
        "breed"};
        map = new HashMap<String,String>();
        map.put("name", "String");
        map.put("list", "List\u003cString\u003e");
        map.put("age", "int");
        map.put( "breed", "String");
        
        testMethod(meth,"public", "none", args,map,  
        "String", arr, 17,25);


        assertTrue(intfs.isEmpty());

        assertTrue(main.get("main_flag").getAsBoolean() == true);
        assertEquals(addQuotations("println"),main.get("main_method").toString());

    }


}
