package uk.ac.st_andrews.inspect4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class JSONWriterGson  {
    private MethodCollection methods;
    private  InterfaceCollection interfaces;
    private ClassCollection classes;
    private  VariableCollection variables;
    
    public JSONWriterGson(MethodCollection mc, InterfaceCollection ic, ClassCollection cc, VariableCollection vc){
        this.methods = mc;
        this.interfaces = ic;
        this.classes = cc ;
        this.variables = vc;
    }

    public JSONWriterGson(ClassCollection cc){
        this.methods = null;
        this.interfaces = null;
        this.classes = cc ;
        this.variables = null;
    }

    public JSONWriterGson(InterfaceCollection ic){
        this.methods = null;
        this.interfaces = ic;
        this.classes = null;
        this.variables = null;

        Field nameField;
        try {
            nameField = Charset.class.getDeclaredField("name");
             nameField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }


    public void write(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        serialiseReturnStatements(gsonBuilder);
        serialiseParameters(gsonBuilder);

        Gson gson = gsonBuilder.create();  
        String classesAsJson = gson.toJson(classes); 

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.json"));
            writer.append(classesAsJson);
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write to json!");
        }

    }

    public void writeInterfaces(){

        GsonBuilder gsonBuilder2 = new GsonBuilder();
        serialiseReturnStatements(gsonBuilder2);
        serialiseParameters(gsonBuilder2);

        Gson gson2 = gsonBuilder2.create();  
        String interfacesAsJson = gson2.toJson(interfaces); 


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.json"));
            writer.append(interfacesAsJson);
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write to json!");
        }

    }

    private void serialiseReturnStatements(GsonBuilder gb){
        Type returnStmtType = new TypeToken<ReturnStmt>() {}.getType();  

        JsonSerializer<ReturnStmt> serializer = new JsonSerializer<ReturnStmt>() {  
        @Override
        public JsonElement serialize(ReturnStmt src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonReturn = new JsonObject();

                jsonReturn.addProperty("return", src.toString());

                return jsonReturn;
            }
        };

        gb.registerTypeAdapter(returnStmtType, serializer);

    }

    private void serialiseParameters(GsonBuilder gb){
        Type parameterType = new TypeToken<Parameter>() {}.getType();  

        JsonSerializer<Parameter> serializer = new JsonSerializer<Parameter>() {  
        @Override
        public JsonElement serialize(Parameter src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonReturn = new JsonObject();

                jsonReturn.addProperty("parameter", src.toString());

                return jsonReturn;
            }
        };

        gb.registerTypeAdapter(parameterType, serializer);

    }

}


