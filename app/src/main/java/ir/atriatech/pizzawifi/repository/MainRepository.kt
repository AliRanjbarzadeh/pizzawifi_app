package ir.atriatech.pizzawifi.repository

import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.base.outCome
import ir.atriatech.pizzawifi.base.BaseRepository
import ir.atriatech.pizzawifi.common.network.RequestService
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
import javax.inject.Inject

class MainRepository @Inject constructor(
	private val requestService: RequestService,
	context: Context
) : BaseRepository(context) {
	var msgOutcome = outCome<Msg>()

	//region Logout
	var logoutOutcome = outCome<Msg>()

	fun logout(pushToken: String, bag: CompositeDisposable) =
		requestToNetwork(logoutOutcome, requestService.logout(pushToken), bag, false)
	//endregion

	//region Home
	var homeOutcome = outCome<HomeBase>()
	var blogOutcome = outCome<Blog>()
	var pushTokenOutcome = outCome<Msg>()
	var logErrorOutcome = outCome<Msg>()

	fun home(branchId: Int, bag: CompositeDisposable) = requestToNetwork(homeOutcome, requestService.home(branchId), bag)
	fun blogDetail(id: Int, bag: CompositeDisposable) =
		requestToNetwork(blogOutcome, requestService.blogDetail(id), bag)

	fun savePushToken(pushToken: String, bag: CompositeDisposable) =
		requestToNetwork(pushTokenOutcome, requestService.savePushToken(pushToken), bag)

	fun saveLogError(logError: String, bag: CompositeDisposable) =
		requestToNetwork(logErrorOutcome, requestService.saveLogError(logError), bag)
	//endregion

	//region Upload image
	var uploadImageOutcome = outCome<Msg>()

	fun uploadImage(image: MultipartBody.Part, bag: CompositeDisposable) =
		requestToNetwork(uploadImageOutcome, requestService.uploadImage(image), bag)
	//endregion

	//region Support
	var supportArchiveOutcome = outCome<MutableList<Support>>()
	var supportDetailOutcome = outCome<MutableList<Support>>()
	var supportReplyOutcome = outCome<Support>()

	fun supportArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(supportArchiveOutcome, requestService.supportArchive(limit, offset), bag)

	fun supportAdd(title: String, message: String, bag: CompositeDisposable) =
		requestToNetwork(supportReplyOutcome, requestService.supportAdd(title, message), bag)

	fun supportDetail(id: Int, bag: CompositeDisposable) =
		requestToNetwork(supportDetailOutcome, requestService.supportDetail(id), bag)

	fun supportReply(id: Int, message: String, bag: CompositeDisposable) =
		requestToNetwork(supportReplyOutcome, requestService.supportReply(id, message), bag)

	fun supportNotice(id: Int, bag: CompositeDisposable) =
		requestToNetwork(msgOutcome, requestService.supportNotice(id), bag)
	//endregion

	//region Wallet
	var walletOutcome = outCome<MutableList<Wallet>>()
	var walletIncreaseOutcome = outCome<Msg>()
	var walletCheckOutcome = outCome<Wallet>()

	fun walletArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(walletOutcome, requestService.walletArchive(limit, offset), bag)

	fun walletIncrease(amount: Int, bag: CompositeDisposable) =
		requestToNetwork(walletIncreaseOutcome, requestService.walletIncrease(amount), bag)

	fun walletCheckout(id: Int, bag: CompositeDisposable) =
		requestToNetwork(walletCheckOutcome, requestService.walletCheckout(id), bag)
	//endregion

	//region EditInfo
	var editInfoOutcome = outCome<Msg>()

	fun editInfo(name: String, birthDate: String, weddingDate: String, bag: CompositeDisposable) =
		requestToNetwork(
			editInfoOutcome,
			requestService.editInfo(name, birthDate, weddingDate),
			bag
		)
	//endregion

	//region Competition
	var competitionOutcome = outCome<MutableList<Competition>>()
	var competitionDetailOutcome = outCome<Competition>()
	var competitionGetOutcome = outCome<Tournament>()
	var competitionSaveOutcome = outCome<Msg>()

	fun competitionArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(competitionOutcome, requestService.competitionArchive(limit, offset), bag)

	fun competitionDetail(id: Int, bag: CompositeDisposable) =
		requestToNetwork(competitionDetailOutcome, requestService.competitionDetail(id), bag)

	fun competitionGet(bag: CompositeDisposable) =
		requestToNetwork(competitionGetOutcome, requestService.competitionGet(), bag)

	fun competitionSave(competitionId: Int, questions: String, bag: CompositeDisposable) =
		requestToNetwork(
			competitionSaveOutcome,
			requestService.competitionSave(competitionId, questions),
			bag
		)
	//endregion

	//region Address
	var addressOutcome = outCome<MutableList<Address>>()
	var addressSearchOutcome = outCome<MutableList<AddressSearch>>()
	var addressAddOutcome = outCome<Msg>()
	var addressDeleteOutcome = outCome<Msg>()

	fun addressArchive(limit: Int, offset: Int, branchId: Int?, bag: CompositeDisposable) =
		requestToNetwork(
			addressOutcome,
			requestService.addressArchive(limit, offset, branchId = branchId),
			bag
		)

	fun addressSearch(place: String, bag: CompositeDisposable) =
		requestToNetwork(addressSearchOutcome, requestService.addressSearch(place), bag)

	fun addressAdd(
		name: String,
		mobile: String,
		address: String,
		lat: Double,
		lng: Double,
		forceAdd: Int,
		branchId: Int?,
		bag: CompositeDisposable
	) =
		requestToNetwork(
			addressAddOutcome,
			requestService.addressAdd(name, mobile, address, lat, lng, forceAdd, branchId),
			bag
		)

	fun addressEdit(
		id: Int,
		name: String,
		mobile: String,
		address: String,
		lat: Double,
		lng: Double,
		forceAdd: Int,
		bag: CompositeDisposable
	) =
		requestToNetwork(
			addressAddOutcome,
			requestService.addressEdit(id, name, mobile, address, lat, lng, forceAdd),
			bag
		)

	fun addressDelete(id: Int, bag: CompositeDisposable) =
		requestToNetwork(addressDeleteOutcome, requestService.addressDelete(id), bag)
	//endregion

	//region Messages
	var messageOutcome = outCome<MutableList<Message>>()

	fun messageArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(messageOutcome, requestService.messageArchive(limit, offset), bag)
	//endregion

	//region Lottery
	var lotteryOutcome = outCome<MutableList<LotteryModel>>()

	fun lotteryArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(lotteryOutcome, requestService.lotteryArchive(limit, offset), bag)
	//endregion

	//region Change number
	var changeNumberOutcome = outCome<Msg>()
	var smsOutcome = outCome<Msg>()
	var resendCodeOutcome = outCome<Msg>()
	var verifyCodeOutcome = outCome<Msg>()

	fun changeNumber(mobile: String, bag: CompositeDisposable) =
		requestToNetwork(changeNumberOutcome, requestService.changeNumber(mobile), bag)

	fun sendCode(mobile: String, bag: CompositeDisposable) =
		requestToNetwork(smsOutcome, requestService.changeNumberSendCode(mobile), bag)

	fun resendCode(mobile: String, bag: CompositeDisposable) =
		requestToNetwork(resendCodeOutcome, requestService.changeNumberResendCode(mobile), bag)

	fun verifyCode(mobile: String, code: String, bag: CompositeDisposable) = requestToNetwork(
		verifyCodeOutcome,
		requestService.changeNumberVerifyCode(mobile, code),
		bag
	)
	//endregion

	//region More
	var moreOutcome = outCome<GeneralModel>()
	var faqOutcome = outCome<MutableList<Faq>>()
	var moreBranchesOutcome = outCome<MutableList<CityModel>>()
	var contactOutcome = outCome<ContactUs>()
	var areaOutcome = outCome<MutableList<AreaItem>>()

	fun about(bag: CompositeDisposable) = requestToNetwork(moreOutcome, requestService.about(), bag)
	fun law(bag: CompositeDisposable) = requestToNetwork(moreOutcome, requestService.law(), bag)
	fun faq(bag: CompositeDisposable) = requestToNetwork(faqOutcome, requestService.faq(), bag)
	fun moreBranches(bag: CompositeDisposable) = requestToNetwork(moreBranchesOutcome, requestService.moreBranches(), bag)
	fun contact(bag: CompositeDisposable) = requestToNetwork(contactOutcome, requestService.contact(), bag)
	fun area(bag: CompositeDisposable) = requestToNetwork(areaOutcome, requestService.area(), bag)

	var clubOutcome = outCome<MutableList<ClubItemModel>>()
	var clubUserOutcome = outCome<MutableList<ClubItemProfileModel>>()
	var clubUseOutcome = outCome<Msg>()

	fun clubArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(clubOutcome, requestService.clubArchive(limit, offset), bag)

	fun clubUsedArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(clubUserOutcome, requestService.clubUsedArchive(limit, offset), bag)

	fun clubUse(id: Int, bag: CompositeDisposable) =
		requestToNetwork(clubUseOutcome, requestService.clubUseGift(id), bag)
	//endregion

	//region Order Archive
	var orderOutcome = outCome<MutableList<Order>>()
	var orderDetailOutcome = outCome<Order>()

	fun orderArchive(limit: Int, offset: Int, orderId: Int, bag: CompositeDisposable) =
		requestToNetwork(orderOutcome, requestService.orderArchive(limit, offset, orderId), bag)

	fun orderDetail(orderId: Int, bag: CompositeDisposable) =
		requestToNetwork(orderDetailOutcome, requestService.orderDetail(orderId), bag)
	//endregion

	//region Product Archive
	var productOutcome = outCome<MutableList<Product>>()
	var productDetailOutcome = outCome<Product>()
	var productOfferOutcome = outCome<ShopCartOfferModel>()

	fun productArchive(
		limit: Int,
		offset: Int,
		categoryId: Int,
		branchId: Int,
		bag: CompositeDisposable
	) = requestToNetwork(
		productOutcome,
		requestService.productArchive(limit, offset, categoryId, branchId),
		bag
	)

	fun productDetail(id: Int, branchId: Int, bag: CompositeDisposable) =
		requestToNetwork(productDetailOutcome, requestService.productDetail(id, branchId), bag)

	fun productOfferArchive(branchId: Int, bag: CompositeDisposable) =
		requestToNetwork(productOfferOutcome, requestService.productOfferArchive(branchId), bag)
	//endregion

	//region Maker
	var makerOutcome = outCome<Maker>()

	fun maker(branchId: Int, bag: CompositeDisposable) =
		requestToNetwork(makerOutcome, requestService.maker(branchId), bag)
	//endregion

	//region ShopCart
	var shopCartDecideOutcome = outCome<ShopCartDecide>()
	var toolsOutcome = outCome<ShopCartTools>()
	var discountOutcome = outCome<Msg>()
	var oredrAddOutcome = outCome<Msg>()
	var oredrCheckPaymentOutcome = outCome<Msg>()

	fun shopCartTools(addressId: Int?, branchId: Int, bag: CompositeDisposable) = requestToNetwork(
		toolsOutcome,
		requestService.shopCartTools(addressId = addressId, branchId = branchId),
		bag
	)

	fun shopCartDecide(branchId: Int, bag: CompositeDisposable) = requestToNetwork(
		shopCartDecideOutcome,
		requestService.shopCartDecide(branchId = branchId),
		bag
	)

	fun checkDiscount(code: String, branchId: Int, bag: CompositeDisposable) =
		requestToNetwork(discountOutcome, requestService.checkDiscount(code, branchId), bag)

	fun checkOrderPayment(id: Int, bag: CompositeDisposable) =
		requestToNetwork(oredrCheckPaymentOutcome, requestService.checkOrderPayment(id), bag)

	fun addOrder(
		discountCode: String,
		isUseWallet: Int,
		orderJson: String,
		addressId: Int,
		branchId: Int,
		forceAddAddress: Int,
		forceAddTime: Int,
		deliverType: Int,
		paymentType: Int,
		reserveTime: String,
		description: String,
		bag: CompositeDisposable
	) =
		requestToNetwork(
			oredrAddOutcome, requestService.addOrder(
				discountCode = discountCode,
				isUseWallet = isUseWallet,
				orderJson = orderJson,
				addressId = addressId,
				branchId = branchId,
				forceAddAddress = forceAddAddress,
				forceAddTime = forceAddTime,
				deliverType = deliverType,
				reserveTime = reserveTime,
				paymentType = paymentType,
				description = description
			), bag
		)

	var msgOutcome2 = outCome<Msg>()
	fun orderNotice(id: Int, bag: CompositeDisposable) =
		requestToNetwork(msgOutcome2, requestService.orderNotice(id), bag)
	//endregion

	//region Branch Archive
	var branchOutcome = outCome<MutableList<Branch>>()

	fun branchArchive(cityId: Int, lat: Double?, lng: Double?, bag: CompositeDisposable) =
		requestToNetwork(branchOutcome, requestService.branchArchive(cityId, lat, lng), bag)

	//region city Archive
	var cityOutcome = outCome<MutableList<CityModel>>()

	fun cityArchive(lat: Double?, lng: Double?, bag: CompositeDisposable) =
		requestToNetwork(cityOutcome, requestService.cityArchive(lat, lng), bag)

	var provinceOutcome = outCome<MutableList<ProvinceModel>>()
	fun provinceArchive(bag: CompositeDisposable) =
		requestToNetwork(provinceOutcome, requestService.provinceArchive(), bag)


	//region BranchForM request
	var branchFormOutcome = outCome<Msg>()

	fun addBranchRequest(
		firstName: RequestBody,
		lastName: RequestBody,
		mobile: RequestBody,
		phone: RequestBody,
		email: RequestBody,
		age: RequestBody,
		province: RequestBody,
		city: RequestBody,
		storeStatus: RequestBody,
		address: RequestBody?,
		area: RequestBody?,
		currentSkill: RequestBody,
		investment: RequestBody,
		restaurantSkill: RequestBody,
		learning: RequestBody,
		turnover: RequestBody,
		description: RequestBody,
		resume: MutableList<MultipartBody.Part>,
		documents: MutableList<MultipartBody.Part>, bag: CompositeDisposable
	) = requestToNetwork(
		branchFormOutcome, requestService.addBranchRequest(
			firstName,
			lastName,
			mobile,
			phone,
			email,
			age,
			province,
			city,
			storeStatus,
			address,
			area,
			currentSkill,
			investment,
			restaurantSkill,
			learning,
			turnover,
			description,
			resume,
			documents
		), bag
	)
	//endregion

	//region Pizza Archive
	var pizzaOutcome = outCome<MutableList<Pizza>>()
	var pizzaSendToMenuOutcome = outCome<Msg>()
	var customerOutcome = outCome<MutableList<Pizza>>()
	var commentOutcome = outCome<MutableList<Comment>>()
	var commentAddOutcome = outCome<Msg>()

	fun pizzaArchive(limit: Int, offset: Int, name: String, bag: CompositeDisposable) =
		requestToNetwork(pizzaOutcome, requestService.pizzaArchive(limit, offset, name), bag)

	fun pizzaSendToMenu(
		id: RequestBody,
		image: MultipartBody.Part? = null,
		bag: CompositeDisposable
	) = requestToNetwork(pizzaSendToMenuOutcome, requestService.pizzaSendToMenu(id, image), bag)

	fun pizzaDelete(id: Int, bag: CompositeDisposable) =
		requestToNetwork(pizzaSendToMenuOutcome, requestService.pizzaDelete(id), bag)

	fun customerArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(customerOutcome, requestService.customerArchive(limit, offset), bag)

	fun commentArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(commentOutcome, requestService.commentArchive(limit, offset), bag)

	fun commentAdd(
		id: RequestBody,
		rate: RequestBody,
		comment: RequestBody,
		image: MultipartBody.Part? = null,
		bag: CompositeDisposable
	) =
		requestToNetwork(
			commentAddOutcome,
			requestService.commentAdd(id, rate, comment, image),
			bag
		)
	//endregion

	//region Survey
	var surveyOutcome = outCome<MutableList<Question>>()
	var surveyAddOutcome = outCome<Msg>()

	fun survey(bag: CompositeDisposable) =
		requestToNetwork(surveyOutcome, requestService.survey(), bag)

	fun surveyAdd(orderId: Int, answers: String, description: String, bag: CompositeDisposable) =
		requestToNetwork(
			surveyAddOutcome, requestService.surveyAdd(
				orderId = orderId,
				answers = answers,
				description = description
			), bag
		)
	//endregion

       //campaign List
	var campaignOutcome: PublishSubject<Outcome<MutableList<Campaign>>> = outCome<MutableList<Campaign>>()

	fun campaignArchive(limit: Int, offset: Int, bag: CompositeDisposable) =
		requestToNetwork(campaignOutcome, requestService.campaignArchive(limit, offset), bag)

	var campaignDemoOutcome: PublishSubject<Outcome<CampaignDemo>> = outCome<CampaignDemo>()

	fun campaignDemo( bag: CompositeDisposable) =
		requestToNetwork(campaignDemoOutcome, requestService.campaignDemo(), bag)

	fun campaignDetail(id:Int, bag: CompositeDisposable) =
		requestToNetwork(campaignDemoOutcome, requestService.campaignDetail(id), bag)
	var campaignSaveOutcome = outCome<Msg>()

	fun campaignSave(
		id: RequestBody,
		instagram: RequestBody,
		image: MultipartBody.Part? = null,
		bag: CompositeDisposable
	) = requestToNetwork(campaignSaveOutcome, requestService.campaignSave(id, instagram, image), bag)
	//endregion
}