package com.github.tcgeneric.securityscout

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.github.tcgeneric.securityscout.securitythreat.DisplayDataProvider
import com.github.tcgeneric.securityscout.securitythreat.SecurityThreatInfoProvider
import com.github.tcgeneric.securityscout.securitythreat.TargetContextProvider
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        val shared = PreferenceManager.getDefaultSharedPreferences(this)
        if(shared.getString("target", null) == null)
            shared.edit().putString("target", "ahnlab").apply()
        reloadAlert(shared.getString("target", null)) // Non-nullability is guaranteed
    }

    private fun getId(type:String, name:String):Int {
        return resources.getIdentifier(name, type, "com.github.tcgeneric.securityscout")
    }

    private fun reloadAlert(id:String?) {
        val source = findViewById<TextView>(R.id.source)
        val updated = findViewById<TextView>(R.id.updateDate)
        val graph = findViewById<ProgressBar>(R.id.pieChart)
        val display = findViewById<TextView>(R.id.display)
        val target = TargetContextProvider.map[id]!!
        Thread(Runnable {
            val data = SecurityThreatInfoProvider.getFuture(target.url, target.targetHTML).get(3, TimeUnit.SECONDS)
            if(data == null) {
                this.runOnUiThread {
                    Toast.makeText(this, R.string.retrieve_error, Toast.LENGTH_SHORT).show()
                }
                return@Runnable
            }
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
            handler.post {
                val color = ContextCompat.getColor(this, getId("color", data.colorId))
                source.text = resources.getString(R.string.source_default_value, target.id)
                graph.max = (data.threatLevelMax -1) * 100
                display.text = data.display
                graph.progressDrawable.setTint(color)
                display.setTextColor(color)
                updated.text = resources.getString(R.string.update_default_value,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.SECOND))
            }
            handler.postDelayed({
                val anim = ObjectAnimator.ofInt(graph, "progress", 0, data.threatLevel*100)
                anim.duration = 1000L
                anim.interpolator = DecelerateInterpolator()
                anim.start()
            }, 100L)
        }).start()
    }
}
