package org.simpleinvoice.server.util.yaml

import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule
import java.io.File

object YamlUtil {
    /**
     * Create properties from a YAML file
     *
     * @param path path to the YAML file
     * @return properties
     */
    inline fun <reified T> fromYaml(path: String): T =
        try {
            YAMLMapper
                .builder()
                .addModule(KotlinModule.Builder().build())
                .build()
                .readValue(File(path), T::class.java)
        } catch (e: Exception) {
            println("Illegal format for file: '$path'")
            e.printStackTrace()
            throw e
        }
}
