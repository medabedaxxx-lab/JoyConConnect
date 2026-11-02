package cz.joyconconnect

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var statusText: TextView
    private lateinit var eventText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val title = TextView(this).apply {
            text = "🎮 JoyConConnect"
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 20)
        }

        statusText = TextView(this).apply {
            textSize = 17f
            setPadding(0, 20, 0, 20)
        }

        val bluetoothButton = Button(this).apply {
            text = "Připojit Joy-Con přes Bluetooth"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            }
        }

        val refreshButton = Button(this).apply {
            text = "Obnovit připojené ovladače"
            setOnClickListener {
                showDevices()
            }
        }

        val eventTitle = TextView(this).apply {
            text = "Test tlačítek a páček:"
            textSize = 20f
            setPadding(0, 24, 0, 12)
        }

        eventText = TextView(this).apply {
            text = "Zatím čekám na vstup z ovladače."
            textSize = 16f
            setPadding(0, 12, 0, 20)
        }

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(title)
            addView(statusText)
            addView(bluetoothButton)
            addView(refreshButton)
            addView(eventTitle)
            addView(eventText)
        }

        scroll.addView(content)
        layout.addView(scroll)
        setContentView(layout)

        showDevices()
    }

    private fun showDevices() {
        val devices = InputDevice.getDeviceIds()
            .mapNotNull { InputDevice.getDevice(it) }
            .filter {
                it.sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD ||
                it.sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
            }

        statusText.text = if (devices.isEmpty()) {
            "Žádný herní ovladač zatím není rozpoznán.\n\nPřipoj Joy-Con přes Bluetooth a potom obnov seznam."
        } else {
            "Rozpoznané ovladače:\n\n" + devices.joinToString("\n\n") {
                "🎮 ${it.name}\nID: ${it.id}"
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        eventText.text = "Stisknuté tlačítko:\n${KeyEvent.keyCodeToString(keyCode)}"
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        eventText.text = "Uvolněné tlačítko:\n${KeyEvent.keyCodeToString(keyCode)}"
        return true
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
            val x = event.getAxisValue(MotionEvent.AXIS_X)
            val y = event.getAxisValue(MotionEvent.AXIS_Y)
            val z = event.getAxisValue(MotionEvent.AXIS_Z)
            val rz = event.getAxisValue(MotionEvent.AXIS_RZ)

            eventText.text = """
                Pohyb páček:

                Levá páčka X: $x
                Levá páčka Y: $y
                Pravá páčka X: $z
                Pravá páčka Y: $rz
            """.trimIndent()

            return true
        }

        return super.onGenericMotionEvent(event)
    }
}
