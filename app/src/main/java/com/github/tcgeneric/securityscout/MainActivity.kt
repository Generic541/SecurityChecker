package com.github.tcgeneric.securityscout

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import android.widget.TextView
import com.github.tcgeneric.securityscout.securitythreat.DisplayDataProvider
import com.github.tcgeneric.securityscout.securitythreat.SecurityThreatInfoProvider
import com.github.tcgeneric.securityscout.securitythreat.TargetContextProvider
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        val source = findViewById<TextView>(R.id.source)
        // TODO: Below code is stub
        val target = TargetContextProvider.map["ahnlab"]!!
        val future = SecurityThreatInfoProvider.getFuture(target.url, target.targetHTML)
        source.post {
            source.text = future.get(3, TimeUnit.SECONDS).display
        }
    }

    private fun getId(type:String, name:String):Int {
        return resources.getIdentifier(name, type, "com.github.tcgeneric.securityscout")
    }
}
