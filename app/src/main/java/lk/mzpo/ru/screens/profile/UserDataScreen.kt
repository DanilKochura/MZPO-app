package lk.mzpo.ru.screens.profile

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.ProfileItem
import lk.mzpo.ru.models.User
import lk.mzpo.ru.screens.ProfileHeader
import lk.mzpo.ru.ui.components.CustomTextField
import lk.mzpo.ru.ui.components.DatePickerDemo
import lk.mzpo.ru.ui.components.DateTextField
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.NameTextField
import lk.mzpo.ru.ui.components.PhoneTextField
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.components.checkPhone
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreen(
    user: User,
    navHostController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel()
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
            val ctx = LocalContext.current

            profileViewModel.user.value = user


            val snils = remember {
                mutableStateOf(TextFieldValue(user.userData?.snils.toString()))
            }

            val seria = remember {
                mutableStateOf(TextFieldValue(user.userData?.passSeries.toString()))
            }
            val number = remember {
                mutableStateOf(TextFieldValue(user.userData?.passNumber.toString()))
            }
            val place_given = remember {
                mutableStateOf(TextFieldValue(user.userData?.passPoi.toString()))
            }
            val place_living = remember {
                mutableStateOf(TextFieldValue(user.userData?.passRegistration.toString()))
            }
            val code = remember {
                mutableStateOf(TextFieldValue(user.userData?.passDpt.toString()))
            }
            val date_given = remember {
                mutableStateOf(TextFieldValue(user.userData?.passDoi.toString().replace("-", "")))
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
                                   text = "Редактировать профиль",
                                   textAlign = TextAlign.Center,
                                   fontSize = 20.sp,
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(vertical = 10.dp)
                               )

                               CustomTextField(name = "СНИЛС", placeholder = "СНИЛС", value = snils,modifier = Modifier.fillMaxWidth())
                               Row(Modifier.fillMaxWidth()) {
                                   CustomTextField(name = "Серия паспорта", placeholder = "XXXX", value = seria ,modifier = Modifier
                                       .fillMaxWidth(0.5f)
                                       .padding(end = 5.dp))
                                   CustomTextField(name = "Номер паспорта", placeholder = "XXXXXX", value = number ,modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(start = 5.dp))

                               }
                               CustomTextField(name = "Место выдачи паспорта", placeholder = "СНИЛС", value = place_given,modifier = Modifier.fillMaxWidth())
                               Row(Modifier.fillMaxWidth()) {
                                   DateTextField(name = "Дата выдачи", value = date_given,modifier = Modifier
                                       .fillMaxWidth(0.5f)
                                       .padding(end = 5.dp))
                                   CustomTextField(name = "Код подразделения", placeholder = "ул. Кузекцкий мост 21/5", value = code,modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(start = 5.dp))
                               }
                               CustomTextField(name = "Место регистрации", placeholder = "XXX-XXX", value = place_given,modifier = Modifier.fillMaxWidth())
                           }



                            Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                                Button(onClick = {
                                                 val date_pass = date_given.value.text.substring(0,4) + "-" + date_given.value.text.substring(4,6) + "-" + date_given.value.text.substring(6,8)
                                                    val data = user.userData
                                                    if(data !== null)
                                                    {
                                                        data.passDoi = date_pass
                                                        data.snils = snils.value.text
                                                        data.passSeries = seria.value.text
                                                        data.passNumber = number.value.text
                                                        data.passPoi = place_given.value.text
                                                        data.passRegistration = place_living.value.text
                                                        data.passDpt = code.value.text
                                                        profileViewModel.userData(data, ctx);
                                                    }

                                                 }, modifier = Modifier
                                    .padding(15.dp)
                                    .width(250.dp), colors = ButtonDefaults.buttonColors(containerColor = Aggressive_red)) {
                                    Text(text = "Сохранить")
                                }
                            }
                        }

                    }
                }
            }
        }
    )
}
