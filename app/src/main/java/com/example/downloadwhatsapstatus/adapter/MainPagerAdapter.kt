package com.example.downloadwhatsapstatus.adapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.downloadwhatsapstatus.fragments.ImagesFragment

class MainPagerAdapter(private val context: Context, fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        Log.e("GetItem>>", "${position} <<")
        return when(position){
            0->ImagesFragment(true)
            else->ImagesFragment(false)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0->"Images"
            else->"Videos"
        }
    }



}