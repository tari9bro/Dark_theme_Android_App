package com.phstudio.darktheme.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.UiModeManager
import android.app.UiModeManager.DISABLE_CAR_MODE_GO_HOME
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.phstudio.darktheme.R
import com.phstudio.darktheme.broadcast.AutoChangeDark
import com.phstudio.darktheme.broadcast.AutoChangeLight
import java.text.SimpleDateFormat
import java.util.*

class ChangerFragment : Fragment() {
    private lateinit var alarmManager: AlarmManager
    private var flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
    private var uiModeManager: UiModeManager? = null
    private lateinit var btChanger: Button
    private lateinit var tvMode: TextView
    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }
    private fun isOverlayGranted() =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_changer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiModeManager = getSystemService(requireContext(), UiModeManager::class.java)
        btChanger = view.findViewById(R.id.btChanger)
        tvMode = view.findViewById(R.id.tvMode)
        display()
        btChanger.setOnClickListener {
            changeButton()
        }
        requestOverlayPermission()
        val sharedPreferences = this.requireActivity()
            .getSharedPreferences(resources.getString(R.string.app_package), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val tgbFilters = view.findViewById<ToggleButton>(R.id.tgbFilters)

        val seek = view.findViewById<SeekBar>(R.id.sbSun)
        seek?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                //onProgressChanged
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                //onStartTrackingTouch
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                val intValue = ((seek.progress) * 2.55).toInt()
                tgbFilters.isChecked = true
                editor.putInt("color0", intValue).apply()
                editor.putInt("color", 0).apply()
                startView()
            }
        })

        val seek2 = view.findViewById<SeekBar>(R.id.sbLight)
        seek2?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                //onProgressChanged
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                //onStartTrackingTouch
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                val intValue = ((seek.progress) * 2.55).toInt()
                tgbFilters.isChecked = true
                editor.putInt("color1", intValue).apply()
                editor.putInt("color", 1).apply()
                startView()
            }
        })

        val seek3 = view.findViewById<SeekBar>(R.id.sbMoon)
        seek3?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                //onProgressChanged
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                //onStartTrackingTouch
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                val intValue = ((seek.progress) * 2.55).toInt()
                tgbFilters.isChecked = true
                editor.putInt("color2", intValue).apply()
                editor.putInt("color", 2).apply()
                startView()
            }
        })

        val color0 = sharedPreferences.getInt("color0", 50)
        val color1 = sharedPreferences.getInt("color1", 30)
        val color2 = sharedPreferences.getInt("color2", 80)

        seek.progress = (color0 / 2.55).toInt()
        seek2.progress = (color1 / 2.55).toInt()
        seek3.progress = (color2 / 2.55).toInt()

        tgbFilters.apply {
            isChecked = OverlayService.isActive
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        OverlayService.start(context)
                    } else {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.not_support),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        OverlayService.stop(context)
                    } else {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.not_support),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val btTimeDark = view.findViewById<Button>(R.id.btTimeDark)
        val btTimeLight = view.findViewById<Button>(R.id.btTimeLight)
        val ibTimeDark = view.findViewById<ImageButton>(R.id.ibTimeDark)
        val ibTimeLight = view.findViewById<ImageButton>(R.id.ibTimeLight)

        var time: Long



        flags = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else -> PendingIntent.FLAG_UPDATE_CURRENT
        }

        val darkText = sharedPreferences.getString("DarkText", "")
        val lightText = sharedPreferences.getString("LightText", "")

        val darkHistory = sharedPreferences.getBoolean("DarkHistory", false)
        val lightHistory = sharedPreferences.getBoolean("LightHistory", false)

        if (lightHistory) {
            btTimeLight.text = lightText
            ibTimeLight.setImageResource(R.drawable.ic_off)
        } else {
            btTimeLight.text = getString(R.string.time_null)
            ibTimeLight.setImageResource(R.drawable.ic_on)
        }

        if (darkHistory) {
            btTimeDark.text = darkText
            ibTimeDark.setImageResource(R.drawable.ic_off)
        } else {
            btTimeDark.text = getString(R.string.time_null)
            ibTimeDark.setImageResource(R.drawable.ic_on)
        }

        ibTimeDark.setOnClickListener {
            val darkHistory2 = sharedPreferences.getBoolean("DarkHistory", false)
            if (darkHistory2) {
                editor.putBoolean("DarkHistory", false).apply()
                editor.putString("DarkText", "OFF").apply()
                btTimeDark.text = getString(R.string.time_null)
                ibTimeDark.setImageResource(R.drawable.ic_on)

                val intent = Intent(activity, AutoChangeDark::class.java)
                val pendingIntent = PendingIntent.getBroadcast(activity, 1, intent, flags)
                alarmManager.cancel(pendingIntent)
                Toast.makeText(activity, getString(R.string.stop_theme), Toast.LENGTH_SHORT).show()
            } else {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val minute = calendar[Calendar.MINUTE]
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH]
                val day = calendar[Calendar.DAY_OF_MONTH]
                val timePickerDialog = TimePickerDialog(activity, { _, i, i1 ->
                    val timeToNotify = "$i:$i1"
                    val dateToNotify = day.toString() + "-" + (month + 1) + "-" + year
                    val dateAndTime = "$dateToNotify $timeToNotify"
                    val df = SimpleDateFormat("d-M-yyyy HH:mm")
                    time = df.parse(dateAndTime)!!.time
                    if (time < System.currentTimeMillis()) {
                        time += 86400000
                    }
                    editor.putBoolean("DarkHistory", true).apply()
                    btTimeDark.text = formatTime24(i, i1)
                    ibTimeDark.setImageResource(R.drawable.ic_off)
                    editor.putString("DarkText", formatTime24(i, i1))
                        .apply()
                    setDark(time)
                }, hour, minute, true)//24:00
                timePickerDialog.show()
            }
        }

        ibTimeLight.setOnClickListener {
            val lightHistory2 = sharedPreferences.getBoolean("LightHistory", false)
            if (lightHistory2) {
                editor.putBoolean("LightHistory", false).apply()
                editor.putString("LightText", "OFF").apply()
                btTimeLight.text = getString(R.string.time_null)
                ibTimeLight.setImageResource(R.drawable.ic_on)

                val intent = Intent(activity, AutoChangeLight::class.java)
                val pendingIntent = PendingIntent.getBroadcast(activity, 2, intent, flags)
                alarmManager.cancel(pendingIntent)
                Toast.makeText(activity, getString(R.string.stop_theme), Toast.LENGTH_SHORT).show()
            } else {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val minute = calendar[Calendar.MINUTE]
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH]
                val day = calendar[Calendar.DAY_OF_MONTH]
                val timePickerDialog = TimePickerDialog(activity, { _, i, i1 ->
                    val timeToNotify = "$i:$i1"
                    val dateToNotify = day.toString() + "-" + (month + 1) + "-" + year
                    val dateAndTime = "$dateToNotify $timeToNotify"
                    val df = SimpleDateFormat("d-M-yyyy HH:mm")
                    time = df.parse(dateAndTime)!!.time
                    if (time < System.currentTimeMillis()) {
                        time += 86400000
                    }
                    editor.putBoolean("LightHistory", true).apply()
                    btTimeLight.text = formatTime24(i, i1)
                    ibTimeLight.setImageResource(R.drawable.ic_off)
                    editor.putString("LightText", formatTime24(i, i1)).apply()
                    setLight(time)
                }, hour, minute, true)//24:00
                timePickerDialog.show()
            }
        }
    }
    private fun startView() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            context?.let { OverlayService.start(it) }
        } else {
            Toast.makeText(
                context,
                resources.getString(R.string.not_support),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    @Suppress("DEPRECATION")
    private fun requestOverlayPermission() {
        if (isOverlayGranted()) return
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + resources.getString(R.string.app_package))
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }
    @Suppress("DEPRECATION")
    @SuppressLint("ResourceType")
    private fun changeButton() {
        if (getDarkMode()) {
            if (Build.VERSION.SDK_INT >= 29) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.system_in_settings),
                    Toast.LENGTH_SHORT
                ).show()
                this.startActivityForResult(Intent(Settings.ACTION_DISPLAY_SETTINGS), 0)
                displayLight()
            }
            if (Build.VERSION.SDK_INT in 23..28) {
                uiModeManager!!.nightMode = UiModeManager.MODE_NIGHT_NO
                displayLight()
            }
            if (Build.VERSION.SDK_INT <= 22) {
                val uiManager =
                    requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiManager.disableCarMode(DISABLE_CAR_MODE_GO_HOME)
                uiManager.nightMode = UiModeManager.MODE_NIGHT_NO
                displayLight()
            }
        } else {
            if (Build.VERSION.SDK_INT >= 29) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.system_in_settings),
                    Toast.LENGTH_SHORT
                ).show()
                this.startActivityForResult(Intent(Settings.ACTION_DISPLAY_SETTINGS), 0)
                displayDark()
            }
            if (Build.VERSION.SDK_INT in 23..28) {
                uiModeManager!!.nightMode = UiModeManager.MODE_NIGHT_YES
                displayDark()
            }
            if (Build.VERSION.SDK_INT <= 22) {
                val uiManager =
                    requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiManager.enableCarMode(0)
                uiManager.nightMode = UiModeManager.MODE_NIGHT_YES
                displayDark()
            }
        }
    }

    private fun getDarkMode(): Boolean {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> {
                return false
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                return true
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                return false
            }
        }
        return false
    }

    private fun displayDark() {
        tvMode.text = resources.getString(R.string.dark_mode_active)
        btChanger.text = resources.getString(R.string.change_to_light_mode)
    }

    private fun displayLight() {
        btChanger.text = resources.getString(R.string.change_to_dark_mode)
        tvMode.text = resources.getString(R.string.light_mode_active)
    }

    private fun display() {
        if (getDarkMode()) {
            displayDark()
        } else {
            displayLight()
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!isOverlayGranted()) {
                requireActivity().finish()
            }
        }
    }

    private fun setDark(timeInMillis: Long) {
        val intent = Intent(activity, AutoChangeDark::class.java)
        val pendingIntent = PendingIntent.getBroadcast(activity, 1, intent, flags)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            86400000,
            pendingIntent
        )
        Toast.makeText(activity, getString(R.string.start_theme), Toast.LENGTH_SHORT).show()
    }

    private fun setLight(timeInMillis: Long) {
        val intent = Intent(activity, AutoChangeLight::class.java)
        val pendingIntent = PendingIntent.getBroadcast(activity, 2, intent, flags)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            86400000,
            pendingIntent
        )
        Toast.makeText(activity, getString(R.string.start_theme), Toast.LENGTH_SHORT).show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val time: String
        val formattedMinute: String = if (minute / 10 == 0) {
            "0$minute"
        } else {
            "" + minute
        }
        time = when {
            hour == 0 -> {
                "12:$formattedMinute " + resources.getString(R.string.AM)
            }
            hour < 12 -> {
                "$hour:$formattedMinute " + resources.getString(R.string.AM)
            }
            hour == 12 -> {
                "12:$formattedMinute " + resources.getString(R.string.PM)
            }
            else -> {
                val temp = hour - 12
                "$temp:$formattedMinute " + resources.getString(R.string.PM)
            }
        }

        return time
    }

    private fun formatTime24(hour: Int, minute: Int): String {
        val formattedMinute: String = if (minute / 10 == 0) {
            "0$minute"
        } else {
            "" + minute
        }
        val formattedHour: String = if (hour / 10 == 0) {
            "0$hour"
        } else {
            "" + hour
        }
        return "$formattedHour:$formattedMinute"
    }
}