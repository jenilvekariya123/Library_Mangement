package com.jenil.librarymanagement.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.DialogLoadingBinding
import com.jenil.librarymanagement.databinding.ShowDialogBinding

object DialogUtils {

    private lateinit var dialog : Dialog

    fun showDialog(context: Activity, title: String, positiveButton: String,negativeButton: String, onDialogButtonClick: OnDialogButtonClick) {
        dialog = Dialog(context)
        val binding : ShowDialogBinding = ShowDialogBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        val window : Window? = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(((getScreenWidth(context) * 0.9).toInt()),
            ViewGroup.LayoutParams.WRAP_CONTENT)

        if (negativeButton == null){
            binding.btnNegative.visibility = View.GONE
        }else{
            binding.btnNegative.text = negativeButton
        }

        if (title.isNotEmpty() && positiveButton.isNotEmpty()){
            binding.tvTitle.text = title
            binding.btnPositive.text = positiveButton
        }

        binding.btnNegative.setOnClickListener { onDialogButtonClick.onNegativeButtonClick() }
        binding.btnPositive.setOnClickListener { onDialogButtonClick.onPositiveButtonClick() }
        binding.ivClose.setOnClickListener { onDialogButtonClick.onNegativeButtonClick() }

        dialog.show()
    }

    @SuppressLint("ResourceAsColor")
    fun loader(context : Context, loadingText : String){

        dialog = Dialog(context)
        val binding = DialogLoadingBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvLoadingData.text = loadingText
        binding.tvLoadingData.setTextColor(R.color.black)
        if (dialog.window != null){
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
        }
        dialog.show()
    }

    fun dismissDialog(){
        if (dialog.isShowing){
            dialog.dismiss()
        }

    }

    @Suppress("DEPRECATION")
    private fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(android.view.WindowInsets.Type.systemBars())
            val bounds = windowMetrics.bounds
            bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }


    interface OnDialogButtonClick {
        fun onPositiveButtonClick()

        fun onNegativeButtonClick()
    }
}