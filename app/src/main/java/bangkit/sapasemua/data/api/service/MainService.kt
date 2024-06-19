package amirlabs.sapasemua.data.api.service

import amirlabs.sapasemua.data.model.BaseResponse
import amirlabs.sapasemua.data.model.Module
import amirlabs.sapasemua.data.model.Quiz
import amirlabs.sapasemua.data.model.QuizResult
import amirlabs.sapasemua.data.model.SubModule
import amirlabs.sapasemua.data.model.User
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@JvmSuppressWildcards
interface MainService {
    @POST("auth/login")
    fun login(@Body body: Map<String, Any>): Single<BaseResponse<User>>

    @POST("auth/register")
    fun register(@Body body: Map<String, Any>): Single<BaseResponse<User>>

    @GET("auth/{id}")
    fun getProfile(@Path("id") userId: String): Single<BaseResponse<User>>

    @POST("auth/update")
    fun updateProfile(@Body body:MultipartBody): Single<BaseResponse<User>>

    @GET("module/get-all")
    fun getAllModule(): Single<BaseResponse<List<Module>>>

    @GET("module/{id}")
    fun getOneModule(@Path("id") moduleId:String): Single<BaseResponse<Module>>

    @GET("module/{id}/submodule")
    fun getLessons(@Path("id") moduleId: String): Single<BaseResponse<List<SubModule>>>

    @GET("module/lesson/{id}")
    fun getOneSubModule(@Path("id") lessonId:String): Single<BaseResponse<SubModule>>

    @POST("module/submodule/create")
    fun createSubmodule(@Body body: MultipartBody): Single<BaseResponse<SubModule>>

    @DELETE("module/{id}")
    fun deleteModule(@Path("id") moduleId:String): Single<BaseResponse<Module>>

    @POST("module/create")
    fun createModule(@Body body: MultipartBody): Single<BaseResponse<Module>>

    @POST("module/{module_id}/edit")
    fun editModule(@Path("module_id") moduleId:String, @Body body: MultipartBody): Single<BaseResponse<Module>>

    @POST("module/submodule/{submodule_id}/edit")
    fun editSubmodule(@Path("submodule_id") submoduleId:String, @Body body: MultipartBody): Single<BaseResponse<SubModule>>

    @DELETE("module/submodule/{id}")
    fun deleteSubmodule(@Path("id") submoduleId:String): Single<BaseResponse<SubModule>>

    @POST("module/quiz/create")
    fun createQuiz(@Body body: MultipartBody): Single<BaseResponse<Quiz>>

    @GET("module/quiz/{id}")
    fun getOneQuiz(@Path("id") quizId:String): Single<BaseResponse<Quiz>>

    @POST("module/quiz/{id}/edit")
    fun editQuiz(@Path("id") quizId:String, @Body body: MultipartBody): Single<BaseResponse<Quiz>>

    @DELETE("module/quiz/{id}")
    fun deleteQuiz(@Path("id") quizId:String): Single<BaseResponse<Quiz>>

    @GET("module/{id}/quiz")
    fun getQuizByModule(@Path("id") moduleId:String): Single<BaseResponse<List<Quiz>>>

    @GET("module/{id}/quiz/list")
    fun getQuizQuestion(@Path("id") moduleId: String): Single<BaseResponse<List<Quiz>>>

    @POST("module/quiz/submit")
    fun submitQuiz(@Body body: Map<String, Any>): Single<BaseResponse<QuizResult>>

    @GET("module/quiz/result")
    fun getQuizResultByUser(@Body body: Map<String, Any>): Single<BaseResponse<List<QuizResult>>>

    @GET("module/quiz/result/{id}")
    fun getQuizResult(@Path("id") resultId: String): Single<BaseResponse<QuizResult>>

    @GET("module/quiz/{id}/result")
    fun getAllQuizResult(@Path("id") userId: String): Single<BaseResponse<List<QuizResult>>>

    @POST("forum/create")
    fun createForum(@Body body: Map<String, Any>): Single<BaseResponse<Forum>>

    @GET("forum/get-all")
    fun getForum(@Query("page") page:Int, @Query("pageSize") pageSize:Int): Single<BaseResponse<List<Forum>>>

    @GET("forum/{id}")
    fun getForumDetail(@Path("id") forumId:String): Single<BaseResponse<Forum>>

    @POST("forum/{forum_id}/comment")
    fun addComment(@Path("forum_id") forumId:String, @Body body: Map<String, Any>): Single<BaseResponse<Forum>>
}