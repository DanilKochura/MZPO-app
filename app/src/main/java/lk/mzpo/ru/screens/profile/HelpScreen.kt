package lk.mzpo.ru.screens.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import lk.mzpo.ru.BuildConfig
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.User
import lk.mzpo.ru.network.retrofit.FeedbackService
import lk.mzpo.ru.network.retrofit.UploadRequestBody
import lk.mzpo.ru.screens.ProfileHeader
import lk.mzpo.ru.ui.components.CustomTextField
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.NameTextField
import lk.mzpo.ru.ui.components.PhoneTextField
import lk.mzpo.ru.ui.components.URIPathHelper
import lk.mzpo.ru.ui.components.checkPhone
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.HelpViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    user: User,
    navHostController: NavHostController,
    helpViewModel: HelpViewModel = viewModel(),
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

//            helpViewModel.getData(context = ctx)

            val email = remember {
                mutableStateOf(TextFieldValue(user.email))
            }

            val phone = remember {
                mutableStateOf(checkPhone(user.phone.toString()))
            }
            val name = remember {
                mutableStateOf(TextFieldValue(user.name))
            }
            val isError = remember {
                mutableStateOf(false)
            }
            val theme = remember {
                mutableStateOf(TextFieldValue())
            }
            val text = remember {
                mutableStateOf(TextFieldValue())
            }
            val imageUri = remember { mutableStateOf<Uri?>(null) }
            val disabled = remember {
                mutableStateOf(false)
            }

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
                    ProfileHeader(navHostController = navHostController)
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



                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp)
                                .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.SpaceBetween) {

                           Column (modifier = Modifier){
                               Text(
                                   text = "Сообщить о проблеме",
                                   textAlign = TextAlign.Center,
                                   fontSize = 20.sp,
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(vertical = 10.dp)
                               )
                               NameTextField(name = name, modifier = Modifier.fillMaxWidth())
                               PhoneTextField(phone = phone, isError = isError, readonly = true, modifier = Modifier.fillMaxWidth())
                               EmailTextField(email = email, isError = isError, readonly = true, modifier = Modifier.fillMaxWidth())
                               CustomTextField(name = "Тема", placeholder = "Тема обращения", value = theme, modifier = Modifier.fillMaxWidth())
                               CustomTextField(name = "Сообщение", placeholder = "Описание проблемы", value = text, modifier = Modifier.fillMaxWidth())

                               PickImageFromGallery(imageUri)
                           }
                                val types = listOf(
                                    "Отдел сопровождения",
                                    "Методический отдел ",
                                    "Международный клуб выпускников",
                                    " Технические вопросы "


                                )
                                val descrs = listOf(
                                    "(по вопросам обучения, переноса обучения)",
                                    "(по вопросам выдачи документов)",
                                    "",
                                    "(по вопросам неработающего личного кабинета, курса и другим техническим ошибкам личного кабинета)"
                                )
                                val selectedIndex = remember{mutableStateOf(-1)}
                                Column {
//                                    Text(text = "Выберите тип обращения", modifier = Modifier.padding(top = 10.dp, bottom = 5.dp))
//                                    Divider()
//                                    for(i in types.indices)
//                                    {
//
//                                        Row(modifier = Modifier
//                                            .fillMaxWidth()
//                                            .selectable(
//                                                selected = i == selectedIndex.value,
//                                                onClick = {
//                                                    if (selectedIndex.value != i)
//                                                        selectedIndex.value =
//                                                            i else selectedIndex.value = -1
//                                                })
//                                            .padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
//                                            Column(Modifier.fillMaxWidth(0.8f)) {
//                                                Text(text = types[i])
//                                                Text(text = descrs[i], fontSize = 12.sp, color = Color.Gray, lineHeight = 12.sp)
//                                            }
//                                            if(selectedIndex.value == i)
//                                            {
//                                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "", tint = Primary_Green)
//                                            }
//                                        }
//                                        Divider()
//                                    }
                                   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                       Button(onClick = {
                                           if (text.value.text.isEmpty() or theme.value.text.isEmpty())
                                           {
                                               Toast.makeText(ctx, "Заполните тему и текст обращения!", Toast.LENGTH_SHORT).show()
                                               return@Button
                                           }
                                                        var bodyPart: MultipartBody.Part? = null
                                                       val uriPathHelper = URIPathHelper()
                                                       if (imageUri.value !== null)
                                                       {
                                                           val filePath = uriPathHelper.getPath(ctx, imageUri.value!!)
                                                           val file: File = File(filePath.toString())
                                                           val test =
                                                               RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                                                           val body = UploadRequestBody(file, "image")
                                                           bodyPart = MultipartBody.Part.createFormData(
                                                               "files[0]",
                                                               file.name, body
                                                           )
                                                       }

                                                       val pref = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
                                                       val token = pref.getString("token_lk", "")
                                                       FeedbackService().sendFeedback(
                                                           "Bearer " + token?.trim('"'),
                                                           bodyPart,
                                                           BuildConfig.VERSION_NAME.toString(),
                                                           "android",
                                                           text.value.text,
                                                           theme.value.text

                                                       ).enqueue(object : retrofit2.Callback<ResponseBody> {
                                                           override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                                               Log.e("API Request", "I got an error and i don't know why :(")
                                                               Log.e("API Request", t.message.toString())
                                                               Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже!", Toast.LENGTH_SHORT).show()


                                                           }

                                                           override fun onResponse(
                                                               call: Call<ResponseBody>,
                                                               response: Response<ResponseBody>
                                                           ) {
                                                               Log.d("API Request", response.body().toString())
                                                               Log.d("API Request", response.message())
                                                               Log.d("API Request", response.errorBody().toString())
                                                               Log.d("API Request", response.raw().body.toString())
                                                               if (response.isSuccessful) {
                                                                   Toast.makeText(
                                                                       ctx,
                                                                       "Обращение сохранено!",
                                                                       Toast.LENGTH_SHORT
                                                                   ).show()

                                                               }
                                                               disabled.value = true
                                                           }
                                                       })
//                                                        helpViewModel.sendFeedback(feedback = feedback, ctx)



                                       }, enabled = !disabled.value, modifier = Modifier
                                           .padding(vertical = 10.dp)
                                           .width(150.dp), colors = ButtonDefaults.buttonColors(containerColor = Aggressive_red, contentColor = Color.White)) {
                                           Text(text = "Отправить")
                                       }
                                   }
                                }

                        }

                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PickImageFromGallery(imageUri: MutableState<Uri?>) {

    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri.value = uri
        }
    val disabled = remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            val launcher_ = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission Accepted: Do something
                    Log.d("ExampleScreen", "PERMISSION GRANTED")

                } else {
                    // Permission Denied: Do something
                    Log.d("ExampleScreen", "PERMISSION DENIED")
                }
            }

            val rem = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { Log.d("MyLogHETE", "TEST") }
            val cameraPermissionState = rememberMultiplePermissionsState(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    // permission not needed
                    emptyList()
                }
            )


            Button(
                onClick = {
                    // Camera permission state

                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        launcher.launch("image/*")
                    } else {
                        rem.launch(Manifest.permission.CAMERA)
//                    cameraPermissionState.launchPermissionRequest()
                        Toast.makeText(
                            context,
                            "Разрешите приложению доступ к галерее",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary_Green),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
                Text(text = "Добавить изображение", color = Color.White)
            }
        }
        LaunchedEffect(key1 = imageUri.value) {
            imageUri.let {
                imageUri.value = it.value
                if (it.value !== null) {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it.value)

                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver, it.value!!)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }
                }
            }
        }
        if (bitmap.value !== null) {

            Image(
                bitmap = bitmap.value!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))


    }


}

