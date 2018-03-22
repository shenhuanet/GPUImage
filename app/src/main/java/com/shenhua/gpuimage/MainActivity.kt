package com.shenhua.gpuimage

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import com.shenhua.libs.gpuimage.GPUImageFilterTools
import com.shenhua.libs.gpuimage.filters.GPUImageContrastFilter
import com.shenhua.libs.gpuimage.filters.GPUImageFilter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mFilter: GPUImageFilter
    private lateinit var mFilterAdjuster: GPUImageFilterTools.FilterAdjuster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mFilterAdjuster.adjust(progress)
                gpuImageView.requestRender()
            }
        })
        mFilter = GPUImageContrastFilter(2.0f)
        mFilterAdjuster = GPUImageFilterTools.FilterAdjuster(mFilter)
        val fl = GPUImageFilterTools.getFilters()
        val adapter = FilterAdapter(this, R.layout.item, fl.names)
        listview.adapter = adapter
        listview.setOnItemClickListener { _, _, position, _ ->
            val filter = GPUImageFilterTools.createFilterForType(this@MainActivity, fl.filters[position])
            seekBar!!.progress = 0
            switchFilter(filter)
            gpuImageView!!.requestRender()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_origin)
        gpuImageView!!.setImage(bitmap)
    }

    private fun switchFilter(filter: GPUImageFilter?) {
        mFilter = filter!!
        gpuImageView!!.filter = mFilter
        mFilterAdjuster = GPUImageFilterTools.FilterAdjuster(mFilter)
        seekBar!!.visibility = if (mFilterAdjuster.canAdjust()) View.VISIBLE else View.INVISIBLE
    }
}
