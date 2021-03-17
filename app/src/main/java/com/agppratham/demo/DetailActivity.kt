package com.agppratham.demo

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*


class DetailActivity : AppCompatActivity() {

    private var uri: String? = null
    var c: Calendar? = null
    var isFrom: String? = null
    val CODE_WRITE_SETTINGS_PERMISSION = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        c = Calendar.getInstance()
        isFrom = intent.getStringExtra("setData")

        edtTime.setOnClickListener {

            var mHour = c?.get(Calendar.HOUR_OF_DAY)
            var mMinutes = c?.get(Calendar.MINUTE)


            var dialog: TimePickerDialog = TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Dialog,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                    c?.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c?.set(Calendar.MINUTE, minute);

                    updateTime(hourOfDay, minute);
                },
                mHour!!,
                mMinutes!!,
                false
            )
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        btnSave.setOnClickListener {
            /*if (checkSystemWritePermission()) {
                startAlarm()
            }*/
            youDesirePermissionCode(this)
        }

        edtMusic.setOnClickListener {
            var intent = Intent(this, MusicListActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    private fun checkSystemWritePermission(): Boolean {
        var retVal = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this)
            Log.d("TAG", "Can Write Settings: $retVal")
            if (retVal) {
                Toast.makeText(this, "Write allowed :-)", Toast.LENGTH_LONG).show()
            } else {
                openAndroidPermissionsMenu()
            }
        }
        return retVal
    }

    private fun openAndroidPermissionsMenu() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + getPackageName())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun youDesirePermissionCode(context: Activity) {
        val permission: Boolean
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_SETTINGS
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (permission) {
            //do your code
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)

                context.startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION)
            } else {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.WRITE_SETTINGS),
                    CODE_WRITE_SETTINGS_PERMISSION
                )
            }
        }
    }

    private fun updateTime(hours: Int, mins: Int) {
        var hours = hours
        var timeSet = ""
        if (hours > 12) {
            hours -= 12
            timeSet = "PM"
        } else if (hours == 0) {
            hours += 12
            timeSet = "AM"
        } else if (hours == 12) timeSet = "PM" else timeSet = "AM"
        var minutes = ""
        minutes = if (mins < 10) "0$mins" else mins.toString()

        // Append in a StringBuilder
        val aTime = StringBuilder().append(hours).append(':')
            .append(minutes).append(" ").append(timeSet).toString()
        edtTime.setText(aTime)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            edtMusic.setText(data?.getStringExtra("text"))
            uri = data?.getStringExtra("uri")
        }
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)) {
            Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
            startAlarm()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startAlarm()
        }
    }

    private fun startAlarm() {
        // SET TIME HERE
        c?.set(Calendar.DAY_OF_WEEK, 2);
        var time = edtTime.text.toString().trim()
        if (time.equals("")) {
            Toast.makeText(this, "You did not set time!", Toast.LENGTH_LONG).show();
        } else {
            val intent = Intent(this, AlarmReceiver::class.java)
            var alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
            val manager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            manager?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                c?.timeInMillis!!, AlarmManager.INTERVAL_DAY * 7,
                alarmIntent
            )

            val resultIntent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            if (isFrom.equals("btn1")) {
                resultIntent.putExtra("setData", "btn1")
            } else if (isFrom.equals("btn2")) {
                resultIntent.putExtra("setData", "btn2")
            } else if (isFrom.equals("btn3")) {
                resultIntent.putExtra("setData", "btn3")
            } else if (isFrom.equals("btn4")) {
                resultIntent.putExtra("setData", "btn4")
            }
            resultIntent.putExtra("time", time)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}