package com.appedia.runtracker.util

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.appedia.runtracker.util.Constants.REQUEST_CODE_LOCATION_PERMISSION
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

object PermissionUtils {

    private fun aboveOrEqualToAndroidQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun hasLocationPermission(context: Context) =
        if (aboveOrEqualToAndroidQ()) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

    fun requestLocationPermissions(host: Fragment) {
        if (aboveOrEqualToAndroidQ()) {
            EasyPermissions.requestPermissions(
                host,
                "You need location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }else{
            EasyPermissions.requestPermissions(
                host,
                "You need location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    fun somePermissionDeniedForever(host: Fragment, perms: MutableList<String>): Boolean {
        return EasyPermissions.somePermissionPermanentlyDenied(host,perms)
    }

    fun showAppSettingsDialog(fragment: Fragment) {
        AppSettingsDialog.Builder(fragment).build().show()
    }

    fun onRequestPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray, receivers: Fragment) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,receivers)
    }

}