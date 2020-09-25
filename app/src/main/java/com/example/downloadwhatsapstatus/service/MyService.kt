package com.example.downloadwhatsapstatus.service

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.example.downloadwhatsapstatus.MainActivity
import com.example.downloadwhatsapstatus.R
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

interface OnStatusFoundListener {
    fun getListOfPaths(listOfPaths: ArrayList<String>)
}

class MyService() : Service() {

    //RemoteView
    private lateinit var notiRemoteViews: RemoteViews

    //Notification Builder
    private lateinit var builder: NotificationCompat.Builder


    //fileObserver
    var observer: FileObserver? = null

    private lateinit var notificationManager: NotificationManagerCompat

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (action != null) when (action) {
                ACTION_START_FOREGROUND_SERVICE -> {
                    startForegroundService()
                    Toast.makeText(
                        applicationContext,
                        "Foreground service is started.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopForegroundService()
                    Toast.makeText(
                        applicationContext,
                        "Foreground service is stopped.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                ACTION_PLAY -> Toast.makeText(
                    applicationContext,
                    "You click Play button.",
                    Toast.LENGTH_LONG
                ).show()
                ACTION_PAUSE -> Toast.makeText(
                    applicationContext,
                    "You click Pause button.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    /*fun getImagePath(): ArrayList<String> {
        // image path list

        observer!!.startWatching()

        // return imgPath List
        return list
    }
*/

    override fun onDestroy() {
        super.onDestroy()
        observer!!.stopWatching()
    }

    /* Used to build and start foreground service. */
    private fun startForegroundService() {

        // Create notification builder.
        builder = NotificationCompat.Builder(this)

        //remoteView init
        notiRemoteViews =
            RemoteViews(this.applicationContext.packageName, R.layout.custom_noti_view)

        //observe Status Folder
        val list: ArrayList<String> = ArrayList()

        val fileToStatus =
            File(Environment.getExternalStorageDirectory().toString() + WHATSAPP_STATUS_FOLDER_PATH)
        // fetching file path from storage
        observer = object : FileObserver(fileToStatus.absolutePath) {
            override fun onEvent(event: Int, file: String?) {

                Log.e("OnEvent>>", "${event==FileObserver.CREATE} <<")

                val listFile = fileToStatus.listFiles()
                Log.e("FilesInFolder>>", "${listFile} <<<SizE<<<")

                if (listFile != null && listFile.isNullOrEmpty()) {
                    Arrays.sort(listFile)
                }
                if (listFile != null) {
                    for (imgFile in listFile) {
                        val model = imgFile.absolutePath
                        list.add(model)

                    }

                    listener!!.getListOfPaths(list)
                    updateNotification(builder, list.last())

                }


            }
        }

        observer!!.startWatching()

        Log.e("SizeOfStatus>> ", "${list.size} <<<")


        Log.e(TAG_FOREGROUND_SERVICE, "Start foreground service.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service")
        } else {

            // Create notification default intent.
            val intent = Intent()
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            //update noti image
                updateNotification(builder, list.last())

            // Make notification show big text.
            val bigTextStyle = NotificationCompat.BigTextStyle()
            //bigTextStyle.setBigContentTitle("Music player implemented by foreground service.")
            //bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.")
            // Set big text style.
            builder.setStyle(bigTextStyle)
            builder.setWhen(System.currentTimeMillis())
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setCustomContentView(notiRemoteViews)
            val largeIconBitmap =
                BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
            builder.setLargeIcon(largeIconBitmap)
            // Make the notification max priority.
            builder.priority = Notification.PRIORITY_MAX
            // Make head-up notification.
            builder.setFullScreenIntent(pendingIntent, true)

            // Add Play button intent in notification.
            val playIntent = Intent(this, MyService::class.java)
            playIntent.action = ACTION_PLAY
            val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0)
            val playAction = NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                pendingPlayIntent
            )
            builder.addAction(playAction)

            // Add Pause button intent in notification.
            val pauseIntent = Intent(this, MyService::class.java)
            pauseIntent.action = ACTION_PAUSE
            val pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
            val prevAction = NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                pendingPrevIntent
            )
            builder.addAction(prevAction)

            // Build the notification.
            val notification = builder.build()

            //get Status
            notificationManager.notify(1, notification)

            // Start foreground service.
            startForeground(1, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String) {
        val resultIntent = Intent(this, MainActivity::class.java)
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val chan =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setCustomContentView(notiRemoteViews)
            .setContentIntent(resultPendingIntent) //intent
            .build()
        notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, notificationBuilder.build())
        startForeground(1, notification)
    }

    private fun stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.")

        // Stop foreground service and remove the notification.
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()
    }

    companion object {
        private const val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val WHATSAPP_STATUS_FOLDER_PATH = "/WhatsApp/Media/.Statuses/"
        //listener
        var listener: OnStatusFoundListener?=null


    }

    fun updateNotification(
        notificationBuilder: NotificationCompat.Builder,
        imageUri: String

    ) {

        Log.e("NotiUri>>", "$imageUri <<<")

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("WhatsApp Status")
            .setContentText("Download Whatapp Status")
            .setOngoing(true)
            //.setGroup(downloadNotification.groupId.toString())
            .setGroupSummary(false)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setCustomContentView(notiRemoteViews)

        //notificationBuilder.setProgress(0, 0, false)
        notiRemoteViews.setImageViewUri(R.id.remote_iv_main, Uri.parse(imageUri))

        notificationManager.notify(1, notificationBuilder.build())
    }


}