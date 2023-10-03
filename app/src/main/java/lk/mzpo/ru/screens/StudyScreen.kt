package lk.mzpo.ru.screens

import android.content.Context
import android.util.JsonToken
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
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
import lk.mzpo.ru.network.retrofit.Data2Amo
import lk.mzpo.ru.network.retrofit.SendDataToAmo
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.components.isValidEmail
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    navHostController: NavHostController,
    studyViewModel: StudyViewModel = viewModel()
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
        bottomBar = { BottomNavigationMenu(navController = navHostController) },
        content = { padding ->
            Log.d("StudyLog", "entered")
            val context = LocalContext.current
            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token_lk", "")

            studyViewModel.testAuth(context, token.toString(), navHostController)
            LaunchedEffect(key1 = studyViewModel.auth_tested.value, block = {
                if(studyViewModel.auth_tested.value)
                {
                    if (token != null) {
                        studyViewModel.getData(token, context = context)
                    }
                }
            })
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
                        SearchViewPreview();
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

                        Text(text = studyViewModel.user.value.name, modifier = Modifier.padding(20.dp), fontSize = 20.sp)
                        Text(text = studyViewModel.user.value.email, modifier = Modifier.padding(20.dp), fontSize = 20.sp)
                        Text(text = studyViewModel.user.value.id.toString(), modifier = Modifier.padding(20.dp), fontSize = 20.sp)
                    }
                }
            }
        }
    )
}


@Composable
fun Login(token: String?, navHostController: NavHostController)
{
    val password = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val login = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val bl = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Log.d("MyLog", "test1")
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Авторизация", textAlign = TextAlign.Center, fontSize = 28.sp)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly, ) {
            Image(painter = painterResource(id = R.drawable.mirk_logo), contentDescription = "MirkLogo", modifier = Modifier.size(120.dp))
            Image(painter = painterResource(id = R.drawable.mzpo_logo), contentDescription = "MzpoLogo", modifier = Modifier.size(120.dp))
        }
        Text(text = "Для получения доступа к учебным материалам, войдите в аккаунт", textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))


        EmailTextField(email = login, isError = bl)
        PasswordTextField(password = password, isError = bl)
        Row(
            modifier = Modifier.padding(all = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
//                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val data = AuthData(login.value.text, password.value.text, token.toString())
                    val test =  context.getSharedPreferences("session", Context.MODE_PRIVATE)
                    val gson = Gson()
                    test.edit().putString("auth_data", gson.toJson(data)).apply()
                    AuthService.login(data, context, navHostController)

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
