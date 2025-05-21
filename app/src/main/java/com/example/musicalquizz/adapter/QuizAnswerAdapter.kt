package com.example.musicalquizz.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.model.AnswerDraft
import com.example.musicalquizz.databinding.ItemQuizAnswerBinding

class QuizAnswerAdapter(
    private val onAnswerChanged: (AnswerDraft) -> Unit
) : ListAdapter<AnswerDraft, QuizAnswerAdapter.VH>(DiffCallback()) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemQuizAnswerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: ItemQuizAnswerBinding) :
        RecyclerView.ViewHolder(b.root) {

        private var current: AnswerDraft? = null

        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                current?.let {
                    it.answerText = s?.toString().orEmpty()
                    onAnswerChanged(it)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        init {
            b.etAnswerText.addTextChangedListener(textWatcher)
            b.cbIsCorrect.setOnCheckedChangeListener { _, checked ->
                current?.let {
                    it.isCorrect = checked
                    onAnswerChanged(it)
                }
            }
        }

        fun bind(ans: AnswerDraft) {
            // убираем watcher перед установкой текста
            b.etAnswerText.removeTextChangedListener(textWatcher)

            current = ans
            b.etAnswerText.setText(ans.answerText)
            b.etAnswerText.setSelection(ans.answerText.length)
            b.cbIsCorrect.isChecked = ans.isCorrect

            // добавляем watcher обратно
            b.etAnswerText.addTextChangedListener(textWatcher)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AnswerDraft>() {
        override fun areItemsTheSame(old: AnswerDraft, new: AnswerDraft) =
            old.id == new.id

        override fun areContentsTheSame(old: AnswerDraft, new: AnswerDraft) =
            old == new
    }
}
