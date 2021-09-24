package osama.com.angryportscanner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
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


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottom_nav.setupWithNavController(navController)
        setSupportActionBar(toolbar)

        supportActionBar?.elevation = 2f
        appBarConfiguration =
            AppBarConfiguration.Builder(
                setOf(
                    R.id.deviceFragment,
                    R.id.appPreferenceFragment,
                    R.id.savedSearchFragment
                )
            )
                .build()
        setupActionBarWithNavController(navController, appBarConfiguration)

        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.deviceInfoFragment) {
                bottom_nav.visibility = View.GONE
            } else {
                bottom_nav.visibility = View.VISIBLE
            }

            if (destination.id == R.id.deviceFragment) {
                deviceInfoToolsBar.visibility = View.VISIBLE
            } else {
                deviceInfoToolsBar.visibility = View.GONE
            }
        }

        val interfaces = viewModel.fetchAvailableInterfaces()
        val interfaceNames =
            interfaces.map { "${it.interfaceName} - ${it.address.hostAddress}/${it.prefix}" }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, interfaceNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        interfaceSpinner.adapter = adapter

        interfaceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, i: Int, p3: Long) {
                val bundle = bundleOf("interface_name" to interfaces[i].interfaceName)
                nav_host_fragment.findNavController().navigate(R.id.deviceFragment, bundle)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


    }


    override fun onListFragmentInteraction(item: DeviceWithName?, view: View) {
        val bundle = bundleOf("deviceId" to item?.deviceId, "deviceIp" to item?.ip)
        nav_host_fragment.findNavController().navigate(R.id.deviceInfoFragment, bundle)
    }
}
