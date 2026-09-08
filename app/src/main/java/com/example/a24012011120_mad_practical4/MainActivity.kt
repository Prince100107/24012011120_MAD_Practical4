package com.example.a24012011120_mad_practical4

import android.app.PendingIntent
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var textAlarm: TextView
    lateinit var cardSetAlarm: MaterialCardView
    lateinit var alarmTimeValue: TextView

    lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textAlarm=findViewById<TextClock>(R.id.textView4)
        cardSetAlarm=findViewById(R.id.card_list)
        alarmTimeValue=findViewById<TextView>(R.id.alarmTimeValue)
        cardSetAlarm.visibility= View.GONE
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        findViewById<MaterialButton>(R.id.btnSetAlarm).setOnClickListener {
            checkExactAlarmPermission()
            showTimedialog()
        }
        findViewById<MaterialButton>(R.id.btnCancelAlarm).setOnClickListener {
            setAlarm(-1, AlarmBroadcastReceiver.STOP_VAL)
        }
    }
    private fun showTimedialog(){
        val cldr: Calendar= Calendar.getInstance()
        val h:Int = cldr.get(Calendar.HOUR_OF_DAY)
        val m:Int=cldr.get(Calendar.MINUTE)
        val picker= TimePickerDialog(
            this,
            {tp,sHour,sMinute->sendDialogDataToActivity(sHour,sMinute)},
            h,m,false
        )
        picker.show()
    }
    private fun checkExactAlarmPermission(): Boolean{
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return false
            }
            else
                return true
        }
        return false
    }
    private fun sendDialogDataToActivity(hour:Int,minute:Int){
        val calendar=java.util.Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)

        cardSetAlarm.visibility= View.VISIBLE
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        alarmTimeValue.text = sdf.format(calendar.time)
        setAlarm(calendar.timeInMillis, AlarmBroadcastReceiver.START_VAL)

    }

    fun setAlarm(timeInMillis:Long,start_stop:String){
        val intent= Intent(this, AlarmBroadcastReceiver::class.java)

        if(start_stop == AlarmBroadcastReceiver.START_VAL){
            intent.putExtra(AlarmBroadcastReceiver.SERVICE_KEY, AlarmBroadcastReceiver.START_VAL)

            val pendingIntent= PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_IMMUTABLE)
            if(checkExactAlarmPermission())
            {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this, "No permission to set Alarm", Toast.LENGTH_LONG).show()
            }
        }
        else if(start_stop == AlarmBroadcastReceiver.STOP_VAL)
        {
            intent.putExtra(AlarmBroadcastReceiver.SERVICE_KEY, AlarmBroadcastReceiver.STOP_VAL)

            val pendingIntent= PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_IMMUTABLE)
            sendBroadcast(intent)
            pendingIntent?.let { alarmManager.cancel(it) }
            cardSetAlarm.visibility= View.GONE
            alarmTimeValue.text="--:-- --"
        }
    }


}