package com.simplemobiletools.commons.dialogs

import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.app.ActivityCompat
import com.agppratham.demo.Constants.ALARM_SOUND_TYPE_NOTIFICATION
import com.agppratham.demo.Constants.PERMISSION_READ_STORAGE
import com.agppratham.demo.Constants.SILENT
import com.agppratham.demo.R
import com.agppratham.demo.activity.AlarmActivity
import com.agppratham.demo.activity.BaseActivity
import com.agppratham.demo.helper.baseConfig
import com.agppratham.demo.helper.getAdjustedPrimaryColor
import com.agppratham.demo.helper.getDefaultAlarmSound
import com.agppratham.demo.model.AlarmSound
import com.agppratham.demo.model.RadioItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_select_alarm_sound.view.*
import java.util.*

class SelectAlarmSoundDialog(val activity: AlarmActivity, val currentUri: String, val audioStream: Int, val pickAudioIntentId: Int,
                             val type: Int, val loopAudio: Boolean, val onAlarmPicked: (alarmSound: AlarmSound?) -> Unit,
                             val onAlarmSoundDeleted: (alarmSound: AlarmSound) -> Unit) : BaseActivity() {
    private val ADD_NEW_SOUND_ID = -2
    private val view = activity.layoutInflater.inflate(R.layout.dialog_select_alarm_sound, null)
    private var systemAlarmSounds = ArrayList<AlarmSound>()
    private var yourAlarmSounds = ArrayList<AlarmSound>()
    private var mediaPlayer: MediaPlayer? = null
    private val config = activity.baseConfig
    private val dialog: AlertDialog

    init {
       getAlarmSounds(type) {
            systemAlarmSounds = it
            gotSystemAlarms()
        }

        view.dialog_select_alarm_your_label.setTextColor(activity.getAdjustedPrimaryColor())
        view.dialog_select_alarm_system_label.setTextColor(activity.getAdjustedPrimaryColor())

        addYourAlarms()

        dialog = AlertDialog.Builder(activity)
                .setOnDismissListener { mediaPlayer?.stop() }
                .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
                    window?.volumeControlStream = audioStream
                }
    }

    private fun addYourAlarms() {
        view.dialog_select_alarm_your_radio.removeAllViews()
        val token = object : TypeToken<ArrayList<AlarmSound>>() {}.type
        yourAlarmSounds = Gson().fromJson<ArrayList<AlarmSound>>(config.yourAlarmSounds, token) ?: ArrayList()
        yourAlarmSounds.add(AlarmSound(ADD_NEW_SOUND_ID, activity.getString(R.string.add_new_sound), ""))
        yourAlarmSounds.forEach {
            addAlarmSound(it, view.dialog_select_alarm_your_radio)
        }
    }

    private fun gotSystemAlarms() {
        systemAlarmSounds.forEach {
            addAlarmSound(it, view.dialog_select_alarm_system_radio)
        }
    }

    private fun addAlarmSound(alarmSound: AlarmSound, holder: ViewGroup) {
        val radioButton = (activity.layoutInflater.inflate(R.layout.item_select_alarm_sound, null) as AppCompatRadioButton).apply {
            text = alarmSound.title
            isChecked = alarmSound.uri == currentUri
            id = alarmSound.id
            setOnClickListener {
                alarmClicked(alarmSound)

                if (holder == view.dialog_select_alarm_system_radio) {
                    view.dialog_select_alarm_your_radio.clearCheck()
                } else {
                    view.dialog_select_alarm_system_radio.clearCheck()
                }
            }

         /*   if (alarmSound.id != -2 && holder == view.dialog_select_alarm_your_radio) {
                setOnLongClickListener {
                    val items = arrayListOf(RadioItem(1, context.getString(R.string.remove)))

                    RadioGroupDialog(activity, items) {
                        removeAlarmSound(alarmSound)
                    }
                    true
                }
            }*/
        }

        holder.addView(radioButton, RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun alarmClicked(alarmSound: AlarmSound) {
        when {
            alarmSound.uri == SILENT -> mediaPlayer?.stop()
            alarmSound.id == ADD_NEW_SOUND_ID -> {
                val action = Intent.ACTION_OPEN_DOCUMENT
                Intent(action).apply {
                    type = "audio/*"
                    activity.startActivityForResult(this, pickAudioIntentId)
                    flags = flags or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                }
                dialog.dismiss()
            }
            else -> try {
                mediaPlayer?.reset()
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer().apply {
                        setAudioStreamType(audioStream)
                        isLooping = loopAudio
                    }
                }

                mediaPlayer?.apply {
                    setDataSource(activity, Uri.parse(alarmSound.uri))
                    prepare()
                    start()
                }
            } catch (e: Exception) {
               // activity.showErrorToast(e)
            }
        }
    }

    private fun removeAlarmSound(alarmSound: AlarmSound) {
        val token = object : TypeToken<ArrayList<AlarmSound>>() {}.type
        yourAlarmSounds = Gson().fromJson<ArrayList<AlarmSound>>(config.yourAlarmSounds, token) ?: ArrayList()
        yourAlarmSounds.remove(alarmSound)
        config.yourAlarmSounds = Gson().toJson(yourAlarmSounds)
        addYourAlarms()

        if (alarmSound.id == view.dialog_select_alarm_your_radio.checkedRadioButtonId) {
            view.dialog_select_alarm_your_radio.clearCheck()
            view.dialog_select_alarm_system_radio.check(systemAlarmSounds.firstOrNull()?.id ?: 0)
        }

        onAlarmSoundDeleted(alarmSound)
    }

    private fun dialogConfirmed() {
        if (view.dialog_select_alarm_your_radio.checkedRadioButtonId != -1) {
            val checkedId = view.dialog_select_alarm_your_radio.checkedRadioButtonId
            onAlarmPicked(yourAlarmSounds.firstOrNull { it.id == checkedId })
        } else {
            val checkedId = view.dialog_select_alarm_system_radio.checkedRadioButtonId
            onAlarmPicked(systemAlarmSounds.firstOrNull { it.id == checkedId })
        }
    }
    fun getAlarmSounds(type: Int, callback: (ArrayList<AlarmSound>) -> Unit) {
        val alarms = ArrayList<AlarmSound>()
        val manager = RingtoneManager(activity)
        manager.setType(if (type == ALARM_SOUND_TYPE_NOTIFICATION) RingtoneManager.TYPE_NOTIFICATION else RingtoneManager.TYPE_ALARM)

        try {
            val cursor = manager.cursor
            var curId = 1
            val silentAlarm = AlarmSound(curId++, activity.getString(R.string.no_sound), SILENT)
            alarms.add(silentAlarm)

            val defaultAlarm = activity.getDefaultAlarmSound(type)
            config.appId
            alarms.add(defaultAlarm)

            while (cursor.moveToNext()) {
                val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                var uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
                val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
                if (!uri.endsWith(id)) {
                    uri += "/$id"
                }

                val alarmSound = AlarmSound(curId++, title, uri)
                alarms.add(alarmSound)
            }
            callback(alarms)
        } catch (e: Exception) {
            if (e is SecurityException) {
                handlePermission(PERMISSION_READ_STORAGE) {
                    if (it) {
                        getAlarmSounds(type, callback)
                    } else {
                      //  showErrorToast(e)
                        callback(ArrayList())
                    }
                }
            } else {
             //  showErrorToast(e)
                callback(ArrayList())
            }
        }
    }
}
