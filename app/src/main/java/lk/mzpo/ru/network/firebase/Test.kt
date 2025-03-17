package lk.mzpo.ru.network.firebase

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseHelpers()
{

    companion object {
        @SuppressLint("StaticFieldLeak")
        val db = Firebase.firestore

        public fun createOrUpdateToken(token: String?, context: Context)
        {
            db.collection("users")
                .whereEqualTo("token", token)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty)
                    {
                        val user = hashMapOf(
                            "token" to token,
                        )
                        db.collection("users")
                            .add(user)

                            .addOnSuccessListener { documentReference ->
                                Log.d("myLog111", "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("myLog111", "Error adding document", e)
                            }
                    } else
                    {
                        for (document in documents) {
                            Log.d("myLog111", "${document.id} => ${document.data}")
                        }
                    }


                }
                .addOnFailureListener { exception ->
                    val user = hashMapOf(
                        "token" to token,
                    )
                    db.collection("users")
                        .add(user)

                        .addOnSuccessListener { documentReference ->
                            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                            test.edit().putString("firebase_id", documentReference.id.toString()).apply()
                            val text = test.getString("firebase_id", "")
                            Log.d("CartTest", text.toString())
                            Log.d("myLog111", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("myLog111", "Error adding document", e)
                        }
                    // Log and toast
//                sendToken(token, context)
                }

        }
        fun find(values: List<HashMap<String, String>>, value: String): Boolean {
            return values.any { it["id"] == value }
        }

        fun addToCart(token: String?, hashMap: HashMap<String, String>)
        {
            db.collection("users")
                .whereEqualTo("token", token)
                .get()
                .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if( document.data["cart"] !== null)
                            {
                                val cart = document.data["cart"] as ArrayList<HashMap<String, String>>
                                Log.d("CartTest", cart.joinToString("|")+" "+hashMap["id"].toString())
                                if(!find(cart, hashMap["id"].toString()))
                                {
                                    cart.add(hashMap)
                                }
                                db.collection("users").document(document.id).update("cart", cart)
                            }
                            else
                            {
                                db.collection("users").document(document.id).update("cart", listOf(hashMap))
                            }

                        }



                }




        }


        fun deleteFromCart(token: String?, id: Int)
        {
            db.collection("users")

                    .whereEqualTo("token", token)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val cart = document.data["cart"] as ArrayList<HashMap<String, String>>
                            var deleted = cart.find { it["id"] == id.toString() }
                            cart.remove(deleted)
                                db.collection("users").document(document.id).update("cart", cart)

                        }



                    }



        }

        fun getCart(token: String?, cartL: MutableState<List<HashMap<String, String>>>) {
            val list = arrayListOf<HashMap<String, String>>()
            Log.d("CartLog", token.toString())
            db.collection("users")
                .whereEqualTo("token", token)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if(document.contains("cart"))
                        {
                            val cart = document.data["cart"] as ArrayList<HashMap<String, String>>
                            for (carr in cart)
                            {
                                list.add(carr)
                            }
                        }

                    }
                    Log.d("CartLog", list.joinToString(", "))
                    cartL.value = list
                    return@addOnSuccessListener


                }
                .addOnFailureListener {
                    Log.d("CartLog", it.message.toString())
                    cartL.value = emptyList()
                }
        }

         fun getCartSum(token: String?, cartSum: MutableState<Int>) {

            db.collection("users")
                .whereEqualTo("token", token)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                       if(document.data["cart"] != null)
                       {
                           val cart = document.data["cart"] as ArrayList<HashMap<String, String>>
                           cartSum.value = cart.size
                           Log.d("MyLog", cart.size.toString())
                       } else
                           cartSum.value = 0
                    }
                    return@addOnSuccessListener


                }
                .addOnFailureListener{e ->
                    Log.d("MyLog", "Errror $e")
                }
        }
    }

}