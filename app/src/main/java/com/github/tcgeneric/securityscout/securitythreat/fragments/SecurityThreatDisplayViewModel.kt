package com.github.tcgeneric.securityscout.securitythreat.fragments

import androidx.lifecycle.ViewModel
import com.github.tcgeneric.securityscout.securitythreat.providers.SecurityThreatInfoProvider

class SecurityThreatDisplayViewModel : ViewModel() {
    var cachedThreatInfo:SecurityThreatInfoProvider.SecurityThreatInfo? = null
}