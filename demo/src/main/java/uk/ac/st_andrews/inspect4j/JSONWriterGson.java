package uk.ac.st_andrews.inspect4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
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

    private FileInfo fileInfo;
    private final String FILE_SEPERATOR = FileSystems.getDefault().getSeparator();

    public JSONWriterGson(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public void write(String directory) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        addCustomSerialisers(gsonBuilder);

        Gson gson = gsonBuilder.create();
        String fileInfoAsJson = gson.toJson(fileInfo);
        String fileName = fileInfo.getFileNameBase() + ".json";

        try {

            String jsonDirPath = directory + FILE_SEPERATOR + "json_files"; // linux specific path syntax
            File dir = new File(jsonDirPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    System.out.println("Could not create output directories!");
                    return;
                }
            }

            String jsonFilePath = dir.getAbsolutePath() + FILE_SEPERATOR + fileName;

            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFilePath));
            writer.append(fileInfoAsJson);
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write to json!");
        }

    }

    private void addCustomSerialisers(GsonBuilder gb) {
        serialiseFile(gb);

        serialiseClass(gb);
        serialiseInterface(gb);

        serialiseMethod(gb);
        serialiseMainInfo(gb);

        serialiseLambda(gb);
        serialiseMethodReference(gb);
        // serialiseVariable(gb);
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
                        if (x.getClassCategory() == ClassInterfaceCategory.STANDARD) {
                            JsonElement cl = context.serialize(x, Class.class);
                            classCollectionJsonObject.add(x.getName(), cl);
                        }
                    });
                    jsonFile.add("classes", classCollectionJsonObject);
                }

                JsonObject interfaceCollectionJsonObject = new JsonObject();
                if (src.getInterfaces() != null) {
                    ArrayList<Interface> intfs = src.getInterfaces().getInterfaces();
                    intfs.forEach(x -> {
                        if (x.getInterfaceCategory() == ClassInterfaceCategory.STANDARD) {
                            JsonElement intf = context.serialize(x, Interface.class);
                            interfaceCollectionJsonObject.add(x.getName(), intf);
                        }
                    });
                    jsonFile.add("interfaces", interfaceCollectionJsonObject);
                }

                if (src.getMain().hasMain()) {
                    JsonElement mainElement = context.serialize(src.getMain(), MainInfo.class);
                    jsonFile.add("main_info", mainElement);
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

                JsonArray interfacesJsonArray = new JsonArray();
                for (Interface intf : src.getInterfaces()) {
                    JsonObject interfaceJsonObject = new JsonObject();
                    JsonElement interfaceElement = context.serialize(intf, Interface.class);
                    interfaceJsonObject.add(intf.getName(), interfaceElement);
                    interfacesJsonArray.add(interfaceJsonObject);
                }

                JsonArray innerClassJsonArray = new JsonArray();
                JsonArray staticNestedClassJsonArray = new JsonArray();
                for (Class cl : src.getClasses()) {
                    JsonObject nestedClassesJsonObject = new JsonObject();
                    if (cl.getClassCategory() == ClassInterfaceCategory.INNER) {
                        JsonElement classElement = context.serialize(cl, Class.class);
                        nestedClassesJsonObject.add(cl.getName(), classElement);
                        innerClassJsonArray.add(nestedClassesJsonObject);

                    } else if (cl.getClassCategory() == ClassInterfaceCategory.STATIC_NESTED) {
                        JsonElement classElement = context.serialize(cl, Class.class);
                        nestedClassesJsonObject.add(cl.getName(), classElement);
                        staticNestedClassJsonArray.add(nestedClassesJsonObject);
                    }
                }

                jsonDetails.addProperty("access_modifier", src.getAccessModifer().toString().toLowerCase());
                jsonDetails.addProperty("non_access_modifier", src.getNonAccessModifer().toString().toLowerCase());
                jsonDetails.add("extend", superJsonArray);
                jsonDetails.add("implement", implJsonArray);
                jsonDetails.add("type_params", typeParamsJsonArray);

                JsonObject lineNumbers = new JsonObject();
                lineNumbers.addProperty("min_lineno", src.getLineMin());
                lineNumbers.addProperty("max_lineno", src.getLineMax());
                jsonDetails.add("min_max_lineno", lineNumbers);

                jsonDetails.add("methods", methodsJsonArray);
                jsonDetails.add("nested_interfaces", interfacesJsonArray);
                jsonDetails.add("inner_classes", innerClassJsonArray);
                jsonDetails.add("static_nested_classes", staticNestedClassJsonArray);

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

                JsonArray returnStmtsJsonArray = new JsonArray();
                if (src.getReturnStmts() != null) {
                    src.getReturnStmts().forEach(x -> returnStmtsJsonArray.add(new JsonPrimitive(x.toString())));
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

                JsonArray referencesJsonArray = new JsonArray();
                if (src.getReferences() != null) {
                    for (MethodReference reference : src.getReferences()) {
                        JsonElement methodElement = context.serialize(reference, MethodReference.class);
                        referencesJsonArray.add(methodElement);

                    }
                }

                JsonArray localClassesJsonArray = new JsonArray();

                for (Class cl : src.getClasses()) {
                    // System.out.println("Class "+cl.getName()+ " is local:" +cl.isLocalClass());
                    if (cl.getClassCategory() == ClassInterfaceCategory.LOCAL) {
                        // System.out.println("Local Class - json: ");
                        JsonObject localClassesJsonObject = new JsonObject();
                        JsonElement methodElement = context.serialize(cl, Class.class);
                        localClassesJsonObject.add(cl.getName(), methodElement);
                        localClassesJsonArray.add(localClassesJsonObject);
                    }
                }

                jsonDetails.addProperty("doc", src.getJavaDoc());

                jsonDetails.addProperty("access_modifier", src.getAccessModifer().toString().toLowerCase());
                jsonDetails.addProperty("non_access_modifier", src.getNonAccessModifer().toString().toLowerCase());
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
                jsonDetails.add("method_references", referencesJsonArray);

                // jsonDetails.add("references", lambdasJsonArray);#
                jsonDetails.add("local_classes", localClassesJsonArray);
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

    private void serialiseMethodReference(GsonBuilder gb) {
        Type parameterType = new TypeToken<MethodReference>() {
        }.getType();

        JsonSerializer<MethodReference> serialiser = new JsonSerializer<MethodReference>() {
            @Override
            public JsonElement serialize(MethodReference src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonReference = new JsonObject();

                jsonReference.addProperty("containing_entity", src.getContainingEntity());
                jsonReference.addProperty("identifier", src.getIdentifier());

                return jsonReference;
            }
        };

        gb.registerTypeAdapter(parameterType, serialiser);

    }

    private void serialiseInterface(GsonBuilder gb) {
        Type parameterType = new TypeToken<Interface>() {
        }.getType();

        JsonSerializer<Interface> serialiser = new JsonSerializer<Interface>() {
            @Override
            public JsonElement serialize(Interface src, Type typeOfSrc, JsonSerializationContext context) {
                // JsonObject jsonInterface = new JsonObject();
                JsonObject jsonDetails = new JsonObject();

                jsonDetails.addProperty("doc", src.getJavaDoc());

                JsonArray methodsJsonArray = new JsonArray();
                for (Method md : src.getMethods()) {
                    JsonElement methodElement = context.serialize(md, Method.class);
                    methodsJsonArray.add(methodElement);
                }

                JsonArray interfacesJsonArray = new JsonArray();
                for (Interface intf : src.getInterfaces()) {
                    JsonObject interfaceCollectionJsonObject = new JsonObject();
                    JsonElement interfaceElement = context.serialize(intf, Interface.class);
                    interfaceCollectionJsonObject.add(intf.getName(), interfaceElement);
                    interfacesJsonArray.add(interfaceCollectionJsonObject);
                }

                JsonArray classesJsonArray = new JsonArray();
                for (Class cl : src.getClasses()) {
                    JsonObject classCollectionJsonObject = new JsonObject();
                    JsonElement classElement = context.serialize(cl, Class.class);
                    classCollectionJsonObject.add(cl.getName(), classElement);
                    classesJsonArray.add(classCollectionJsonObject);

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

                jsonDetails.addProperty("access_modifier", src.getAccessModifer().toString().toLowerCase());
                jsonDetails.add("type_params", typeParamsJsonArray);
                jsonDetails.add("extend", extendJsonArray);
                jsonDetails.add("methods", methodsJsonArray);
                jsonDetails.add("nested_interfaces", interfacesJsonArray);
                jsonDetails.add("nested_classes", classesJsonArray);

                JsonObject lineNumbers = new JsonObject();
                lineNumbers.addProperty("min_lineno", src.getLineMin());
                lineNumbers.addProperty("max_lineno", src.getLineMax());
                jsonDetails.add("min_max_lineno", lineNumbers);

                return jsonDetails;
            }

        };
        gb.registerTypeAdapter(parameterType, serialiser);

    }

    private void serialiseMainInfo(GsonBuilder gb) {
        Type parameterType = new TypeToken<MainInfo>() {
        }.getType();

        JsonSerializer<MainInfo> serialiser = new JsonSerializer<MainInfo>() {
            @Override
            public JsonElement serialize(MainInfo src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonMainInfo = new JsonObject();

                jsonMainInfo.addProperty("main_flag", src.hasMain());
                jsonMainInfo.addProperty("main_method", src.getMainMethod());

                return jsonMainInfo;
            }

        };
        gb.registerTypeAdapter(parameterType, serialiser);

    }

    // private void serialiseVariable(GsonBuilder gb) {
    // Type parameterType = new TypeToken<Variable>() {
    // }.getType();

    // JsonSerializer<Variable> serialiser = new JsonSerializer<Variable>() {
    // @Override
    // public JsonElement serialize(Variable src, Type typeOfSrc,
    // JsonSerializationContext context) {
    // JsonObject jsonVariable = new JsonObject();

    // jsonVariable.addProperty(src.getName(), src.getMethodCalled());

    // return jsonVariable;
    // }
    // };

    // gb.registerTypeAdapter(parameterType, serialiser);

    // }
}
