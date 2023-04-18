package com.example.unsplash.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.unsplash.databinding.FragmentSplashBinding
import com.example.unsplash.utils.ViewBindingFragment

class SplashFragment: ViewBindingFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate)  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
            if (isOnboardingFinished()) {
                val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                findNavController().navigate(action)
            } else {
                val action = SplashFragmentDirections.actionSplashFragmentToViewPagerFragment()
                findNavController().navigate(action)
            }
        }, 2000)
    }

    private fun isOnboardingFinished(): Boolean {
        val sharedPrefs = requireActivity().getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            return sharedPrefs.getBoolean("Finished", false)
    }
}