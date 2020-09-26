package com.example.downloadwhatsapstatus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.downloadwhatsapstatus.MainActivity
import com.example.downloadwhatsapstatus.R
import com.example.downloadwhatsapstatus.adapter.StatusAdapter
import kotlinx.android.synthetic.main.fragment_images.view.*

class ImagesFragment(private val isImage: Boolean) : Fragment() {

    private lateinit var adapter: StatusAdapter

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_images, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isImage){
            adapter = StatusAdapter(isImage)
            adapter.setList(MainActivity.listImages)
        }
        else{
            adapter=StatusAdapter(isImage)
            adapter.setList(MainActivity.listVideos)
        }

        view.rvWhatsAppStatus.layoutManager = GridLayoutManager(requireContext(), 3)
        view.rvWhatsAppStatus.adapter = adapter


    }
}