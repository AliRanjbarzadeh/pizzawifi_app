package ir.atriatech.pizzawifi.common.network

import io.reactivex.Single
import ir.atriatech.core.entities.Msg
import ir.atriatech.pizzawifi.entities.*
import ir.atriatech.pizzawifi.entities.customerMenu.Comment
import ir.atriatech.pizzawifi.entities.home.Blog
import ir.atriatech.pizzawifi.entities.home.HomeBase
import ir.atriatech.pizzawifi.entities.home.maker.Maker
import ir.atriatech.pizzawifi.entities.home.tournament.Tournament
import ir.atriatech.pizzawifi.entities.more.area.AreaItem
import ir.atriatech.pizzawifi.entities.more.contactus.ContactUs
import ir.atriatech.pizzawifi.entities.more.faq.Faq
import ir.atriatech.pizzawifi.entities.orders.Order
import ir.atriatech.pizzawifi.entities.orders.survey.Question
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.entities.profile.address.Address
import ir.atriatech.pizzawifi.entities.profile.address.AddressSearch
import ir.atriatech.pizzawifi.entities.profile.campaign.Campaign
import ir.atriatech.pizzawifi.entities.profile.campaign.CampaignDemo
import ir.atriatech.pizzawifi.entities.profile.club.ClubItemModel
import ir.atriatech.pizzawifi.entities.profile.club.ClubItemProfileModel
import ir.atriatech.pizzawifi.entities.profile.competition.Competition
import ir.atriatech.pizzawifi.entities.profile.lottery.LotteryModel
import ir.atriatech.pizzawifi.entities.profile.messages.Message
import ir.atriatech.pizzawifi.entities.profile.support.Support
import ir.atriatech.pizzawifi.entities.profile.wallet.Wallet
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartDecide
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartTools
import ir.atriatech.pizzawifi.ui.main.shopcart.offer.ShopCartOfferModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface RequestService {

	//region Login
	@FormUrlEncoded
	@POST("login")
	fun login(@Field("mobile") mobile: String): Single<Msg>

	@FormUrlEncoded
	@POST("login/sms")
	fun sendCode(@Field("mobile") mobile: String): Single<Msg>

	@FormUrlEncoded
	@POST("login/resendCode")
	fun resendCode(@Field("mobile") mobile: String): Single<Msg>

	@FormUrlEncoded
	@POST("login/verify")
	fun verifyCode(@Field("mobile") mobile: String, @Field("code") code: String): Single<Msg>

	@FormUrlEncoded
	@POST("register")
	fun register(@Field("name") name: String, @Field("introducer") introducer: String): Single<Msg>
	//endregion

	/**
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Home>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	@FormUrlEncoded
	@POST("home")
	fun home(@Field("branchId") branchId: Int): Single<HomeBase>

	@FormUrlEncoded
	@POST("blog/detail")
	fun blogDetail(@Field("id") id: Int): Single<Blog>

	@FormUrlEncoded
	@POST("profile/push_token")
	fun savePushToken(@Field("pushToken") pushToken: String): Single<Msg>

	@FormUrlEncoded
	@POST("logError")
	fun saveLogError(@Field("logError") logError: String): Single<Msg>

	/**
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Profile>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	//region Upload image
	@Multipart
	@POST("profile/upload")
	fun uploadImage(@Part image: MultipartBody.Part): Single<Msg>
	//endregion

	//region Logout
	@FormUrlEncoded
	@POST("profile/logout")
	fun logout(@Field("pushToken") pushToken: String): Single<Msg>
	//endregion

	//region Change Number
	@FormUrlEncoded
	@POST("changeNumber")
	fun changeNumber(@Field("mobile") mobile: String): Single<Msg>

	@FormUrlEncoded
	@POST("changeNumber/sms")
	fun changeNumberSendCode(@Field("mobile") mobile: String): Single<Msg>

	@FormUrlEncoded
	@POST("changeNumber/resendCode")
	fun changeNumberResendCode(@Field("mobile") mobile: String): Single<Msg>

	@FormUrlEncoded
	@POST("changeNumber/verify")
	fun changeNumberVerifyCode(
		@Field("mobile") mobile: String,
		@Field("code") code: String
	): Single<Msg>
	//endregion

	//region Support
	@FormUrlEncoded
	@POST("support")
	fun supportArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<Support>>

	@FormUrlEncoded
	@POST("support/add")
	fun supportAdd(
		@Field("title") title: String,
		@Field("content") message: String
	): Single<Support>

	@FormUrlEncoded
	@POST("support/detail")
	fun supportDetail(@Field("supportId") id: Int): Single<MutableList<Support>>

	@FormUrlEncoded
	@POST("support/reply")
	fun supportReply(@Field("replyId") id: Int, @Field("content") message: String): Single<Support>

	@FormUrlEncoded
	@POST("support/notice")
	fun supportNotice(@Field("supportId") id: Int): Single<Msg>
	//endregion

	//region Wallet
	@FormUrlEncoded
	@POST("wallet")
	fun walletArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<Wallet>>

	@FormUrlEncoded
	@POST("wallet/increase")
	fun walletIncrease(@Field("amount") amount: Int): Single<Msg>

	@FormUrlEncoded
	@POST("wallet/checkout")
	fun walletCheckout(@Field("id") id: Int): Single<Wallet>
	//endregion

	//region EditInfo
	@FormUrlEncoded
	@POST("profile/info")
	fun editInfo(
		@Field("name") name: String,
		@Field("birthDate") birthDate: String,
		@Field("weddingDate") weddingDate: String
	): Single<Msg>
	//endregion

	//region Competition
	@FormUrlEncoded
	@POST("profile/competitions")
	fun competitionArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<Competition>>

	@FormUrlEncoded
	@POST("profile/competition_detail")
	fun competitionDetail(@Field("id") id: Int): Single<Competition>

	@GET("competition")
	fun competitionGet(): Single<Tournament>


	@FormUrlEncoded
	@POST("competition/save")
	fun competitionSave(
		@Field("competitionId") competitionId: Int,
		@Field("questions") questions: String
	): Single<Msg>
	//endregion

	//region Address
	@FormUrlEncoded
	@POST("address")
	fun addressArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int,
		@Field("branchId") branchId: Int?
	): Single<MutableList<Address>>

	@FormUrlEncoded
	@POST("address/search")
	fun addressSearch(@Field("place") place: String): Single<MutableList<AddressSearch>>

	@FormUrlEncoded
	@POST("address/add")
	fun addressAdd(
		@Field("name") name: String,
		@Field("mobile") mobile: String,
		@Field("address") address: String,
		@Field("lat") lat: Double,
		@Field("lng") lng: Double,
		@Field("forceAdd") forceAdd: Int,
		@Field("branchId") branchId: Int? = null
	): Single<Msg>

	@FormUrlEncoded
	@POST("address/edit")
	fun addressEdit(
		@Field("id") id: Int,
		@Field("name") name: String,
		@Field("mobile") mobile: String,
		@Field("address") address: String,
		@Field("lat") lat: Double,
		@Field("lng") lng: Double,
		@Field("forceAdd") forceAdd: Int
	): Single<Msg>

	@FormUrlEncoded
	@POST("address/delete")
	fun addressDelete(@Field("id") id: Int): Single<Msg>
	//endregion

	//region Messages
	@FormUrlEncoded
	@POST("profile/messages")
	fun messageArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<Message>>

	//endregion


	//region Messages
	@FormUrlEncoded
	@POST("profile/lotteries")
	fun lotteryArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<LotteryModel>>
	//endregion

	//region Pizzas
	@FormUrlEncoded
	@POST("profile/pizza")
	fun pizzaArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int,
		@Field("name") name: String
	): Single<MutableList<Pizza>>

	@FormUrlEncoded
	@POST("customer")
	fun customerArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<Pizza>>

	@FormUrlEncoded
	@POST("customer/comments")
	fun commentArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<Comment>>

	@Multipart
	@POST("customer/add_comment")
	fun commentAdd(
		@Part("id") id: RequestBody,
		@Part("rate") rate: RequestBody,
		@Part("comment") comment: RequestBody,
		@Part image: MultipartBody.Part?
	): Single<Msg>

	@Multipart
	@POST("profile/send_pizza")
	fun pizzaSendToMenu(
		@Part("id") id: RequestBody,
		@Part image: MultipartBody.Part? = null
	): Single<Msg>

	@FormUrlEncoded
	@POST("profile/delete_pizza")
	fun pizzaDelete(@Field("id") id: Int): Single<Msg>
	//endregion


	//region Campaigns
	@FormUrlEncoded
	@POST("profile/campaigns")
	fun campaignArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<Campaign>>


	@FormUrlEncoded
	@POST("campaign/detail")
	fun campaignDetail(
		@Field("id") limit: Int,
	): Single<CampaignDemo>


	@GET("campaign/demo")
	fun campaignDemo(): Single<CampaignDemo>

	@Multipart
	@POST("campaign/upload")
	fun campaignSave(
		@Part("id") id: RequestBody,
		@Part("instagram") instagram: RequestBody,
		@Part image: MultipartBody.Part?
	): Single<Msg>

	//endregion

	/**
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<More>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	//region More
	@GET("more/about")
	fun about(): Single<GeneralModel>

	@GET("more/law")
	fun law(): Single<GeneralModel>

	@GET("more/faq")
	fun faq(): Single<MutableList<Faq>>

	@GET("branch")
	fun moreBranches(): Single<MutableList<CityModel>>

	@GET("more/contact")
	fun contact(): Single<ContactUs>

	@GET("more/area")
	fun area(): Single<MutableList<AreaItem>>

	@FormUrlEncoded
	@POST("gift/app/crm")
	fun clubArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<ClubItemModel>>

	@FormUrlEncoded
	@POST("gift/use/crm")
	fun clubUseGift(
		@Field("id") id: Int
	): Single<Msg>

	@FormUrlEncoded
	@POST("gift/used_app/crm")
	fun clubUsedArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int
	): Single<MutableList<ClubItemProfileModel>>
	//endregion

	/**
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Orders>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	//region Order Archive
	@FormUrlEncoded
	@POST("orders")
	fun orderArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int,
		@Field("orderId") orderId: Int
	): Single<MutableList<Order>>

	@FormUrlEncoded
	@POST("orders/detail")
	fun orderDetail(@Field("id") orderId: Int): Single<Order>

	@GET("survey")
	fun survey(): Single<MutableList<Question>>

	@FormUrlEncoded
	@POST("survey/add")
	fun surveyAdd(
		@Field("orderId") orderId: Int,
		@Field("answers") answers: String,
		@Field("description") description: String
	): Single<Msg>
	//endregion

	/**
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Product>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	//region Product Archive
	@FormUrlEncoded
	@POST("product")
	fun productArchive(
		@Field("limit") limit: Int,
		@Field("offset") offset: Int,
		@Field("categoryId") categoryId: Int,
		@Field("branchId") branchId: Int
	): Single<MutableList<Product>>
	//endregion

	@FormUrlEncoded
	@POST("product/detail")
	fun productDetail(@Field("id") id: Int, @Field("branchId") branchId: Int): Single<Product>
	//endregion

	@FormUrlEncoded
	@POST("product/offers")
	fun productOfferArchive(@Field("branchId") branchId: Int): Single<ShopCartOfferModel>

	/**
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Maker>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	//region Maker
	@FormUrlEncoded
	@POST("maker")
	fun maker(@Field("branchId") branchId: Int): Single<Maker>
	//endregion

	/**
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<ShopCart>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	//region ShopCart until Checkout
	@FormUrlEncoded
	@POST("shopcart/tools")
	fun shopCartTools(
		@Field("addressId") addressId: Int?,
		@Field("branchId") branchId: Int
	): Single<ShopCartTools>

	@FormUrlEncoded
	@POST("shopcart/decide")
	fun shopCartDecide(@Field("branchId") branchId: Int): Single<ShopCartDecide>

	@FormUrlEncoded
	@POST("shopcart/discount")
	fun checkDiscount(@Field("code") code: String, @Field("branchId") branchId: Int): Single<Msg>

	@FormUrlEncoded
	@POST("shopcart/checkout")
	fun checkOrderPayment(@Field("id") id: Int): Single<Msg>

	@FormUrlEncoded
	@POST("shopcart/add")
	fun addOrder(
		@Field("discountCode") discountCode: String,
		@Field("isUseWallet") isUseWallet: Int,
		@Field("orderJson") orderJson: String,
		@Field("addressId") addressId: Int,
		@Field("branchId") branchId: Int,
		@Field("forceAddArea") forceAddAddress: Int,
		@Field("forceAddTime") forceAddTime: Int,
		@Field("deliverType") deliverType: Int,
		@Field("reserveTime") reserveTime: String,
		@Field("paymentType") paymentType: Int,
		@Field("description") description: String
	): Single<Msg>

	@FormUrlEncoded
	@POST("shopcart/notice")
	fun orderNotice(@Field("id") orderId: Int): Single<Msg>
	//endregion

//    //region Branch Archive
//    @GET("branch")  //--> use if there is not nearest branch button in the design
//    fun branchArchive(): Single<MutableList<Branch>>

	@FormUrlEncoded
	@POST("branch")
	fun branchArchive(
		@Field("cityId") cityId: Int,
		@Field("lat") lat: Double? = null,
		@Field("lng") lng: Double? = null
	): Single<MutableList<Branch>>


	@FormUrlEncoded
	@POST("city")
	fun cityArchive(
		@Field("lat") lat: Double? = null,
		@Field("lng") lng: Double? = null
	): Single<MutableList<CityModel>>

	@GET("site/branch")
	fun provinceArchive(): Single<MutableList<ProvinceModel>>


	// request form for branch
	@Multipart
	@POST("site/branch")
	fun addBranchRequest(
		@Part("firstName") firstName: RequestBody,
		@Part("lastName") lastName: RequestBody,
		@Part("mobile") mobile: RequestBody,
		@Part("phone") phone: RequestBody,
		@Part("email") email: RequestBody,                     /*Optional*/
		@Part("age") age: RequestBody, /*int*/
		@Part("province") province: RequestBody,
		@Part("city") city: RequestBody,
		@Part("storeStatus") storeStatus: RequestBody, /*@Int (1: Malek - 2: Rahn - 3: Ejareh)	*/
		@Part("address") address: RequestBody? = null,
		@Part("area") area: RequestBody? = null, /*int*/
		@Part("currentSkill") currentSkill: RequestBody,
		@Part("investment") investment: RequestBody, /*int*/
		@Part("restaurantSkill") restaurantSkill: RequestBody,
		@Part("learning") learning: RequestBody,               /*Optional*/
		@Part("turnover") turnover: RequestBody, /*int*/       /*Optional*/
		@Part("description") description: RequestBody,         /*Optional*/
		@Part resume: MutableList<MultipartBody.Part>,
		@Part documents: MutableList<MultipartBody.Part>
	): Single<Msg>

	//endregion
}