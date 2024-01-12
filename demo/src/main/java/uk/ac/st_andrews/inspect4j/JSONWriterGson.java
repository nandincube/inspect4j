package uk.ac.st_andrews.inspect4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class JSONWriterGson {
    private MethodCollection methods;
    private InterfaceCollection interfaces;
    private ClassCollection classes;
    private VariableCollection variables;
    private FileInfo fileInfo;

    public JSONWriterGson(FileInfo fileInfo, MethodCollection mc, InterfaceCollection ic, ClassCollection cc,
            VariableCollection vc) {
        this.methods = mc;
        this.interfaces = ic;
        this.classes = cc;
        this.variables = vc;
        this.fileInfo = fileInfo;
    }

    public JSONWriterGson(FileInfo fileInfo) {
        this.methods = null;
        this.interfaces = null;
        this.classes = null;
        this.variables = null;
        this.fileInfo = fileInfo;
    }

    public JSONWriterGson(ClassCollection cc) {
        this.methods = null;
        this.interfaces = null;
        this.classes = cc;
        this.variables = null;
    }

    public JSONWriterGson(InterfaceCollection ic) {
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

    public void write(String directory) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        addCustomSerialisers(gsonBuilder);

        Gson gson = gsonBuilder.create();
        String fileInfoAsJson = gson.toJson(fileInfo);
        String fileName = fileInfo.getFileNameBase()+".json";
    
       


        try {

            String jsonDirPath = directory+"\\json_files";
            File dir = new File(jsonDirPath);
            if (!dir.exists()) {
                if(!dir.mkdirs()){
                    System.out.println("Could not create output directories!");
                    return;
                }
            }

            String jsonFilePath = dir.getAbsolutePath() +"\\"+fileName;

            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFilePath));
            writer.append(fileInfoAsJson);
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write to json!");
        }

    }

    public void writeInterfaces() {

        GsonBuilder gsonBuilder2 = new GsonBuilder();
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

    private void addCustomSerialisers(GsonBuilder gb) {
        serialiseMethod(gb);
        serialiseClass(gb);
        serialiseFile(gb);
        serialiseLambda(gb);
        serialiseInterface(gb);
        //serialiseVariable(gb)
        //serialiseMethodReference(gb)
    }

    private void serialiseFile(GsonBuilder gb) {
        Type parameterType = new TypeToken<FileInfo>() {
        }.getType();

        JsonSerializer<FileInfo> serialiser = new JsonSerializer<FileInfo>() {
            @Override
            public JsonElement serialize(FileInfo src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonFile = new JsonObject();
                JsonObject jsonDetails = new JsonObject();

                jsonDetails.addProperty("path", src.getPath());
                jsonDetails.addProperty("fileNameBase", src.getFileNameBase());
                jsonDetails.addProperty("extension", src.getExtension());
                jsonDetails.addProperty("doc", src.getJavaDoc());
                jsonFile.add("file", jsonDetails);

                JsonObject classCollectionJsonObject = new JsonObject();
                if (src.getClasses() != null) {
                    ArrayList<Class> cls = src.getClasses().getClasses();
                    cls.forEach(x -> {
                        JsonElement cl = context.serialize(x, Class.class);
                        classCollectionJsonObject.add(x.getName(), cl);

                    });
                    jsonFile.add("classes", classCollectionJsonObject);
                }

                JsonObject interfaceCollectionJsonObject = new JsonObject();
                if (src.getInterfaces() != null) {
                    ArrayList<Interface> intfs = src.getInterfaces().getInterfaces();
                    intfs.forEach(x -> {
                        JsonElement intf = context.serialize(x, Interface.class);
                        interfaceCollectionJsonObject.add(x.getName(), intf);
                    });
                    jsonFile.add("interfaces", interfaceCollectionJsonObject);
                }

                return jsonFile;
            }

        };
        gb.registerTypeAdapter(parameterType, serialiser);

    }

    private void serialiseClass(GsonBuilder gb) {
        Type parameterType = new TypeToken<Class>() {
        }.getType();

        JsonSerializer<Class> serialiser = new JsonSerializer<Class>() {
            @Override
            public JsonElement serialize(Class src, Type typeOfSrc, JsonSerializationContext context) {
                // JsonObject jsonClass = new JsonObject();
                JsonObject jsonDetails = new JsonObject();

                jsonDetails.addProperty("doc", src.getJavaDoc());

                JsonArray methodsJsonArray = new JsonArray();
                System.out.println("Methods size: " + src.getMethods().size());
                for (Method md : src.getMethods()) {
                    JsonElement methodElement = context.serialize(md, Method.class);
                    methodsJsonArray.add(methodElement);
                }

                JsonArray implJsonArray = new JsonArray();
                for (String impInt : src.getImplementedInterfaces()) {
                    implJsonArray.add(new JsonPrimitive(impInt.toString()));
                }

                JsonArray typeParamsJsonArray = new JsonArray();
                for (String type : src.getTypeParams()) {
                    typeParamsJsonArray.add(new JsonPrimitive(type.toString()));
                }

                JsonArray superJsonArray = new JsonArray();
                for (String superC : src.getSuperClasses()) {
                    superJsonArray.add(new JsonPrimitive(superC.toString()));
                }

                JsonArray classesJsonArray = new JsonArray();
                for (Class cl : src.getClasses()) {
                    JsonElement methodElement = context.serialize(cl, Class.class);
                    classesJsonArray.add(methodElement);
                }

                jsonDetails.add("methods", methodsJsonArray);
                jsonDetails.add("type_params", typeParamsJsonArray);
                jsonDetails.add("extend", superJsonArray);
                jsonDetails.add("implement", implJsonArray);

                jsonDetails.add("classes", classesJsonArray);

                JsonObject lineNumbers = new JsonObject();
                lineNumbers.addProperty("min_lineno", src.getLineMin());
                lineNumbers.addProperty("max_lineno", src.getLineMax());
                jsonDetails.add("min_max_lineno", lineNumbers);

                // jsonClass.add(src.getName(), jsonDetails);
                if (src.isInnerClass()) {
                    JsonObject jsonInnerClass = new JsonObject();
                    JsonObject detailsCopy = new JsonObject();
                    detailsCopy.add(src.getName(), jsonDetails);
                    jsonInnerClass.add("inner_classes", detailsCopy);
                    jsonDetails = jsonInnerClass;
                }

                if (src.isLocalClass()) {
                    JsonObject jsonLocalClass = new JsonObject();
                    JsonObject detailsCopy = new JsonObject();
                    detailsCopy.add(src.getName(), jsonDetails);
                    jsonLocalClass.add("local_classes", detailsCopy);
                    jsonDetails = jsonLocalClass;
                }
                return jsonDetails;
            }

        };
        gb.registerTypeAdapter(parameterType, serialiser);

    }

    private void serialiseMethod(GsonBuilder gb) {
        Type parameterType = new TypeToken<Method>() {
        }.getType();

        JsonSerializer<Method> serialiser = new JsonSerializer<Method>() {
            @Override
            public JsonElement serialize(Method src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonMethod = new JsonObject();
                JsonObject jsonDetails = new JsonObject();
                // jsonObject.addProperty("declarationAsString", src.getDeclarationAsString());

                JsonArray returnStmtsJsonArray = new JsonArray();
                if (src.getReturnStmts() != null) {
                    src.getReturnStmts().forEach(x -> returnStmtsJsonArray.add(new JsonPrimitive(x)));
                }

                JsonArray paramNamesJsonArray = new JsonArray();
                JsonObject paramTypesJsonObject = new JsonObject();
                if (src.getParams() != null) {
                    HashMap<String, String> params = src.getParams();
                    params.forEach((a, b) -> {
                        paramNamesJsonArray.add(new JsonPrimitive(a));
                        paramTypesJsonObject.addProperty(a, b);
                    });
                }

                JsonArray lambdasJsonArray = new JsonArray();
                if (src.getLambdas() != null) {
                    for (Lambda lambda : src.getLambdas()) {
                        JsonElement methodElement = context.serialize(lambda, Lambda.class);
                        lambdasJsonArray.add(methodElement);

                    }
                }

                JsonArray classesJsonArray = new JsonArray();
                for (Class cl : src.getClasses()) {
                    JsonElement methodElement = context.serialize(cl, Class.class);
                    classesJsonArray.add(methodElement);
                }

                jsonDetails.addProperty("doc", src.getJavaDoc());
                jsonDetails.add("args", paramNamesJsonArray);
                jsonDetails.add("arg_types", paramTypesJsonObject);
                jsonDetails.addProperty("return_type", src.getReturnType());
                jsonDetails.add("returns", returnStmtsJsonArray);

                JsonObject lineNumbers = new JsonObject();
                lineNumbers.addProperty("min_lineno", src.getLineMin());
                lineNumbers.addProperty("max_lineno", src.getLineMax());
                jsonDetails.add("min_max_lineno", lineNumbers);

                JsonObject jsonVariables = new JsonObject();
                src.getStoredVarCalls().stream()
                        .forEach(x -> jsonVariables.addProperty(x.getName(), x.getMethodCalled()));
                jsonDetails.add("store_vars_calls", jsonVariables);
                jsonDetails.add("lambdas", lambdasJsonArray);
                // jsonDetails.add("references", lambdasJsonArray);
                jsonDetails.add("classes", classesJsonArray);
                // jsonDetails.add("interfaces", intefacesJsonArray);

                jsonMethod.add(src.getName(), jsonDetails);

                return jsonMethod;
            }
        };

        gb.registerTypeAdapter(parameterType, serialiser);

    }

    private void serialiseLambda(GsonBuilder gb) {
        Type parameterType = new TypeToken<Lambda>() {
        }.getType();

        JsonSerializer<Lambda> serialiser = new JsonSerializer<Lambda>() {
            @Override
            public JsonElement serialize(Lambda src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonLambda = new JsonObject();
                // JsonObject jsonDetails = new JsonObject();

                // jsonObject.addProperty("declarationAsString", src.getDeclarationAsString());

                JsonArray returnStmtsJsonArray = new JsonArray();
                if (src.getReturnStmts() != null) {
                    src.getReturnStmts().forEach(x -> returnStmtsJsonArray.add(new JsonPrimitive(x)));
                }

                JsonArray paramNamesJsonArray = new JsonArray();
                JsonObject paramTypesJsonObject = new JsonObject();
                if (src.getParams() != null) {
                    HashMap<String, String> params = src.getParams();
                    params.forEach((a, b) -> {
                        paramNamesJsonArray.add(new JsonPrimitive(a));
                        paramTypesJsonObject.addProperty(a, b);
                    });
                }

                jsonLambda.add("args", paramNamesJsonArray);
                jsonLambda.add("arg_types", paramTypesJsonObject);
                jsonLambda.addProperty("body", src.getBodyAsString());
                jsonLambda.add("returns", returnStmtsJsonArray);

                JsonObject lineNumbers = new JsonObject();
                lineNumbers.addProperty("min_lineno", src.getLineMin());
                lineNumbers.addProperty("max_lineno", src.getLineMax());
                jsonLambda.add("min_max_lineno", lineNumbers);

                return jsonLambda;
            }
        };

        gb.registerTypeAdapter(parameterType, serialiser);

    }

    private void serialiseInterface(GsonBuilder gb) {
        Type parameterType = new TypeToken<Interface>() {}.getType();

        JsonSerializer<Interface> serialiser = new JsonSerializer<Interface>() {
            @Override
            public JsonElement serialize(Interface src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonInterface = new JsonObject();
                JsonObject jsonDetails = new JsonObject();

                jsonDetails.addProperty("doc", src.getJavaDoc());

                JsonArray methodsJsonArray = new JsonArray();
                for (Method md : src.getMethods()) {
                    JsonElement methodElement = context.serialize(md, Method.class);
                    methodsJsonArray.add(methodElement);
                }

                JsonArray typeParamsJsonArray = new JsonArray();
                if (src.getTypeParams() != null) {
                    for (String type : src.getTypeParams()) {
                        typeParamsJsonArray.add(new JsonPrimitive(type.toString()));
                    }
                }

                JsonArray extendJsonArray = new JsonArray();
                for (String impInt : src.getExtendedInterfaces()) {
                    extendJsonArray.add(new JsonPrimitive(impInt.toString()));
                }

                jsonDetails.add("type_params", typeParamsJsonArray);
                jsonDetails.add("extend", extendJsonArray);
                jsonDetails.add("methods", methodsJsonArray);

                JsonObject lineNumbers = new JsonObject();
                lineNumbers.addProperty("min_lineno", src.getLineMin());
                lineNumbers.addProperty("max_lineno", src.getLineMax());
                jsonDetails.add("min_max_lineno", lineNumbers);

                jsonInterface.add(src.getName(), jsonDetails);
                return jsonInterface;
            }

        };
        gb.registerTypeAdapter(parameterType, serialiser);

    }
}
