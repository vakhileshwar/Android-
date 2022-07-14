package com.example.bio


import android.content.Context
import android.os.Build

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


/**

 * This class has is useful for Device authentication.
 *
 * @property context the context from where we are calling
 * @constructor Creates an empty BioMetricsUtil.
 */
class BioMetricsUtil constructor(private val context: Context) {
    private var biometricManager: BiometricManager = BiometricManager.from(context)

    /**
     * authenticate device with best available options
     * @param onFinish block containing state to handling post authentication
     */

    fun authenticate(onFinish: (success: Boolean) -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)
        BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onFinish(false)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onFinish(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFinish(false)
                }

            }).authenticate(biometricPromptInfo())
    }


    /**
     * Create BiometricPrompt Info based on available authentication status
     *
     * @return BiometricPrompt Info
     */
    private fun biometricPromptInfo(): BiometricPrompt.PromptInfo {
        return if (canAuthenticateBiometrics()) {
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("context.getString(R.string.biometric_title)")
                .setSubtitle("context.getString(R.string.biometric_subtitle)")
                .setDescription("context.getString(R.string.biometric_description)")
                .setNegativeButtonText("context.getString(R.string.cancel)")
                .build()
        } else {
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("context.getString(R.string.passcode_title)")
                .setDescription("context.getString(R.string.passcode_description)")
                .setAllowedAuthenticators((BiometricManager.Authenticators.DEVICE_CREDENTIAL))
                .build()
        }
    }

    /**
     * Performs an operation to find does user Device has  authentication based ob device os version
     *
     * @return The result of device  authentication status bases based on device OS version
     */
    fun canAuthenticate(): Boolean {
        var canAuthenticate = canAuthenticateBiometrics()
        if (!canAuthenticate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                canAuthenticate = canAuthenticateCredentials()
            }
        }
        return canAuthenticate
    }

    /**
     * Performs an operation to find does user Device has Biometric authentication
     *
     * @return The result of device biometric authentication status
     */
    private fun canAuthenticateBiometrics(): Boolean {
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Performs an operation to find does user Device has Biometric authentication
     *
     * @return The result of device password authentication status
     */
    private fun canAuthenticateCredentials(): Boolean {
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }

}