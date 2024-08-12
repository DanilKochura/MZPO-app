package lk.mzpo.ru.screens

import CoursePreview
import Prices
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources.Theme
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import lk.mzpo.ru.InDev
import lk.mzpo.ru.OrderActivity
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.CartItem
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.network.retrofit.PaymentRequest
import lk.mzpo.ru.network.retrofit.PurchaseService
import lk.mzpo.ru.network.retrofit.RetrofitClient
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lk.mzpo.ru.network.retrofit.CheckPaymentRequest
import lk.mzpo.ru.network.retrofit.PaymentResponse
import lk.mzpo.ru.network.retrofit.YookassaConfigs
import lk.mzpo.ru.ui.components.LoadableScreen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
fun sendTokenToServer(token: String, app_token: String, uri: UriHandler, cartId: Int, cartViewModel: CartViewModel) {
    val request = PaymentRequest(
        token = token,
        cartId = cartId
    )
    Log.d("MyLogTest", token)
    Log.d("MyLogTest", app_token)
    Log.d("MyLogTest", cartId.toString())

    PurchaseService.apiService.completePayment("Bearer $app_token", request).enqueue(object : Callback<PaymentResponse> {
        override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
            if (response.isSuccessful) {
                val paymentResponse = response.body()
                cartViewModel.payment_id.value = paymentResponse?.payment_id.toString()
                Log.d("MyLogTEST", paymentResponse?.redirect_url.toString())
                Log.d("Payment", "Server response: ${paymentResponse?.status}")
                uri.openUri(paymentResponse?.redirect_url.toString())
                // Обработайте успешный ответ сервера
            } else {
                Log.e("Payment", "Server error: ${response.errorBody()?.string()}")
                // Обработайте ошибку сервера
            }
        }

        override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
            Log.e("Payment", "Network error: ${t.message}")
            // Обработайте сетевую ошибку
        }
    })
}
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navHostController: NavHostController, cartViewModel: CartViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0)

) {
    val uri = LocalUriHandler.current
   



    LaunchedEffect(cartViewModel.payment_status.value) {
        if (cartViewModel.payment_status.value == 2) {
            delay(2000)
            cartViewModel.showLoader.value = false
        }
    }


    val scope_for_payments = rememberCoroutineScope()

    val ctx = LocalContext.current
    val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token_lk", "")!!.trim('"')
    val paymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val token_yoo = Checkout.createTokenizationResult(result.data!!).paymentToken
            Log.d("Payment", "Payment successful, token: $token_yoo")
//            Toast.makeText(ctx, "Payment successful, token: $token_yoo", Toast.LENGTH_SHORT).show()
            token_yoo.let {
//                Toast.makeText(ctx, cartViewModel.selected_course.value.toString(), Toast.LENGTH_SHORT).show()
                sendTokenToServer(it, token, uri, cartViewModel.selected_course.value, cartViewModel)
            }
            // Обработайте успешное получение токена
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.d("Payment", "Payment canceled")
//            Toast.makeText(ctx, "Payment canceled", Toast.LENGTH_SHORT).show()
            // Обработайте отмену оплаты
        } else {
            Log.e("Payment", "Payment error:")
            Toast.makeText(ctx, "Payment error", Toast.LENGTH_SHORT).show()
            // Обработайте ошибку
        }
    }


    val scope = rememberCoroutineScope()

    val   cart = remember() {
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
                        SearchViewPreview(navHostController = navHostController);
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

                            }
                            if(cartViewModel.courses.isEmpty() || cartViewModel.auth_tested.value != AuthStatus.AUTH)
                            {

                                item {
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

                            } else if(cartViewModel.auth_tested.value == AuthStatus.AUTH)
                            {
                                itemsIndexed(cartViewModel.courses)
                                {
                                        _, value ->
                                    val course = value.course
                                    var price = value.price

                                    CartCourse(value, price = price, {
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
                                        navHostController.navigate("course/${value.course_id}")
                                    }, {
                                        val config = YookassaConfigs.getConfig(value.org_id)
                                        cartViewModel.selected_course.value = value.id
                                        cartViewModel.selected_course_org.value = value.org_id
//                                        Toast.makeText(ctx, "Test", Toast.LENGTH_SHORT).show()
                                        val paymentMethodTypes = setOf(
                                            PaymentMethodType.BANK_CARD,
//                                            PaymentMethodType.SBERBANK,
//                                            PaymentMethodType.YOO_MONEY,
//                                            PaymentMethodType.SBP,
                                        )
                                        val paymentParameters = PaymentParameters(
                                            amount = Amount(value.price.toBigDecimal(), Currency.getInstance("RUB")),
                                            title = course.name,
                                            shopId = config.shopId,
//                                            shopId = "957329",
                                            savePaymentMethod = SavePaymentMethod.OFF,
                                            customReturnUrl = "https://lk.mzpo-s.ru",
                                            paymentMethodTypes = paymentMethodTypes,
//                                            authCenterClientId =  "n2af44sofjfjsftsig09i5j5m32ui0pi6",
                                            clientApplicationKey =  config.clientApplicationKey,
//                                            clientApplicationKey = "test_OTU3MzI5XqsIBDAa7IHgGIlSoU8JqPgAJDLvSc8X-Bc",
                                            subtitle = ""
                                        )
//
                                        val intent = createTokenizeIntent(
                                            ctx,
                                            paymentParameters,
                                            TestParameters(showLogs = true)
                                        )
                                        paymentLauncher.launch(intent)
                                    })
                                }
                            }


                        }
                    }
                }
                if (cartViewModel.showLoader.value)
                {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black.copy(alpha = 0.3f)))
                    {
                        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            if (cartViewModel.payment_status.value == 0)
                            {
                                CircularProgressIndicator(Modifier.fillMaxWidth(0.5f), strokeCap = StrokeCap.Round, strokeWidth = 5.dp, trackColor = Color.White, color = Color.Transparent)
                            } else if (cartViewModel.payment_status.value == 1)
                            {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "", Modifier.fillMaxWidth(0.5f), tint = Primary_Green)
                            }
                        }
                    }
                }
            }
        }
    )

}




@Composable
fun CartCourse(item: CartItem, price: Int, onDelete: () -> Unit, onImageClick: () -> Unit = {}, onBuy: () -> Unit = {}, modifier: Modifier = Modifier)
{
    var text = "";
    if(item.type == "dist")
    {
        text = "Дистанционно"
    } else if (item.type== "sale15")
    {
        text = "Очно в группе"
    } else if (item.course.prices.ind == price)
    {
        text = "Индивидуально"
    } else if (item.course.prices.weekend == price)
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
                AsyncImage(model = item.course.image, contentDescription = item.course.id.toString(), modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable {
                        onImageClick.invoke()
                    }
                    .clip(RoundedCornerShape(15.dp)), contentScale = ContentScale.Crop)
                Column(modifier = Modifier
                    .weight(2f)
                    .padding(start = 10.dp)
                    .height(120.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Text(item.course.name, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 16.sp)
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

                    Column {
                        Row{
                            if (item.course.isDist())
                            {
                                Text(text = "Дата окончания: ", color = Color.Gray, fontSize = 12.sp)
                                Text(text = item.date!!, color = Aggressive_red, fontSize = 12.sp)
                            }else
                            {
                                if (item.group !== null)
                                {
                                    Text(text = "Дата начала: ", color = Color.Gray, fontSize = 12.sp)
                                    Text(text = item.group.start, color = Aggressive_red, fontSize = 12.sp)
                                }
                            }
                        }
                        Row{
                            Text(text = "Форма обучения: ", color = Color.Gray, fontSize = 12.sp)
                            Text(text = text, color = Aggressive_red, fontSize = 12.sp)
                        }
                    }

                }
            }

        }
        Column(modifier = Modifier
            .padding(7.dp)
            .fillMaxWidth()) {
            Divider(Modifier.padding(vertical = 5.dp))
            val agreement = remember {
                mutableStateOf(false)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.course.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                if (item.course.isDist())
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
            val ctx = LocalContext.current

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text(text = price.toString(), fontWeight = FontWeight.Bold, color = Aggressive_red, fontSize = 25.sp)
                    if (item.purchase_type == "course")
                    {
                        Text(text = price.times(1.15).toBigDecimal().setScale(-2, RoundingMode.UP).toInt().toString(), textDecoration = TextDecoration.LineThrough, modifier = Modifier.padding(start = 10.dp), fontSize = 18.sp)
                    } 
                }

                Button(onClick = {
                                 if (agreement.value)
                                 {
                                     onBuy.invoke()
                                 } else
                                 {
                                     Toast.makeText(ctx, "Вы не подтвердили согласие с договором оферты", Toast.LENGTH_SHORT).show()
                                 }
                }, modifier = Modifier, shape = RoundedCornerShape(10), colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red, contentColor = Color.White)) {
                    Text("Купить", color = Color.White)
                }
            }
            val uriHandler = LocalUriHandler.current
            if (item.purchase_type == "recovery")
            {
                Text(text = "Продление доступа за 50% стоимости")
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = agreement.value,
                    onCheckedChange = { agreement.value = it },
                    modifier = Modifier
                        .padding(start = 5.dp, end = 10.dp)
                        .size(12.dp),
                    colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = Primary_Green)
                    )
                Text(text = "Согласен с условиями ", fontSize = 12.sp)
                Text(
                    text = "договора оферты",
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://lk.mzpo-s.ru/build/documents/oferta_"+item.org_id+".pdf")
                    },
                    color = Color.Blue, fontSize = 12.sp)

            }
        }
    }

}
