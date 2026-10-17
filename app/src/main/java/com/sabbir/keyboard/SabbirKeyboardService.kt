package com.sabbir.keyboard

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class SabbirKeyboardService : InputMethodService() {

    private var languageIndex = 0
    private var keyboardHeight = 300

    private val languages = arrayOf(
        "বাংলা", "English", "العربية", "اردو"
    )

    private val rows = arrayOf(
        arrayOf(
            arrayOf("১","২","৩","৪","৫","৬","৭","৮","৯","০"),
            arrayOf("া","ি","ী","ু","ূ","ে","ৈ","ো","ৌ"),
            arrayOf("ক","খ","গ","ঘ","ঙ","চ","ছ","জ","ঝ","ঞ"),
            arrayOf("ট","ঠ","ড","ঢ","ণ","ত","থ","দ","ধ","ন"),
            arrayOf("প","ফ","ব","ভ","ম","য","র","ল","শ","ষ"),
            arrayOf("স","হ","ড়","ঢ়","য়","ং","ঃ","ঁ")
        ),
        arrayOf(
            "1 2 3 4 5 6 7 8 9 0".split(" ").toTypedArray(),
            "q w e r t y u i o p".split(" ").toTypedArray(),
            "a s d f g h j k l".split(" ").toTypedArray(),
            "z x c v b n m".split(" ").toTypedArray()
        ),
        arrayOf(
            "١ ٢ ٣ ٤ ٥ ٦ ٧ ٨ ٩ ٠".split(" ").toTypedArray(),
            "ض ص ث ق ف غ ع ه خ ح".split(" ").toTypedArray(),
            "ش س ي ب ل ا ت ن م ك".split(" ").toTypedArray(),
            "ئ ء ؤ ر لا ى ة و".split(" ").toTypedArray()
        ),
        arrayOf(
            "۱ ۲ ۳ ۴ ۵ ۶ ۷ ۸ ۹ ۰".split(" ").toTypedArray(),
            "ق و ع ر ت ے ء ی ہ پ".split(" ").toTypedArray(),
            "ا س د ف گ ح ج ک ل".split(" ").toTypedArray(),
            "ز ش چ ط ب ن م".split(" ").toTypedArray()
        )
    )

    private val green = Color.rgb(10, 48, 38)
    private val keyColor = Color.rgb(32, 86, 68)
    private val gold = Color.rgb(225, 194, 120)

    private val handler = Handler(Looper.getMainLooper())
    private var deleteStarted = false

    private val deleteTask = object : Runnable {
        override fun run() {
            currentInputConnection?.deleteSurroundingText(1, 0)
            handler.postDelayed(this, 75)
        }
    }

    override fun onCreateInputView(): View = createKeyboard()

    private fun createKeyboard(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(3, 5, 3, 5)
            setBackgroundColor(green)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        header.addView(makeButton("🕋", 1f) {
            changeLanguage()
        })

        header.addView(makeButton(languages[languageIndex], 2f) {
            changeLanguage()
        })

        header.addView(makeButton("🎙️", 1f) {
            Toast.makeText(
                this,
                "ভয়েস টাইপিং পরবর্তী ধাপে যুক্ত করা হবে",
                Toast.LENGTH_SHORT
            ).show()
        })

        header.addView(makeButton("⚙️", 1f) {
            keyboardHeight = if (keyboardHeight == 300) 360 else 300
            setInputView(createKeyboard())
        })

        root.addView(
            header,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 48
            )
        )

        for (line in rows[languageIndex]) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            for (key in line) {
                row.addView(
                    makeButton(key, 1f) {
                        currentInputConnection?.commitText(key, 1)
                    }
                )
            }

            root.addView(
                row,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
                )
            )
        }

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        bottom.addView(makeButton("?123", 1.2f) {
            Toast.makeText(
                this, "সংখ্যা ও প্রতীক পরবর্তী ধাপে যুক্ত হবে",
                Toast.LENGTH_SHORT
            ).show()
        })

        bottom.addView(makeButton("😊", 1f) {
            currentInputConnection?.commitText("😊", 1)
        })

        bottom.addView(makeButton("সাব্বির", 3.5f) {
            currentInputConnection?.commitText(" ", 1)
        })

        bottom.addView(makeButton("⌫", 1.2f) {})

        bottom.addView(makeButton("↵", 1.1f) {
            currentInputConnection?.commitText("\n", 1)
        })

        root.addView(
            bottom,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 50
            )
        )

        return root
    }

    private fun changeLanguage() {
        languageIndex = (languageIndex + 1) % languages.size
        setInputView(createKeyboard())
    }

    private fun makeButton(
        label: String,
        weight: Float,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = label
            textSize = 17f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            setPadding(0, 0, 0, 0)

            background = GradientDrawable().apply {
                setColor(keyColor)
                cornerRadius = 9f
                setStroke(1, gold)
            }

            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                weight
            ).apply {
                setMargins(2, 2, 2, 2)
            }

            if (label == "⌫") {
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            deleteStarted = false
                            handler.postDelayed({
                                deleteStarted = true
                                handler.removeCallbacks(deleteTask)
                                handler.post(deleteTask)
                            }, 350)
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            handler.removeCallbacks(deleteTask)
                            handler.removeCallbacksAndMessages(null)
                            if (!deleteStarted) {
                                currentInputConnection
                                    ?.deleteSurroundingText(1, 0)
                            }
                            true
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            handler.removeCallbacks(deleteTask)
                            handler.removeCallbacksAndMessages(null)
                            true
                        }

                        else -> true
                    }
                }
            } else {
                setOnClickListener { action() }
            }
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        handler.removeCallbacksAndMessages(null)
        super.onFinishInputView(finishingInput)
    }
}
