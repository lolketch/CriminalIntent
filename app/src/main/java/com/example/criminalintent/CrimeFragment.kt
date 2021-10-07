package com.example.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.time.days
import kotlin.time.hours

private const val REQUEST_DATE = 0
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"
private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
class CrimeFragment: Fragment() {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private val crimeDetailViewModel:
            CrimeDetailViewModel by lazy { ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()

        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
                viewLifecycleOwner,
                Observer { crime -> crime?.let {
                        this.crime = crime
                        updateUI()
                    }
                })
    }

    override fun onStart() {
        super.onStart()
        val constraintBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build()
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintBuilder)
                .setTitleText("Выберите дату")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(Calendar.HOUR)
                .setMinute(Calendar.MINUTE)
                .build()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?,
                                           start: Int,
                                           count: Int,
                                           after: Int) {
            // Это пространство оставлено пустым специально
            }
            override fun onTextChanged(sequence: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                crime.title = sequence.toString()
            }
            override fun
                    afterTextChanged(sequence: Editable?) {
                    // И это
            }
        }
        titleField.addTextChangedListener(titleWatcher)
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
        }
        dateButton.setOnClickListener {
            materialDatePicker.addOnPositiveButtonClickListener {
                val date2 = Date(materialDatePicker.selection!!)
                val hours = crime.date.hours
                val min = crime.date.minutes
                crime.date = date2
                crime.date.hours = hours
                crime.date.minutes = min
                updateUI()
            }
            materialDatePicker.show(requireActivity().supportFragmentManager, "fragment_tag")
        }
        timeButton.setOnClickListener {
            materialTimePicker.addOnPositiveButtonClickListener {
                val newHour: Int = materialTimePicker.hour
                val newMinute: Int = materialTimePicker.minute
                crime.date.hours = newHour
                crime.date.minutes = newMinute
                updateUI()
            }
            materialTimePicker.show(requireActivity().supportFragmentManager, "fragment_tag")
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }


    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState() // А иначе оно анимируется(стр423)
        }
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}