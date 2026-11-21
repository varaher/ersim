package com.example.engine

import android.content.Context

/** Loads every case JSON from assets/cases. The only Android-specific engine file. */
object CaseLibrary {
    private const val DIR = "cases"

    fun loadAll(context: Context): List<CaseDefinition> =
        (context.assets.list(DIR) ?: emptyArray())
            .filter { it.endsWith(".json") }
            .sorted()
            .map { name -> load(context, name) }

    fun load(context: Context, fileName: String): CaseDefinition =
        context.assets.open("$DIR/$fileName").bufferedReader(Charsets.UTF_8).use {
            CaseParser.parse(it.readText())
        }
}
