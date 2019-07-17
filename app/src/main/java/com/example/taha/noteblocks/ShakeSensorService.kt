package com.example.taha.noteblocks

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import android.widget.Toast

class ShakeSensorService:Service(),SensorEventListener{
    var sensor: Sensor? = null
    var sensorManager: SensorManager? = null
    override fun onBind(intent: Intent?): IBinder? {
    return null!!
}
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //لسنا بحاجة إليها الأن.
    }
    //متغيرات مبدئية لحساب التغير في قيمة حساسات الجهاز بالنسبة للمحاور الثلاثة
    var xold = 0.0
    var yold = 0.0
    var zold = 0.0
    var threadShould = 1510.0
    var oldtime: Long = 0
    override fun onSensorChanged(event: SensorEvent?) {
        //تخزين إحداثيات الجهاز الحالية
        var x = event!!.values[0]
        var y = event!!.values[1]
        var z = event!!.values[2]

        // قراءة الوقت الحالي من الجهاز
        var currentTime = System.currentTimeMillis()

        // إعطاء فرصة للجهاز قبل تشغيل الدالة مرة أخرى
        if ((currentTime - oldtime) > 100) {
            var timeDiff = currentTime - oldtime
            oldtime = currentTime

            // يتم تحديد ما إذا كانت سرعة حركة الجهاز كافية لتحديد الأمر عن طريق المعادلة
            var speed = Math.abs(x + y + z - xold - yold - zold) / timeDiff * 5000
            Log.v("speed = ",""+speed)
            if (speed > threadShould) {
                var v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                v.vibrate(300)
                Toast.makeText(applicationContext, "سيتم تشغيل صفحة الملاحظة الجديدة", Toast.LENGTH_LONG).show()
                var newNoteIntent = Intent(this, AddNote::class.java)
                startActivity(newNoteIntent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        isServiceRunning=true

    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager!!.unregisterListener(this)
        isServiceRunning=false

    }
    companion object{
        var isServiceRunning=false
    }
}