
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
import android.widget.ScrollView
import android.widget.Toast

class SabbirKeyboardService : InputMethodService() {

    private var languageIndex = 0
    private var emojiMode = false
    private var deleteStarted = false

    private val languages = arrayOf(
        "বাংলা", "English", "العربية", "اردو"
    )

    private val layouts = arrayOf(
        arrayOf(
            "১ ২ ৩ ৪ ৫ ৬ ৭ ৮ ৯ ০",
            "া ি ী ু ূ ে ৈ ো ৌ",
            "ক খ গ ঘ ঙ চ ছ জ ঝ ঞ",
            "ট ঠ ড ঢ ণ ত থ দ ধ ন",
            "প ফ ব ভ ম য র ল শ ষ",
            "স হ ড় ঢ় য় ং ঃ ঁ"
        ),
        arrayOf(
            "1 2 3 4 5 6 7 8 9 0",
            "q w e r t y u i o p",
            "a s d f g h j k l",
            "z x c v b n m"
        ),
        arrayOf(
            "١ ٢ ٣ ٤ ٥ ٦ ٧ ٨ ٩ ٠",
            "ض ص ث ق ف غ ع ه خ ح",
            "ش س ي ب ل ا ت ن م ك",
            "ئ ء ؤ ر لا ى ة و"
        ),
        arrayOf(
            "۱ ۲ ۳ ۴ ۵ ۶ ۷ ۸ ۹ ۰",
            "ق و ع ر ت ے ء ی ہ پ",
            "ا س د ف گ ح ج ک ل",
            "ز ش چ ط ب ن م"
        )
    )

    private val green = Color.rgb(10, 48, 38)
    private val keyGreen = Color.rgb(32, 86, 68)
    private val gold = Color.rgb(225, 194, 120)

    private val handler = Handler(Looper.getMainLooper())

    private val deleteTask = object : Runnable {
        override fun run() {
            if (deleteStarted) {
                currentInputConnection?.deleteSurroundingText(1, 0)
                handler.postDelayed(this, 75)
            }
        }
    }

    override fun onCreateInputView(): View {
        return createKeyboard()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun insertText(value: String) {
        val connection = currentInputConnection

        if (connection == null) {
            Toast.makeText(
                this,
                "লেখার ঘরে আগে চাপ দিন, তারপর কিবোর্ড ব্যবহার করুন",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        connection.commitText(value, 1)
    }

    private fun createKeyboard(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(3), dp(4), dp(3), dp(4))
            setBackgroundColor(green)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        header.addView(makeButton("🕋", 1f, 42) {
            languageIndex = (languageIndex + 1) % languages.size
            refreshKeyboard()
        })

        header.addView(makeButton(languages[languageIndex], 2f, 42) {
            languageIndex = (languageIndex + 1) % languages.size
            refreshKeyboard()
        })

        header.addView(makeButton("😊", 1f, 42) {
            emojiMode = !emojiMode
            refreshKeyboard()
        })

        header.addView(makeButton("⚙", 1f, 42) {
            Toast.makeText(
                this,
                "কিবোর্ড সেটিংস পরবর্তী আপডেটে যুক্ত হবে",
                Toast.LENGTH_SHORT
            ).show()
        })

        root.addView(
            header,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(46)
            )
        )

        if (emojiMode) {
            val emojis = listOf(
                "😀", "😂", "🥰", "😍", "😊", "😉",
                "😭", "😎", "🤲", "🙏", "❤️", "💚",
                "🌙", "⭐", "🕋", "🕌", "☪️", "📿",
                "👍", "👏", "🌹", "✨", "🔥", "💯"
            )

            val scroll = ScrollView(this)
            val emojiGrid = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            for (chunk in emojis.chunked(6)) {
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                }

                for (emoji in chunk) {
                    row.addView(makeButton(emoji, 1f, 48) {
                        insertText(emoji)
                    })
                }

                emojiGrid.addView(
                    row,
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(50)
                    )
                )
            }

            scroll.addView(emojiGrid)
            root.addView(
                scroll,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(200)
                )
            )
        } else {
            for (line in layouts[languageIndex]) {
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                }

                val keys = line.split(" ")

                for (key in keys) {
                    row.addView(makeButton(key, 1f, 44) {
                        insertText(key)
                    })
                }

                root.addView(
                    row,
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(46)
                    )
                )
            }
        }

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        bottom.addView(makeButton("?123", 1.1f, 48) {
            Toast.makeText(
                this@SabbirKeyboardService,
                "সংখ্যা ও প্রতীকের লেআউট পরবর্তী ধাপে হবে",
                Toast.LENGTH_SHORT
            ).show()
        })

        bottom.addView(makeButton("😊", 0.9f, 48) {
            emojiMode = !emojiMode
            refreshKeyboard()
        })

        bottom.addView(makeButton("সাব্বির", 2.7f, 48) {
            insertText(" ")
        })

        bottom.addView(makeButton("⌫", 1.1f, 48) {})

        bottom.addView(makeButton("↵", 1f, 48) {
            val connection = currentInputConnection
            if (connection != null) {
                connection.commitText("\n", 1)
            }
        })

        root.addView(
            bottom,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        return root
    }

    private fun refreshKeyboard() {
        setInputView(createKeyboard())
    }

    private fun makeButton(
        label: String,
        weight: Float,
        height: Int,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = label
            textSize = if (label.length > 2) 14f else 19f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            setPadding(dp(1), 0, dp(1), 0)

            background = GradientDrawable().apply {
                setColor(keyGreen)
                cornerRadius = dp(7).toFloat()
                setStroke(dp(1), gold)
            }

            layoutParams = LinearLayout.LayoutParams(
                0,
                dp(height),
                weight
            ).apply {
                setMargins(dp(2), dp(2), dp(2), dp(2))
            }

            if (label == "⌫") {
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            deleteStarted = false
                            handler.removeCallbacks(deleteTask)

                            handler.postDelayed({
                                deleteStarted = true
                                handler.post(deleteTask)
                            }, 350)

                            true
                        }

                        MotionEvent.ACTION_UP,
                        MotionEvent.ACTION_CANCEL -> {
                            handler.removeCallbacks(deleteTask)

                            if (!deleteStarted &&
                                event.action == MotionEvent.ACTION_UP
                            ) {
                                currentInputConnection
                                    ?.deleteSurroundingText(1, 0)
                            }

                            deleteStarted = false
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
        deleteStarted = false
        handler.removeCallbacksAndMessages(null)
        super.onFinishInputView(finishingInput)
    }
}
