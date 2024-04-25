package lk.mzpo.ru.models

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.math.log

class Cart {

    public var cart: MutableState<List<String>> = mutableStateOf(listOf())
    public fun getCart(context: Context): List<String> {
        val token = context.getSharedPreferences("cart", 0)
        if(!token.contains("cart"))
        {
            cart = mutableStateOf(listOf())
            return cart.value
        }
        cart.value =token.getString("cart", Context.ACCOUNT_SERVICE).toString().split("|").toTypedArray().toList() as ArrayList<String>

        return cart.value

    }

    public fun addToCart(context: Context, course: Course)
    {
        val list = cart.value as ArrayList<String>
        list.add(course.id.toString())
        cart.value = list
        val string = cart.value.joinToString("|")
        val token = context.getSharedPreferences("cart", 0)
        token.edit().putString("cart",string).apply()
//        Log.d("MyLog", this.getCart(context).joinToString("|"))
//        Log.d("MyLog", cart.value.size.toString())
    }


    public fun addToLKCart(context: Context, course: Course)
    {

    }

}