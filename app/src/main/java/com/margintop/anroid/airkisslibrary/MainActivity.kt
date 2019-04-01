package com.margintop.anroid.airkisslibrary

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.margintop.anroid.library.Callback
import com.margintop.anroid.library.Closeable
import com.margintop.anroid.library.autoLink
import com.margintop.anroid.library.getCurrentSSID
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mCloseable: Closeable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mEtSSID.setText(getCurrentSSID(this))
        mEtPsw.setText("ab123123")
        mTvResult.setText("配网结果：")

        val progress = ProgressDialog(this).apply {
            setMessage("正在连接......")
            setCancelable(false)
        }
        mBtnAutoLink.setOnClickListener {
            progress.show()
            mTvResult.setText("配网结果：\n")
            mCloseable = autoLink(mEtSSID.text.toString().trim(), mEtPsw.text.toString().trim(),
                object : Callback {
                    override fun onSuccess() {
                        progress.hide()
                        mTvResult.append("配网成功\n${Thread.currentThread().name}")
                    }

                    override fun onFailure(message: String?) {
                        progress.hide()
                        mTvResult.append("配网失败\n${Thread.currentThread().name}\n$message")
                    }
                })
        }
        mBtnClose.setOnClickListener {
            progress.show()
            mTvResult.setText("配网结果：\n")
            mCloseable = autoLink(mEtSSID.text.toString().trim(), mEtPsw.text.toString().trim(),
                object : Callback {
                    override fun onSuccess() {
                        progress.hide()
                        mTvResult.append("配网成功\n${Thread.currentThread().name}")
                    }

                    override fun onFailure(message: String?) {
                        progress.hide()
                        mTvResult.append("配网失败\n${Thread.currentThread().name}\n$message")
                    }
                })
            Handler().postDelayed({ mCloseable?.close() }, 2000)
        }
    }
}
