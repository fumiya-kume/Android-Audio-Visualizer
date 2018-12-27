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
import com.paramsen.noise.Noise
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
            AudioFormat.ENCODING_PCM_8BIT
        )

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_8BIT,
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

                    val noise = Noise.real().optimized().init(buffer.count(), true)

                    val fftResult = noise
                        .fft(buffer.map { it.toFloat() }.toFloatArray())

                    Log.d("", buffer.count().toString())

                    refreshGraph(
                        chart,
                        noise
                            .fft(buffer.map { it.toFloat() }.toFloatArray())
                            .filterIndexed { index, fl -> index % 2 == 0 && index != 0 }
                            .map { Math.abs(it) }
                            .chunked(bufferSize / 10)
                            .map { it.max() ?: Float.MIN_VALUE }
                    )
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
