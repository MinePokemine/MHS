package com.minepokemine.mhs.savedata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.minepokemine.mhs.PluginMHS;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

public class KVDataStorage<K,V> {
    private HashMap<K, V> data = new HashMap<>();
    private boolean saved;
    private final String fileName;

    private final TypeToken mapType;

    @FunctionalInterface
    public interface GetSet<T> {
        T edit(T input);
    }

    public KVDataStorage(String fileName, Type keyType, Type valType) {
        this.data = new HashMap<>(); this.fileName = fileName;
        mapType = TypeToken.getParameterized(
            HashMap.class,
            keyType,
            valType
        );
    }

    public void add (K id, V val) {
        data.put(id, val);
        saved = false;
    }

    public void remove (K id) {
        data.remove(id);
    }

    public boolean set (K id, V newVal) {
        if (data.containsKey(id)) {
            data.replace(id, newVal);
            saved = false;
            return true;
        }
        return false;
    }

    public boolean getSet (K id, GetSet<V> func) {
        return set(id, func.edit(get(id)));
    }

    public boolean has (K id) {
        return data.containsKey(id);
    }

    public V get(K id) { return data.get(id); }

    public void save () throws IOException {
        Gson gson = new Gson();
        File file = new File(PluginMHS.instance.getDataFolder().getAbsoluteFile() + "/" + fileName + ".json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = new FileWriter(file, false);
        gson.toJson(data, writer);
        writer.flush();
        writer.close();
        saved = true;
        PluginMHS.instance.logger.info("Player Data Saved");
    }

    public void load () throws IOException {
        Gson gson = new Gson();
        File file = new File(PluginMHS.instance.getDataFolder().getAbsoluteFile() + "/" + fileName + ".json");
        if (file.exists()) {
            Reader reader = new FileReader(file);
            data = (HashMap<K, V>) gson.fromJson(reader, mapType);
            PluginMHS.instance.logger.info("Player Data Loaded");
        }
        saved = true;
    }

    public String getDataJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    public HashMap<K, V> getAll() { return data; }
    public boolean isSaved() { return saved; }
}
