package com.bullet.core.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewParent
import androidx.annotation.NonNull
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatViewInflater
import androidx.appcompat.widget.VectorEnabledTintResources
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.ViewCompat
import com.bullet.core.base.skin.SkinManager
import com.bullet.core.base.skin.SkinResource
import com.bullet.core.base.skin.attr.SkinAttr
import com.bullet.core.base.skin.attr.SkinView
import com.bullet.core.base.skin.callback.ISkinChangeListener
import com.bullet.core.base.skin.support.SkinAppCompatViewInflater
import com.bullet.core.base.skin.support.SkinAttrSupport
import org.xmlpull.v1.XmlPullParser

abstract class BaseSkinActivity : BaseActivity(),
        LayoutInflater.Factory2, ISkinChangeListener {

    private var mAppCompatViewInflater: SkinAppCompatViewInflater? = null
    private val lollipop = Build.VERSION.SDK_INT < 21


    override fun onCreate(savedInstanceState: Bundle?) {
        SkinManager.init(this)
        intercept()
        super.onCreate(savedInstanceState)
    }

    /**
     * 拦截 View 的创建
     */
    private fun intercept() {
        val layoutInflater = LayoutInflater.from(this)
        LayoutInflaterCompat.setFactory2(layoutInflater, this)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    /**
     * 拦截 View 后，创建 View 会走下面这个方法
     * 这个方法在一个页面中会被调用多次
     */
    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        //创建 View
        val view = createView(parent, name, context, attrs)
        //分析属性 textColor src background
        if (view != null) {
            //获取当前 view 需要换肤的资源类型
            val skinAttrs: List<SkinAttr> = SkinAttrSupport.getSkinAttrs(context, attrs)
            //对要换肤的 View 进行包装
            val skinView = SkinView(view, skinAttrs)
            //交给 SkinManager 进行管理
            managerSkinView(skinView)
            //判断是否需要换肤、
            SkinManager.checkChangeSkin(skinView)
        }
        return view
    }

    /**
     * 统一管理 SkinView
     */
    private fun managerSkinView(skinView: SkinView) {
        //获取当前 activity 的资源
        var skinViews = SkinManager.getSkinViews(this)
        if (skinViews == null) {
            //创建新的
            skinViews = mutableListOf()
            //注册当前 activity
            SkinManager.register(this, skinViews)
        }
        //将要换肤的 View 添加进与当前 activity 对应的集合中
        skinViews.add(skinView)
    }

    /**
     * 子类如果需要可重写此方法，该方法会在换肤的时候调用
     * skinResources 是资源包的资源
     */
    override fun changeSkin(skinResource: SkinResource) {

    }

    override fun onDestroy() {
        //反注册
        SkinManager.unregister(this)
        super.onDestroy()
    }

    /**
     * --------------------------------------------------------------------------------------
     * 因为创建 View 需要进行兼容，所以这里直接 copy 系统源码
     * 下面代码都是 AppCompatDelegateImpl 中的
     * AppCompatDelegateImpl 中拦截了 View ，进行了兼容
     * 这里直接 copy 过来使用
     */
    @SuppressLint("Recycle", "PrivateResource", "RestrictedApi")
    private fun createView(
            parent: View?, name: String, @NonNull context: Context, @NonNull attrs: AttributeSet
    ): View? {
        if (mAppCompatViewInflater == null) {
            val a = context.obtainStyledAttributes(R.styleable.AppCompatTheme)
            val viewInflaterClassName = a.getString(R.styleable.AppCompatTheme_viewInflaterClass)
            if (viewInflaterClassName == null || AppCompatViewInflater::class.java.name == viewInflaterClassName) {
                // Either default class name or set explicitly to null. In both cases
                // create the base inflater (no reflection)
                mAppCompatViewInflater = SkinAppCompatViewInflater()
            } else {
                mAppCompatViewInflater = try {
                    val viewInflaterClass = Class.forName(viewInflaterClassName)
                    viewInflaterClass.getDeclaredConstructor()
                            .newInstance() as SkinAppCompatViewInflater
                } catch (t: Throwable) {
                    Log.i(
                            "BaseSkinActivity", "Failed to instantiate custom view inflater "
                            + viewInflaterClassName + ". Falling back to default.", t
                    )
                    SkinAppCompatViewInflater()
                }

            }
        }

        var inheritContext = false
        if (lollipop) {
            inheritContext = if (attrs is XmlPullParser)
                (attrs as XmlPullParser).depth > 1
            else
                shouldInheritContext(parent as ViewParent)// If we have a XmlPullParser, we can detect where we are in the layout
            // Otherwise we have to use the old heuristic
        }
        return mAppCompatViewInflater!!.createView(
                parent, name, context, attrs, inheritContext,
                lollipop, /* Only read android:theme pre-L (L+ handles this anyway) */
                true, /* Read read app:theme as a fallback at all times for legacy reasons */
                VectorEnabledTintResources.shouldBeUsed() /* Only tint wrap the context if enabled */
        )
    }

    private fun shouldInheritContext(viewParent: ViewParent?): Boolean {
        var parent: ViewParent? = viewParent ?: return false
        val windowDecor = window.decorView
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true
            } else if (parent === windowDecor || parent !is View
                    || ViewCompat.isAttachedToWindow((parent as View?)!!)
            ) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false
            }
            parent = viewParent.parent
        }
    }
}