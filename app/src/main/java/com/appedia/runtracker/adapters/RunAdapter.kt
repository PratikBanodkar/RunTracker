package com.appedia.runtracker.adapters

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appedia.runtracker.R
import com.appedia.runtracker.data.db.entities.Run
import com.appedia.runtracker.util.Constants.getFormattedCaloriesForUI
import com.appedia.runtracker.util.Constants.getFormattedDateForUI
import com.appedia.runtracker.util.Constants.getFormattedDistanceForUI
import com.appedia.runtracker.util.Constants.getFormattedRunTimeForUI
import com.appedia.runtracker.util.Constants.getFormattedSpeedForUI
import com.bumptech.glide.Glide

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {


    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val runImageView = itemView.findViewById<ImageView>(R.id.runImageView)
        private val runDateTextView = itemView.findViewById<TextView>(R.id.runDateTextView)
        private val runDurationTextView = itemView.findViewById<TextView>(R.id.runDurationTextView)
        private val runDistanceTextView = itemView.findViewById<TextView>(R.id.runDistanceTextView)
        private val runSpeedTextView = itemView.findViewById<TextView>(R.id.runSpeedTextView)
        private val runCaloriesTextView = itemView.findViewById<TextView>(R.id.runCaloriesTextView)
        fun bind(run: Run) {
            Glide.with(runImageView).load(run.image).into(runImageView)
            runDateTextView.text = getFormattedDateForUI(run.runDateInMillis)
            runDurationTextView.text = getFormattedRunTimeForUI(run.runDurationInMillis)
            runDistanceTextView.text = getFormattedDistanceForUI(run.distanceMTR)
            runSpeedTextView.text = getFormattedSpeedForUI(run.avgSpeedKMPH)
            runCaloriesTextView.text = getFormattedCaloriesForUI(run.caloriesBurned)
        }
    }

    class SpacingItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceSize
                }
                bottom = spaceSize
            }
        }
    }

    private var diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Run, newItem: Run) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RunViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_run_list_item, parent, false)
    )

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.bind(run)
    }

    override fun getItemCount() = differ.currentList.size
}