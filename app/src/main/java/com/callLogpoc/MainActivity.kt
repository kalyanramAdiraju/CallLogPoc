package com.callLogpoc


import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CallLog
import android.provider.CallLog.Calls.*

import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView


import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    var READ_CALL_LOG_CODE = 21;
    var stringBuffer: StringBuffer = StringBuffer()
    var textView:TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        textView=findViewById(R.id.textView)
        setupPermissions()
    }


    fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("TE", "Permission to record denied")
            makeRequest()
        } else {
            getCallHistoryData()
        }
    }

    fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG),
                READ_CALL_LOG_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_CALL_LOG_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("Permission ", "Permission has been denied by user")
                } else {
                    Log.i("Permission ", "Permission has been granted by user")
                    getCallHistoryData()


                }
            }
        }
    }

    private fun getCallHistoryData() {
        var numberString: String = ""
        var dateString: String = ""
        var typeString: String = ""
        var durationString: String = ""
        var dir: String = ""
        var dircode: Int
        var managedCursor: Cursor?=null

        try {
            val contacts = CallLog.Calls.CONTENT_URI
            managedCursor = applicationContext.contentResolver.query(contacts, null, null, null, CallLog.Calls.DATE + " DESC")
            var number: Number = managedCursor.getColumnIndex(NUMBER)
            var type: Number
            var date: Number = managedCursor.getColumnIndex(DATE)
            var duration: Number = managedCursor.getColumnIndex(DURATION)

            while (managedCursor.moveToNext()) {
                numberString = managedCursor.getString(number as Int)
                dateString = managedCursor.getString(date as Int)
                durationString = managedCursor.getString(duration as Int)
                type = managedCursor.getInt(managedCursor.getColumnIndex(TYPE))
                when (type) {
                    MISSED_TYPE -> dir = "Missed Calls"
                    INCOMING_TYPE -> dir = "Incoming Calls"
                    OUTGOING_TYPE -> dir = "OutGoing Calls"
                }
                stringBuffer.append("\nPhone Number:--- " + numberString + " \nCall Type:--- "
                        + dir + " \nCall Date:--- " + getDateFormatString(dateString)
                        + " \nCall duration in sec :--- " + durationString)
                stringBuffer.append("\n==================================================")
                textView?.setText(stringBuffer)

            }
            managedCursor.close()
        } catch (exception: SecurityException) {
            Log.e("exception", exception.toString())
        }



    }

    private fun getDateFormatString(dateString: String): String {
        var dateLong: Long = dateString.toLong()
        var simpleDateFormat: SimpleDateFormat
        simpleDateFormat = SimpleDateFormat("dd-MM-yy HH:mm")
        var finalDate: String = simpleDateFormat.format(Date(dateLong))
        return finalDate
    }
}
