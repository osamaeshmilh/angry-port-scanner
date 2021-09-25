package osama.com.angryportscanner.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.android.synthetic.main.fragment_deviceinfo_list.view.*
import kotlinx.android.synthetic.main.fragment_port_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import osama.com.angryportscanner.R
import osama.com.angryportscanner.ScanViewModel
import osama.com.angryportscanner.model.Device
import osama.com.angryportscanner.model.Port
import osama.com.angryportscanner.model.enums.Protocol
import osama.com.angryportscanner.scanner.PortScanner
import osama.com.angryportscanner.util.AppPreferences
import osama.com.angryportscanner.util.CopyUtil


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DeviceInfoFragment.OnListFragmentInteractionListener] interface.
 */
class DeviceInfoFragment : Fragment() {
    lateinit var viewModel: ScanViewModel


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.device_details_menu, menu)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                Toast.makeText( requireContext(), "Share Clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.copy -> {
                Toast.makeText( requireContext(), "Copy Clicked", Toast.LENGTH_SHORT).show()

            }
            R.id.save -> {
                Toast.makeText( requireContext(), "Save Clicked", Toast.LENGTH_SHORT).show()

            }
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_deviceinfo_list, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ScanViewModel::class.java)
        val recyclerView = view.findViewById<RecyclerViewCommon>(R.id.list)
        val argumentDeviceId = arguments?.getLong("deviceId")!!
        val copyUtil = CopyUtil(view)

        val deviceTypeTextView = view.findViewById<TextView>(R.id.deviceTypeTextView)
        val deviceIpTextView = view.findViewById<TextView>(R.id.deviceIpTextView)
        val deviceNameTextView = view.findViewById<TextView>(R.id.deviceNameTextView)
        val deviceHwAddressTextView = view.findViewById<TextView>(R.id.deviceHwAddressTextView)
        val deviceVendorTextView = view.findViewById<TextView>(R.id.deviceVendorTextView)

        copyUtil.makeTextViewCopyable((deviceTypeTextView))
        copyUtil.makeTextViewCopyable((deviceIpTextView))
        copyUtil.makeTextViewCopyable(deviceNameTextView)
        copyUtil.makeTextViewCopyable(deviceHwAddressTextView)
        copyUtil.makeTextViewCopyable(deviceVendorTextView)

        viewModel.deviceDao.getById(argumentDeviceId).observe(viewLifecycleOwner, {
            fetchInfo(it.asDevice)
            deviceTypeTextView.text = getString(it.deviceType.label)
            deviceIpTextView.text = it.ip.hostAddress
            deviceNameTextView.text = if (it.isScanningDevice) {
                getString(R.string.this_device)
            } else {
                it.deviceName
            }
            deviceHwAddressTextView.text =
                it.hwAddress?.getAddress(AppPreferences(this).hideMacDetails)
            deviceVendorTextView.text = it.vendorName

            view.deviceIcon.apply {
                setImageResource(it.deviceType.icon)
                badgeColor = ContextCompat.getColor(context, R.color.live_port_color)
            }

        })


//        val ports =  viewModel.portDao.getAllForDevice(argumentDeviceId)
        val ports = MutableLiveData(
            listOf(
                Port(
                    0,
                    port = 2200,
                    protocol = Protocol.TCP,
                    deviceId = 11111
                ),
                Port(
                    1,
                    port = 4400,
                    protocol = Protocol.UDP,
                    deviceId = 2222
                ),
                Port(
                    2,
                    port = 4422,
                    protocol = Protocol.TCP,
                    deviceId = 3333
                ),
            )
        )

        recyclerView.setHandler(requireContext(), this, object :
            RecyclerViewCommon.Handler<Port>(R.layout.fragment_port_item, ports) {
            override fun shareIdentity(a: Port, b: Port) = a.port == b.port
            override fun areContentsTheSame(a: Port, b: Port) = a == b
            override fun onClickListener(view: View, value: Port) {
                viewModel.viewModelScope.launch(context = Dispatchers.IO) {
                    val ip = viewModel.deviceDao.getByIdNow(value.deviceId).ip
                    withContext(Dispatchers.Main) {
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("http://${ip}:${value.port}")
                        }.also {
                            startActivity(it)
                        }
                    }
                }
            }


            override fun onLongClickListener(view: View, value: Port): Boolean {
                viewModel.viewModelScope.launch(context = Dispatchers.IO) {
                    val ip = viewModel.deviceDao.getByIdNow(value.deviceId).ip
                    withContext(Dispatchers.Main) {
                        copyUtil.copyText("${ip.hostAddress}:${value.port}")
                    }
                }
                return true
            }

            override fun bindItem(view: View): (value: Port) -> Unit {
                val portNumberTextView: TextView = view.portNumberTextView
                val protocolTextView: TextView = view.protocolTextView
                val serviceTextView: TextView = view.serviceNameTextView

                copyUtil.makeTextViewCopyable(portNumberTextView)
                copyUtil.makeTextViewCopyable(protocolTextView)
                copyUtil.makeTextViewCopyable(serviceTextView)

                return { port ->
                    portNumberTextView.text = port.port.toString()
                    protocolTextView.text = port.protocol.toString()
                    val serviceName = port.description?.serviceName
                    if(serviceName.isNullOrBlank()){
                        serviceTextView.visibility = View.GONE
                    }else {
                        serviceTextView.visibility = View.VISIBLE

                        serviceTextView.text = serviceName
                    }
                }
            }

        })
        return view
    }

    private fun fetchInfo(device: Device) {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                PortScanner(device.ip).scanPorts().forEach {
                    launch {
                        val result = it.await()
                        if (result.isOpen) {
                            viewModel.portDao.upsert(
                                Port(
                                    0,
                                    result.port,
                                    result.protocol,
                                    device.deviceId
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}