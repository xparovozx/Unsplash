package com.example.unsplash.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.unsplash.databinding.FragmentThirdBinding
import com.example.unsplash.utils.ViewBindingFragment

class ThirdFragment: ViewBindingFragment<FragmentThirdBinding>(FragmentThirdBinding::inflate)  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.finish.setOnClickListener {
            val action = ViewPagerFragmentDirections.actionViewPagerFragmentToLoginFragment()
            findNavController().navigate(action)
            onboardingFinished()
        }
    }

    private fun onboardingFinished(){
val sharedPrefs = requireActivity().getSharedPreferences("onboarding", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
      editor.putBoolean("Finished", true)
      editor.apply()
    }
}