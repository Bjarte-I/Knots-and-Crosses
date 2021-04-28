package com.example.knotsandcrosses.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.knotsandcrosses.databinding.DialogGameIdBinding

class GameIdDialog(gameId: String) : DialogFragment() {
    internal lateinit var listener:GameDialogListener
    private val dialogText = gameId

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = DialogGameIdBinding.inflate(inflater)

            binding.tvGameId.text = dialogText

            builder.apply {
                setTitle("Game Id:")
                setPositiveButton("Okay") { dialog, which ->
                    dialog.cancel()
                }
                setView(binding.root)
            }

            builder.create()


        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as GameDialogListener
        } catch (e:ClassCastException){
            throw ClassCastException(("$context must implement GameDialogListener"))

        }
    }

}