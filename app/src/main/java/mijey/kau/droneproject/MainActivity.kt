package mijey.kau.droneproject

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.content.Intent
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var i: Intent
    lateinit var mRecognizer: SpeechRecognizer
    var inGame = false
    var dontTouch = false
        set(value) {
            if(value) drone_image.setImageDrawable(resources.getDrawable(R.drawable.drone_pick_up))
            else drone_image.setImageDrawable(resources.getDrawable(R.drawable.drone))
            field = value
        }
    var isMoving = false
        set(value) {
            if(value) drone_image.setImageDrawable(resources.getDrawable(R.drawable.drone_move))
            else drone_image.setImageDrawable(resources.getDrawable(R.drawable.drone))
            field = value
        }
    var developMode = false
        set(value) {
            if (value) {
                // develop mode를 true로
                // 버튼 보이기
                num1_button.visibility = View.VISIBLE
                num2_button.visibility = View.VISIBLE
                num3_button.visibility = View.VISIBLE
                num4_button.visibility = View.VISIBLE
                num5_button.visibility = View.VISIBLE
                num6_button.visibility = View.VISIBLE
                num7_button.visibility = View.VISIBLE
                num8_button.visibility = View.VISIBLE
                num9_button.visibility = View.VISIBLE
                num10_button.visibility = View.VISIBLE
                develop_button.text = "-"
            } else {
                // develop mode를 false로
                // 버튼 숨기기
                num1_button.visibility = View.INVISIBLE
                num2_button.visibility = View.INVISIBLE
                num3_button.visibility = View.INVISIBLE
                num4_button.visibility = View.INVISIBLE
                num5_button.visibility = View.INVISIBLE
                num6_button.visibility = View.INVISIBLE
                num7_button.visibility = View.INVISIBLE
                num8_button.visibility = View.INVISIBLE
                num9_button.visibility = View.INVISIBLE
                num10_button.visibility = View.INVISIBLE
                develop_button.text = "+"
            }
            field = value
        }

    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable
    lateinit var ip: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // requestPermissions으로만 권한 받는건 API 23이상에서 가능
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)

        developMode = false
        mRunnable = Runnable {
            Log.d("TimerTest", "Timer End")
            dontTouch = false
        }

        i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        ip = ip_edit_text.text.toString()

        ok_button.setOnClickListener {
            ip = ip_edit_text.text.toString()
            result_text_view.text = "${ip}로 설정 완료"
        }

        drone_image.setOnClickListener {
            if(inGame) droneCommand(10)   // 뽑기 -> 게임 끝
            else droneCommand(2)  // 게임 시작
        }
        forward_button.setOnClickListener { droneCommand(3) }  // 앞
        backward_button.setOnClickListener { droneCommand(4) } // 뒤
        left_button.setOnClickListener { droneCommand(5) }     // 왼
        right_button.setOnClickListener { droneCommand(6) }    // 오른
        stop_button.setOnClickListener { droneCommand(9) }     // 멈춤

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
                        val rs = Array(mResult.size, {""})  // mResult.size 크기의 String 배열을 ""으로 초기화해서 생성
                        mResult.toArray(rs)

                        val resultCommand: Int = textToDroneCommand(rs[0])
                        droneCommand(resultCommand)

                        val resultString: String = rs[0]
                        result_text_view.text = "${result_text_view.text}\n${resultString}"
                    }
                }
            })
            mRecognizer.startListening(i)
        }

        develop_button.setOnClickListener { developMode = !developMode }
        num1_button.setOnClickListener { droneCommand(1) }
        num2_button.setOnClickListener { droneCommand(2) }
        num3_button.setOnClickListener { droneCommand(3) }
        num4_button.setOnClickListener { droneCommand(4) }
        num5_button.setOnClickListener { droneCommand(5) }
        num6_button.setOnClickListener { droneCommand(6) }
        num7_button.setOnClickListener { droneCommand(7) }
        num8_button.setOnClickListener { droneCommand(8) }
        num9_button.setOnClickListener { droneCommand(9) }
        num10_button.setOnClickListener { droneCommand(10) }

    }

    override fun onDestroy() {
        mHandler.removeCallbacks(mRunnable)
        super.onDestroy()
    }

    fun textToDroneCommand(text: String): Int{
        if(inGame) {
            // 뽑기 중에 인식하는 명령어들
            if(text.contains("끝")) return 1
            else if(text.contains("앞")) return 3
            else if(text.contains("뒤")) return 4
            else if(text.contains("왼")) return 5
            else if(text.contains("오른")) return 6
            else if(text.contains("위")) return 7
            else if(text.contains("아래")) return 8
            else if(text.contains("멈")) return 9
            else if(text.contains("뽑")) return 10
        } else {
            // 뽑기 중이 아니면 시작만 인식함
            if(text.contains("시작")) return 2
        }

        return -1
    }

    fun droneCommand(command: Int) {
        Log.d("CommandTest", "Command: $command, dontTouch: $dontTouch")
        result_text_view.text = "커맨드: $command"

        if(dontTouch) {
            Log.d("CommandTest", "dontTouch return")
            return
        }

        result_text_view.text = "${result_text_view.text}\nㅇㅋ"
        isMoving = true
        when(command) {
            1 -> Network(1, ip).execute()   // finish
            2 -> {  // start
                // 게임 시작 눌렀을 때
                inGame = true
                Network(2, ip).execute()

                forward_button.visibility = View.VISIBLE
                backward_button.visibility = View.VISIBLE
                control_buttons.visibility = View.VISIBLE

                dontTouchTimer(5000)   // 시작를 누르면 5초 동안 입력을 받지 않음
            }
            3 -> Network(3, ip).execute()   // forward
            4 -> Network(4, ip).execute()   // backward
            5 -> Network(5, ip).execute()   // left
            6 -> Network(6, ip).execute()   // right
            7 -> Network(7, ip).execute()   // up
            8 -> Network(8, ip).execute()   // down
            9 -> {
                isMoving = false
                Network(9, ip).execute()
            }   // stop
            10 -> { // pick up
                // 뽑기 눌렀을 때
                inGame = false
                Network(10, ip).execute()

                forward_button.visibility = View.INVISIBLE
                backward_button.visibility = View.INVISIBLE
                control_buttons.visibility = View.INVISIBLE

                dontTouchTimer(10000)   // 뽑기 10초 동안 입력을 받지 않음
            }
            else -> {
                isMoving = false
                Network(-1, ip).execute()
            }
        }
    }

    fun dontTouchTimer(delay: Long) {
        // delay 시간만큼 isMoving이 true
        Log.d("TimerTest", "Timer Start: $delay ms")
        dontTouch = true
        mHandler = Handler()
        mHandler.postDelayed(mRunnable, delay)
    }
}
