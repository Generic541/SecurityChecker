package com.github.tcgeneric.securityscout.securitythreat.fragments

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.github.tcgeneric.securityscout.R
import com.github.tcgeneric.securityscout.securitythreat.providers.SecurityThreatInfoProvider
import com.github.tcgeneric.securityscout.securitythreat.providers.TargetContextProvider
import java.util.*
import java.util.concurrent.TimeUnit


class SecurityThreatDisplayFragment : Fragment() {

    companion object {
        fun newInstance() =
            SecurityThreatDisplayFragment()
    }

    private lateinit var viewModel: SecurityThreatDisplayViewModel
    private lateinit var rootView:View
    private lateinit var rootActivity:FragmentActivity
    private lateinit var sharedPref:SharedPreferences
    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.security_threat_display_fragment, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(rootActivity).get(SecurityThreatDisplayViewModel::class.java)
        if(this.activity != null) {
            rootActivity = this.activity!! // TODO: Don't know when this thing is null
            sharedPref = PreferenceManager.getDefaultSharedPreferences(rootActivity)
            if (sharedPref.getString("target", null) == null)
                sharedPref.edit().putString("target", "ahnlab").apply()
            reloadAlert(sharedPref.getString("target", null))
        }
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.cachedThreatInfo != null)
            reloadAlert(viewModel.cachedThreatInfo)
        else
            reloadAlert(sharedPref.getString("target", null))
    }

    private fun getId(type:String, name:String):Int {
        return resources.getIdentifier(name, type, "com.github.tcgeneric.securityscout")
    }

    // Time-expensive method. do not call frequently.
    private fun getAlert(id:String?): SecurityThreatInfoProvider.SecurityThreatInfo? {
        val target = TargetContextProvider.map[id]!!
        return SecurityThreatInfoProvider.getFuture(target.url, target.targetHTML).get(3, TimeUnit.SECONDS)
    }

    private fun reloadAlert(id:String?) {
        Thread(Runnable {
            val data = getAlert(id)
            viewModel.cachedThreatInfo = data
            reloadAlert(data)
        }).start()
    }

    private fun reloadAlert(data:SecurityThreatInfoProvider.SecurityThreatInfo?) {
        val source = rootView.findViewById<TextView>(R.id.source)
        val updated = rootView.findViewById<TextView>(R.id.updateDate)
        val graph = rootView.findViewById<ProgressBar>(R.id.pieChart)
        val display = rootView.findViewById<TextView>(R.id.display)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))

        if(data == null) {
            rootActivity.runOnUiThread {
                Toast.makeText(rootActivity, R.string.retrieve_error, Toast.LENGTH_SHORT).show()
            }
            return
        }
        handler.post {
            val color = ContextCompat.getColor(rootActivity, getId("color", data.colorId))
            source.text = resources.getString(R.string.source_default_value, data.id)
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
    }
}
