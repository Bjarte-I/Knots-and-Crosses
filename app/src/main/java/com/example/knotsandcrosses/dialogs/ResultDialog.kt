package com.example.knotsandcrosses.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.knotsandcrosses.GameManager
import com.example.knotsandcrosses.databinding.DialogWinOrLooseBinding


class ResultDialog(gameResult: String) : DialogFragment() {
    internal lateinit var listener:ResultDialogListener
    var result = gameResult

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = DialogWinOrLooseBinding.inflate(inflater)

            builder.apply {
                val resultString = "%1s%2s".format("You ", result)
                setTitle("Result")
                binding.tvResult.text = resultString
                setPositiveButton("Go back") { dialog, which ->
                    listener.onDialogWinOrLoose()
                }
                setView(binding.root)
            }

            builder.create()


        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ResultDialogListener
        } catch (e:ClassCastException){
            throw ClassCastException(("$context must implement GameDialogListener"))

        }
    }

}