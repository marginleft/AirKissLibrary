package com.margintop.anroid.library

/**
 * @author margintop
 * @date 2019/4/1
 */
interface Callback {

    fun onSuccess()

    fun onFailure(message: String?)
}