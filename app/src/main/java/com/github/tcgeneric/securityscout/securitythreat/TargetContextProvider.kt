package com.github.tcgeneric.securityscout.securitythreat

class TargetContextProvider {

    class TargetContext(val id:String, val url:String, val targetHTML:String)

    companion object {
        val map = HashMap<String, TargetContext>()
    }
}