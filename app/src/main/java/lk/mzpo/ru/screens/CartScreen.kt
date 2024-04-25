package lk.mzpo.ru.screens

import CoursePreview
import Prices
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources.Theme
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import lk.mzpo.ru.InDev
import lk.mzpo.ru.OrderActivity
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.CartViewModel
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizeIntent
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.GooglePayParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import kotlin.math.round

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navHostController: NavHostController, cartViewModel: CartViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0)

) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token", "")

    val cart = remember() {
        mutableStateOf(listOf( hashMapOf("" to "")))
    }

    val cartSum = remember {
        cart_sum
    }
    AuthService.testAuth(ctx, navHostController, cartViewModel.auth_tested, false);
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
        bottomBar = { BottomNavigationMenu(navController = navHostController, cartSum, cartViewModel.auth_tested) },
        content = { padding ->


            LaunchedEffect(key1 = cartViewModel.auth_tested.value, block = {
                if(cartViewModel.courses.isEmpty())
                {
                    if (cartViewModel.auth_tested.value == AuthStatus.AUTH)
                    {
                        Log.d("CartLog", "LK")
                        cartViewModel.getLkCart(ctx)
                    } else if(cartViewModel.auth_tested.value == AuthStatus.GUEST)
                    {
                        if(!token.isNullOrEmpty())
                        {
                            FirebaseHelpers.getCartSum(token, cartSum)
                        }
                        Log.d("CartLog", "Firebase")

                        FirebaseHelpers.getCart(token, cart)

                    }
                }
            })
            LaunchedEffect(key1 = cart.value, block = {
                if(cartViewModel.auth_tested.value == AuthStatus.GUEST)
                {
                    cartViewModel.getCourses(ctx, cart)
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
                        IconButton(onClick = { if(cartViewModel.auth_tested.value == AuthStatus.AUTH)
                        {
                            navHostController.navigate("notifications")
                        } }) {
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

                        LazyColumn(modifier = Modifier)
                        {
                            item { 
                                Text(text = "Список покупок", fontSize = 24.sp, modifier = Modifier
                                    .padding(top = 15.dp)
                                    .fillMaxWidth(), textAlign = TextAlign.Center)
                                if(cartViewModel.courses.isEmpty())
                                {

                                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(top = 50.dp, start = 50.dp, end = 50.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally ) {
                                            Icon(painter = painterResource(id = R.drawable.baseline_shopping_cart_24), contentDescription = "", modifier = Modifier.size(80.dp), tint = Primary_Green   )
                                            Text(text = "В корзине пока ничего нет", fontSize = 20.sp, modifier = Modifier.padding(top = 10.dp))
                                            Text(text = "Здесь будут храниться курсы, которые вам понравятся", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 5.dp))
                                            OutlinedButton(onClick = { navHostController.navigate("categories") }, border = BorderStroke(2.dp, Primary_Green), modifier = Modifier.padding(vertical = 10.dp)) {
                                                Text(text = "Перейти в каталог", color = Primary_Green, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                }
                            }
                            itemsIndexed(cartViewModel.courses)
                            {
                                _, value ->
                                val course = value.course
                                val price_type =  value.type
                                var price = if (course.prices.dist !== null) course.prices.dist else course.prices.sale15!!

                                if (price_type == "sale15")
                                {
                                    price = course.prices.sale15!!
                                } else if(price_type == "dist")
                                {
                                    price = course.prices.dist!!
                                }else if(price_type == "ind")
                                {
                                    price = course.prices.ind!!
                                }else if(price_type == "weekend")
                                {
                                    price = course.prices.weekend!!
                                }

                               CartCourse(course, price = price, {
                                    if(value.id == 0)
                                    {
                                        cartViewModel.deleteCartItem(ctx, course.id)
                                    } else
                                    {
                                        cartViewModel.deleteCartItem(ctx, value.id)
                                    }
                                   cartViewModel.courses.remove(value)
                                   cartSum.value--
                               }, {
                                   navHostController.navigate("course/${value.id.toString()}")
                               }, {
                                   Toast.makeText(ctx, "Test", Toast.LENGTH_SHORT).show()
                                   val paymentMethodTypes = setOf(
                                       PaymentMethodType.BANK_CARD,
                                       PaymentMethodType.SBERBANK,
                                       PaymentMethodType.YOO_MONEY,
                                       PaymentMethodType.SBP,
                                   )
                                   val paymentParameters = PaymentParameters(
                                       amount = Amount(BigDecimal.valueOf(10.0), Currency.getInstance("RUB")),
                                       title = course.name,
                                       shopId = "25052",
                                       savePaymentMethod = SavePaymentMethod.OFF,
                                       paymentMethodTypes = paymentMethodTypes,
                                       authCenterClientId =  "n2af44sofjfjsftig09i5j5m32ui0pi6",
                                       clientApplicationKey = "01FlvYILde_7fxcew-qL-VvwxY8Epu4Xz3qyoWQJrd_SiNo4SKprzXEosGdIOIB5",
                                       subtitle = ""
                                   )
//
                                   val intent = createTokenizeIntent(
                                       ctx,
                                       paymentParameters,
                                       TestParameters(showLogs = true)
                                   )
                                   startActivity(ctx, intent, null)
                               })
                            }
                        }
                    }
                }
            }
        }
    )
}




@Composable
fun CartCourse(course: CoursePreview, price: Int, onDelete: () -> Unit, onImageClick: () -> Unit = {}, onBuy: () -> Unit = {}, modifier: Modifier = Modifier)
{
    var text = "";
    if(course.prices.dist == price)
    {
        text = "Дистанционно"
    } else if (course.prices.sale15 == price)
    {
        text = "Очно в группе"
    } else if (course.prices.ind == price)
    {
        text = "Индивидуально"
    } else if (course.prices.weekend == price)
    {
        text = "Учись в выходной"
    }


    Card(modifier = modifier
        .padding(horizontal = 10.dp, vertical = 20.dp)
        .shadow(2.dp, RoundedCornerShape(10.dp))
        , colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {
//        Image(painter = painterResource(id = R.drawable.masage), contentDescription = "", modifier = Modifier
//            .height(150.dp)
//            .fillMaxWidth(), contentScale = ContentScale.Crop)
        Box {

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp)) {
                AsyncImage(model = course.image, contentDescription = course.id.toString(), modifier = Modifier
                    .height(100.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable {
                        onImageClick.invoke()
                    }
                    .clip(RoundedCornerShape(15.dp)), contentScale = ContentScale.Crop)
                Column(modifier = Modifier
                    .weight(2f)
                    .padding(start = 10.dp)
                    .height(100.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Text(course.name, modifier = Modifier.weight(1f), maxLines = 3, overflow = TextOverflow.Ellipsis, fontSize = 18.sp)
                        IconButton(modifier = Modifier.width(25.dp), onClick = {
                            onDelete.invoke()
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete", tint = Color.LightGray, modifier = Modifier
                                .size(25.dp)
//                        .background(
//                            Color.Black.copy(0.5f), RoundedCornerShape(50)
//                        )
                              )
                        }
                    }

                    Row{
                        Text(text = "Форма обучения: ", color = Color.Gray, fontSize = 12.sp)
                        Text(text = text, color = Aggressive_red, fontSize = 12.sp)
                    }

                }
            }

        }
        Column(modifier = Modifier
            .padding(7.dp)
            .fillMaxWidth()) {
            Divider(Modifier.padding(vertical = 5.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(course.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                if (course.prices.dist != 0 && course.prices.dist != null)
                {
                    Row{
                        Text(text = "Дистанционно: ", fontWeight = FontWeight.Bold)
                        Text(text = "9000 руб.", color = Aggressive_red)
                    }
                } else
                {
                    Row{
                        Text(text = "Очно: ", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text(text = price.toString(), fontWeight = FontWeight.Bold, color = Aggressive_red, fontSize = 25.sp)
                    Text(text = price.times(1.15).toBigDecimal().setScale(-2, RoundingMode.UP).toInt().toString(), textDecoration = TextDecoration.LineThrough, modifier = Modifier.padding(start = 10.dp), fontSize = 18.sp)
                }
                Button(onClick = { onBuy.invoke() }, modifier = Modifier, shape = RoundedCornerShape(10), colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red, contentColor = Color.White)) {
                    Text("Купить", color = Color.White)
                }
            }
        }
    }
}
