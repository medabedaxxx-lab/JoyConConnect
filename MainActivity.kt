package cz.joyconconnect

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.graphics.Typeface
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var deviceInfo: TextView
    private lateinit var inputInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scroll = ScrollView(this)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
        }
        fun title(s: String) = TextView(this).apply {
            text = s; textSize = 22f; setTypeface(null, Typeface.BOLD)
            setPadding(0, 18, 0, 8)
        }
        page.addView(title("🎮 Joy-Con Connect"))
        page.addView(TextView(this).apply {
            text = "Spáruj oba Joy-Cony přes Bluetooth. Aplikace ukáže, zda je Android rozpoznal, a umožní otestovat jejich tlačítka a páčky."
            textSize = 16f
        })
        page.addView(Button(this).apply {
            text = "Otevřít Bluetooth nastavení"
            setOnClickListener { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }
        })
        page.addView(Button(this).apply {
            text = "Obnovit seznam ovladačů"
            setOnClickListener { refreshDevices() }
        })
        page.addView(title("Stav připojení"))
        status = TextView(this).apply { textSize = 17f; setPadding(0, 6, 0, 8) }
        deviceInfo = TextView(this).apply { textSize = 15f }
        page.addView(status); page.addView(deviceInfo)

        page.addView(title("Test tlačítek a páček"))
        inputInfo = TextView(this).apply {
            text = "Stiskni tlačítko nebo pohni páčkou na ovladači."
            textSize = 16f; setPadding(0, 8, 0, 16)
        }
        page.addView(inputInfo)

        page.addView(title("Virtuální ovladač"))
        page.addView(TextView(this).apply {
            text = "Náhled dotykového rozložení tlačítek (zatím pouze vizuální ovládací plocha)."
            textSize = 14f
        })
        val pad = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 12)
        }
        fun virtualButton(label: String) = Button(this).apply {
            text = label
            isAllCaps = false
            setOnTouchListener { v, e ->
                v.alpha = if (e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_CANCEL) 1f else 0.55f
                true
            }
        }
        val left = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER }
        left.addView(virtualButton("↑"))
        val lr = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        lr.addView(virtualButton("←")); lr.addView(virtualButton("↓")); lr.addView(virtualButton("→"))
        left.addView(lr)
        val right = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER }
        right.addView(virtualButton("X"))
        val xy = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        xy.addView(virtualButton("Y")); xy.addView(virtualButton("A")); xy.addView(virtualButton("B"))
        right.addView(xy)
        pad.addView(left); pad.addView(right)
        page.addView(pad)
        page.addView(TextView(this).apply {
            text = "Poznámka: Android standardně neumí předat dotyková tlačítka z této aplikace do jiné hry jako skutečný fyzický gamepad. K tomu je potřeba podpora hry nebo zvláštní systémové oprávnění."
            textSize = 13f; setTextColor(Color.DKGRAY)
        })
        scroll.addView(page)
        setContentView(scroll)
        refreshDevices()
    }

    override fun onResume() { super.onResume(); if (::status.isInitialized) refreshDevices() }

    private fun refreshDevices() {
        val devices = InputDevice.getDeviceIds().mapNotNull { InputDevice.getDevice(it) }
        val pads = devices.filter {
            (it.sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
            (it.sources and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
        }
        status.text = if (pads.isEmpty()) "⚪ Android zatím nerozpoznal žádný gamepad."
                      else "🟢 Rozpoznáno zařízení: ${pads.size}"
        deviceInfo.text = if (pads.isEmpty()) {
            "Podrž tlačítko SYNC na boku Joy-Conu, v Bluetooth vyber Joy-Con (L) a Joy-Con (R) a spáruj je postupně. Potom se vrať sem."
        } else pads.joinToString("\n\n") {
            "• ${it.name}\nID: ${it.id}\nZdroj: ${if ((it.sources and InputDevice.SOURCE_GAMEPAD) != 0) "Gamepad" else "Joystick"}"
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        inputInfo.text = "Stisknuto: ${KeyEvent.keyCodeToString(keyCode)}\nZařízení: ${event.device?.name ?: "neznámé"}"
        return true
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        inputInfo.text = "Uvolněno: ${KeyEvent.keyCodeToString(keyCode)}\nZařízení: ${event.device?.name ?: "neznámé"}"
        return true
    }
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK &&
            event.action == MotionEvent.ACTION_MOVE) {
            val axes = listOf(MotionEvent.AXIS_X, MotionEvent.AXIS_Y, MotionEvent.AXIS_Z,
                MotionEvent.AXIS_RZ, MotionEvent.AXIS_HAT_X, MotionEvent.AXIS_HAT_Y)
            val values = axes.mapNotNull { axis ->
                val value = event.getAxisValue(axis)
                if (kotlin.math.abs(value) > 0.02f) "${MotionEvent.axisToString(axis)}: %.2f".format(value) else null
            }
            inputInfo.text = "Pohyb páčky – ${event.device?.name ?: "ovladač"}\n" +
                values.joinToString("\n").ifEmpty { "Pohyb zachycen." }
            return true
        }
        return super.onGenericMotionEvent(event)
    }
}
