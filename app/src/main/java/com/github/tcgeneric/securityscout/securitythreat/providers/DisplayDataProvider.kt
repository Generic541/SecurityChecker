package com.github.tcgeneric.securityscout.securitythreat.providers

class DisplayDataProvider
{
    // Because of method yaml.loadAs()
    class DisplayData {
        lateinit var id:String
        var maxThreatLevel: Int = 1
        lateinit var displayStrings: ArrayList<String>
        lateinit var classNames: ArrayList<String>
        lateinit var colorIDs: ArrayList<String>
    }

    companion object {
        val map = HashMap<String, DisplayData>()
    }
}