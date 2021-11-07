package com.example.notesapp

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.roundToInt


class DisplayChart : AppCompatActivity() {
    private lateinit var pieChart: PieChart
    private lateinit var showNoPriorityNotesSwitch : SwitchMaterial
    private lateinit var noPriorityNoteEntry : PieEntry
    private lateinit var pieDataForPieChart: PieData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_chart)
        pieChart = findViewById(R.id.priorityPieChart)
        showNoPriorityNotesSwitch = findViewById(R.id.showNoPriorityNotes)

        val entriesWithLabel = getNoteArrayListData()
        setupPieChart()

        loadPieChartData(entriesWithLabel)
        showNoPriorityNotesSwitch.isChecked = true

        showNoPriorityNotesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                pieDataForPieChart.dataSet.addEntry(noPriorityNoteEntry)
            } else {
                pieDataForPieChart.dataSet.removeEntry(noPriorityNoteEntry)
            }
            pieChart.notifyDataSetChanged()
            pieChart.invalidate()
            pieChart.animateY(1400, Easing.EaseInOutQuad)
        }
        pieChart.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
            override fun onNothingSelected() {
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val pieEntry = e as PieEntry
                val label: String = pieEntry.label
                val numberOfNotes = pieEntry.value
                val noteSorNoS =  "note" + if(numberOfNotes>0) "s" else ""
                val OutputMessage = "you have ${numberOfNotes.roundToInt()} $noteSorNoS with $label priority!"
                showToast(OutputMessage)
            }
        })
    }

    fun showToast(string : String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(this,string,duration).show()
    }

    private fun getNoteArrayListData(): ArrayList<PieEntry> {
        val args = intent.getBundleExtra(MainActivity.KEY_FOR_CHART_ARRAYLIST_IN_ARGS)
        val notesArrayList = args!!.getSerializable(MainActivity.KEY_FOR_CHART_ARRAYLIST_IN_SERIALIZABLE) as ArrayList<Note>?

        val entriesWithNoLabel = mutableListOf <Float> (0.0f,0f,0f,0f)
        if (notesArrayList != null) {
            if (notesArrayList.isEmpty()) showToast("Please add some notes to see some insightful data!")
            for(note in notesArrayList){
                val priority = note.priority
                if(priority >= Note.NO_PRIORITY && priority <= Note.MAX_PRIORITY){
                    entriesWithNoLabel[priority]++
                }
            }
        }

        val entriesWithLabel = ArrayList<PieEntry>()
        entriesWithLabel.add(PieEntry(entriesWithNoLabel[0], "No"))
        entriesWithLabel.add(PieEntry(entriesWithNoLabel[1], "Low"))
        entriesWithLabel.add(PieEntry(entriesWithNoLabel[2], "Mid"))
        entriesWithLabel.add(PieEntry(entriesWithNoLabel[3], "High"))
        noPriorityNoteEntry = entriesWithLabel[0]
        return entriesWithLabel
    }

    private fun setupPieChart() {
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(false)
        pieChart.setEntryLabelTextSize(18f)
        pieChart.valuesToHighlight()
        pieChart.isDrawHoleEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pieChart.setEntryLabelColor(getColor(R.color.design_default_color_secondary))
            pieChart.setCenterTextColor(getColor(R.color.design_default_color_secondary))
            pieChart.setHoleColor(getColor(R.color.background_for_hole_pie_chart))
        }

        pieChart.centerText = "Notes by Priority"
        pieChart.setCenterTextSize(26f)
        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled = false
        val l = pieChart.legend

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            l.textColor = getColor(R.color.opposite_to_theme)
        }
        l.textSize = 16f
        l.formSize = 16f
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true
    }

    private fun loadPieChartData(entries : ArrayList<PieEntry>) {
        entries.reverse()
        val colors = ArrayList<Int>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colors.add(getColor(R.color.priority_red))
            colors.add(getColor(R.color.priority_yellow))
            colors.add(getColor(R.color.priority_green))
            colors.add(Color.LTGRAY)
        }
        else{
            for (color in ColorTemplate.MATERIAL_COLORS) {
                colors.add(color)
            }
        }

        val dataSet = PieDataSet(entries, "Notes With Priority")
        dataSet.colors = colors
        pieDataForPieChart = PieData(dataSet)

        pieDataForPieChart.setDrawValues(true)
//        data.setValueFormatter(PercentFormatter(pieChart))
        pieDataForPieChart.setValueFormatter(MyCustomValueFormatter())

        pieDataForPieChart.setValueTextSize(18f)
        //data.setValueTextColor(R.color.opposite_to_theme)
        pieChart.data = pieDataForPieChart
        pieChart.invalidate()
        pieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    // removes zero value elements and converts the default float value to integer
    class MyCustomValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return if (value == 0f) {
                ""
            } else {
                value.roundToInt().toString()
            }
        }
    }
}