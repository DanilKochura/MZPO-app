package lk.mzpo.ru.screens.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.User
import lk.mzpo.ru.screens.ProfileHeader
import lk.mzpo.ru.ui.components.CustomTextField
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.NameTextField
import lk.mzpo.ru.ui.components.PhoneTextField
import lk.mzpo.ru.ui.components.checkPhone
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.BillsViewModel
import lk.mzpo.ru.viewModel.HelpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    user: User,
    navHostController: NavHostController,
    helpViewModel: HelpViewModel = viewModel()
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

            helpViewModel.getData(context = ctx)

            val email = remember {
                mutableStateOf(TextFieldValue(user.email))
            }

            val phone = remember {
                mutableStateOf(checkPhone(user.userData?.phone.toString()))
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
                                .padding(horizontal = 10.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.SpaceBetween) {

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
                                    Text(text = "Выберите тип обращения", modifier = Modifier.padding(top = 10.dp, bottom = 5.dp))
                                    Divider()
                                    for(i in types.indices)
                                    {

                                        Row(modifier = Modifier
                                            .fillMaxWidth()
                                            .selectable(
                                                selected = i == selectedIndex.value,
                                                onClick = {
                                                    if (selectedIndex.value != i)
                                                        selectedIndex.value =
                                                            i else selectedIndex.value = -1
                                                })
                                            .padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column(Modifier.fillMaxWidth(0.8f)) {
                                                Text(text = types[i])
                                                Text(text = descrs[i], fontSize = 12.sp, color = Color.Gray, lineHeight = 12.sp)
                                            }
                                            if(selectedIndex.value == i)
                                            {
                                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "", tint = Primary_Green)
                                            }
                                        }
                                        Divider()
                                    }
                                   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                       Button(onClick = { }, modifier = Modifier
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
