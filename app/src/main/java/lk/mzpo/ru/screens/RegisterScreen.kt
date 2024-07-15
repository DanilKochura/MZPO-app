package lk.mzpo.ru.screens

import android.content.Context
import android.util.JsonToken
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.google.gson.Gson
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import lk.mzpo.ru.InDev
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.BuyExtendRequest
import lk.mzpo.ru.network.retrofit.Data2Amo
import lk.mzpo.ru.network.retrofit.DemoRegisterRequest
import lk.mzpo.ru.network.retrofit.SendDataToAmo
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.NameTextField
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.components.PhoneTextField
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.components.isValidEmail
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    cart_sum: MutableState<Int> = mutableStateOf(0)
) {
    Scaffold(
//            bottomBar = { BottomNavigationMenu(navController = nav)  },
//            topBar = {
//                if (!topBarDisabled.value) {
//                    TopAppBar(backgroundColor = MaterialTheme.colorScheme.background)
//                    {
//                        Text(
//                            title.value,
//                            fontSize = 22.sp,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                    }
//                }
//            },
        bottomBar = { BottomNavigationMenu(navController = navHostController, cart = cart_sum) },
        content = { padding ->
            val ctx = LocalContext.current
            Box(
                Modifier
                    .background(color = Primary_Green)
                    .fillMaxSize()
            ) {
                //region Top
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.lebed),
                        contentDescription = "lebed_back",
                        modifier = Modifier.padding(1.dp)
                    )
                }
                //endregion

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    //region Search
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp)
                    ) {
                        SearchViewPreview(navHostController);
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "bell",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    //endregion

                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(
                                    topStart = MainRounded,
                                    topEnd = MainRounded
                                )
                            )
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded))
                    ) {
                        val context = LocalContext.current
                        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                        val token = test.getString("token_lk", "")
                        if (!token.isNullOrBlank() && token.isNotEmpty()) {
                            LaunchedEffect(key1 = "test")
                            {
                                Log.d("MyLog", "Study to login!")
                                navHostController.navigate("contracts")
                            }
                        }
                        val device = test.getString("token", "")
                        Log.d("MyLog", "test")
                        Register(token = device, navHostController)

                    }
                }
            }
        }
    )
}

@Composable
fun Register(token: String?, navHostController: NavHostController) {
    val login = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val phone = remember {
        mutableStateOf("")
    }
    val name = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val phoneError = remember {
        mutableStateOf(false)
    }
    val emailError = remember {
        mutableStateOf(false)
    }
    val nameError = remember {
        mutableStateOf(false)
    }

    val password = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val passError = remember {
        mutableStateOf(false)
    }

    val passStage = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Регистрация", textAlign = TextAlign.Center, fontSize = 28.sp)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(
                painter = painterResource(id = R.drawable.mirk_logo),
                contentDescription = "MirkLogo",
                modifier = Modifier.size(120.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.mzpo_logo),
                contentDescription = "MzpoLogo",
                modifier = Modifier.size(120.dp)
            )
        }


        if (!passStage.value)
        {
            Text(
                text = "Для получения доступа к учебным материалам и покупкам, войдите или зарегистрируйтесь",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            EmailTextField(email = login, isError = emailError)
            PhoneTextField(phone = phone, isError = phoneError)
            NameTextField(name = name, isError = nameError)
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
//                modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(phone.value.length != 10)
                        {
                            phoneError.value = true
                            return@Button
                        }else
                        {
                            phoneError.value = false

                        }
                        if (login.value.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(login.value.text).matches())
                        {
                            emailError.value = false
                        } else
                        {
                            emailError.value = true
                            return@Button
                        }
                        if(name.value.text.isEmpty())
                        {
                            nameError.value = true
                            return@Button
                        } else
                        {
                            nameError.value = false
                        }
                        val dpost = DemoRegisterRequest.PostBody(
                            name = name.value.text,
                            email = login.value.text,
                            phone = "+7"+phone.value,
                            demo_key = "AnWqKt8xSkQlTPI"
                        );

                        val pref = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                        val token_ = pref.getString("token_lk", "")
                        DemoRegisterRequest().send(
                            "Bearer " + token_?.trim('"'),
                            dpost

                        ).enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e("API Request", "I got an error and i don't know why :(")
                                Log.e("API Request", t.message.toString())
                                Toast.makeText(context, "Произошла ошибка. Попробуйте позже!", Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                response.body()?.let { responseBody ->
                                    val responseBodyString = responseBody.string()
                                    Log.d("API Response", responseBodyString)
                                    if (response.isSuccessful) {
                                        if(responseBodyString.trim('"') == "already exists")
                                        {
                                            Toast.makeText(context, "У вас уже есть аккаунт!", Toast.LENGTH_LONG).show()
                                            navHostController.navigate("login")
                                        } else if(responseBodyString.trim('"') == "success")
                                        {
                                            Toast.makeText(context, "Успех!", Toast.LENGTH_LONG).show()
                                            passStage.value = true
                                        } else
                                        {
                                            Toast.makeText(context, "Произошла ошибка. Попробуйте позже!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                            }
                        })
//                    val data = AuthData(login.value.text, password.value.text, token.toString())
//                    val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
//                    val gson = Gson()
//                    test.edit().putString("auth_data", gson.toJson(data)).apply()
//                    AuthService.login(data, context, navHostController)

                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Регистрация", color = Color.White)
                }
            }
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Уже зарегистрированы?",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navHostController.navigate("login")
                    })
            }
        } else
        {
            Text(text = "На вашу почту "+login.value.text+" выслан пароль. Введите его в поле ниже для входа в созданный аккаунт", textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp))
            Text(text = "Не забудьте подтвердить вашу почту по ссылке в письме", textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp), fontSize = 10.sp)
            PasswordTextField(password = password, isError = passError)
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
//                modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(password.value.text.isEmpty())
                        {
                            phoneError.value = true
                            return@Button
                        }else
                        {
                            phoneError.value = false
                        }

                        val data = AuthData(login.value.text, password.value.text, token.toString())
                        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                        val gson = Gson()
                        test.edit().putString("auth_data", gson.toJson(data)).apply()
                        AuthService.login(data, context, navHostController, false)
                        navHostController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Войти", color = Color.White)
                }
            }
            
        }
        
    }
}

