package gst.trainingcourse_ex11_thaonx4.ipcmessenger

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Toast
import gst.trainingcourse_ex11_thaonx4.ipcmessenger.Constants.RECEIVE_MESSAGE
import gst.trainingcourse_ex11_thaonx4.ipcmessenger.Constants.RECEIVE_MESSAGE_TO_CLIENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ServerService : Service() {

    private lateinit var mMessengerService: Messenger
//    private var coroutineScope = CoroutineScope(Dispatchers.IO)


    internal class MyHandler(
      context: Context,
      private val applicationContext: Context = context.applicationContext
    ) : Handler() {

        override fun handleMessage(msg: Message) {

            when (msg.what) {
                RECEIVE_MESSAGE_TO_CLIENT -> {
                    val mClientService: Messenger = msg.replyTo
                    val keyReceive = msg.data.getSerializable("key")

                    val receiveClient = when (keyReceive) {
                        0 -> "window"
                        1 -> "Linux"
                        2 -> "Mac"
                        4 -> "Unix"
                        5 -> "Android"
                        else -> null
                    }

                    //can be replaced with coroutine
                    Thread{
                        Thread.sleep(5000)
                        val message = Message.obtain(null, RECEIVE_MESSAGE, 0, 0)
                        message.data.putSerializable("clientReturnValue", receiveClient)
                        mClientService.send(message)
                    }.start()
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        Toast.makeText(applicationContext, "binding server", Toast.LENGTH_SHORT).show()
        mMessengerService = Messenger(MyHandler(applicationContext))
        return mMessengerService.binder
    }
}