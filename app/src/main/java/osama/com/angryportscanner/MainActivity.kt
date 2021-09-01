package osama.com.angryportscanner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.android.synthetic.main.activity_main.*
import osama.com.angryportscanner.model.DBViews.DeviceWithName
import osama.com.angryportscanner.ui.NetworkFragment
import kotlin.collections.forEach as forEach1


class MainActivity : AppCompatActivity(), NetworkFragment.OnListFragmentInteractionListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var viewModel: ScanViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics

        Firebase.messaging.subscribeToTopic("default")
            .addOnCompleteListener { task ->
                Log.d("f", "sub")
                Toast.makeText(baseContext, "default", Toast.LENGTH_SHORT).show()
            }


        val navController = findNavController(R.id.nav_host_fragment)
        drawer_navigation.setupWithNavController(navController)
        setSupportActionBar(toolbar)
        appBarConfiguration =
            AppBarConfiguration.Builder(setOf(R.id.deviceFragment, R.id.appPreferenceFragment))
                .setOpenableLayout(main_drawer_layout)
                .build()
        setupActionBarWithNavController(navController, appBarConfiguration)

        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)

        val interfaceMenu =
            drawer_navigation.menu.addSubMenu(getString(R.string.interfaces_submenu))

        viewModel.fetchAvailableInterfaces().forEach1 { nic ->
            interfaceMenu.add("${nic.interfaceName} - ${nic.address.hostAddress}/${nic.prefix}")
                .also {
                    it.setOnMenuItemClickListener {
                        val bundle = bundleOf("interface_name" to nic.interfaceName)
                        nav_host_fragment.findNavController().navigate(R.id.deviceFragment, bundle)
                        main_drawer_layout.closeDrawers()
                        true
                    }
                    it.setIcon(R.drawable.ic_settings_ethernet_white_24dp)
                    it.isCheckable = true
                    it.isEnabled = true
                }
        }
        val preferences = drawer_navigation.menu.add(getString(R.string.preferences_submenu))
        preferences.setIcon(R.drawable.ic_settings_white_24dp)
        preferences.setOnMenuItemClickListener {
            navController.navigate(R.id.appPreferenceFragment)
            main_drawer_layout.closeDrawers()
            true
        }
    }


    override fun onListFragmentInteraction(item: DeviceWithName?, view: View) {
        val bundle = bundleOf("deviceId" to item?.deviceId, "deviceIp" to item?.ip)
        nav_host_fragment.findNavController().navigate(R.id.deviceInfoFragment, bundle)
    }
}
