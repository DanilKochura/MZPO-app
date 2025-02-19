package lk.mzpo.ru.screens.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.User
import lk.mzpo.ru.screens.ProfileHeader
import lk.mzpo.ru.ui.components.checkPhone
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.JobsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    user: User,
    navHostController: NavHostController,
    jobsViewModel: JobsViewModel = viewModel(),
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


            val uriHandler = LocalUriHandler.current

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
            Log.d("JobLog", user.jobAccess.toString())
            if(user.jobAccess == 1)
            {
                jobsViewModel.auth.value = true
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
                                .padding(horizontal = 10.dp), verticalArrangement = Arrangement.SpaceBetween) {

                           Column {
                               Text(
                                   text = "Трудоустройство",
                                   textAlign = TextAlign.Center,
                                   fontSize = 20.sp,
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(vertical = 10.dp)
                               )
                               Spacer(modifier = Modifier.height(30.dp))
                               if (!jobsViewModel.auth.value) {
                                   Text(text = "Зарегистрируйтесь на портале трудоустройства МЦПО и МИРК для поиска работы.",
                                       Modifier
                                           .fillMaxWidth()
                                           .padding(10.dp), textAlign = TextAlign.Center)
                                   Text(text = "Регистрация произойдет автоматически.",
                                       Modifier
                                           .fillMaxWidth()
                                           .padding(10.dp), textAlign = TextAlign.Center)

                                   Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                                       Checkbox(
                                           checked = jobsViewModel.agreement.value,
                                           onCheckedChange = { jobsViewModel.agreement.value = it }
                                       )
                                       Text(text = "Согласен с условиями")
                                       Text(
                                           text = "Договора оферты",
                                           modifier = Modifier.clickable {
                                            uriHandler.openUri("https://trayektoriya.ru/build/documents/oferta_aplicant.pdf")
                                       },
                                           color = Color.Blue)

                                   }
                                   Row(
                                       Modifier
                                           .fillMaxWidth()
                                           .padding(20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                                       Button(onClick = {
                                           if(jobsViewModel.agreement.value)
                                           {
                                               jobsViewModel.register(ctx)
                                           }
                                       }, colors = ButtonDefaults.buttonColors(containerColor = Primary_Green)) {
                                           Text(text = "Зарегистрироваться")
                                       }
                                   }
                               } else {
                                   Text(text = "Вы уже зарегистрированы на портале. Данные для входа отправлены на вашу почту.",
                                       Modifier
                                           .fillMaxWidth()
                                           .padding(10.dp), textAlign = TextAlign.Center)
                                   Row(
                                       Modifier
                                           .fillMaxWidth()
                                           .padding(20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                                       Button(onClick = {
                                           uriHandler.openUri("https://jobs.mzpo-s.ru/applicant")
                                       }, colors = ButtonDefaults.buttonColors(containerColor = Primary_Green)) {
                                           Text(text = "Перейти")
                                       }
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
