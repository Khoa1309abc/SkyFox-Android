/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.waterfox.android.gecko

import android.content.Context
import mozilla.components.browser.engine.gecko.autofill.GeckoAutocompleteStorageDelegate
import mozilla.components.browser.engine.gecko.ext.toContentBlockingSetting
import mozilla.components.concept.engine.EngineSession.TrackingProtectionPolicy
import mozilla.components.concept.storage.CreditCardsAddressesStorage
import mozilla.components.concept.storage.LoginsStorage
import mozilla.components.lib.crash.handler.CrashHandlerService
import mozilla.components.service.sync.autofill.GeckoCreditCardsAddressesStorageDelegate
import mozilla.components.service.sync.logins.GeckoLoginStorageDelegate
import net.waterfox.android.Config
import net.waterfox.android.ext.components
import net.waterfox.android.ext.settings
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings

object GeckoProvider {
    private var runtime: GeckoRuntime? = null

    @Synchronized
    fun getOrCreateRuntime(
        context: Context,
        autofillStorage: Lazy<CreditCardsAddressesStorage>,
        loginStorage: Lazy<LoginsStorage>,
        trackingProtectionPolicy: TrackingProtectionPolicy
    ): GeckoRuntime {
        if (runtime == null) {
            runtime =
                createRuntime(context, autofillStorage, loginStorage, trackingProtectionPolicy)
        }

        return runtime!!
    }

    private fun createRuntime(
        context: Context,
        autofillStorage: Lazy<CreditCardsAddressesStorage>,
        loginStorage: Lazy<LoginsStorage>,
        policy: TrackingProtectionPolicy
    ): GeckoRuntime {
        val builder = GeckoRuntimeSettings.Builder()

        val runtimeSettings = builder
            .crashHandler(CrashHandlerService::class.java)
            .contentBlocking(policy.toContentBlockingSetting())
            .debugLogging(Config.channel.isDebug)
            .aboutConfigEnabled(Config.channel.isDebug)
            .build()

        val settings = context.components.settings
        if (!settings.shouldUseAutoSize) {
            runtimeSettings.automaticFontSizeAdjustment = false
            val fontSize = settings.fontSizeFactor
            runtimeSettings.fontSizeFactor = fontSize
        }

        val geckoRuntime = GeckoRuntime.create(context, runtimeSettings)

        geckoRuntime.autocompleteStorageDelegate = GeckoAutocompleteStorageDelegate(
            GeckoCreditCardsAddressesStorageDelegate(
                storage = autofillStorage,
                isCreditCardAutofillEnabled = { context.settings().shouldAutofillCreditCardDetails },
                isAddressAutofillEnabled = { context.settings().shouldAutofillAddressDetails }
            ),
            GeckoLoginStorageDelegate(loginStorage)
        )

        return geckoRuntime
    }
}