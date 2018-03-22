package com.shenhua.gpuimage

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Created by shenhua on 2018-03-22-0022.
 * @author shenhua
 *         Email shenhuanet@126.com
 */
class FilterAdapter(context: Context?, resource: Int, objects: MutableList<String>)
    : ArrayAdapter<String>(context, resource, objects) {

    private val resId: Int = resource

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val name: String = getItem(position)
        val view = LayoutInflater.from(context).inflate(resId, parent, false)
        val itemName = view.findViewById<TextView>(R.id.item_name)
        itemName.text = name
        return view
    }
}