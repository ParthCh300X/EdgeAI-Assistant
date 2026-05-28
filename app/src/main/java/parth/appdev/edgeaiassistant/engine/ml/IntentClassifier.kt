package parth.appdev.edgeaiassistant.engine.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class IntentClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val tokenizer = TokenizerHelper(context)

    init {
        try {
            interpreter = Interpreter(loadModelFile())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fd     = context.assets.openFd("intent_model.tflite")
        val stream = FileInputStream(fd.fileDescriptor)
        return stream.channel.map(
            FileChannel.MapMode.READ_ONLY,
            fd.startOffset,
            fd.declaredLength
        )
    }

    fun predict(input: String): Pair<Int, Float> {
        val inputArray = Array(1) { tokenizer.textToSequence(input) }
        val output     = Array(1) { FloatArray(7) }   // 7 classes now

        return try {
            interpreter?.run(inputArray, output)
            val probs    = output[0]
            val maxIndex = probs.indices.maxByOrNull { probs[it] } ?: 2
            Pair(maxIndex, probs[maxIndex])
        } catch (e: Exception) {
            Pair(2, 0f)   // default to GENERAL (index 2) on error
        }
    }
}