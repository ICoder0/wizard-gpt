package com.github.icoder.wizardgpt.util

import java.util.*
import java.util.regex.Pattern
import kotlin.math.sqrt

object EmbeddingUtil {
    private val SPACE_REG = Pattern.compile("\\s+")

    fun similarity(k: Int, s1: String?, s2: String?): Double {
        return if (s1 == null) {
            throw NullPointerException("s1 must not be null")
        } else if (s2 == null) {
            throw NullPointerException("s2 must not be null")
        } else if (s1 == s2) {
            1.0
        } else if (s1.length >= 5 && s2.length >= 5) {
            val profile1: Map<String, Int> = getProfile(s1, k)
            val profile2: Map<String, Int> = getProfile(s2, k)
            dotProduct(profile1, profile2) / (norm(profile1) * norm(profile2))
        } else {
            0.0
        }
    }

    private fun getProfile(string: String, k: Int): Map<String, Int> {
        val shingles = HashMap<String, Int>()
        val stringNoSpace = SPACE_REG.matcher(string).replaceAll(" ")
        for (i in 0 until stringNoSpace.length - k + 1) {
            val shingle = stringNoSpace.substring(i, i + k)
            val old = shingles[shingle]
            if (old != null) {
                shingles[shingle] = old + 1
            } else {
                shingles[shingle] = 1
            }
        }
        return Collections.unmodifiableMap(shingles)
    }

    private fun norm(profile: Map<String, Int>): Double {
        var agg = 0.0
        var entry: Map.Entry<*, *>
        val var3: Iterator<*> = profile.entries.iterator()
        while (var3.hasNext()) {
            entry = var3.next() as Map.Entry<*, *>
            agg += 1.0 * (entry.value as Int).toDouble() * (entry.value as Int).toDouble()
        }
        return sqrt(agg)
    }

    private fun dotProduct(profile1: Map<String, Int>, profile2: Map<String, Int>): Double {
        var smallProfile = profile2
        var largeProfile = profile1
        if (profile1.size < profile2.size) {
            smallProfile = profile1
            largeProfile = profile2
        }

        var agg = 0.0
        val var6: Iterator<*> = smallProfile.entries.iterator()
        while (var6.hasNext()) {
            val (key, value) = var6.next() as Map.Entry<*, *>
            val i = largeProfile[key]
            if (i != null) {
                agg += 1.0 * (value as Int).toDouble() * i.toDouble()
            }
        }
        return agg
    }
}