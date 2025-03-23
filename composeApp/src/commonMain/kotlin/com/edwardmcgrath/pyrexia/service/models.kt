package com.edwardmcgrath.pyrexia.service

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
class LoginResponse(
    val email: String,
    val token: String
)

@Serializable
data class GetStatList(
    val message: String,
    val data: List<Stat>,
    val current_time: Long?
)

@Serializable
data class Stat(
    val program_id: Int,
    val program_name: String,
    val sensor_id: Int,
    val sensor_name: String,
    val sensor_value: Float,
    //val sensor_update_time: Long? = null,
    val control_id: Int,
    val control_name: String,
    val mode: String,
    val enabled: Int,
    val set_point: Float,
    val last_on_time: Long,
    val last_off_time: Long,
    val min_run: Int,
    val min_rest: Int,
    val gpio: Int,
    //val gpio_on_high: Int?,
    val control_on: Int,
    val total_run: Int,
    val run_capacity: Int,
    //val current_time: Long?
)

@Serializable
data class GetStat(
    val message: String,
    val data: Stat
)


/*
internal interface PyrexiaApi {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @GET("/stat/list")
    fun getStatList(): Single<GetStatListDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/programs")
    fun addStat(@Body program: AddStatDto): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @PATCH("/programs/{id}")
    fun updateStat(@Path("id") id: Int,
                   @Body program: AddStatDto): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/stat/{id}/increase")
    fun statIncrease(@Path("id") id: Int): Single<GetStatDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/stat/{id}/decrease")
    fun statDecrease(@Path("id") id: Int): Single<GetStatDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/stat/{id}/enable")
    fun statEnable(@Path("id") id: Int): Single<GetStatDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/stat/{id}/disable")
    fun statDisable(@Path("id") id: Int): Single<GetStatDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @GET("/history")
    fun getHistory(@Query("offset") offset: Int,
                   @Query("limit") limit: Int,
                   @Query("program_id") program_id: Int?,
                   @Query("start_ts") startTs: Int?,
                   @Query("end_ts") endTs: Int?): Single<GetHistoryDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @GET("/sensors")
    fun getSensors(): Single<GetSensorsDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @GET("/controls")
    fun getControls(): Single<GetControlsDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @PATCH("/sensors/{id}")
    fun updateSensor(@Path("id") id: Int,
                     @Body sensor: SensorUpdateDto): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/sensors")
    fun addSensor(@Body sensor: SensorUpdateDto): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @DELETE("/sensors/{id}")
    fun deleteSensor(@Path("id") id: Int): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @PATCH("/controls/{id}")
    fun updateControl(@Path("id") id: Int,
                      @Body control: ControlUpdateDto): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/controls")
    fun addControl(@Body control: ControlUpdateDto): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @DELETE("/controls/{id}")
    fun deleteControl(@Path("id") id: Int): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/controls/{id}/refill")
    fun refill(@Path("id") id: Int): Completable

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/users/login")
    fun login(@Body loginRequestDto: LoginRequestDto): Single<LoginResponseDto>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @POST("/setup/shutdown")
    fun shutdown(): Completable
}
 */