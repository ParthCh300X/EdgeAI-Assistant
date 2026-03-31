package parth.appdev.edgeaiassistant.engine.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class IntentClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null

    init {
        try {
            interpreter = Interpreter(loadModelFile())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("intent_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    private val tokenizer = TokenizerHelper(context)

    fun predict(input: String): Pair<Int, Float> {

        val inputArray = Array(1) { tokenizer.textToSequence(input) }
        val output = Array(1) { FloatArray(6) }

        return try {
            interpreter?.run(inputArray, output)

            val probs = output[0]
            val maxIndex = probs.indices.maxByOrNull { probs[it] } ?: 5
            val confidence = probs[maxIndex]

            Pair(maxIndex, confidence)

        } catch (e: Exception) {
            Pair(5, 0f)
        }
    }

    private fun fallbackPredict(text: String): Int {
        return when {
            text.contains("remind") -> 0
            text.contains("calculate") -> 1
            text.contains("convert") -> 2
            text.contains("note") -> 3
            text.contains("open") -> 4
            else -> 5
        }
    }
}