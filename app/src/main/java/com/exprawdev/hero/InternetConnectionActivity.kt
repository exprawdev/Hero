
package com.exprawdev.hero

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class InternetConnectionActivity : Activity() {

    private lateinit var connectionStatus: TextView
    private lateinit var networkType: TextView
    private lateinit var internetAccess: TextView
    private lateinit var validatedStatus: TextView

    private lateinit var connectivityManager: ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            runOnUiThread {
                updateConnectionInfo()
            }
        }

        override fun onLost(network: Network) {
            runOnUiThread {
                updateConnectionInfo()
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            capabilities: NetworkCapabilities
        ) {
            runOnUiThread {
                updateConnectionInfo()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityManager = getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 24)
        }

        val title = TextView(this).apply {
            text = "INTERNET MONITOR"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Live network connection information"
            textSize = 13f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 28)
        }

        connectionStatus = createValueText()
        networkType = createValueText()
        internetAccess = createValueText()
        validatedStatus = createValueText()

        root.addView(title)
        root.addView(subtitle)

        addInfoCard(root, "CONNECTION STATUS", connectionStatus)
        addInfoCard(root, "NETWORK TYPE", networkType)
        addInfoCard(root, "INTERNET ACCESS", internetAccess)
        addInfoCard(root, "NETWORK VALIDATION", validatedStatus)

        val note = TextView(this).apply {
            text = "HERO checks Android network status. " +
                "A validated network does not guarantee that every website or service is reachable."
            textSize = 12f
            setTextColor(Color.LTGRAY)
            setPadding(4, 20, 4, 8)
        }

        root.addView(note)

        scrollView.addView(root)
        setContentView(scrollView)

        updateConnectionInfo()
    }

    override fun onStart() {
        super.onStart()

        try {
            connectivityManager.registerDefaultNetworkCallback(
                networkCallback
            )
        } catch (_: Exception) {
            updateConnectionInfo()
        }
    }

    override fun onStop() {
        try {
            connectivityManager.unregisterNetworkCallback(
                networkCallback
            )
        } catch (_: Exception) {
            // The callback may not have been registered.
        }

        super.onStop()
    }

    private fun updateConnectionInfo() {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let {
            connectivityManager.getNetworkCapabilities(it)
        }

        if (network == null || capabilities == null) {
            connectionStatus.text = "Disconnected"
            networkType.text = "None"
            internetAccess.text = "Unavailable"
            validatedStatus.text = "Not validated"
            return
        }

        connectionStatus.text = "Connected"

        networkType.text = when {
            capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) -> "Wi-Fi"

            capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) -> "Mobile Data"

            capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_ETHERNET
            ) -> "Ethernet"

            capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_BLUETOOTH
            ) -> "Bluetooth"

            else -> "Other Network"
        }

        val hasInternet = capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )

        val isValidated = capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        )

        internetAccess.text = if (hasInternet) {
            "Network reports internet capability"
        } else {
            "No internet capability"
        }

        validatedStatus.text = if (isValidated) {
            "Validated by Android"
        } else {
            "Not validated"
        }
    }

    private fun createValueText(): TextView {
        return TextView(this).apply {
            text = "Checking..."
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
    }

    private fun addInfoCard(
        parent: LinearLayout,
        label: String,
        value: TextView
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.rgb(20, 30, 48))
        }

        val heading = TextView(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
        }

        card.addView(heading)
        card.addView(value)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        parent.addView(card, params)
    }
}
