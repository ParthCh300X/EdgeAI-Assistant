package parth.appdev.edgeaiassistant.engine.ml

import android.content.Context
import android.util.Log
import org.json.JSONObject

class TokenizerHelper(context: Context) {

    private val wordIndex = mutableMapOf<String, Int>()

    init {
        try {
            val jsonString = context.assets.open("tokenizer.json")
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(jsonString)
            val config = json.getJSONObject("config")

            val wordIndexAny = config.get("word_index")

            val wordIndexJson = when (wordIndexAny) {
                is JSONObject -> wordIndexAny
                is String -> JSONObject(wordIndexAny)
                else -> JSONObject()
            }

            val keys = wordIndexJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                wordIndex[key] = wordIndexJson.getInt(key)
            }

            android.util.Log.d("TOKENIZER_SIZE", "Size: ${wordIndex.size}")

        } catch (e: Exception) {
            android.util.Log.e("TOKENIZER_ERROR", "Error loading tokenizer", e)
        }
    }

    fun textToSequence(text: String, maxLen: Int = 10): FloatArray {

        val cleaned = text
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), "")   // remove punctuation
            .trim()

        val words = cleaned.split(Regex("\\s+"))

        val sequence = FloatArray(maxLen)

        var index = 0
        for (word in words) {
            if (index >= maxLen) break
            sequence[index] = (wordIndex[word] ?: 0).toFloat()
            index++
        }

        return sequence
    }
}