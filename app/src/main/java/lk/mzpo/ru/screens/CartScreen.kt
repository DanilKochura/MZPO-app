package lk.mzpo.ru.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.CartItem
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.network.retrofit.DemoRegisterRequest
import lk.mzpo.ru.network.retrofit.PaymentRequest
import lk.mzpo.ru.network.retrofit.PaymentResponse
import lk.mzpo.ru.network.retrofit.PurchaseService
import lk.mzpo.ru.network.retrofit.YookassaConfigs
import lk.mzpo.ru.services.CustomTabHelper
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.NameTextField
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.components.PhoneTextField
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.components.checkPaymentStatus
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.CartViewModel
import okhttp3.ResponseBody
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
import java.math.RoundingMode
import java.util.Currency


fun sendTokenToServer(
    token: String,
    app_token: String,
    uri: UriHandler,
    cartId: Int,
    cartViewModel: CartViewModel,
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
fun CartScreen(
    navHostController: NavHostController, cartViewModel: CartViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0),
    customTabHelper: CustomTabHelper,
    fromDeep: Boolean
) {
    val uri = LocalUriHandler.current

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
        if (fromDeep == true)
        {
            bottomSheetStatePayment.show()

        }
    }


    val bottomSheetStateDates =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    LaunchedEffect(cartViewModel.payment_status.value) {
        if (cartViewModel.payment_status.value == 2) {
            delay(2000)
            cartViewModel.showLoader.value = false
        }
    }

    if (cartViewModel.confirmationURL.value != null) {
        PaymentWebView(
            url = cartViewModel.confirmationURL.value!!,
            navController = navHostController,
            onClose = { cartViewModel.confirmationURL.value = null }
        )
    }

    val scope_for_payments = rememberCoroutineScope()

    val ctx = LocalContext.current
    val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token_lk", "")!!.trim('"')
    val token_fb = test.getString("token", "")

    val paymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val token_yoo = Checkout.createTokenizationResult(result.data!!).paymentToken
            val data = Checkout.createTokenizationResult(result.data!!).paymentMethodType
            Log.d("Payment", "Payment successful, token: $token_yoo")
//            Toast.makeText(ctx, "Payment successful, token: $token_yoo", Toast.LENGTH_SHORT).show()
            token_yoo.let {
//                Toast.makeText(ctx, cartViewModel.selected_course.value.toString(), Toast.LENGTH_SHORT).show()
                sendTokenToServer(
                    it,
                    token,
                    uri,
                    cartViewModel.selected_course.value,
                    cartViewModel,
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

    val cart = remember() {
        mutableStateOf(listOf(hashMapOf("" to "")))
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
        bottomBar = {
            BottomNavigationMenu(
                navController = navHostController,
                cartSum,
                cartViewModel.auth_tested
            )
        },
        content = { padding ->


            LaunchedEffect(key1 = cartViewModel.auth_tested.value, block = {
                if (cartViewModel.courses.isEmpty()) {
                    if (cartViewModel.auth_tested.value == AuthStatus.AUTH) {
                        Log.d("CartLog", "LK")
                        cartViewModel.getLkCart(ctx)
                    } else if (cartViewModel.auth_tested.value == AuthStatus.GUEST) {
                        if (!token_fb.isNullOrEmpty()) {
                            FirebaseHelpers.getCartSum(token_fb, cartSum)
                        }
                        Log.d("CartLog", "Firebase")

                        FirebaseHelpers.getCart(token_fb, cart)

                    }
                }
            })
            LaunchedEffect(key1 = cart.value, block = {
                if (cartViewModel.auth_tested.value == AuthStatus.GUEST) {
                    cartViewModel.getCourses(ctx, cart)
                }
            })
            LaunchedEffect(bottomSheetStateDates.isVisible) {
                if (!bottomSheetStateDates.isVisible) {
                    // Действие при закрытии
                    cartViewModel.courses.clear()
                    if (cartViewModel.auth_tested.value == AuthStatus.AUTH) {
                        cartViewModel.getLkCart(ctx)
                    }
                }
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
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp)
                    ) {
                        SearchViewPreview(navHostController = navHostController);
                        IconButton(onClick = {
                            if (cartViewModel.auth_tested.value == AuthStatus.AUTH) {
                                navHostController.navigate("notifications")
                            }
                        }) {
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
                                Text(
                                    text = "Список покупок", fontSize = 24.sp, modifier = Modifier
                                        .padding(top = 15.dp)
                                        .fillMaxWidth(), textAlign = TextAlign.Center
                                )

                            }
                            if (cartViewModel.courses.isEmpty()) {

                                item {
                                    Column(
                                        Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceEvenly,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(top = 50.dp, start = 50.dp, end = 50.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_shopping_cart_24),
                                                contentDescription = "",
                                                modifier = Modifier.size(80.dp),
                                                tint = Primary_Green
                                            )
                                            Text(
                                                text = "В корзине пока ничего нет",
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(top = 10.dp)
                                            )
                                            Text(
                                                text = "Здесь будут храниться курсы, которые вам понравятся",
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 5.dp)
                                            )
                                            OutlinedButton(
                                                onClick = { navHostController.navigate("categories") },
                                                border = BorderStroke(2.dp, Primary_Green),
                                                modifier = Modifier.padding(vertical = 10.dp)
                                            ) {
                                                Text(
                                                    text = "Перейти в каталог",
                                                    color = Primary_Green,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                            } else {
                                itemsIndexed(cartViewModel.courses)
                                { index, value ->
                                    val course = value.course
                                    var price = value.price

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
                                        title = course.name,
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
                                    CartCourse(
                                        value, price = price,
                                        onDelete = {
                                            if (value.id == 0) {
                                                cartViewModel.deleteCartItem(ctx, course.id)
                                            } else {
                                                cartViewModel.deleteCartItem(ctx, value.id)
                                            }
                                            cartViewModel.courses.remove(value)
                                            cartSum.value--
                                        },
                                        onImageClick = {
                                            navHostController.navigate("course/${value.course_id}")
                                        },
                                        onBuy = {
                                            if (value.id == 0) {
                                                cartViewModel.selected_course.value =
                                                    value.course_id
                                            } else {
                                                cartViewModel.selected_course.value = value.id
                                            }
                                            cartViewModel.selected_course_org.value = value.org_id
//                                        paymentLauncher.launch(intent)
                                        },
                                        authStatus = cartViewModel.auth_tested.value,
                                        bottomSheet = bottomSheetState,
                                        paymentIntent = intent,
                                        paymentLaucher = paymentLauncher,
                                        onDatesClick = {

                                            if (cartViewModel.selectedCourseIndex.value != index) cartViewModel.groups.clear()
                                            if (value.id == 0) {
                                                cartViewModel.selected_course.value =
                                                    value.course_id
                                            } else {
                                                cartViewModel.selected_course.value = value.id
                                            }
                                            cartViewModel.selectedCourseIndex.value = index
                                            cartViewModel.selectedPrice.value = value.price

                                        },
                                        datesState = bottomSheetStateDates,
                                    )

                                }
                            }


                        }
                    }
                }

                ModalBottomSheetLayout(
                    sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    sheetContent = {
                        val device = test.getString("token", "")
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
                            if (!passStage.value) {
                                Text(
                                    text = "Для получения доступа к учебным материалам и покупкам, заполните контактные данные",
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
                                            if (phone.value.length != 10) {
                                                phoneError.value = true
                                                return@Button
                                            } else {
                                                phoneError.value = false

                                            }
                                            if (login.value.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(
                                                    login.value.text
                                                ).matches()
                                            ) {
                                                emailError.value = false
                                            } else {
                                                emailError.value = true
                                                return@Button
                                            }
                                            if (name.value.text.isEmpty()) {
                                                nameError.value = true
                                                return@Button
                                            } else {
                                                nameError.value = false
                                            }
                                            Log.d(
                                                "MyLog",
                                                cartViewModel.selected_course.value.toString()
                                            )
                                            val dpost = DemoRegisterRequest.PostBody(
                                                name = name.value.text,
                                                email = login.value.text,
                                                phone = "+7" + phone.value,
                                                demo_key = "AnWqKt8xSkQlTPI",
                                                course_id = cartViewModel.selected_course.value
                                            );

                                            val pref = context.getSharedPreferences(
                                                "session",
                                                Context.MODE_PRIVATE
                                            )
                                            val token_ = pref.getString("token_lk", "")
                                            DemoRegisterRequest().send(
                                                "Bearer " + token_?.trim('"'),
                                                dpost

                                            ).enqueue(object : retrofit2.Callback<ResponseBody> {
                                                override fun onFailure(
                                                    call: Call<ResponseBody>,
                                                    t: Throwable
                                                ) {
                                                    Log.e(
                                                        "API Request",
                                                        "I got an error and i don't know why :("
                                                    )
                                                    Log.e("API Request", t.message.toString())
                                                    Toast.makeText(
                                                        context,
                                                        "Произошла ошибка. Попробуйте позже!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                override fun onResponse(
                                                    call: Call<ResponseBody>,
                                                    response: Response<ResponseBody>
                                                ) {
                                                    response.body()?.let { responseBody ->
                                                        val responseBodyString =
                                                            responseBody.string()
                                                        Log.d("API Response", responseBodyString)
                                                        if (response.isSuccessful) {
                                                            if (responseBodyString.trim('"') == "already exists") {
                                                                Toast.makeText(
                                                                    context,
                                                                    "У вас уже есть аккаунт!",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                                passStage.value = true
                                                            } else if (responseBodyString.trim('"').contains("key:")) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Успех!",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                                val pass = responseBodyString.trim('"').replace("key:", "")
                                                                val data = AuthData(
                                                                    login.value.text,
                                                                    pass,
                                                                    token_fb.toString()
                                                                )
                                                                AuthService.login(data,
                                                                    context,
                                                                    navHostController,
                                                                    false,
                                                                    customRedirect = {
                                                                        navHostController.navigate("cart")
                                                                    })

                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Произошла ошибка. Попробуйте позже!",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
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
//            Row(
//                modifier = Modifier.padding(all = 8.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text(
//                    text = "Уже зарегистрированы?",
//                    color = Color.Blue,
//                    textDecoration = TextDecoration.Underline,
//                    modifier = Modifier.clickable {
//                        navHostController.navigate("login")
//                    })
//            }
                            } else {
                                Text(
                                    text = "На вашу почту " + login.value.text + " выслан пароль. Введите его в поле ниже для входа в созданный аккаунт",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp)
                                )
                                Text(
                                    text = "Не забудьте подтвердить вашу почту по ссылке в письме",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp),
                                    fontSize = 10.sp
                                )
                                PasswordTextField(password = password, isError = passError)
                                Row(
                                    modifier = Modifier.padding(all = 8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
//                modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            if (password.value.text.isEmpty()) {
                                                phoneError.value = true
                                                return@Button
                                            } else {
                                                phoneError.value = false
                                            }

                                            val data = AuthData(
                                                login.value.text,
                                                password.value.text,
                                                token_fb.toString()
                                            )
                                            val test = context.getSharedPreferences(
                                                "session",
                                                Context.MODE_PRIVATE
                                            )
                                            val gson = Gson()
                                            test.edit().putString("auth_data", gson.toJson(data))
                                                .apply()
                                            AuthService.login(data,
                                                context,
                                                navHostController,
                                                false,
                                                customRedirect = {
                                                    navHostController.navigate("cart")
                                                })
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
                        Spacer(modifier = Modifier.height(250.dp))
                    },
                    sheetState = bottomSheetState,
                    modifier = Modifier.padding(padding)
                ) {

                }

                ModalBottomSheetLayout(
                    sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    sheetContent = {
                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                            if (cartViewModel.courses.size > 0) {
                                val course =
                                    cartViewModel.courses.get(cartViewModel.selectedCourseIndex.value)
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
                                                    .background(if (cartViewModel.selectedPrice.value == course.course.prices.dist) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (cartViewModel.selectedPrice.value == course.course.prices.dist) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        cartViewModel.selectedPrice.value =
                                                            course.course.prices.dist
                                                        cartViewModel.selectedType.value = "dist"
                                                        cartViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            cartViewModel.selectedType.value
                                                        )
                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Дистанционно",
                                                    Modifier.padding(10.dp),
                                                    color = if (cartViewModel.selectedPrice.value == course.course.prices.dist) Color.White else Color.Black
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
                                                    .background(if (cartViewModel.selectedPrice.value == course.course.prices.sale15) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (cartViewModel.selectedPrice.value == course.course.prices.sale15) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        cartViewModel.selectedPrice.value =
                                                            course.course.prices.sale15
                                                        cartViewModel.selectedType.value = "sale15"
                                                        cartViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            cartViewModel.selectedType.value
                                                        )

                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Очно в группе",
                                                    Modifier.padding(10.dp),
                                                    color = if (cartViewModel.selectedPrice.value == course.course.prices.sale15) Color.White else Color.Black
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
                                                    .background(if (cartViewModel.selectedPrice.value == course.course.prices.ind) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (cartViewModel.selectedPrice.value == course.course.prices.ind) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        cartViewModel.selectedPrice.value =
                                                            course.course.prices.ind!!
                                                        cartViewModel.selectedType.value = "ind"
                                                        cartViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            cartViewModel.selectedType.value
                                                        )

                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Индивидуально",
                                                    Modifier.padding(10.dp),
                                                    color = if (cartViewModel.selectedPrice.value == course.course.prices.ind) Color.White else Color.Black
                                                )
                                            }
                                        }
                                        if (course.course.prices.weekend != 0) {
                                            Column(
                                                modifier = Modifier
                                                    .height(45.dp)
                                                    .padding(end = 7.dp)
                                                    .clip(
                                                        RoundedCornerShape(20)
                                                    )
                                                    .background(if (cartViewModel.selectedPrice.value == course.course.prices.weekend) Primary_Green else Color.Transparent)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (cartViewModel.selectedPrice.value == course.course.prices.weekend) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        cartViewModel.selectedPrice.value =
                                                            course.course.prices.weekend!!
                                                        cartViewModel.selectedType.value = "weekend"

                                                        val cartItem =
                                                            cartViewModel.courses.get(cartViewModel.selectedCourseIndex.value)
                                                        cartItem.price =
                                                            course.course.prices.weekend
                                                        cartViewModel.courses[cartViewModel.selectedCourseIndex.value] =
                                                            cartItem

                                                        cartViewModel.updateType(
                                                            context = ctx,
                                                            token,
                                                            course.id,
                                                            cartViewModel.selectedType.value
                                                        )

                                                    }, verticalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    text = "Учись в выходной",
                                                    Modifier.padding(10.dp),
                                                    color = if (cartViewModel.selectedPrice.value == course.course.prices.weekend) Color.White else Color.Black
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
                                                        .background(if (cartViewModel.selectedDate.value == date) Primary_Green else Color.Transparent)

                                                        .clip(
                                                            RoundedCornerShape(20)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (cartViewModel.selectedDate.value == date) Primary_Green else Color.LightGray,
                                                            RoundedCornerShape(20)
                                                        )
                                                        .clickable {
                                                            cartViewModel.updateDate(
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
                                                        color = if (cartViewModel.selectedDate.value == date) Color.White else Color.Black
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
                                        itemsIndexed(cartViewModel.groups) { _, group ->
                                            Column(
                                                modifier = Modifier
                                                    .padding(5.dp)
                                                    .background(if (cartViewModel.selectedGroup.value == group.id) Primary_Green else Color.Transparent)

                                                    .clip(
                                                        RoundedCornerShape(20)
                                                    )
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (cartViewModel.selectedGroup.value == group.id) Primary_Green else Color.LightGray,
                                                        RoundedCornerShape(20)
                                                    )
                                                    .clickable {
                                                        cartViewModel.updateGroup(
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
                                                        color = if (cartViewModel.selectedGroup.value == group.id) Color.White else Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (cartViewModel.groups.size == 0) {
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
                        }
                    },
                    sheetState = bottomSheetStateDates,
                    modifier = Modifier.padding(padding)
                ) {

                }
                ModalBottomSheetLayout(
                    sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    sheetContent = {
                        val success = remember {
                            mutableStateOf<Boolean?>(null)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {

                            if (success.value == null)
                            {
                                Text(text = "Подождите, платеж обрабатывается банком", fontSize = 25.sp, textAlign = TextAlign.Center, color = Primary_Green, modifier = Modifier.padding(10.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(100.dp),
                                    color = Primary_Green,
                                    strokeWidth = 10.dp,
                                )
                            } else if (success.value == true)
                            {
                                Text(text = "Платеж успешно проведен!", fontSize = 25.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Primary_Green, modifier = Modifier.padding(10.dp))
                                Text(text = "Курс скоро появится в разделе \"Обучение\"", fontSize = 14.sp, textAlign = TextAlign.Center, color = Color.Gray, modifier = Modifier.padding(10.dp))
                                Icon(imageVector = Icons.Default.Check, contentDescription = "", tint = Primary_Green, modifier = Modifier.size(100.dp))
                            } else {
                                Text(text = "Произошла ошибка", fontSize = 25.sp, textAlign = TextAlign.Center, color = Primary_Green, modifier = Modifier.padding(10.dp))
                                Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Aggressive_red, modifier = Modifier.size(100.dp))

                            }
                        }

                            LaunchedEffect(Unit) {
                                checkPaymentStatus(1, token, onPaymentSuccess = {
                                    success.value = true

                                }, onPaymentFailed = {
                                    success.value = false
                                })
                            }
                    },
                    sheetState = bottomSheetStatePayment,
                    modifier = Modifier.padding(padding)
                ) {

                }
            }
        }
    )

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CartCourse(
    item: CartItem,
    price: Int,
    onDelete: () -> Unit,
    onImageClick: () -> Unit = {},
    onBuy: () -> Unit = {},
    paymentIntent: Intent,
    paymentLaucher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    authStatus: AuthStatus = AuthStatus.GUEST,
    bottomSheet: ModalBottomSheetState,
    datesState: ModalBottomSheetState,
    onDatesClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var text = "";
    if (item.type == "dist") {
        text = "Дистанционно"
    } else if (item.type == "sale15") {
        text = "Очно в группе"
    } else if (item.course.prices.ind == price) {
        text = "Индивидуально"
    } else if (item.course.prices.weekend == price) {
        text = "Учись в выходной"
    }
    val coroutineScopeDates = rememberCoroutineScope()
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 20.dp)
            .shadow(2.dp, RoundedCornerShape(10.dp)), colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
//        Image(painter = painterResource(id = R.drawable.masage), contentDescription = "", modifier = Modifier
//            .height(150.dp)
//            .fillMaxWidth(), contentScale = ContentScale.Crop)
        Box {

            Row(
                horizontalArrangement = Arrangement.End, modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)
            ) {
                AsyncImage(model = item.course.image,
                    contentDescription = item.course.id.toString(),
                    modifier = Modifier
                        .height(120.dp)
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable {
                            onImageClick.invoke()
                        }
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop)
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 10.dp)
                        .height(120.dp), verticalArrangement = Arrangement.SpaceBetween
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
                            fontSize = 16.sp
                        )
                        IconButton(modifier = Modifier.width(25.dp), onClick = {
                            onDelete.invoke()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete",
                                tint = Color.LightGray,
                                modifier = Modifier
                                    .size(25.dp)
//                        .background(
//                            Color.Black.copy(0.5f), RoundedCornerShape(50)
//                        )
                            )
                        }
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
                                        .border(1.dp, Aggressive_red, RoundedCornerShape(5.dp))
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                        .clickable {
                                            onDatesClick.invoke()
                                            if (authStatus == AuthStatus.AUTH) {
                                                coroutineScopeDates.launch {
                                                    datesState.show()
                                                }
                                            } else {
                                                coroutineScope.launch {
                                                    bottomSheet.show()
                                                }
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
                                            .border(1.dp, Aggressive_red, RoundedCornerShape(5.dp))
                                            .padding(horizontal = 10.dp, vertical = 2.dp)
                                            .clickable {
                                                onDatesClick.invoke()
                                                if (authStatus == AuthStatus.AUTH) {
                                                    coroutineScopeDates.launch {
                                                        datesState.show()
                                                    }
                                                } else {
                                                    coroutineScope.launch {
                                                        bottomSheet.show()
                                                    }
                                                }
                                            }
                                    )
                                }
                            }
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp)) {
                            Text(text = "Форма обучения: ", color = Color.Gray, fontSize = 12.sp)
                            Text(text = text, color = Aggressive_red, fontSize = 12.sp,
                                modifier = Modifier
                                    .border(1.dp, Aggressive_red, RoundedCornerShape(5.dp))
                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                                    .clickable {
                                        onDatesClick.invoke()
                                        if (authStatus == AuthStatus.AUTH) {
                                            coroutineScopeDates.launch {
                                                datesState.show()
                                            }
                                        } else {
                                            coroutineScope.launch {
                                                bottomSheet.show()
                                            }
                                        }
                                    }
                            )
                        }
                    }

                }
            }

        }
        Column(
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth()
        ) {
            Divider(Modifier.padding(vertical = 5.dp))
            val agreement = remember {
                mutableStateOf(false)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.course.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                if (item.course.isDist()) {
                    Row {
                        Text(text = "Дистанционно: ", fontWeight = FontWeight.Bold)
                        Text(text = "9000 руб.", color = Aggressive_red)
                    }
                } else {
                    Row {
                        Text(text = "Очно: ", fontWeight = FontWeight.Bold)
                    }
                }
            }
            val ctx = LocalContext.current
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = price.toString(),
                        fontWeight = FontWeight.Bold,
                        color = Aggressive_red,
                        fontSize = 25.sp
                    )
                    if (item.purchase_type == "course") {
                        Text(
                            text = price.times(1.15).toBigDecimal().setScale(-2, RoundingMode.UP)
                                .toInt().toString(),
                            textDecoration = TextDecoration.LineThrough,
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = 18.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        if (agreement.value) {
                            onBuy.invoke()
                            if (authStatus == AuthStatus.AUTH) {
                                paymentLaucher.launch(paymentIntent)
                            } else {
                                coroutineScope.launch {

                                    bottomSheet.show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                ctx,
                                "Вы не подтвердили согласие с договором оферты",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier,
                    shape = RoundedCornerShape(10),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Aggressive_red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Купить", color = Color.White)
                }
            }
            val uriHandler = LocalUriHandler.current
            if (item.purchase_type == "recovery") {
                Text(text = "Продление доступа за 50% стоимости")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        uriHandler.openUri("https://trayektoriya.ru/build/documents/oferta_" + item.org_id + ".pdf")
                    },
                    color = Color.Blue, fontSize = 12.sp
                )

            }
        }
    }
    


}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebView(url: String, navController: NavHostController, onClose: () -> Unit) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val newUrl = request?.url.toString()
                        if (newUrl.contains("/mobile/cart")) {
                            (context as? Activity)?.finish() // Закрываем WebView
                            navController.navigate("cart")  // Переход в корзину
                            onClose()
                            return true
                        }
                        return false
                    }
                }
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}


@Preview
@Composable
fun test() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {
        Text(text = "Подождите, платеж обрабатывается банком", fontSize = 25.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Primary_Green,
            strokeWidth = 10.dp,
        )
        Icon(imageVector = Icons.Default.Check, contentDescription = "", tint = Primary_Green, modifier = Modifier.size(100.dp))
    }
}