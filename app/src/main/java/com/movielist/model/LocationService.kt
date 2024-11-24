package com.movielist.model
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Geocoder
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale

//Implimentation inspired by: https://www.youtube.com/watch?v=Jj14sw4Yxk0 , but changes have been made to modernise the code
class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        locationClient
            .getLocationUpdate(5000L)
            .catch { error ->
                error.printStackTrace()
            }.onEach { location ->
                val latitude = location.latitude
                val longitude = location.longitude

                //Get country and region
                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                val adressList = geocoder.getFromLocation(latitude,longitude,1)

                val country = adressList?.getOrNull(0)?.countryName ?: "Country unknown"
                val region = adressList?.getOrNull(0)?.adminArea ?: ""


                // Send location update as a broadcast
                val intent = Intent(LOCATION_UPDATE).apply {
                    putExtra(EXTRA_LATITUDE, latitude)
                    putExtra(EXTRA_LONGITUDE, longitude)
                    putExtra(EXTRA_COUNTRY, country)
                    putExtra(EXTRA_REGION, region)
                }

                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                PostNotification(
                    context = this,
                    contentTitle = "Location detected",
                    contentText = "Cordinates: ($latitude, $longitude), \n Location: $region, $country",
                    importance = NotificationManager.IMPORTANCE_LOW
                )

                stopSelf()
            }.launchIn(serviceScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun stop() {
        stopSelf()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val LOCATION_UPDATE = "com.movielist.LOCATION_UPDATE"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val EXTRA_COUNTRY = "country"
        const val EXTRA_REGION = "region"

    }

}