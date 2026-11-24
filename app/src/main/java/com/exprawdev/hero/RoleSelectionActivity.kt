
package com.exprawdev.hero

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RoleSelectionActivity : AppCompatActivity() {

    private val background = Color.rgb(8, 13, 24)
    private val blue = Color.rgb(23, 105, 255)
    private val white = Color.WHITE
    private val muted = Color.LTGRAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = background
        window.navigationBarColor = background

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(background)
        }

        val title = TextView(this).apply {
            text = "CHOOSE DEVICE ROLE"
            textSize = 25f
            setTextColor(blue)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val description = TextView(this).apply {
            text = "Select how this device will be used in your family."
            textSize = 14f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 32)
        }

        val parentButton = createButton("PARENT DEVICE")
        val childButton = createButton("CHILD DEVICE")

        parentButton.setOnClickListener {
            saveRole(DeviceRole.PARENT)
        }

        childButton.setOnClickListener {
            saveRole(DeviceRole.CHILD)
        }

        root.addView(title)
        root.addView(description)
        root.addView(parentButton)
        root.addView(childButton)

        setContentView(root)
    }

    private fun createButton(label: String): Button {
        return Button(this).apply {
            text = label
            textSize = 15f
            setTextColor(white)
            setBackgroundColor(blue)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            ).apply {
                bottomMargin = 16
            }
        }
    }

    private fun saveRole(role: DeviceRole) {
        DeviceRoleStorage(this).saveRole(role)

        Toast.makeText(
            this,
            "Device role saved: ${role.name}",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}
