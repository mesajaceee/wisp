package com.mesa.wisp.storage

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import java.io.File

/**
 * Manages a single shared JSON file with namespaced data
 */
class JsonPersistenceManager(
    fileName: String = "wisp/data.json"
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val dataFile: File = FabricLoader.getInstance()
        .configDir
        .resolve(fileName)
        .toFile()

    // In-memory cache of all data
    private var rootData: MutableMap<String, Any> = mutableMapOf()
    private var loaded = false

    init {
        dataFile.parentFile?.mkdirs()
    }

    /**
     * Load all data from file into memory
     */
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

    /**
     * Get a namespace handle for a specific data type
     */
    fun <T> getNamespace(
        namespace: String,
        clazz: Class<T>,
        defaultValue: () -> T
    ): NamespaceHandle<T> {
        return NamespaceHandle(this, namespace, clazz, defaultValue)
    }

    /**
     * Read data from a namespace
     */
    internal fun <T> read(namespace: String, clazz: Class<T>, defaultValue: () -> T): T {
        ensureLoaded()
        val rawData = rootData[namespace] ?: return defaultValue()

        return try {
            // Re-serialize to convert LinkedHashMap to proper type
            val json = gson.toJson(rawData)
            gson.fromJson(json, clazz) ?: defaultValue()
        } catch (e: Exception) {
            println("Error reading namespace $namespace: ${e.message}")
            defaultValue()
        }
    }

    /**
     * Write data to a namespace
     */
    internal fun <T> write(namespace: String, data: T) {
        ensureLoaded()
        rootData[namespace] = data as Any
        save()
    }

    /**
     * Update data in a namespace
     */
    internal fun <T> update(namespace: String, clazz: Class<T>, defaultValue: () -> T, block: T.() -> Unit) {
        val data = read(namespace, clazz, defaultValue)
        block(data)
        write(namespace, data)
    }

    /**
     * Save all data to file
     */
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

/**
 * Handle for accessing a specific namespace within the persistence manager
 */
class NamespaceHandle<T>(
    private val manager: JsonPersistenceManager,
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