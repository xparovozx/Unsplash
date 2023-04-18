package com.example.unsplash.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.unsplash.R
import com.example.unsplash.databinding.FragmentFeedBinding
import com.example.unsplash.databinding.FragmentFirstBinding
import com.example.unsplash.utils.ViewBindingFragment

class FirstFragment: ViewBindingFragment<FragmentFirstBinding>(FragmentFirstBinding::inflate)  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.next.setOnClickListener {
            viewPager?.currentItem = 1
        }
    }
}