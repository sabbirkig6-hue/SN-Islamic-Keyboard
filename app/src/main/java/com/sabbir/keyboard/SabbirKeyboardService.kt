
package com.sabbir.keyboard

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class SabbirKeyboardService : InputMethodService() {

    private var languageIndex = 0

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

    private val green = Color.rgb(15, 65, 49)
    private val keyColor = Color.rgb(34, 83, 67)
    private val gold = Color.rgb(231, 198, 126)

    override fun onCreateInputView(): View {
        return createKeyboard()
    }

    private fun createKeyboard(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(4, 8, 4, 8)
            setBackgroundColor(green)
        }

        val header = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
        }

        header.addView(makeButton("🕋", 0.8f) {
            languageIndex = (languageIndex + 1) % languages.size
            setInputView(createKeyboard())
            Toast.makeText(
                this, languages[languageIndex],
                Toast.LENGTH_SHORT
            ).show()
        })

        header.addView(makeButton(languages[languageIndex], 1.2f) {
            languageIndex = (languageIndex + 1) % languages.size
            setInputView(createKeyboard())
        })

        header.addView(makeButton("🎙️", 0.8f) {
            Toast.makeText(
                this,
                "ভয়েস টাইপিং চালু করতে Speech Recognition যুক্ত করতে হবে",
                Toast.LENGTH_SHORT
            ).show()
        })

        header.addView(makeButton("☪️", 0.7f) {
            Toast.makeText(
                this, "আসসালামু আলাইকুম",
                Toast.LENGTH_SHORT
            ).show()
        })

        root.addView(header)

        for (line in layouts[languageIndex]) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            val keys = line.split(" ")
            for (key in keys) {
                row.addView(makeButton(key, 1f) {
                    currentInputConnection?.commitText(key, 1)
                })
            }

            root.addView(
                row,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 48
                )
            )
        }

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        bottom.addView(makeButton("?123", 0.9f) {
            Toast.makeText(
                this, "সংখ্যা ও প্রতীক",
                Toast.LENGTH_SHORT
            ).show()
        })

        bottom.addView(makeButton("😊", 0.8f) {
            currentInputConnection?.commitText("🤲", 1)
        })

        bottom.addView(makeButton("সাব্বির", 3f) {
            currentInputConnection?.commitText(" ", 1)
        })

        bottom.addView(makeButton("⌫", 0.9f) {
            currentInputConnection?.deleteSurroundingText(1, 0)
        })

        bottom.addView(makeButton("↵", 0.9f) {
            currentInputConnection?.commitText("\n", 1)
        })

        root.addView(bottom)

        return root
    }

    private fun makeButton(
        label: String,
        weight: Float,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = label
            textSize = 15f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            setPadding(1, 1, 1, 1)

            background = GradientDrawable().apply {
                setColor(keyColor)
                cornerRadius = 10f
                setStroke(1, gold)
            }

            setOnClickListener { action() }

            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                weight
            ).apply {
                setMargins(2, 2, 2, 2)
            }
        }
    }
}
