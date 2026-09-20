package com.example.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationHelper(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    fun getLocationFlow(): Flow<Location?> = callbackFlow {
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(location)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                trySend(null)
            }
        }

        try {
            val hasFineLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
            val hasCoarseLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (hasFineLocation || hasCoarseLocation) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000L,
                        10f,
                        listener
                    )
                }
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5000L,
                        10f,
                        listener
                    )
                }
            } else {
                trySend(null)
            }
        } catch (e: SecurityException) {
            trySend(null)
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(): Location? {
        return try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            null
        }
    }
}
