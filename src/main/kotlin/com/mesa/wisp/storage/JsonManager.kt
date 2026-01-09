package com.mesa.wisp.storage

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import java.io.File

class JsonManager(
    fileName: String
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val dataFile: File = FabricLoader.getInstance()
        .configDir
        .resolve(fileName)
        .toFile()

    private var rootData: MutableMap<String, Any> = mutableMapOf()
    private var loaded = false

    init {
        dataFile.parentFile?.mkdirs()
    }

    private fun ensureLoaded() {
        if (!loaded) {
            rootData = if (dataFile.exists()) {
                try {
                    dataFile.reader().use { reader ->
                        val type = object : TypeToken<MutableMap<String, Any>>() {}.type
                        gson.fromJson(reader, type) ?: mutableMapOf()
                    }
                } catch (e: Exception) {
                    println("Error loading data: ${e.message}")
                    mutableMapOf()
                }
            } else {
                mutableMapOf()
            }
            loaded = true
        }
    }

    fun <T> getNamespace(
        namespace: String,
        clazz: Class<T>,
        defaultValue: () -> T
    ): NamespaceHandle<T> {
        return NamespaceHandle(this, namespace, clazz, defaultValue)
    }

    internal fun <T> read(namespace: String, clazz: Class<T>, defaultValue: () -> T): T {
        ensureLoaded()
        val rawData = rootData[namespace]

        if (rawData == null) {
            // Namespace missing → use defaults
            val defaults = defaultValue()
            rootData[namespace] = defaults as Any // save to rootData
            save() // write defaults to file
            return defaults
        }

        return try {
            // Re-serialize to convert LinkedHashMap to proper type
            val json = gson.toJson(rawData)
            gson.fromJson(json, clazz) ?: defaultValue()
        } catch (e: Exception) {
            println("Error reading namespace $namespace: ${e.message}")
            defaultValue()
        }
    }


    internal fun <T> write(namespace: String, data: T) {
        ensureLoaded()
        rootData[namespace] = data as Any
        save()
    }

    internal fun <T> update(namespace: String, clazz: Class<T>, defaultValue: () -> T, block: T.() -> Unit) {
        val data = read(namespace, clazz, defaultValue)
        block(data)
        write(namespace, data)
    }

    private fun save() {
        try {
            dataFile.writer().use { writer ->
                gson.toJson(rootData, writer)
            }
        } catch (e: Exception) {
            println("Error saving data: ${e.message}")
        }
    }
}

class NamespaceHandle<T>(
    private val manager: JsonManager,
    private val namespace: String,
    private val clazz: Class<T>,
    private val defaultValue: () -> T
) {
    fun get(): T {
        return manager.read(namespace, clazz, defaultValue)
    }

    fun set(data: T) {
        manager.write(namespace, data)
    }

    fun update(block: T.() -> Unit) {
        manager.update(namespace, clazz, defaultValue, block)
    }
}