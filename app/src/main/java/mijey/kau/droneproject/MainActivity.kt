package mijey.kau.droneproject

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var i: Intent
    lateinit var mRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // requestPermissions으로만 권한 받는건 API 23이상에서 가능
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)

        i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        record_button.setOnClickListener {
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            mRecognizer.setRecognitionListener(object: RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {}
                override fun onResults(results: Bundle?) {
                    if(results != null){
                        var key = SpeechRecognizer.RESULTS_RECOGNITION
                        val mResult = results.getStringArrayList(key)
                        val rs = Array<String>(mResult.size, {""})  // mResult.size 크기의 String 배열을 ""으로 초기화해서 생성
                        mResult.toArray(rs)
                        audio_result_text_view.text = rs[0]
                        command_result_text_view.text = "커맨드: " + textToDroneCommand(rs[0]).toString()
                    }
                }
            })
            mRecognizer.startListening(i)
        }
    }

    fun textToDroneCommand(text: String): Int{
        when(text){
            "오른쪽" -> return 0
            "왼쪽" -> return 1
            else -> return -1
        }
    }
}
