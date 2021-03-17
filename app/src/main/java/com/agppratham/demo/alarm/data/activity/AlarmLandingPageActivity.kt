package com.agppratham.demo.alarm.data.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.agppratham.demo.Constants.SILENT
import com.agppratham.demo.Constants.isOreoPlus
import com.agppratham.demo.R
import com.agppratham.demo.alarm.data.Alarm
import com.agppratham.demo.helper.getAdjustedPrimaryColor
import kotlinx.android.synthetic.main.activity_alarm_landing_page.*

class AlarmLandingPageActivity : AppCompatActivity() {
    private val increaseVolumeHandler = Handler()
    private val maxReminderDurationHandler = Handler()
    private val swipeGuideFadeHandler = Handler()
    private var didVibrate = false
    private val INCREASE_VOLUME_DELAY = 3000L
    private var alarm: Alarm? = null
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var lastVolumeValue = 0.1f
    private var dragDownX = 0f
    companion object
    {
        fun launchIntent(context: Context?): Intent {
            val i = Intent(context, AlarmLandingPageActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            return i
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_landing_page)
        showOverLockscreen()
        val maxDuration = 8000
        maxReminderDurationHandler.postDelayed({
           finish()
        }, maxDuration * 1000L)
        setupAlarmButtons()
        setupEffects()
    }
    private fun setupAlarmButtons() {
        reminder_stop.visibility = View.GONE
        reminder_draggable_background.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulsing_animation))
        reminder_draggable_background.setColorFilter(getAdjustedPrimaryColor(), PorterDuff.Mode.SRC_IN)

        reminder_dismiss.setColorFilter(resources.getColor(R.color.accent), PorterDuff.Mode.SRC_IN)
        reminder_draggable.setColorFilter(resources.getColor(R.color.accent), PorterDuff.Mode.SRC_IN)
        reminder_snooze.setColorFilter(resources.getColor(R.color.accent), PorterDuff.Mode.SRC_IN)

        var minDragX = 0f
        var maxDragX = 0f
        var initialDraggableX = 0f

        reminder_dismiss.onGlobalLayout {
            minDragX = reminder_snooze.left.toFloat()
            maxDragX = reminder_dismiss.left.toFloat()
            initialDraggableX = reminder_draggable.left.toFloat()
        }

        reminder_draggable.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dragDownX = event.x
                    reminder_draggable_background.animate().alpha(0f)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    dragDownX = 0f
                    if (!didVibrate) {
                        reminder_draggable.animate().x(initialDraggableX).withEndAction {
                            reminder_draggable_background.animate().alpha(0.2f)
                        }

                        reminder_guide.animate().alpha(1f).start()
                        swipeGuideFadeHandler.removeCallbacksAndMessages(null)
                        swipeGuideFadeHandler.postDelayed({
                            reminder_guide.animate().alpha(0f).start()
                        }, 2000L)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    reminder_draggable.x = Math.min(maxDragX, Math.max(minDragX, event.rawX - dragDownX))
                    if (reminder_draggable.x >= maxDragX - 50f) {
                        if (!didVibrate) {
                            reminder_draggable.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            didVibrate = true
                           finish()
                        }

                    } else if (reminder_draggable.x <= minDragX + 50f) {
                        if (!didVibrate) {
                            reminder_draggable.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            didVibrate = true
                        }
                    }
                }
            }
            true
        }
    }
    private fun setupEffects() {

       // val doVibrate = if (alarm != null) alarm!!.vibrate else config.timerVibrate
     //   if (doVibrate) {
            val pattern = LongArray(2) { 500 }
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                vibrator?.vibrate(pattern, 0)
            }
    //    }

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if (soundUri.toString() != SILENT) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_ALARM)
                    setDataSource(this@AlarmLandingPageActivity, soundUri)
                    setVolume(lastVolumeValue, lastVolumeValue)
                    isLooping = true
                    prepare()
                    start()
                }
            } catch (e: Exception) {

            }
                scheduleVolumeIncrease()
        }
    }
    fun View.onGlobalLayout(callback: () -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        })
    }
    private fun scheduleVolumeIncrease() {
        increaseVolumeHandler.postDelayed({
            lastVolumeValue = Math.min(lastVolumeValue + 0.1f, 1f)
            mediaPlayer?.setVolume(lastVolumeValue, lastVolumeValue)
            scheduleVolumeIncrease()
        }, INCREASE_VOLUME_DELAY)
    }
    fun showOverLockscreen() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }
    override fun onDestroy() {
        super.onDestroy()
        increaseVolumeHandler.removeCallbacksAndMessages(null)
        maxReminderDurationHandler.removeCallbacksAndMessages(null)
        swipeGuideFadeHandler.removeCallbacksAndMessages(null)
        destroyEffects()
    }
    private fun destroyEffects() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
    }

}