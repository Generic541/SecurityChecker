package com.github.tcgeneric.securityscout

import android.app.Application
import android.content.res.AssetManager
import android.util.Log
import com.github.tcgeneric.securityscout.securitythreat.DisplayDataProvider
import com.github.tcgeneric.securityscout.securitythreat.TargetContextProvider
import org.yaml.snakeyaml.Yaml
import java.io.File

class SecurityScout:Application() {

    override fun onCreate() {
        val assetMgr = resources.assets
        super.onCreate()
        loadDisplayData(assetMgr)
        loadTargetContexts(assetMgr)
    }

    private fun hasExtension(filename:String, ext:String):Boolean {
        val splited = filename.split(".")
        if(splited.size < 2)
            return false
        return splited[1] == ext
    }

    private fun loadDisplayData(assetMgr:AssetManager) {
        val yaml = Yaml()
        val filenames = assetMgr.list("")!!

        for(name in filenames) {
            if(!hasExtension(name, "yml"))
                continue

            val stream = assetMgr.open(name)
            val data = yaml.loadAs(stream, DisplayDataProvider.DisplayData::class.java)
            DisplayDataProvider.map[data.id] = data
            stream.close()
        }
    }

    private fun loadTargetContexts(assetMgr:AssetManager) {
        val file = assetMgr.open("targets.csv").bufferedReader()
        var line = file.readLine()
        while(line != null) {
            val input = line.split(",")
            val data = TargetContextProvider.TargetContext(input[0], input[1], input[2])
            TargetContextProvider.map.put(data.id, data)
            line = file.readLine()
        }
        file.close()
    }
}