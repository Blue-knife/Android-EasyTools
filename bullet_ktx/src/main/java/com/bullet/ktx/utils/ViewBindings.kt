package com.xcf.lazycook.common.ktx

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.xcf.lazycook.common.ktx.Mode.Bind
import com.xcf.lazycook.common.ktx.Mode.Inflate

/** 简化的VB */

enum class Mode {
    Inflate, Bind
}

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

@MainThread
inline fun <reified VB : ViewBinding> View.viewBindings() = lazy { inflateViewBinding<VB>(inflater = context.inflater) }

@MainThread
inline fun <reified VB : ViewBinding> Dialog.viewBindings() = lazy { inflateViewBinding<VB>(inflater = context.inflater) }

@MainThread
inline fun <reified VB : ViewBinding> Context.viewBindings() = lazy { inflateViewBinding<VB>(inflater = inflater) }

@MainThread
inline fun <reified VB : ViewBinding> Activity.viewBindings() = lazy {
    inflateViewBinding<VB>(inflater = inflater)
}

@MainThread
inline fun <reified VB : ViewBinding> Fragment.viewBindings(mode: Mode = Inflate) = lazy {
    when (mode) {
        Inflate -> inflateViewBinding<VB>(inflater = requireContext().inflater)
        Bind -> bindViewBinding(requireView())
    }
}

inline fun <reified VB : ViewBinding> inflateViewBinding(inflater: LayoutInflater): VB {
    return VB::class.java.getMethod("inflate", LayoutInflater::class.java)
        .invoke(null, inflater) as VB
}

inline fun <reified VB : ViewBinding> bindViewBinding(view: View): VB {
    return VB::class.java.getMethod("bind", View::class.java)
        .invoke(null, view) as VB
}
