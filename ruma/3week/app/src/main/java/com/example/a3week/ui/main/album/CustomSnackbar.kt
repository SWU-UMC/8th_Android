package com.example.a3week.ui.main.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.a3week.R
import com.example.a3week.databinding.CustomSnackbarBinding
import com.google.android.material.snackbar.Snackbar

class CustomSnackbar(view: View, private val message: String) {

    companion object {

        fun make(view: View, message: String) = CustomSnackbar(view, message)
    }

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", 5000)
    private val snackbarLayout = snackbar.view as ViewGroup


    private val inflater = LayoutInflater.from(context)
    private val snackbarBinding: CustomSnackbarBinding
            = DataBindingUtil.inflate(inflater, R.layout.custom_snackbar, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            addView(snackbarBinding.root, 0)
        }
    }

    private fun initData() {
        snackbarBinding.customSnackbarTv.text = message
        snackbarBinding.customSnackbarBtn.setOnClickListener {
            // OK 버튼을 클릭했을 때 실행할 동작을 정의할 수 있다.
        }
    }

    fun show() {
        snackbar.show()
    }
}