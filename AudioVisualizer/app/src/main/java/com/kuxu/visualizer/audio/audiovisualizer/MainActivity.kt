package com.kuxu.visualizer.audio.audiovisualizer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chart = chart

        val entities = Array(100) { i ->
            Entry(i.toFloat(), Random.nextInt(IntRange(0, 100)).toFloat())
        }.toList()
        val lineDataSet = LineDataSet(entities, "タイトル")

        chart.data = LineData(lineDataSet)
        chart.invalidate()
    }
}
