package com.playereden.gridhunt.core

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.security.MessageDigest

/**
 * Versioned fingerprint of authored chapter content. Not a project/replica ID,
 * signature, save format or authorization token. Artwork and external marker
 * rendering are outside this contract; their map revision must remain accurate.
 */
object ChapterManifest {
    const val schemaVersion = 1

    fun fingerprint(chapter: ExpeditionChapter): String {
        val bytes = ByteArrayOutputStream()
        DataOutputStream(bytes).use { out ->
            fun field(value: String) {
                val encoded = value.toByteArray(Charsets.UTF_8)
                out.writeInt(encoded.size)
                out.write(encoded)
            }
            fun optional(value: String?) {
                out.writeBoolean(value != null)
                if (value != null) field(value)
            }
            field("gridhunt-chapter-manifest")
            out.writeInt(schemaVersion)
            field(chapter.id)
            out.writeInt(chapter.revision)
            field(chapter.name)
            field(chapter.story)
            val map = chapter.map
            field(map.id)
            out.writeInt(map.revision)
            field(map.name)
            listOf(map.bounds.west,map.bounds.south,map.bounds.east,map.bounds.north)
                .forEach { out.writeDouble(it) }
            out.writeInt(map.landmarks.size)
            map.landmarks.forEach { landmark ->
                field(landmark.id)
                field(landmark.name)
                out.writeDouble(landmark.point.easting)
                out.writeDouble(landmark.point.northing)
                field(landmark.clue)
                field(landmark.glyph)
            }
            out.writeInt(chapter.objectives.size)
            chapter.objectives.forEach { objective ->
                field(objective.id)
                field(objective.verb.name)
                field(objective.title)
                field(objective.prompt)
                field(objective.expectedCell.reference)
                optional(objective.landmarkId)
                optional(objective.markerId)
                field(objective.discovery)
            }
        }
        return MessageDigest.getInstance("SHA-256").digest(bytes.toByteArray())
            .joinToString("") { (it.toInt() and 255).toString(16).padStart(2,'0') }
    }
}
