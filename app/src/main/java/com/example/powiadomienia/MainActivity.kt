package com.example.powiadomienia

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.truncate

class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "channel"
        const val NOTIFICATION_1 = 1
        const val NOTIFICATION_2 = 2
        const val NOTIFICATION_3 = 3
        const val KEY_TEXT = "text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createNotificationChannel()
        getPermission()

        val button1 = findViewById<Button>(R.id.notificationButton1)
        button1.setOnClickListener{
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Moje powiadomienie")
                .setContentText("To jest treść mojego powiadomienia")
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)){
                if(checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                    notify(NOTIFICATION_1, builder.build())
                }
            }
        }

        val button2 = findViewById<Button>(R.id.notificationButton2)
        button2.setOnClickListener{
            val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText("To jest przykład powiadomienia ze stylem. Pozwala on zmieścić więcej informacji")

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Powiadomienie")
                .setStyle(bigTextStyle)

            with(NotificationManagerCompat.from(this)){
                notify(NOTIFICATION_2, builder.build())
            }
        }

        val button3 = findViewById<Button>(R.id.notificationButton3)
        button3.setOnClickListener {
            val remoteInput = RemoteInput.Builder(KEY_TEXT)
                .setLabel("Wprowadź swoje imię: ")
                .build()

            val replyIntent = Intent(this, MainActivity::class.java)
            val replyPendingIntent = PendingIntent.getActivity(this, 0, replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

            val action = NotificationCompat.Action.Builder(
                android.R.drawable.ic_input_get, "Odpowiedź", replyPendingIntent
            ).addRemoteInput(remoteInput).build()

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Powiadomienie")
                .setContentText("Odpowiedz proszę")
                .addAction(action)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)){
                notify(NOTIFICATION_3, builder.build())
            }
        }

        //handleIntentReplay()


    }

    override fun onResume() {
        super.onResume()
        handleIntentReplay()

    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Channel"
            val descriptionText = "Mój kanał powiadomień"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPermission(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun handleIntentReplay(){
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        if(remoteInput != null){
            val replyText = remoteInput.getCharSequence(KEY_TEXT).toString()
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = replyText
            with(NotificationManagerCompat.from(this)){
                cancel(NOTIFICATION_3)
            }
        }
    }
}