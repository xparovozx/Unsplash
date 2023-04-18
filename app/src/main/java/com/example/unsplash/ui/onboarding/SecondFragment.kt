package com.example.unsplash.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.unsplash.R
import com.example.unsplash.databinding.FragmentSecondBinding
import com.example.unsplash.utils.ViewBindingFragment

class SecondFragment: ViewBindingFragment<FragmentSecondBinding>(FragmentSecondBinding::inflate)  {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.next2.setOnClickListener {
            viewPager?.currentItem = 2
        }
    }
}