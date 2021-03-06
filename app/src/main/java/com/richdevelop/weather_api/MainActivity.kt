package com.richdevelop.weather_api

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.richdevelop.weather_api.fragments.TimeWeatherFragment
import me.yokeyword.fragmentation.Fragmentation
import me.yokeyword.fragmentation.SupportActivity
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : SupportActivity() {

    companion object {
        private val REQUIRED_SDK_PERMISSIONS =
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
        private const val REQUEST_CODE_ASK_PERMISSIONS = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.yellow_bg))
        setContentView(R.layout.layout_main)

        Fragmentation.builder()
            // show stack view. Mode: BUBBLE, SHAKE, NONE
            .stackViewMode(Fragmentation.BUBBLE)
            .debug(BuildConfig.DEBUG)
            .install()

        checkPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                for (index in permissions.indices.reversed()) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(
                            this, resources.getString(R.string.permissions), Toast.LENGTH_LONG
                        ).show()
                        finish()
                        return
                    }
                }
                // all permissions were granted
                val top = topFragment
                if (top == null) {
                    loadRootFragment(
                        R.id.layout_main, TimeWeatherFragment()
                    )  //load root Fragment
                }
            }
        }
    }

    private fun checkPermissions() {
        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (missingPermissions.isNotEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults
            )
        }
    }
}
