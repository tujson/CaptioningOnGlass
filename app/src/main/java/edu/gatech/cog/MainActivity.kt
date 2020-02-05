package edu.gatech.cog

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val speechSubscriptionKey = ""
    private val serviceRegion = ""
    private var speechRecognizer: SpeechRecognizer? = null

    private lateinit var adapter: TextAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val content = mutableListOf<String>()
        adapter = TextAdapter(content)
        rvText.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvText.layoutManager = layoutManager

        thread {
            val config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)
            speechRecognizer = SpeechRecognizer(config)
            speechRecognizer?.startContinuousRecognitionAsync()
            speechRecognizer?.recognizing?.addEventListener { _, speechRecognitionEventArgs ->
                Log.v(TAG, "Recognizing: ${speechRecognitionEventArgs.result.text}")
                runOnUiThread {
                    tvText.text = speechRecognitionEventArgs.result.text
                }
            }
            speechRecognizer?.recognized?.addEventListener { _, speechRecognitionEventArgs ->
                Log.v(TAG, "Recognized: ${speechRecognitionEventArgs.result.text}")
                content.add(speechRecognitionEventArgs.result.text)
                runOnUiThread {
                    tvText.text = ""
                    adapter.notifyItemInserted(content.size)
                    rvText.scrollToPosition(content.size - 1)

                    Log.v(TAG, "Transcript: $content")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        speechRecognizer?.startContinuousRecognitionAsync()
    }

    override fun onPause() {
        super.onPause()
        speechRecognizer?.close()
    }
}
