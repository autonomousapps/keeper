package com.slack.keeper

import com.android.zipflinger.ZipArchive
import com.android.zipflinger.ZipSource
import java.io.File

/**
 * Returns a sequence of pairs representing the class files and their relative names for use in a
 * zip entry.
 */
internal fun File.classesSequence(): Sequence<Pair<String, File>> {
  val prefix = absolutePath
  return walkTopDown()
      .filter { it.extension == "class" }
      .filterNot { "META-INF" in it.name }
      .map { it.absolutePath.removePrefix(prefix).removePrefix("/") to it }
}

/**
 * Extracts classes from the target [jar] into this archive.
 */
internal fun ZipArchive.extractClassesFrom(jar: File) {
  val jarSource = ZipSource(jar)
  jarSource.entries()
      .filterNot { "META-INF" in it.key }
      .forEach { (name, entry) ->
        if (!entry.isDirectory && entry.name.endsWith(".class")) {
          val entryName = name.removePrefix(".")
          delete(entryName)
          jarSource.select(entryName, name)
        }
      }
  add(jarSource)
}