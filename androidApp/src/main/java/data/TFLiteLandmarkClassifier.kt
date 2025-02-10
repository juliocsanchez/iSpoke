package data

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.example.ispoke.android.Classificator
import com.example.ispoke.android.LandmarkClassificator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class TFLiteLandmarkClassifier(
    private val context: Context,
    private val threshold: Float = 0.7f,
    private val maxResult : Int = 1
) : LandmarkClassificator {

    private var classifier: ImageClassifier ? = null

    private fun setupClassifier(){
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(1)
            .setScoreThreshold(threshold)
            .build()

        try {
            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "best_float32.tflite",
                options
            )
        }catch(e : IllegalStateException){
                e.printStackTrace()
        }
    }
    override fun classify(bitmap: Bitmap, rotation: Int): List<Classificator> {
        if (classifier == null){
            setupClassifier()
        }
        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrietationFromRotation(rotation))
            .build()

        val results =  classifier?.classify(tensorImage, imageProcessingOptions)

        return  results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Classificator(
                    category.displayName,
                    category.score
                )
            }
        }?.distinctBy { it.letter } ?: emptyList()
    }

    private fun getOrietationFromRotation(rotation: Int) : ImageProcessingOptions.Orientation{
         return when(rotation) {
             Surface.ROTATION_0 -> ImageProcessingOptions.Orientation.RIGHT_TOP
             Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
             Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
             else -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
         }
    }
}