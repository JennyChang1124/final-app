package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.strokepatientsvoicerecoveryapp.databinding.QuestionOverviewBinding
import com.google.firebase.database.*
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class QuestionOverviewActivity : AppCompatActivity() {

    private lateinit var binding: QuestionOverviewBinding
    private lateinit var username: String
    private lateinit var sp1Selection: String
    private lateinit var selectedTitle: String

    private lateinit var QuizSheet : String
    private var QuizTotalSum : Int = 0
    private var Ans : String =""
    private var type: String = ""

    private val hints: MutableList<String> = mutableListOf()
    private var score: Int = 10
    private var TotalScore: Int = 0
    private var TotalAnsSum: Int = 10

    private val randomQnum : Int = 0
    private val recordList: MutableList<RecordData> = mutableListOf()
    private val DateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuestionOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        QuizSheet = "1Cjet_xxbV9EqxhUpAAl40YLharHsV_wT5JFd8OIXCdA"
        username = intent.getStringExtra("username") ?: ""
        sp1Selection = intent.getStringExtra("sp1Selection") ?: ""
        selectedTitle = intent.getStringExtra("selectedTitle") ?: ""

        initView()

        // 選題目
        when (selectedTitle) {
            "食物" -> QuizTotalSum = 148
            "生活" -> QuizTotalSum = 107
            "流暢" -> QuizTotalSum = 141
            "理解" -> QuizTotalSum = 114
            "重述-簡單" -> QuizTotalSum = 27
            "重述-困難" -> QuizTotalSum = 22
        }
        val randomQnum = (1..QuizTotalSum).random()
        getTheQuizFromSheet(randomQnum)

        binding.hint.setOnClickListener{
            showHint()
        }
        binding.next.setOnClickListener{
            isAnsCorrect()
            TotalScore += score
            Log.d("isAnsCorrect", "score: $score, Total: $TotalScore, Ans: $TotalAnsSum")
            TotalAnsSum += 10
            score = 10
            SaveRecData(randomQnum, Ans, score)
            initView()
            val randomQnum = (1..QuizTotalSum).random()
            getTheQuizFromSheet(randomQnum)
        }

//      ======================timer=====================================
        val textView = binding.countdownTimer

        val timeValue = intent.getIntExtra("timeValue", 0) // 從Intent中檢索時間值
        val timeDuration = TimeUnit.MINUTES.toMillis(timeValue.toLong()) // 設定倒數時間
        val tickInterval: Long = 10 //接收回調的間隔

        object : CountDownTimer(timeDuration, tickInterval) {
            var millis: Long = 1000 //1000=1秒
            override fun onTick(millisUntilFinished: Long) {
                millis -= tickInterval
                if (millis == 0L) millis = 1000
                val timerText = String.format(
                    Locale.getDefault(), "%2d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )//公式
                            ),
                    millis
                )
                textView.text = timerText
            }
            // 倒數完畢時
            override fun onFinish() {
                textView.text = "時間到" // 補切換畫面
                // 結算成績
                val result = (TotalScore.toDouble() / TotalAnsSum.toDouble() * 100).toInt()
                val intent = Intent(this@QuestionOverviewActivity, TimesupOverviewActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("sp1Selection", sp1Selection)
                intent.putExtra("Score", result)
                intent.putExtra("timeValue", timeValue)
                startActivity(intent)
            }
        }.start()
//      ======================timer end=====================================
    }
    // =========================function=======================================
    // 初始畫面，全部隱藏
    private fun initView(){
        binding.q1Evdashimg.visibility = View.GONE
        binding.q2Evdashimg.visibility = View.GONE
        binding.q3Evdashimg.visibility = View.GONE
        binding.q4Evdashimg.visibility = View.GONE
        binding.q5Evdashimg.visibility = View.GONE
        binding.q6Evdashimg.visibility = View.GONE
    }

    // 開資料庫
    fun readQuestionContent(questionNumber: String, callback: (DataSnapshot) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(QuizSheet)
            .child(selectedTitle)
            .child(questionNumber)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(dataSnapshot)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // 打開正確的難度、類型的題目View
    private fun getTheQuizFromSheet(questionNumber : Int) {
        val questionNumber = questionNumber.toString()

        readQuestionContent(questionNumber) { dataSnapshot ->
            val currQuestion = dataSnapshot.value as? Map<*, *> ?: return@readQuestionContent

            type = currQuestion["題型"].toString()

            when (type) {
                "複誦句子" -> binding.q1Evdashimg.visibility = View.VISIBLE
                "簡單應答" -> binding.q2Evdashimg.visibility = View.VISIBLE
                "聽覺理解" -> binding.q3Evdashimg.visibility = View.VISIBLE
                "圖物配對" -> binding.q4Evdashimg.visibility = View.VISIBLE
                "口語描述" -> binding.q5Evdashimg.visibility = View.VISIBLE
                "詞語表達" -> binding.q6Evdashimg.visibility = View.VISIBLE
            }

            when (type) {
                "複誦句子" -> {
                    binding.qSpeech.tvImage1.text = currQuestion["題目"].toString()
                }
                "簡單應答" -> {
                    LoadImage(currQuestion["圖片1"].toString()) { drawable ->
                        binding.qSpeechImage.tvImage2.setImageDrawable(drawable)
                    }
                }
                "聽覺理解" -> {
                    binding.qChooseImage.tvText2.text = currQuestion["題目"].toString()
                    LoadImage(currQuestion["圖片1"].toString()) { drawable ->
                        binding.qChooseImage.tvImage4.setImageDrawable(drawable)
                    }
                    LoadImage(currQuestion["圖片2"].toString()) { drawable ->
                        binding.qChooseImage.tvImage5.setImageDrawable(drawable)
                    }
                    LoadImage(currQuestion["圖片3"].toString()) { drawable ->
                        binding.qChooseImage.tvImage6.setImageDrawable(drawable)
                    }
                    LoadImage(currQuestion["圖片4"].toString()) { drawable ->
                        binding.qChooseImage.tvImage7.setImageDrawable(drawable)
                    }

                }
                "圖物配對" -> {
                    binding.qDragText.tvOption4.text = currQuestion["答案1"].toString()
                    binding.qDragText.tvOption5.text = currQuestion["答案2"].toString()
                    binding.qDragText.tvOption6.text = currQuestion["答案3"].toString()
                    LoadImage(currQuestion["圖片1"].toString()) { drawable ->
                        binding.qDragText.tvImage8.setImageDrawable(drawable)
                    }
                    LoadImage(currQuestion["圖片2"].toString()) { drawable ->
                        binding.qDragText.tvImage9.setImageDrawable(drawable)
                    }
                    LoadImage(currQuestion["圖片3"].toString()) { drawable ->
                        binding.qDragText.tvImage10.setImageDrawable(drawable)
                    }

                    val tvOption4 = binding.qDragText.tvOption4
                    val tvOption5 = binding.qDragText.tvOption5
                    val tvOption6 = binding.qDragText.tvOption6

                    tvOption4.setOnTouchListener(DragTouchListener())
                    tvOption5.setOnTouchListener(DragTouchListener())
                    tvOption6.setOnTouchListener(DragTouchListener())
                }

                "口語描述" -> {
                    LoadImage(currQuestion["圖片1"].toString()) { drawable ->
                        binding.qDescribeImage.tvImage3.setImageDrawable(drawable)
                    }
                    binding.qDescribeImage.tvText3.text = currQuestion["題目"].toString()
                }
                "詞語表達" -> {
                    binding.qChooseSentence.tvOptionOne.text = currQuestion["選項1"].toString()
                    binding.qChooseSentence.tvOptionTwo.text = currQuestion["選項2"].toString()
                    binding.qChooseSentence.tvOptionThree.text = currQuestion["選項3"].toString()
                    LoadImage(currQuestion["圖片1"].toString()) { drawable ->
                        binding.qChooseSentence.tvImage.setImageDrawable(drawable)
                    }
                }
            }

            // Get hints from the current question
            val hint1 = currQuestion["提示1"].toString()
            val hint2 = currQuestion["提示2"].toString()
            val hint3 = currQuestion["提示3"].toString()

            // Update the hints list
            hints.clear()
            if (hint1.isNotEmpty()) hints.add(hint1)
            if (hint2.isNotEmpty()) hints.add(hint2)
            if (hint3.isNotEmpty()) hints.add(hint3)

            Ans = currQuestion["答案1"].toString()
        }
    }

    //拖曳按鈕
    private class DragTouchListener : View.OnTouchListener {
        private var offsetX = 0
        private var offsetY = 0

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 記錄觸摸點的偏移量，用於在拖動過程中保持位置
                    offsetX = motionEvent.rawX.toInt() - view.x.toInt()
                    offsetY = motionEvent.rawY.toInt() - view.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    // 更新TextView的位置
                    val newX = motionEvent.rawX.toInt() - offsetX
                    val newY = motionEvent.rawY.toInt() - offsetY
                    view.x = newX.toFloat()
                    view.y = newY.toFloat()
                }
            }
            return true
        }
    }
    fun LoadImage(url: String?, callback: (Drawable?) -> Unit) {
        Thread {
            try {
                val `is`: InputStream = URL(url).content as InputStream
                val drawable = Drawable.createFromStream(`is`, "src name")
                runOnUiThread {
                    callback(drawable)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    callback(null)
                }
            }
        }.start()
    }


    private fun showHint() {
        if (hints.isEmpty()) {
            Toast.makeText(this@QuestionOverviewActivity, "沒有提示囉~", Toast.LENGTH_SHORT).show()
        } else {
            val hintToShow = hints.removeAt(0)
            score--
            runOnUiThread {
                Toast.makeText(this@QuestionOverviewActivity, hintToShow, Toast.LENGTH_SHORT).show()
            }
        }
        if (hints.isEmpty()) {
            score = 6
        }
    }

    private fun isAnsCorrect(){
        when (type) {
            "複誦句子" -> {
                val userAnswer = binding.qSpeech.editWord3.toString().trim()
                if(Ans != userAnswer){ score-- }
                recordList.add(RecordData(getCurrentDateTime(), randomQnum, Ans))
            }
            "簡單應答" -> {
                val userAnswer = binding.qSpeechImage.tvText4.toString().trim()
                if(Ans != userAnswer){ score-- }
                recordList.add(RecordData(getCurrentDateTime(), randomQnum, Ans))
            }
            "聽覺理解" -> {
                recordList.add(RecordData(getCurrentDateTime(), randomQnum, Ans))
                binding.qChooseImage.tvImage4.setOnClickListener { binding.next.performClick() }
                binding.qChooseImage.tvImage5.setOnClickListener { binding.next.performClick() }
                binding.qChooseImage.tvImage6.setOnClickListener { binding.next.performClick() }
                binding.qChooseImage.tvImage7.setOnClickListener { binding.next.performClick() }
            }
            "圖物配對" -> {
                recordList.add(RecordData(getCurrentDateTime(), randomQnum, Ans))
            }
            "口語描述" -> {
                val userAnswer = binding.qDescribeImage.editWord2.toString().trim()
                if(Ans != userAnswer){ score-- }
                recordList.add(RecordData(getCurrentDateTime(), randomQnum, Ans))
            }
            "詞語表達" -> {
                val selectedOption = when {
                    binding.qChooseSentence.tvOptionOne.isSelected -> binding.qChooseSentence.tvOptionOne.text.toString()
                    binding.qChooseSentence.tvOptionTwo.isSelected -> binding.qChooseSentence.tvOptionTwo.text.toString()
                    binding.qChooseSentence.tvOptionThree.isSelected -> binding.qChooseSentence.tvOptionThree.text.toString()
                    else -> ""
                }
                if (selectedOption != Ans) { score-- }
                recordList.add(RecordData(getCurrentDateTime(), randomQnum, Ans))
                binding.qChooseSentence.tvOptionOne.setOnClickListener { binding.next.performClick() }
                binding.qChooseSentence.tvOptionTwo.setOnClickListener { binding.next.performClick() }
                binding.qChooseSentence.tvOptionThree.setOnClickListener { binding.next.performClick() }
            }
        }

    }

    private fun SaveRecData(randomQnum: Int, ans: String, score: Int) {
        val currentTime = getCurrentDateTime()

        val recordData = RecordData(currentTime, randomQnum, ans, score)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val recordRef: DatabaseReference = database.getReference("紀錄")
        val userRef: DatabaseReference = recordRef.child(username)
        val dateTimeRef: DatabaseReference = userRef.child(DateTime)
        val timeRef: DatabaseReference = dateTimeRef.child(currentTime)

        timeRef.setValue(recordData)
            .addOnSuccessListener {
                Log.d("SaveRecData", "資料儲存成功")
            }
            .addOnFailureListener {
                Log.e("SaveRecData", "資料儲存失敗")
            }
    }

    private fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
}

data class RecordData(
    val time: String = "",
    val questionNumber: Int = 0,
    val answer: String = "",
    val score: Int = 0
)

