package com.kuxu.visualizer.audio.audiovisualizer

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshGraph(chart, listOf(0.0, 0.1).map { it.toFloat() })

        val samplingRate = 44100

        val bufferSize = AudioRecord.getMinBufferSize(
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize * 100
        )

        audioRecord.positionNotificationPeriod = bufferSize
        audioRecord.startRecording()

        audioRecord.setRecordPositionUpdateListener(
            object : AudioRecord.OnRecordPositionUpdateListener {
                override fun onMarkerReached(recorder: AudioRecord?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onPeriodicNotification(recorder: AudioRecord?) {
                    val buffer = ShortArray(bufferSize)
                    recorder?.read(buffer, 0, bufferSize)
                    refreshGraph(chart, buffer.map { it.toFloat() })
                    Log.d("audio", buffer.count().toString())
                }

            })
    }

    private fun refreshGraph(chart: LineChart, data: List<Float>) {
        val entities = data.mapIndexed { index, fl -> Entry(index.toFloat(), fl) }

        val lineDataSet = LineDataSet(entities, "タイトル")

        chart.data = LineData(lineDataSet)
        chart.invalidate()
    }
}
