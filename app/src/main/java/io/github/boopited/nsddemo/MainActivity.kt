package io.github.boopited.nsddemo

import android.net.nsd.NsdServiceInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import io.github.boopited.nsd.NsdClient
import io.github.boopited.nsd.NsdServer

class MainActivity : AppCompatActivity(), NsdServer.Callback, NsdClient.Callback {

    private val serverStart: Button by lazy { findViewById<Button>(R.id.start_server) }
    private val serverStop: Button by lazy { findViewById<Button>(R.id.stop_server) }
    private val discoveryStart: Button by lazy { findViewById<Button>(R.id.start_discovery) }
    private val discoveryStop: Button by lazy { findViewById<Button>(R.id.stop_discovery) }

    private val asClient: CheckBox by lazy { findViewById<CheckBox>(R.id.as_client) }

    private val nsdServer by lazy { NsdServer(this, SERVICE_NAME, SERVICE_TYPE, this) }
    private val nsdClient by lazy { NsdClient(this, SERVICE_TYPE, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        asClient.isChecked = false
        asClient.setOnCheckedChangeListener { buttonView, isChecked ->
            serverStart.isEnabled = !isChecked
            serverStop.isEnabled = !isChecked
            discoveryStart.isEnabled = isChecked
            discoveryStop.isEnabled = isChecked
            if (isChecked) {
                nsdServer.stop()
            } else {
                nsdClient.stop()
            }
        }

        serverStart.setOnClickListener { nsdServer.start(SERVICE_PORT) }
        serverStop.setOnClickListener { nsdServer.stop() }
        discoveryStart.setOnClickListener { nsdClient.start() }
        discoveryStop.setOnClickListener { nsdClient.stop() }
    }

    override fun onRegistered(serviceInfo: NsdServiceInfo, errorCode: Int) {
        Log.i(TAG, "$serviceInfo")
        Toast.makeText(this,
            if (errorCode == 0) "Service started" else "Service start fail",
            Toast.LENGTH_SHORT).show()
    }

    override fun onUnregistered(serviceInfo: NsdServiceInfo, errorCode: Int) {
        Toast.makeText(this,
            if (errorCode == 0) "Service stopped" else "Service stop fail",
            Toast.LENGTH_SHORT).show()
    }

    override fun onDiscoveryStart(success: Boolean, errorCode: Int) {
        Toast.makeText(this,
            if (errorCode == 0) "Discovery started" else "Discovery start fail",
            Toast.LENGTH_SHORT).show()
    }

    override fun onDiscoveryStop(success: Boolean, errorCode: Int) {
        Toast.makeText(this,
            if (errorCode == 0) "Discovery stopped" else "Discovery stop fail",
            Toast.LENGTH_SHORT).show()
    }

    override fun onServiceFound(serviceInfo: NsdServiceInfo) {
        Log.i(TAG, "Found $serviceInfo")
    }

    override fun onServiceLost(serviceInfo: NsdServiceInfo) {
        Log.i(TAG, "Lost $serviceInfo")
    }

    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
        Log.i(TAG, "Resovled $serviceInfo")
    }

    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        Log.i(TAG, "Failed resolve $serviceInfo, $errorCode")
    }

    override fun onDestroy() {
        nsdClient.stop()
        nsdServer.stop()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val SERVICE_NAME = "I'm a printer"
        private const val SERVICE_TYPE = "_ipp._tcp"
        private const val SERVICE_PORT = 1234
    }
}
