package com.example.musicalquizz.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton

class AnswerAdapter(

    private var isMulti: Boolean,

    private val onToggle: (answerId: Long, checked: Boolean) -> Unit
) : RecyclerView.Adapter<AnswerAdapter.VH>() {

    companion object {
        private const val TYPE_SINGLE = 0
        private const val TYPE_MULTI = 1
    }

    private var answers: List<AnswerEntity> = emptyList()
    private val selectedIds = mutableSetOf<Long>()
    private var submitted = false


    fun submitList(
        newAnswers: List<AnswerEntity>,
        multi: Boolean,
        initialSelection: Set<Long>
    ) {
        answers = newAnswers
        isMulti = multi
        selectedIds.clear()
        selectedIds.addAll(initialSelection)
        submitted = false
        notifyDataSetChanged()
    }


    fun markSubmitted() {
        submitted = true
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (isMulti) TYPE_MULTI else TYPE_SINGLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val context = parent.context
        val button: CompoundButton = if (viewType == TYPE_MULTI) {
            MaterialCheckBox(context)
        } else {
            MaterialRadioButton(context)
        }

        button.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        ).apply {
            val margin = dp(context, 8)
            setMargins(0, margin, 0, margin)
        }

        val padH = dp(context, 16)
        val padV = dp(context, 12)
        button.setPadding(padH, padV, padH, padV)
        return VH(button)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val answer = answers[position]
        val button = holder.button

        button.text = answer.answerText
        button.isChecked = selectedIds.contains(answer.id)


        button.setOnClickListener {
            if (submitted) return@setOnClickListener
            if (!isMulti) {
                selectedIds.clear()
                selectedIds.add(answer.id)
                notifyDataSetChanged()
            } else {
                if (button.isChecked) selectedIds.add(answer.id) else selectedIds.remove(answer.id)
                notifyItemChanged(position)
            }
            onToggle(answer.id, button.isChecked)
        }

        if (submitted) {
            val bgColor = when {
                answer.isCorrect                -> Color.GREEN
                selectedIds.contains(answer.id) -> Color.RED
                else                             -> Color.TRANSPARENT
            }
            button.setBackgroundColor(bgColor)
        } else {
            button.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun getItemCount(): Int = answers.size

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: CompoundButton = itemView as CompoundButton
    }

    // dp -> px
    private fun dp(context: Context, value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
