package com.example.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment: Fragment() {
    private var dataList = emptyList<Crime>()
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel:CrimeListViewModel by lazy { ViewModelProviders.of(this).get(CrimeListViewModel::class.java) }

    interface Callbacks {
        fun onCrimeSelected(crimeId:UUID)
    }
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter


        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes -> crimes?.let {
                Log.i(TAG, "Got crimes${crimes.size}")
                adapter?.setData(it)
//                updateUI(crimes)
                }
            })
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

//    private fun updateUI(crimes: List<Crime>) {
////        adapter = CrimeAdapter(crimes)
//        adapter?.submitList(crimes)
//
//        println("SUKA " + adapter?.currentList)
////        crimeRecyclerView.adapter = adapter
//    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    private inner class CrimeHolder(view:View):RecyclerView.ViewHolder(view),View.OnClickListener {
        private lateinit var crime: Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) { View.VISIBLE } else { View.GONE }
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }
private inner class CrimeAdapter(var crimes:List<Crime>): RecyclerView.Adapter<CrimeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
        return CrimeHolder(view)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val currentItem = crimes[position]
        holder.bind(currentItem)
    }


    override fun getItemCount(): Int = crimes.size

    fun setData(toDoData: List<Crime>) {
        val toDoDiffUtil = TodoDiffUtil(crimes, toDoData)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)
        crimes = toDoData
        toDoDiffResult.dispatchUpdatesTo(this)
    }
}
//    private inner class CrimeAdapter(var crimes:List<Crime>): ListAdapter<Crime,CrimeHolder>(DiffUtilCallback) {
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
//            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
//            return CrimeHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
//            val crime = getItem(position)
//            holder.bind(crime)
//        }
//
//
//        override fun getItemCount(): Int = currentList.size
//
//
//    }
//
//    private object DiffUtilCallback: DiffUtil.ItemCallback<Crime>() {
//        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
//            return oldItem == newItem
//        }
//    }
}