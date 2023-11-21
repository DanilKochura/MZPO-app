package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import lk.mzpo.ru.models.Cart
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.Document
import lk.mzpo.ru.models.Group
import lk.mzpo.ru.models.Module
import lk.mzpo.ru.models.Prices
import lk.mzpo.ru.network.retrofit.AuthStatus
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CourseViewModel   (
): ViewModel(){

    val auth_tested = mutableStateOf(AuthStatus.TEST) // флаг аунтификации
    val courses = mutableStateOf(listOf<Course>()) // курс
    val modalForm = mutableStateOf(false) // флаг для диалогового окна с формой
    val tabIndex = mutableStateOf("Инфо") // активная вкладка меню

    val monthes = listOf("Декабрь", "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    var isDist = mutableStateOf(false)
    val availible_months = mutableStateOf(listOf<Int>()) // список доступных месяцев для расписания
    val admissions = mutableStateOf(listOf<String>())
    val selectedPrice = mutableStateOf(0) //выбранная цена для главной
    val selectedType = mutableStateOf("sale15") //выбранный тип цены для главной
    val selectedMonth = mutableStateOf(0) // выбранный месяц для расписания
    val db = Firebase.firestore


    /**
     * Парсинг курса после получения
     * @param response - JSON-ответ сервера
     * @see Course
     */
    fun getCourse(response: String): List<Course> {


        if (response.isEmpty()) return listOf()
        Log.d("MyLog", response)
        val list = ArrayList<Course>()
        try {
            val mainObject = JSONObject(response)
            val pattern = DateTimeFormatter.ofPattern("yyyy-MM-DD HH:mm")
                //mainObject.length()
                val item = mainObject as JSONObject
                val prices = item.getJSONObject("prices")
                var groups = JSONArray()
                var docs = JSONArray()
                var adms = JSONArray()
            var month = JSONArray();
            try {
                groups = item.getJSONArray("groups")
            } catch (_: Exception)
            {
            }
            try {
                adms = item.getJSONArray("docs_admissions")
            } catch (_: Exception)
            {
            }
                val grAr = arrayListOf<Group>()
            try {
                docs = item.getJSONArray("documents")
            } catch (_: Exception)
            {
            }

                val docAr = arrayListOf<Document>()
                val admsArr = arrayListOf<String>()
                var mods = JSONArray()
            try {
                mods = item.getJSONArray("modules")
            } catch (_: Exception)
            {

            }

                if(docs.length() > 0)
                {
                    try {
                        for (j in 0 until docs.length())
                        {
                            val gr = docs[j] as JSONObject
                            docAr.add(
                                Document(
                                    gr.getString("doc_name"),
                                    gr.getString("image")
                                )
                            )
                        }
                    } catch (e: Exception)
                    {
                        Log.e("MyLogE", e.toString())
                    }
                }
            if(adms.length() > 0)
            {
                Log.d("MyLog", adms.toString())
                try {
                    for (j in 0 until adms.length())
                    {

                        val gr = adms[j] as String
                        admsArr.add(gr)
                    }
                } catch (e: Exception)
                {
                    Log.e("MyLogE", e.toString())
                }
            }
                if(groups.length() > 0)
                {
                    try {
                        for (j in 0 until groups.length())
                        {
                            val gr = groups[j] as JSONObject
                            grAr.add(
                                Group(
                                    gr.getInt("id"),
                                    gr.getInt("course_id"),
                                    gr.getString("title"),
                                    LocalDate.parse(gr.getString("start_date")),
                                    LocalDate.parse(gr.getString("end_date")),
                                    LocalTime.parse(gr.getString("time")),
                                    gr.getString("uid"),
                                    gr.getString("teacher"),
                                    if(gr.isNull("teacher_name"))
                                    {
                                      "Не указан"
                                    } else
                                    {
                                        gr.getString("teacher_name")
                                    },
                                    gr.getInt("month"),

                                    )
                            )
                        }
                    } catch (e: Exception)
                    {
                        Log.d("MyLog", "Groups:  "+e.toString())
                    }
                }
                var modulesAr = listOf<Module>()
                if(mods.length() == 0)
                {
                    modulesAr = listOf()
                } else
                {
                    modulesAr = getModules(mods)
                }
                val images = arrayListOf<String>()
                val saray = item.getJSONArray("images")
                for (j in 0 until saray.length())
                {
                    images.add(saray[j] as String)
                }
                list.add(
                    Course(
                        item.getInt("id"),
                        item.getInt("category_id"),
                        item.getString("site_name"),
                        item.getString("prefix"),
                        item.getInt("hours"),
                        getPrice(prices),
                        item.getString("description"),
                        item.getString("uid_1c"),
                        images,
                        grAr,
                        docAr,
                        admsArr,
                        modulesAr,
                        item.getString("doctype")

                    )
                )

            try {
                month = item.getJSONArray("sched_month")
                Log.d("MyLog", "Mo: "+month.toString())
                val mlist = arrayListOf<Int>()
                for (j in 0 until month.length())
                {
                    Log.d("MyLog", month[j].toString())
                    val m = month[j].toString().toInt()
                    mlist.add(m)
                }
                availible_months.value = mlist
                selectedMonth.value = mlist[0]

            } catch (e: Exception)
            {
                Log.d("MyLog", e.toString())
            }

        } catch (e: Exception)
        {
            Log.d("MyLog", e.toString())
        }
        return list

    }


    /**
     * Получение списка модулей для курса
     * @see Module
     */
    fun getModules(obj: JSONArray): ArrayList<Module> {
        var list = arrayListOf<Module>()
        for (i in 0 until obj.length())
        {
            val item = obj[i] as JSONObject
            list.add(
                Module(
                    item.getString("site_name"),
                    item.getInt("hours"),
                )
            )
        }
        return list
    }

    fun getPrice(obj: JSONObject): Prices
    {
        var sale15 = 0
        var ind = 0
        var dist = 0
        var weekend = 0;
        try {
            sale15 = obj.getInt("sale15")
        } catch (e: Exception)
        {

        }
        try {
            ind = obj.getInt("ind")
        } catch (e: Exception)
        {

        }
        try {
            dist = obj.getInt("dist")
        } catch (e: Exception)
        {

        }
        try {
            weekend = obj.getInt("weekend")
        } catch (e: Exception)
        {

        }

        if(this.selectedPrice.value == 0)
        {
            if( dist != 0)
            {
                isDist.value = true
                this.selectedPrice.value = dist
            } else
            {
                this.selectedPrice.value = if (sale15 != 0) sale15 else ind;
            }
        }
        return Prices(sale15, dist, ind, weekend)
    }

    /**
     * Получение курса из ЛК по ID
     * @param id - ЛК ID
     * @param context - Контекст для очереди
     */
    fun getData(id: Int, context: Context,)
    {
        Log.d("MyLog1", id.toString())

        val url = "https://lk.mzpo-s.ru/mobile/course/$id"
        val queue = Volley.newRequestQueue(context)
        val sRequest = StringRequest(
            Request.Method.GET,
            url,
            {
                    response ->

                val co = getCourse(response)
                courses.value = co


            },
            {
                Log.d("MyLog", "VolleyError: $it")
            }
        )
        queue.add(sRequest)
    }

}