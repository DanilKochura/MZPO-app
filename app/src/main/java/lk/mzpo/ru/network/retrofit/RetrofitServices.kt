package lk.mzpo.ru.network.retrofit

import CoursePreview
import android.util.Log
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.Prices
import retrofit2.Call
import lk.mzpo.ru.ui.components.stories.Story
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import kotlin.math.log

interface CourseApi
{
    @GET("course/{id}")
    suspend  fun getCourseById(@Path("id") id: Int ): CoursePreview



    @GET("all-courses")
    fun getAllCourses(): List<CoursePreview>
}


interface DataToAmoApi
{
    @POST("siteform/retail")
    fun send(@Body requestBody: RequestBody): Response<ResponseBody>
}