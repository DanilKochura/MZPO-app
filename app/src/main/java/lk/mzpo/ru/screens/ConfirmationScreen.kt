package lk.mzpo.ru.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.network.retrofit.PaymentRequest
import lk.mzpo.ru.network.retrofit.PaymentResponse
import lk.mzpo.ru.network.retrofit.PurchaseService
import lk.mzpo.ru.network.retrofit.YookassaConfigs
import lk.mzpo.ru.services.CustomTabHelper
import lk.mzpo.ru.ui.components.PromoCodeInputField
import lk.mzpo.ru.ui.components.checkPaymentStatus
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.ConfirmationViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizeIntent
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters
import java.util.Currency


fun sendTokenToServer(
    token: String,
    app_token: String,
    uri: UriHandler,
    cartId: Int,
    cartViewModel: ConfirmationViewModel,
    ctx: Context,
    customTabHelper: CustomTabHelper,
    type: PaymentMethodType
) {
    val request = PaymentRequest(
        token = token,
        cartId = cartId,
        type = type
    )
    Log.d("MyLogTest", token)
    Log.d("MyLogTest", app_token)
    Log.d("MyLogTest", cartId.toString())

    PurchaseService.apiService.completePayment("Bearer $app_token", request)
        .enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(
                call: Call<PaymentResponse>,
                response: Response<PaymentResponse>
            ) {
                if (response.isSuccessful) {
                    val paymentResponse = response.body()
                    cartViewModel.payment_id.value = paymentResponse?.payment_id.toString()
                    Log.d("MyLogTEST", paymentResponse?.redirect_url.toString())
                    Log.d("Payment", "Server response: ${paymentResponse?.status}")
//                    uri.openUri(paymentResponse?.redirect_url.toString())
//                    openPaymentPage(context = ctx, paymentResponse?.redirect_url.toString())
                    customTabHelper.onClosed = {
                        cartViewModel.orderId.value = paymentResponse?.order_id
                        cartViewModel.showDialog.value = true
                    }
                    customTabHelper.openUrl(paymentResponse?.redirect_url.toString())
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ConfirmationScreen(
    navHostController: NavHostController,
    confirmationViewModel: ConfirmationViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0),
    customTabHelper: CustomTabHelper,
    fromDeep: Boolean,
    cartId: Int
) {
    val uri = LocalUriHandler.current
    val uriHandler = LocalUriHandler.current

    val bottomSheetStatePayment =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )

    if (bottomSheetStatePayment.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                navHostController.navigate("study ")
            }
        }
    }
    LaunchedEffect(key1 = "") {
        if (fromDeep == true) {
            bottomSheetStatePayment.show()

        }
    }


    val bottomSheetStateDates =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)


    LaunchedEffect(confirmationViewModel.payment_status.value) {
        if (confirmationViewModel.payment_status.value == 2) {
            delay(2000)
            confirmationViewModel.showLoader.value = false
        }
    }



    val scope_for_payments = rememberCoroutineScope()

    val ctx = LocalContext.current
    val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token_lk", "")!!.trim('"')
    val agreement = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(bottomSheetStateDates.isVisible) {
        if (!bottomSheetStateDates.isVisible) {
            // Действие при закрытии
            confirmationViewModel.getCartItem(cartId, token = token)

        }
    }
    val paymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val token_yoo = Checkout.createTokenizationResult(result.data!!).paymentToken
            val data = Checkout.createTokenizationResult(result.data!!).paymentMethodType
            Log.d("Payment", "Payment successful, token: $token_yoo")
//            Toast.makeText(ctx, "Payment successful, token: $token_yoo", Toast.LENGTH_SHORT).show()
            token_yoo.let {
//                Toast.makeText(ctx, confirmationViewModel.selected_course.value.toString(), Toast.LENGTH_SHORT).show()
                sendTokenToServer(
                    it,
                    token,
                    uri,
                    cartId,
                    confirmationViewModel,
                    ctx,
                    customTabHelper,
                    data
                )
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

    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val scope = rememberCoroutineScope()
    val coroutineScopeDates = rememberCoroutineScope()


    val cartSum = remember {
        cart_sum
    }
    AuthService.testAuth(ctx, navHostController, confirmationViewModel.auth_tested, false);
    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                navController = navHostController,
                cartSum,
                confirmationViewModel.auth_tested
            )
        },
        content = { padding ->


            LaunchedEffect(key1 = confirmationViewModel.auth_tested.value, block = {
                if (confirmationViewModel.cartItem.value == null) {
                    if (confirmationViewModel.auth_tested.value == AuthStatus.AUTH) {
                        Log.d("CartLog", "LK")
                        confirmationViewModel.getCartItem(cartId, token = token)
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
                        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp)
                    ) {
                        IconButton(
                            onClick = { navHostController.navigateUp() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                        Row(
                            modifier = Modifier.weight(7f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Оформление заказа", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = {
                            navHostController.navigate("notifications")
                        }, modifier = Modifier.weight(1f)) {
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

                        if (confirmationViewModel.cartItem.value !== null) {

                            val item = confirmationViewModel.cartItem.value!!
                            val promoCode = remember {
                                mutableStateOf("")
                            }
                            val isSuccess = remember {
                                mutableStateOf(false)
                            }
                            val promo_text = remember {
                                mutableStateOf<String?>(null)
                            }
                            val isError = remember {
                                mutableStateOf(false)
                            }
                            if (item.sale != 0 && item.promocode.isNotEmpty()) {
                                isSuccess.value = true
                                promo_text.value = "Промокод применен"
                                promoCode.value = item.promocode
                            }
                            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                Box {

                                    Row(
                                        horizontalArrangement = Arrangement.End, modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(7.dp)
                                    ) {
                                        AsyncImage(
                                            model = item.course.image,
                                            contentDescription = item.course.id.toString(),
                                            modifier = Modifier
                                                .height(135.dp)
                                                .weight(1f)
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(15.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(
                                            modifier = Modifier
                                                .weight(2f)
                                                .padding(start = 10.dp)
                                                .height(135.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text(
                                                    item.course.name,
                                                    modifier = Modifier.weight(1f),
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Column {
                                                Row {
                                                    if (item.course.isDist()) {
                                                        Text(
                                                            text = "Дата окончания: ",
                                                            color = Color.Gray,
                                                            fontSize = 12.sp
                                                        )
                                                        Text(
                                                            text = item.date!!,
                                                            color = Aggressive_red,
                                                            fontSize = 12.sp,
                                                            modifier = Modifier
                                                                .border(
                                                                    1.dp,
                                                                    Aggressive_red,
                                                                    RoundedCornerShape(5.dp)
                                                                )
                                                                .padding(
                                                                    horizontal = 10.dp,
                                                                    vertical = 2.dp
                                                                )
                                                                .clickable {

                                                                    confirmationViewModel.selected_course.value =
                                                                        cartId
                                                                    confirmationViewModel.selectedPrice.value =
                                                                        confirmationViewModel.cartItem.value!!.price
                                                                    coroutineScopeDates.launch {
                                                                        bottomSheetStateDates.show()
                                                                    }
                                                                }

                                                        )
                                                    } else {
                                                        if (item.group !== null) {
                                                            Text(
                                                                text = "Дата начала: ",
                                                                color = Color.Gray,
                                                                fontSize = 12.sp
                                                            )
                                                            Text(
                                                                text = item.group.start,
                                                                color = Aggressive_red,
                                                                fontSize = 12.sp,
                                                                modifier = Modifier
                                                                    .border(
                                                                        1.dp,
                                                                        Aggressive_red,
                                                                        RoundedCornerShape(5.dp)
                                                                    )
                                                                    .padding(
                                                                        horizontal = 10.dp,
                                                                        vertical = 2.dp
                                                                    )
                                                                    .clickable {

                                                                        confirmationViewModel.selected_course.value =
                                                                            cartId
                                                                        confirmationViewModel.selectedPrice.value =
                                                                            confirmationViewModel.cartItem.value!!.price
                                                                        coroutineScopeDates.launch {
                                                                            bottomSheetStateDates.show()
                                                                        }
                                                                    }
                                                            )
                                                        }
                                                    }
                                                }
                                                Row(modifier = Modifier.padding(vertical = 5.dp)) {
                                                    Text(
                                                        text = "Форма обучения: ",
                                                        color = Color.Gray,
                                                        fontSize = 12.sp
                                                    )
                                                    Text(
                                                        text = item.getPriceName(),
                                                        color = Aggressive_red,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier
                                                            .border(
                                                                1.dp,
                                                                Aggressive_red,
                                                                RoundedCornerShape(5.dp)
                                                            )
                                                            .padding(
                                                                horizontal = 10.dp,
                                                                vertical = 2.dp
                                                            )
                                                            .clickable {

                                                                confirmationViewModel.selected_course.value =
                                                                    cartId
                                                                confirmationViewModel.selectedPrice.value =
                                                                    confirmationViewModel.cartItem.value!!.price
                                                                coroutineScopeDates.launch {
                                                                    bottomSheetStateDates.show()
                                                                }
                                                            }
                                                    )
                                                }
                                            }

                                        }
                                    }


                                }
//                                HorizontalDivider()
                                PromoCodeInputField(
                                    value = promoCode,
                                    isError = isError,
                                    isSuccess = isSuccess,
                                    promo_text
                                ) {
                                    if (promoCode.value.length > 0)
                                    {
                                        confirmationViewModel.checkPromocode(token, promoCode.value, promo_text, isError)
                                    }
                                }

                                Column(
                                    modifier = Modifier.padding(
                                        vertical = 15.dp,
                                        horizontal = 5.dp
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Стоимость курса:",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = item.priceCart.full_price.toString() + " ₽",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Скидка по акции:",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "-" + item.priceCart.sale.toString() + " ₽",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (item.sale != 0) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Скидка по промокоду:",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "-" + item.sale.toString() + " ₽",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Итого:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 25.sp
                                        )
                                        Text(
                                            text = item.priceCart.price.toString() + " ₽",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 25.sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    Checkbox(
                                        checked = agreement.value,
                                        onCheckedChange = { agreement.value = it },
                                        modifier = Modifier
                                            .padding(start = 5.dp, end = 10.dp)
                                            .size(20.dp),
                                        colors = CheckboxDefaults.colors(checkedColor = Primary_Green)
                                    )
                                    Text(text = "Согласен с условиями ", fontSize = 15.sp)
                                    Text(
                                        text = "договора оферты",
                                        modifier = Modifier.clickable {
                                            uriHandler.openUri("https://trayektoriya.ru/build/documents/oferta_" + item.org_id + ".pdf")
                                        },
                                        color = Color.Blue, fontSize = 15.sp
                                    )

                                }
                            }
                        }
                    }
                }
                ModalBottomSheetLayout(
                    sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    sheetContent = {
                        val success = remember {
                            mutableStateOf<Boolean?>(null)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(10.dp)
                        ) {

                            if (success.value == null) {
                                Text(
                                    text = "Подождите, платеж обрабатывается банком",
                                    fontSize = 25.sp,
                                    textAlign = TextAlign.Center,
                                    color = Primary_Green,
                                    modifier = Modifier.padding(10.dp)
                                )
                                CircularProgressIndicator(
                                    modifier = Modifier.size(100.dp),
                                    color = Primary_Green,
                                    strokeWidth = 10.dp,
                                )
                            } else if (success.value == true) {
                                Text(
                                    text = "Платеж успешно проведен!",
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Primary_Green,
                                    modifier = Modifier.padding(10.dp)
                                )
                                Text(
                                    text = "Курс скоро появится в разделе \"Обучение\"",
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(10.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "",
                                    tint = Primary_Green,
                                    modifier = Modifier.size(100.dp)
                                )
                            } else {
                                Text(
                                    text = "Произошла ошибка",
                                    fontSize = 25.sp,
                                    textAlign = TextAlign.Center,
                                    color = Primary_Green,
                                    modifier = Modifier.padding(10.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "",
                                    tint = Aggressive_red,
                                    modifier = Modifier.size(100.dp)
                                )

                            }
                        }

                        if (fromDeep == true) {
                            LaunchedEffect(Unit) {
                                checkPaymentStatus(1, token, onPaymentSuccess = {
                                    success.value = true
                                    return@checkPaymentStatus

                                }, onPaymentFailed = {
                                    success.value = false
                                })
                            }
                        }
                    },
                    sheetState = bottomSheetStatePayment,
                    modifier = Modifier.padding(padding)
                ) {

                }

                ModalBottomSheetLayout(
                    sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    sheetContent = {
                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                            if (confirmationViewModel.cartItem.value !== null) {
                                val course =
                                    confirmationViewModel.cartItem.value!!
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.calendar_1__traced_),
                                        contentDescription = "",
                                        Modifier
                                            .size(30.dp)
                                            .padding(end = 7.dp)
                                    )
                                    Text(text = "Выбрать даты обучения", fontSize = 20.sp)
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(10.dp),
                                    thickness = 2.dp
                                )
                                Text(text = "Форма обучения")
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 15.dp)
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    if (course.course.prices.dist !== null) {
                                        if (course.course.prices.dist != 0) {
                                            Column(
                                                modifier = Modifier
                                                    .height(45.dp)
                                                    .padding(end = 7.dp)
                                                    .clip(
                                                        RoundedCornerShape(20)
                                                    )
                                                    .background(if (confirmationViewModel.selectedPrice.value == course.course.prices.dist) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (confirmationViewModel.selectedPrice.value == course.course.prices.dist) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        confirmationViewModel.selectedPrice.value =
                                                            course.course.prices.dist
                                                        confirmationViewModel.selectedType.value =
                                                            "dist"
                                                        confirmationViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            confirmationViewModel.selectedType.value
                                                        )
                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Дистанционно",
                                                    Modifier.padding(10.dp),
                                                    color = if (confirmationViewModel.selectedPrice.value == course.course.prices.dist) Color.White else Color.Black
                                                )
                                            }
                                        }
                                    } else {
                                        if (course.course.prices.sale15 != null) {
                                            Column(
                                                modifier = Modifier
                                                    .height(45.dp)
                                                    .padding(end = 7.dp)
                                                    .clip(
                                                        RoundedCornerShape(20)
                                                    )
                                                    .background(if (confirmationViewModel.selectedPrice.value == course.course.prices.sale15) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (confirmationViewModel.selectedPrice.value == course.course.prices.sale15) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        confirmationViewModel.selectedPrice.value =
                                                            course.course.prices.sale15
                                                        confirmationViewModel.selectedType.value =
                                                            "sale15"
                                                        confirmationViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            confirmationViewModel.selectedType.value
                                                        )

                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Очно в группе",
                                                    Modifier.padding(10.dp),
                                                    color = if (confirmationViewModel.selectedPrice.value == course.course.prices.sale15) Color.White else Color.Black
                                                )
                                            }
                                        }
                                        if (course.course.prices.ind != null) {
                                            Column(
                                                modifier = Modifier
                                                    .height(45.dp)
                                                    .padding(end = 7.dp)
                                                    .clip(
                                                        RoundedCornerShape(20)
                                                    )
                                                    .background(if (confirmationViewModel.selectedPrice.value == course.course.prices.ind) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (confirmationViewModel.selectedPrice.value == course.course.prices.ind) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        confirmationViewModel.selectedPrice.value =
                                                            course.course.prices.ind!!
                                                        confirmationViewModel.selectedType.value =
                                                            "ind"

                                                        confirmationViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            confirmationViewModel.selectedType.value
                                                        )

                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Индивидуально",
                                                    Modifier.padding(10.dp),
                                                    color = if (confirmationViewModel.selectedPrice.value == course.course.prices.ind) Color.White else Color.Black
                                                )
                                            }
                                        }
                                        if (course.course.prices.weekend != null) {
                                            Column(
                                                modifier = Modifier
                                                    .height(45.dp)
                                                    .padding(end = 7.dp)
                                                    .clip(
                                                        RoundedCornerShape(20)
                                                    )
                                                    .background(if (confirmationViewModel.selectedPrice.value == course.course.prices.weekend) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (confirmationViewModel.selectedPrice.value == course.course.prices.weekend) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        confirmationViewModel.selectedPrice.value =
                                                            course.course.prices.weekend!!
                                                        confirmationViewModel.selectedType.value =
                                                            "weekend"



                                                        confirmationViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            confirmationViewModel.selectedType.value
                                                        )

                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Учись в выходной",
                                                    Modifier.padding(10.dp),
                                                    color = if (confirmationViewModel.selectedPrice.value == course.course.prices.weekend) Color.White else Color.Black
                                                )
                                            }
                                        }
                                        if (course.course.prices.intensive != null) {
                                            Column(
                                                modifier = Modifier
                                                    .height(45.dp)
                                                    .padding(end = 7.dp)
                                                    .clip(
                                                        RoundedCornerShape(20)
                                                    )
                                                    .background(if (confirmationViewModel.selectedPrice.value == course.course.prices.intensive) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (confirmationViewModel.selectedPrice.value == course.course.prices.intensive) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        confirmationViewModel.selectedPrice.value =
                                                            course.course.prices.intensive!!
                                                        confirmationViewModel.selectedType.value =
                                                            "intensive"

                                                        val cartItem =
                                                            confirmationViewModel.cartItem.value!!
                                                        cartItem.price =
                                                            course.course.prices.intensive
                                                        confirmationViewModel.courses[confirmationViewModel.selectedCourseIndex.value] =
                                                            cartItem

                                                        confirmationViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            confirmationViewModel.selectedType.value
                                                        )

                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Интенсив",
                                                    Modifier.padding(10.dp),
                                                    color = if (confirmationViewModel.selectedPrice.value == course.course.prices.intensive) Color.White else Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(10.dp),
                                    thickness = 2.dp
                                )
                                Text(text = "Доступные даты")
                                if (course.course.isDist()) {
                                    if (!course.dist_dates.isNullOrEmpty())
                                    {
                                        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                                            itemsIndexed(course.dist_dates) { _, date ->
                                                Column(
                                                    modifier = Modifier
                                                        .padding(5.dp)
                                                        .background(if (confirmationViewModel.selectedDate.value == date) Primary_Green else Color.Transparent)

                                                        .clip(
                                                            RoundedCornerShape(20)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (confirmationViewModel.selectedDate.value == date) Primary_Green else Color.LightGray,
                                                            RoundedCornerShape(20)
                                                        )
                                                        .clickable {
                                                            confirmationViewModel.updateDate(
                                                                ctx,
                                                                token,
                                                                course.course_id,
                                                                date
                                                            )

                                                        }, verticalArrangement = Arrangement.Center
                                                )
                                                {
                                                    Text(
                                                        text = date,
                                                        modifier = Modifier.padding(10.dp),
                                                        textAlign = TextAlign.Center,
                                                        color = if (confirmationViewModel.selectedDate.value == date) Color.White else Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    } else
                                    {
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)


                                                .clip(
                                                    RoundedCornerShape(20)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.LightGray,
                                                    RoundedCornerShape(20)
                                                ), verticalArrangement = Arrangement.Center
                                        )
                                        {

                                            Text(
                                                text = "Для выбранного типа нет групп. Уточняйте даты у Вашего менеджера",
                                                Modifier.padding(10.dp),
                                                color = Color.Black
                                            )
                                        }
                                    }
                                } else {
                                    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                                        itemsIndexed(confirmationViewModel.groups) { _, group ->
                                            Column(
                                                modifier = Modifier
                                                    .padding(5.dp)
                                                    .background(if (confirmationViewModel.selectedGroup.value == group.id) Primary_Green else Color.Transparent)

                                                    .clip(
                                                        RoundedCornerShape(20)
                                                    )
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (confirmationViewModel.selectedGroup.value == group.id) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        confirmationViewModel.updateGroup(
                                                            ctx,
                                                            token,
                                                            course.course_id,
                                                            group.id!!
                                                        )

                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                group.startDate?.let {
                                                    Text(
                                                        text = it,
                                                        Modifier.padding(10.dp),
                                                        color = if (confirmationViewModel.selectedGroup.value == group.id) Color.White else Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (confirmationViewModel.groups.size == 0) {
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)


                                                .clip(
                                                    RoundedCornerShape(20)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.LightGray,
                                                    RoundedCornerShape(20)
                                                ), verticalArrangement = Arrangement.Center
                                        )
                                        {

                                            Text(
                                                text = "Для выбранного типа нет групп. Уточняйте даты у Вашего менеджера",
                                                Modifier.padding(10.dp),
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    },
                    sheetState = bottomSheetStateDates,
                    modifier = Modifier.padding(padding)
                ) {

                }


            }
        },
        floatingActionButton = {
            if (confirmationViewModel.cartItem.value != null) {
                val value = confirmationViewModel.cartItem.value!!
                val config = YookassaConfigs.getConfig(value.org_id)

                val paymentMethodTypes = setOf(
                    PaymentMethodType.BANK_CARD,
//                                            PaymentMethodType.SBERBANK,
//                                            PaymentMethodType.YOO_MONEY,
                    PaymentMethodType.SBP,
                )
                val paymentParameters = PaymentParameters(
                    amount = Amount(
                        value.price.toBigDecimal(),
                        Currency.getInstance("RUB")
                    ),
                    title = value.course.name,
                    shopId = config.shopId,
//                                            shopId = "957329",
                    savePaymentMethod = SavePaymentMethod.OFF,
                    customReturnUrl = "https://trayektoriya.ru/mobile/cart",
                    paymentMethodTypes = paymentMethodTypes,
//                                            authCenterClientId =  "n2af44sofjfjsftsig09i5j5m32ui0pi6",
                    clientApplicationKey = config.clientApplicationKey,
//                                            clientApplicationKey = "test_OTU3MzI5XqsIBDAa7IHgGIlSoU8JqPgAJDLvSc8X-Bc",
                    subtitle = ""
                )
//
                val intent = createTokenizeIntent(
                    ctx,
                    paymentParameters,
                    TestParameters(showLogs = true)
                )
                FloatingActionButton(
                    onClick = {

                        if (agreement.value) {

                            paymentLauncher.launch(intent)

                        } else {
                            Toast.makeText(
                                ctx,
                                "Вы не подтвердили согласие с договором оферты",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    containerColor = Aggressive_red,
                    contentColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(45.dp)
                        .padding(start = 30.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "",
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(text = "Купить", color = Color.White)
                    }
                }
            }
        },
    )

}


