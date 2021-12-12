package gst.trainingcourse_ex11_thaonx4.ipcmessenger

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import gst.trainingcourse_ex11_thaonx4.ipcmessenger.Constants.RECEIVE_MESSAGE
import gst.trainingcourse_ex11_thaonx4.ipcmessenger.Constants.RECEIVE_MESSAGE_TO_CLIENT
import gst.trainingcourse_ex11_thaonx4.ipcmessenger.Constants.SEND_MESSAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClientService : Service() {

    private lateinit var mMessengerServer: Messenger
    private lateinit var mMessengerClient: Messenger

//    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    val listNumber = arrayListOf(0, 1, 2, 4, 5)

    @SuppressLint("HandlerLeak")
    inner class MyHandler(
       context: Context,
       private val applicationContext: Context = context.applicationContext
    ) : Handler() {

        override fun handleMessage(msg: Message) {

            when (msg.what) {
                SEND_MESSAGE -> {
                        sendMessageToServerService(msg)
                }

                RECEIVE_MESSAGE -> {
                    val receiveMessage = msg.data.getSerializable("clientReturnValue")
                    Log.d("xuanthao", "receive: $receiveMessage")

                    //can be replaced with coroutine
                    Thread{
                        Thread.sleep(5000)
                        sendMessageToServerService(msg)
                    }.start()
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    private fun sendMessageToServerService(msg: Message) {
        if (!this::mMessengerServer.isInitialized) {
            val iBinder = msg.data.getBinder("binder")
            mMessengerServer = Messenger(iBinder)
        }
        val randomNumber = listNumber.random()

        val message = Message.obtain(null, RECEIVE_MESSAGE_TO_CLIENT, 0, 0)
        Log.d("xuanthao", "send : $randomNumber")

            message.data.putSerializable("key", randomNumber)
            message.replyTo = mMessengerClient
            mMessengerServer.send(message)
    }

    override fun onBind(p0: Intent?): IBinder? {
        Toast.makeText(applicationContext, "binding client", Toast.LENGTH_SHORT).show()
        mMessengerClient = Messenger(MyHandler(applicationContext))
        return mMessengerClient.binder
    }
}