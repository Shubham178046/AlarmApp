package com.agppratham.demo.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.agppratham.demo.helper.getPermissionString
import com.agppratham.demo.helper.hasPermission

open class BaseActivity : AppCompatActivity() {
    var isAskingPermissions = false
    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    private val GENERIC_PERM_HANDLER = 100
    override fun onStop() {
        super.onStop()
        actionOnPermission = null
    }
    fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (hasPermission(permissionId)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(this, arrayOf(getPermissionString(permissionId)), GENERIC_PERM_HANDLER)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAskingPermissions = false
        if (requestCode == GENERIC_PERM_HANDLER && grantResults.isNotEmpty()) {
            actionOnPermission?.invoke(grantResults[0] == 0)
        }
    }
}