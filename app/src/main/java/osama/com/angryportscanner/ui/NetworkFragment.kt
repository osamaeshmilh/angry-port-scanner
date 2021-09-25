package osama.com.angryportscanner.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_device.view.*
import kotlinx.android.synthetic.main.fragment_network_list.*
import kotlinx.coroutines.launch
import osama.com.angryportscanner.R
import osama.com.angryportscanner.ScanViewModel
import osama.com.angryportscanner.model.DBViews.DeviceWithName
import osama.com.angryportscanner.repositories.ScanRepository
import osama.com.angryportscanner.ui.widgets.BadgeImageView
import osama.com.angryportscanner.util.AppPreferences
import osama.com.angryportscanner.util.CopyUtil
import kotlin.math.roundToInt
import kotlin.random.Random


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [NetworkFragment.OnListFragmentInteractionListener] interface.
 */
class NetworkFragment : Fragment(), LifecycleObserver {
    private var listener: OnListFragmentInteractionListener? = null
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(ScanViewModel::class.java)
    }

    private lateinit var argumentInterfaceName: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_network_list, container, false)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onReady() {

        scanBtn.setOnClickListener {
            scanBtn.isEnabled = false
            Log.e("With dot separator", fromIpInput.text())
            Log.e("Without separator", fromIpInput.text(separator = ""))

            Log.e("With dot separator", toIpInput.text())
            runScan()
        }

        val copyUtil = CopyUtil(this.requireView())

        argumentInterfaceName = arguments?.getString("interface_name")!!


        viewModel.devices.observe(viewLifecycleOwner, {
            placeholderView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })



        viewModel.scanProgress.observe(viewLifecycleOwner, {
            when (it) {
                is ScanRepository.ScanProgress.ScanFinished -> {
                    progressBar.visibility = View.INVISIBLE
                    pingTv.visibility = View.INVISIBLE
                    scanBtn.isEnabled = true

                }
                is ScanRepository.ScanProgress.ScanRunning -> {
                    progressBar.visibility = View.VISIBLE
                    pingTv.visibility = View.VISIBLE
                    progressBar.progress = (it.progress * 1000.0).roundToInt()
                    //todo update actual ping
                    pingTv.text = "192.168.1.${it.progress * 100}"

                }
                is ScanRepository.ScanProgress.ScanNotStarted -> {
                    progressBar.visibility = View.INVISIBLE
                    pingTv.visibility = View.INVISIBLE
                }
            }
        })

        devicesList.setHandler(
            requireContext(),
            this,
            object : RecyclerViewCommon.Handler<DeviceWithName>(
                R.layout.fragment_device,
                viewModel.devices
            ) {
                @SuppressLint("UnsafeOptInUsageError")
                override fun bindItem(view: View): (DeviceWithName) -> Unit {
                    val ipTextView: TextView = view.ipTextView
                    val macTextView: TextView = view.macTextView
                    val vendorTextView: TextView = view.vendorTextView
                    val deviceNameTextView: TextView = view.deviceNameTextView
                    val deviceIcon: BadgeImageView = view.device_icon

                    copyUtil.makeTextViewCopyable(macTextView)

                    return { item ->
                        ipTextView.text = item.ip.hostAddress


                        item.hwAddress?.let {
                            macTextView.text = it.getAddress(
                                AppPreferences(
                                    this@NetworkFragment
                                ).hideMacDetails
                            )
                        }?: run { macTextView.visibility = View.GONE }

//                        vendorTextView.text = item.vendorName
                        deviceNameTextView.text = if (item.isScanningDevice) {
                            getString(R.string.this_device)
                        } else {
                            item.deviceName
                        }
                        deviceIcon.setImageResource(item.deviceType.icon)
                        // todo set badge color based on port status
                        deviceIcon.badgeColor = ContextCompat.getColor(
                            requireContext(),
                            listOf(
                                R.color.live_port_color,
                                R.color.dead_port_color,
                                R.color.has_open_ports_color
                            )[Random.nextInt(3)]
                        )


                    }
                }

                override fun onClickListener(view: View, value: DeviceWithName) {
                    Log.e("EEEE","Clicked")

                    listener?.onListFragmentInteraction(value, view)
                }

                override fun onLongClickListener(view: View, value: DeviceWithName): Boolean {
                    return copyUtil.copyText(value.ip.hostAddress)
                }

                override fun shareIdentity(a: DeviceWithName, b: DeviceWithName) =
                    a.deviceId == b.deviceId

                override fun areContentsTheSame(a: DeviceWithName, b: DeviceWithName) = a == b

            })


    }


    private fun runScan() {
        viewModel.viewModelScope.launch {
            val network = viewModel.startScan(argumentInterfaceName)
            val view = this@NetworkFragment.view
            if (network == null && view != null) {
                Snackbar.make(
                    view,
                    getString(R.string.error_network_not_found),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(this)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        lifecycle.removeObserver(this)
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: DeviceWithName?, view: View)
    }
}
