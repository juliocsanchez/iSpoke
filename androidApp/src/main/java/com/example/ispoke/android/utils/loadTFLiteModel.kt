import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel

fun loadTFLiteModel(context: Context): Interpreter {
    val assetFileDescriptor = context.assets.openFd("modelo_libras.tflite")
    val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = assetFileDescriptor.startOffset
    val declaredLength = assetFileDescriptor.declaredLength
    val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    return Interpreter(modelBuffer, Interpreter.Options().apply {
        setNumThreads(4)
    })
}