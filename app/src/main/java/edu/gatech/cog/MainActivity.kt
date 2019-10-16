package edu.gatech.cog

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import edu.cmu.pocketsphinx.*
import java.io.File
import java.lang.Exception

class MainActivity : Activity() {

    private val DIGITS_SEARCH = "digits"
    private val PHONE_SEARCH = "phones"
    private var speechRecognizer: SpeechRecognizer? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_main)

        val assets = Assets(this)
        val assetsDir = assets.syncAssets()
        setupRecognizer(assetsDir)

    }

    private fun setupRecognizer(assetsDir: File) {
        speechRecognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(File(assetsDir, "en-us-ptm"))
                .setDictionary(File(assetsDir, "cmudict-en-us.dict"))
                .recognizer

        speechRecognizer?.addGrammarSearch(DIGITS_SEARCH, File(assetsDir, "digits.gram"))
        speechRecognizer?.addAllphoneSearch(PHONE_SEARCH, File(assetsDir, "en-phone.dmp"))

        speechRecognizer?.addListener(object: RecognitionListener{
            override fun onResult(p0: Hypothesis?) {
                Log.v("MainActivity", "Result ${p0?.hypstr}")
            }

            override fun onPartialResult(p0: Hypothesis?) {
                Log.v("MainActivity", "Partial ${p0?.hypstr}")
                runOnUiThread {
                    (findViewById(R.id.tvText) as TextView).text = p0?.hypstr
                }
            }

            override fun onTimeout() {
                Log.v("MainActivity", "Timeout")
            }

            override fun onBeginningOfSpeech() {
                Log.v("MainActivity", "BeginningOfSpeech")
            }

            override fun onEndOfSpeech() {
                Log.v("MainActivity", "EndOfSpeech")
            }

            override fun onError(p0: Exception?) {
                Log.e("MainActivity", "speechRecognizer", p0)
            }

        })

        speechRecognizer?.startListening("digits")
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.cancel()
        speechRecognizer?.shutdown()
    }
}
