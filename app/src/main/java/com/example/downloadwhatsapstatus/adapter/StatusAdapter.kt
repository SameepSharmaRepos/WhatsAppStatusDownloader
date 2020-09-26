package com.example.downloadwhatsapstatus.adapter


import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadwhatsapstatus.R
import kotlinx.android.synthetic.main.rt_status.view.*
import java.io.File
import java.io.IOException
import java.io.InputStream


class StatusAdapter(private val isImage:Boolean) : RecyclerView.Adapter<StatusAdapter.MyViewHolder>() {

    private val statuses = mutableListOf<String>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rt_status, parent, false)
        )

        return holder
    }


    override fun getItemCount(): Int {
        if (statuses.isEmpty())
            return 0
        return statuses.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val path = statuses[position]
        val uri = Uri.parse(path)
            holder.itemView.rt_iv.visibility = View.VISIBLE
            holder.itemView.rt_vv.visibility = View.GONE
        if (isImage)
            holder.itemView.rt_iv.setImageURI(uri)
        else{
         val thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND)
            holder.itemView.rt_iv.setImageBitmap(thumb)
        }
        /*else {
            holder.itemView.rt_vv.visibility = View.VISIBLE
            holder.itemView.rt_iv.visibility = View.GONE
            holder.itemView.rt_vv.setVideoURI(uri)
        }*/

    }

    fun setList(list: List<String>) {
        statuses.clear()
        statuses.addAll(list)
    }

    fun checkIsImage(context: Context, uri: Uri?): Boolean {
        val contentResolver: ContentResolver = context.getContentResolver()
        val type = contentResolver.getType(uri!!)
        if (type != null) {
            return type.startsWith("image/")
        } else {
            // try to decode as image (bounds only)
            var inputStream: InputStream? = null
            try {
                inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeStream(inputStream, null, options)
                    return options.outWidth > 0 && options.outHeight > 0
                }
            } catch (e: IOException) {
                // ignore
            } finally {
                inputStream?.close()
            }
        }
        // default outcome if image not confirmed
        return false
    }

}