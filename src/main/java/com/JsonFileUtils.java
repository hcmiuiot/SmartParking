package com;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;

public final class JsonFileUtils<T> {
    final Class<T> typeClass;

    private static Gson gson = new Gson();

    public JsonFileUtils(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    public T load(String configFileName) {
        try {
            JsonReader reader = new JsonReader(new FileReader(configFileName));
            T loadedConfig = gson.fromJson(reader, typeClass.getClass());
            return loadedConfig;
        } catch (FileNotFoundException e) {
            System.err.println("Can not load json config file <"+configFileName+">");
            return null;
        }
    }

    public static void save(String configFileName, Object obj) {
        try {
            Writer writer = new FileWriter(configFileName);
//            System.out.println(gson.toJson(obj));
            gson.toJson(obj, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
