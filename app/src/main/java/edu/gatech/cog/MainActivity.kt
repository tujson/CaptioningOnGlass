package edu.gatech.cog

import android.app.Activity
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import com.google.api.gax.rpc.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.*
import com.google.protobuf.ByteString
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : Activity() {

    private val samplingRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
            samplingRate, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
    )
    private val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC, samplingRate,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize
    )
    private var audioData: ByteArray = ByteArray(bufferSize)
    private var isRecording = false
    private var recordingThread: Thread? = null
    private val isFirstRequest = AtomicBoolean(true)

    private val speechClient by lazy {
        // NOTE: The line below uses an embedded credential (res/raw/sa.json).
        //       You should not package a credential with real application.
        //       Instead, you should get a credential securely from a server.
        applicationContext.resources.openRawResource(R.raw.credential).use {
            SpeechClient.create(SpeechSettings.newBuilder()
                    .setCredentialsProvider { GoogleCredentials.fromStream(it) }
                    .build())
        }
    }
    private lateinit var requestStream: ClientStream<StreamingRecognizeRequest>

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_main)

        // start streaming the data to the server and collect responses
        requestStream = speechClient.streamingRecognizeCallable().splitCall(object : ResponseObserver<StreamingRecognizeResponse> {
            override fun onComplete() {
                Log.v("MainActivity", "speechClient: onComplete")

            }

            override fun onResponse(response: StreamingRecognizeResponse?) {
                Log.v("MainActivity", "speechClient: " + response?.getResults(0)?.getAlternatives(0)?.transcript)
            }

            override fun onError(t: Throwable?) {
                Log.e("MainActivity", "speechClient", t)
            }

            override fun onStart(controller: StreamController?) {
                Log.v("MainActivity", "speechClient: onStart")
            }

        })

        startRecording()
    }

    override fun onPause() {
        super.onPause()
        recordingThread?.interrupt()
    }

    private fun startRecording() {
        isRecording = true
        recorder.startRecording()

        recordingThread = Thread(Runnable {
            while (isRecording) {
                recorder.read(audioData, 0, bufferSize)

                process(audioData)

//                Thread.sleep(250)
            }
        })
        recordingThread?.start()
    }

    private fun process(byteArray: ByteArray) {
        val builder = StreamingRecognizeRequest.newBuilder()
                .setAudioContent(ByteString.copyFrom(byteArray))

        // if first time, include the config
        if (isFirstRequest.getAndSet(false)) {
            builder.streamingConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(RecognitionConfig.newBuilder()
                            .setLanguageCode("en-US")
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setSampleRateHertz(16000)
                            .build())
                    .setInterimResults(false)
                    .setSingleUtterance(false)
                    .build()
        }

        // send the next request
        requestStream.send(builder.build())
    }
}
