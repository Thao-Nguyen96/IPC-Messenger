package gst.trainingcourse_ex11_thaonx4.ipcmessenger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Toast
import gst.trainingcourse_ex11_thaonx4.ipcmessenger.Constants.RECEIVE_MESSAGE_TO_CLIENT
import gst.trainingcourse_ex11_thaonx4.ipcmessenger.Constants.SEND_MESSAGE
import gst.trainingcourse_ex11_thaonx4.ipcmessenger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mMessengerClient: Messenger
    private var isClientServiceConnected: Boolean = false
    private lateinit var mMessengerServerIBinder: IBinder
    private var isServerServiceConnected: Boolean = false

    private var mClientServiceCollection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            mMessengerClient = Messenger(iBinder)
            isClientServiceConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isClientServiceConnected = false

        }
    }


    private var mServerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            mMessengerServerIBinder = iBinder!!
            isServerServiceConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isServerServiceConnected = false
        }

    }

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartClientService.setOnClickListener {
            startClientService()
        }

        binding.btnStartServerService.setOnClickListener {
            startServerService()
        }

        binding.btnStartCommunicate.setOnClickListener {
            startCommunicate()
        }
    }

    private fun startClientService() {
        val intent = Intent(this, ClientService::class.java)
        bindService(intent, mClientServiceCollection, Context.BIND_AUTO_CREATE)
    }

    private fun startServerService() {
        val intent = Intent(this, ServerService::class.java)
        bindService(intent, mServerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun startCommunicate() {

        if (!isClientServiceConnected || !isServerServiceConnected) {
            Toast.makeText(this, "start client and server", Toast.LENGTH_SHORT).show()
        } else {

            val msg = Message.obtain(null, SEND_MESSAGE, 0, 0)
            msg.data.putBinder("binder", mMessengerServerIBinder)

            mMessengerClient.send(msg)
        }
    }
}