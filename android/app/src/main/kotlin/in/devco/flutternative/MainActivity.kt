package `in`.devco.flutternative

import androidx.annotation.NonNull
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "in.devco.flutternative/translate"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "translate") {
                val options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.getAllLanguages().random())
                        .build()
                val translator = Translation.getClient(options)
                lifecycle.addObserver(translator)

                val conditions = DownloadConditions.Builder()
                        .requireWifi()
                        .build()
                translator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener {
                            translator.translate(call.argument<String>("text").orEmpty())
                                    .addOnSuccessListener { translatedText ->
                                        result.success(translatedText)
                                    }
                                    .addOnFailureListener { exception ->
                                        result.error("UNAVAILABLE", exception.message, null)
                                    }
                        }
                        .addOnFailureListener { exception ->
                            result.error("UNAVAILABLE", exception.message, null)
                        }
            } else {
                result.notImplemented()
            }
        }
    }

}
