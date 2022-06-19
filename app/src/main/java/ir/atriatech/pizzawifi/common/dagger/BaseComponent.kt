package ir.atriatech.pizzawifi.common.dagger

import dagger.Component
import ir.atriatech.pizzawifi.base.MyMessageService
import ir.atriatech.pizzawifi.common.dagger.base_app.BaseAppComponent
import ir.atriatech.pizzawifi.ui.intro.step1.IntroStep1FragmentViewModel
import ir.atriatech.pizzawifi.ui.login.step1.LoginStep1FragmentViewModel
import ir.atriatech.pizzawifi.ui.login.step2.LoginStep2FragmentViewModel
import ir.atriatech.pizzawifi.ui.login.step3.LoginStep3FragmentViewModel
import ir.atriatech.pizzawifi.ui.main.MainActivity
import ir.atriatech.pizzawifi.ui.main.branch.BranchFragment
import ir.atriatech.pizzawifi.ui.main.branch.BranchFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.category.CategoryFragment
import ir.atriatech.pizzawifi.ui.main.category.CategoryFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.category.product.ProductAdapter
import ir.atriatech.pizzawifi.ui.main.category.product.ProductFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.city.CityFragment
import ir.atriatech.pizzawifi.ui.main.city.CityFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.HomeFragment
import ir.atriatech.pizzawifi.ui.main.home.HomeFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.blogdetail.BlogDetailFragment
import ir.atriatech.pizzawifi.ui.main.home.blogdetail.BlogDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.blogdetail.BlogSliderAdapter
import ir.atriatech.pizzawifi.ui.main.home.bloggallery.BlogGalleryAdapter
import ir.atriatech.pizzawifi.ui.main.home.bloggallery.BlogGalleryFragment
import ir.atriatech.pizzawifi.ui.main.home.bloggallery.BlogGalleryFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.bloggallery.BlogGalleryFullScreenAdapter
import ir.atriatech.pizzawifi.ui.main.home.blogvideo.BlogVideoAdapter
import ir.atriatech.pizzawifi.ui.main.home.blogvideo.BlogVideoFragment
import ir.atriatech.pizzawifi.ui.main.home.blogvideo.BlogVideoFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.customer.CustomerMenuAdapter
import ir.atriatech.pizzawifi.ui.main.home.customer.CustomerMenuFragment
import ir.atriatech.pizzawifi.ui.main.home.customer.CustomerMenuFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.customer.detail.CustomerMenuDetailFragment
import ir.atriatech.pizzawifi.ui.main.home.customer.detail.CustomerMenuDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.customer.detail.comment.AddCommentFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.product.HomeProductAdapter
import ir.atriatech.pizzawifi.ui.main.home.tournament.TournamentFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.home.tournament.questions.TournamentQuestionFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.maker.*
import ir.atriatech.pizzawifi.ui.main.maker.decide.DecideFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.maker.laststep.MakerLastStepFragment
import ir.atriatech.pizzawifi.ui.main.maker.laststep.MakerLastStepFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.maker.laststep.MakerLastStepMaterialAdapter
import ir.atriatech.pizzawifi.ui.main.maker.laststep.MakerLastStepProductlAdapter
import ir.atriatech.pizzawifi.ui.main.maker.products.MakerProductAdapter
import ir.atriatech.pizzawifi.ui.main.maker.products.MakerProductsFragment
import ir.atriatech.pizzawifi.ui.main.maker.products.MakerProductsFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.maker.stepmaterials.MakerStepMaterialsAdapter
import ir.atriatech.pizzawifi.ui.main.maker.stepmaterials.MakerStepsFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.more.MoreFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.more.about.AboutFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.more.area.AreaFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.more.branches.MoreBranchFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.more.contact.ContactUsFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.more.faq.FaqFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.more.law.LawFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.more.requestForm.RequestFormViewModel
import ir.atriatech.pizzawifi.ui.main.orders.OrdersFragment
import ir.atriatech.pizzawifi.ui.main.orders.OrdersFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.orders.detail.OrderDetailFragment
import ir.atriatech.pizzawifi.ui.main.orders.detail.OrderDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.orders.detail.OrderItemsAdapter
import ir.atriatech.pizzawifi.ui.main.orders.survey.SurveyFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.productdetail.ProductDetailFragment
import ir.atriatech.pizzawifi.ui.main.productdetail.ProductDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.ProfileFragment
import ir.atriatech.pizzawifi.ui.main.profile.ProfileFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.address.AddressFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.address.detail.AddressDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.address.detail.AddressDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.address.map.AddressMapFragment
import ir.atriatech.pizzawifi.ui.main.profile.address.map.AddressMapFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.campaign.CampaignListFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.campaign.detail.CampaignDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.campaign.detail.CampaignDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.campaign.register.CampaignRegisterFragment
import ir.atriatech.pizzawifi.ui.main.profile.campaign.register.CampaignRegisterFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.changemobile.ChangeMobileStep1FragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.changemobile.ChangeMobileStep2FragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.club.*
import ir.atriatech.pizzawifi.ui.main.profile.competition.CompetitionFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.competition.detail.CompetitionDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.editinfo.EditInfoFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.lottery.LotteryFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.lottery.detail.LotteryDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.messages.MessagesFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.messages.detail.MessageDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.pizza.PizzaAdapter
import ir.atriatech.pizzawifi.ui.main.profile.pizza.PizzaFragment
import ir.atriatech.pizzawifi.ui.main.profile.pizza.PizzaFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.pizza.detail.PizzaDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.pizza.detail.PizzaDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.pizza.detail.PizzaDetailMaterialAdapter
import ir.atriatech.pizzawifi.ui.main.profile.support.SupportFragment
import ir.atriatech.pizzawifi.ui.main.profile.support.SupportFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.support.add.AddSupportFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.support.detail.SupportDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.support.detail.SupportDetailFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.profile.wallet.WalletFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.shopcart.*
import ir.atriatech.pizzawifi.ui.main.shopcart.address.ShopCartAddressFragment
import ir.atriatech.pizzawifi.ui.main.shopcart.address.ShopCartAddressFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.shopcart.checkout.ShopCartCheckoutFragment
import ir.atriatech.pizzawifi.ui.main.shopcart.checkout.ShopCartCheckoutFragmentViewModel
import ir.atriatech.pizzawifi.ui.main.shopcart.offer.OfferDialogViewModel
import ir.atriatech.pizzawifi.ui.main.shopcart.offer.OffersDialogFragment
import ir.atriatech.pizzawifi.ui.main.shopcart.offer.ProductOfferAdapter

@BaseScope
@Component(dependencies = [BaseAppComponent::class])
interface BaseComponent {
	fun inject(introStep1FragmentViewModel: IntroStep1FragmentViewModel)
	fun inject(introStep1FragmentViewModel: LoginStep1FragmentViewModel)
	fun inject(loginStep2FragmentViewModel: LoginStep2FragmentViewModel)
	fun inject(loginStep3FragmentViewModel: LoginStep3FragmentViewModel)
	fun inject(moreFragmentViewModel: MoreFragmentViewModel)
	fun inject(requestFormViewModel: RequestFormViewModel)
	fun inject(contactUsFragmentViewModel: ContactUsFragmentViewModel)
	fun inject(homeFragmentViewModel: HomeFragmentViewModel)
	fun inject(categoryFragmentViewModel: CategoryFragmentViewModel)
	fun inject(productFragmentViewModel: ProductFragmentViewModel)
	fun inject(productDetailFragmentViewModel: ProductDetailFragmentViewModel)
	fun inject(customerMenuFragmentViewModel: CustomerMenuFragmentViewModel)
	fun inject(customerMenuDetailFragmentViewModel: CustomerMenuDetailFragmentViewModel)
	fun inject(addCommentFragmentViewModel: AddCommentFragmentViewModel)
	fun inject(makerStep1FragmentViewModel: MakerStep1FragmentViewModel)
	fun inject(ordersFragmentViewModel: OrdersFragmentViewModel)
	fun inject(orderDetailFragmentViewModel: OrderDetailFragmentViewModel)
	fun inject(profileFragmentViewModel: ProfileFragmentViewModel)
	fun inject(pizzaFragmentViewModel: PizzaFragmentViewModel)
	fun inject(pizzaDetailFragmentViewModel: PizzaDetailFragmentViewModel)
	fun inject(supportFragmentViewModel: SupportFragmentViewModel)
	fun inject(addSupportFragmentViewModel: AddSupportFragmentViewModel)
	fun inject(supportDetailFragmentViewModel: SupportDetailFragmentViewModel)
	fun inject(walletFragmentViewModel: WalletFragmentViewModel)
	fun inject(editInfoFragmentViewModel: EditInfoFragmentViewModel)
	fun inject(competitionFragmentViewModel: CompetitionFragmentViewModel)
	fun inject(competitionDetailFragmentViewModel: CompetitionDetailFragmentViewModel)
	fun inject(addressFragmentViewModel: AddressFragmentViewModel)
	fun inject(addressMapFragmentViewModel: AddressMapFragmentViewModel)
	fun inject(addressMapFragment: AddressMapFragment)
	fun inject(addressDetailFragmentViewModel: AddressDetailFragmentViewModel)
	fun inject(changeMobileStep1FragmentViewModel: ChangeMobileStep1FragmentViewModel)
	fun inject(changeMobileStep2FragmentViewModel: ChangeMobileStep2FragmentViewModel)
	fun inject(messagesFragmentViewModel: MessagesFragmentViewModel)
	fun inject(messageDetailFragmentViewModel: MessageDetailFragmentViewModel)
	fun inject(lotteryFragmentViewModel: LotteryFragmentViewModel)
	fun inject(lotteryDetailFragmentViewModel: LotteryDetailFragmentViewModel)
	fun inject(aboutFragmentViewModel: AboutFragmentViewModel)
	fun inject(lawFragmentViewModel: LawFragmentViewModel)
	fun inject(faqFragmentViewModel: FaqFragmentViewModel)
	fun inject(areaFragmentViewModel: AreaFragmentViewModel)
	fun inject(shopCartFragmentViewModel: ShopCartFragmentViewModel)
	fun inject(homeProductAdapter: HomeProductAdapter)
	fun inject(profileFragment: ProfileFragment)
	fun inject(addressDetailFragment: AddressDetailFragment)
	fun inject(productAdapter: ProductAdapter)
	fun inject(productDetailFragment: ProductDetailFragment)
	fun inject(mainActivity: MainActivity)
	fun inject(makerStep2FragmentViewModel: MakerStep2FragmentViewModel)
	fun inject(breadAdapter: BreadAdapter)
	fun inject(makerStep2Fragment: MakerStep2Fragment)
	fun inject(makerStep3FragmentViewModel: MakerStep3FragmentViewModel)
	fun inject(makerStepsFragmentViewModel: MakerStepsFragmentViewModel)
	fun inject(makerStepMaterialsAdapter: MakerStepMaterialsAdapter)
	fun inject(makerStep3Fragment: MakerStep3Fragment)
	fun inject(makerProductsFragmentViewModel: MakerProductsFragmentViewModel)
	fun inject(makerProductAdapter: MakerProductAdapter)
	fun inject(makerProductsFragment: MakerProductsFragment)
	fun inject(homeFragment: HomeFragment)
	fun inject(categoryFragment: CategoryFragment)
	fun inject(makerLastStepFragmentViewModel: MakerLastStepFragmentViewModel)
	fun inject(makerLastStepMaterialAdapter: MakerLastStepMaterialAdapter)
	fun inject(makerLastStepFragment: MakerLastStepFragment)
	fun inject(makerLastStepProductlAdapter: MakerLastStepProductlAdapter)
	fun inject(decideFragmentViewModel: DecideFragmentViewModel)
	fun inject(shopCartFragment: ShopCartFragment)
	fun inject(shopCartAdapter: ShopCartAdapter)
	fun inject(productOfferAdapter: ProductOfferAdapter)
	fun inject(offerDialogViewModel: OfferDialogViewModel)
	fun inject(offersDialogFragment: OffersDialogFragment)
	fun inject(shopCartAddressFragmentViewModel: ShopCartAddressFragmentViewModel)
	fun inject(shopCartAddressFragment: ShopCartAddressFragment)
	fun inject(shopCartDecideFragmentViewModel: ShopCartDecideFragmentViewModel)
	fun inject(shopCartDecideFragment: ShopCartDecideFragment)
	fun inject(branchFragmentViewModel: BranchFragmentViewModel)
	fun inject(shopCartCheckoutFragmentViewModel: ShopCartCheckoutFragmentViewModel)
	fun inject(shopCartCheckoutFragment: ShopCartCheckoutFragment)
	fun inject(orderDetailFragment: OrderDetailFragment)
	fun inject(pizzaDetailFragment: PizzaDetailFragment)
	fun inject(pizzaDetailMaterialAdapter: PizzaDetailMaterialAdapter)
	fun inject(customerMenuAdapter: CustomerMenuAdapter)
	fun inject(customerMenuFragment: CustomerMenuFragment)
	fun inject(customerMenuDetailFragment: CustomerMenuDetailFragment)
	fun inject(orderItemsAdapter: OrderItemsAdapter)
	fun inject(surveyFragmentViewModel: SurveyFragmentViewModel)
	fun inject(pizzaFragment: PizzaFragment)
	fun inject(tournamentFragmentViewModel: TournamentFragmentViewModel)
	fun inject(tournamentQuestionFragmentViewModel: TournamentQuestionFragmentViewModel)
	fun inject(myMessageService: MyMessageService)
	fun inject(ordersFragment: OrdersFragment)
	fun inject(supportDetailFragment: SupportDetailFragment)
	fun inject(supportFragment: SupportFragment)
	fun inject(pizzaAdapter: PizzaAdapter)
	fun inject(branchFragment: BranchFragment)
	fun inject(cityFragment: CityFragment)
	fun inject(cityFragmentViewModel: CityFragmentViewModel)
	fun inject(blogVideoFragmentViewModel: BlogVideoFragmentViewModel)
	fun inject(blogVideoFragment: BlogVideoFragment)
	fun inject(blogVideoAdapter: BlogVideoAdapter)
	fun inject(blogGalleryAdapter: BlogGalleryAdapter)
	fun inject(blogGalleryFragment: BlogGalleryFragment)
	fun inject(blogGalleryFragmentViewModel: BlogGalleryFragmentViewModel)
	fun inject(blogDetailFragment: BlogDetailFragment)
	fun inject(blogSliderAdapter: BlogSliderAdapter)
	fun inject(blogDetailFragmentViewModel: BlogDetailFragmentViewModel)
	fun inject(blogGalleryFullScreenAdapter: BlogGalleryFullScreenAdapter)
	fun inject(moreBranchFragmentViewModel: MoreBranchFragmentViewModel)
	fun inject(campaignListFragmentViewModel: CampaignListFragmentViewModel)
	fun inject(campaignDetailFragmentViewModel: CampaignDetailFragmentViewModel)
	fun inject(campaignRegisterFragmentViewModel: CampaignRegisterFragmentViewModel)
	fun inject(campaignRegisterFragment: CampaignRegisterFragment)
	fun inject(campaignDetailFragment: CampaignDetailFragment)
	fun inject(clubFragmentViewModel: ClubFragmentViewModel)
	fun inject(clubFragment: ClubFragment)
	fun inject(clubAdapter: ClubAdapter)
	fun inject(userClubFragmentViewModel: UserClubFragmentViewModel)
	fun inject(userClubFragment: UserClubFragment)
	fun inject(userClubAdapter: UserClubAdapter)
}