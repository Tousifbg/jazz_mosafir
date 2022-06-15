package pk.mosafir.travsol.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.BuildConfig
import pk.mosafir.travsol.R
import pk.mosafir.travsol.adapter.CityListAdapter
import pk.mosafir.travsol.adapter.OfferAdapter
import pk.mosafir.travsol.databinding.HomeFragmentBinding
import pk.mosafir.travsol.databinding.HomeFragmentBinding.*
import pk.mosafir.travsol.interfaces.CitySelector
import pk.mosafir.travsol.model.LikeDataModal
import pk.mosafir.travsol.model.data
import pk.mosafir.travsol.response.DiscoverPakistanCity
import pk.mosafir.travsol.response.Offer
import pk.mosafir.travsol.ui.base.BaseFragment
import pk.mosafir.travsol.ui.book_flight.BookFlightFragment
import pk.mosafir.travsol.ui.book_hotel.BookHotelFragment
import pk.mosafir.travsol.ui.home_trending.*
import pk.mosafir.travsol.ui.plan_tour.PlanTourFragment
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.viewmodel.OffersViewModel
import pk.mosafir.travsol.webview.AnalyticsWebInterface
import pk.mosafir.travsol.webview.DataFromJS
import pk.mosafir.travsol.webview.WebViewActivity
import java.io.IOException
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import android.util.Base64
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment(), CitySelector, BillingProcessor.IBillingHandler {
    private lateinit var dialog: Dialog
    private val lp = WindowManager.LayoutParams()

    private val mOffersList = mutableListOf<Offer>()
    private val mCityList = mutableListOf<DiscoverPakistanCity>()
    private val mCityList2 = mutableListOf<DiscoverPakistanCity>()
//    private var likeNumber1 = 2
//    private var likeNumber2 = 2
//    private var likeNumber3 = 3

    private var dataModalList = ArrayList<LikeDataModal>()

//    private lateinit var viewLike1: View
//    private lateinit var viewLike2: View
//    private lateinit var viewLike3: View
//
//    private lateinit var inflater: LayoutInflater
    internal lateinit var fragmentManager: FragmentManager
    private lateinit var transaction: FragmentTransaction

    private val mOffersAdapter = OfferAdapter(mOffersList)
    private var cityAdapter = CityListAdapter("", mCityList, this)

    private val viewModel: OffersViewModel by viewModel()
    private lateinit var _binding: HomeFragmentBinding
    private val binding get() = _binding
    private var bp: BillingProcessor? = null
    override fun onResume() {
        super.onResume()
        //3 cards view for on base of recent
//        likeNumber1 = dataModalList[0].dataList!!.size
//        likeNumber2 = dataModalList[1].dataList!!.size
//        likeNumber3 = dataModalList[2].dataList!!.size
//        binding.like1.removeAllViews()
//        binding.like2.removeAllViews()
//        binding.like3.removeAllViews()
//        inflater = LayoutInflater.from(context)
//        when (likeNumber1) {
//            1 -> viewLike1 = inflater.inflate(R.layout.main_one_card, binding.like1)
//            2 -> viewLike1 = inflater.inflate(R.layout.main_two_card, binding.like1)
//            3 -> viewLike1 = inflater.inflate(R.layout.main_three_card, binding.like1)
//            4 -> viewLike1 = inflater.inflate(R.layout.main_four_card, binding.like1)
//            5 -> viewLike1 = inflater.inflate(R.layout.main_five_card, binding.like1)
//        }
//        when (likeNumber2) {
//            1 -> viewLike2 = inflater.inflate(R.layout.main_one_card, binding.like2)
//            2 -> viewLike2 = inflater.inflate(R.layout.main_two_card, binding.like2)
//            3 -> viewLike2 = inflater.inflate(R.layout.main_three_card, binding.like2)
//            4 -> viewLike2 = inflater.inflate(R.layout.main_four_card, binding.like2)
//            5 -> viewLike2 = inflater.inflate(R.layout.main_five_card, binding.like2)
//        }
//        when (likeNumber3) {
//            1 -> viewLike3 = inflater.inflate(R.layout.main_one_card, binding.like3)
//            2 -> viewLike3 = inflater.inflate(R.layout.main_two_card, binding.like3)
//            3 -> viewLike3 = inflater.inflate(R.layout.main_three_card, binding.like3)
//            4 -> viewLike3 = inflater.inflate(R.layout.main_four_card, binding.like3)
//            5 -> viewLike3 = inflater.inflate(R.layout.main_five_card, binding.like3)
//        }
//        viewLike1.initialize(dataModalList[0].dataList!!.size, requireContext(), dataModalList[0])
//        viewLike2.initialize(dataModalList[1].dataList!!.size, requireContext(), dataModalList[1])
//        viewLike3.initialize(dataModalList[2].dataList!!.size, requireContext(), dataModalList[2])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cacheStore = CacheStore.getInstance()
        _binding = inflate(inflater, container, false)
        dialog = Dialog(requireActivity(), R.style.full_screen_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding.content.startRippleAnimation()
        // billing initializing
        bp = BillingProcessor(
            requireContext(),
            getString(R.string.iap_key),
            getString(R.string.mer_id),
            this
        )
        binding.notification.setOnClickListener {
            val dialogue = Dialog(requireContext(), R.style.full_screen_dialog)
            dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogue.setContentView(R.layout.notification_dialog)
            dialogue.setCancelable(false)
            val close = dialogue.findViewById<TextView>(R.id.cancel)
            close.setOnClickListener {
                dialogue.cancel()
            }

            dialogue.show()
        }
        bp!!.initialize()
        allWebViewYehnHere()

        //you may like portion
        val dataList = ArrayList<data>()
        dataList.add(data(1, "Lahore1", "Dubai", "12000"))
        dataList.add(data(3, "Lahore2", "Dubai", "27500"))
        var likeDataModal = LikeDataModal("Skardu", "", dataList)
        dataModalList.add(likeDataModal)

        val dataList2 = ArrayList<data>()
        dataList2.add(data(1, "Lahore1", "Dubai", "23000"))
        dataList2.add(data(3, "Lahore2", "Dubai", "21000"))
        dataList2.add(data(12, "Lahore3", "Dubai", "24900"))
        dataList2.add(data(14, "Lahore4", "Dubai", "20000"))
        likeDataModal = LikeDataModal("Dubai", "", dataList2)
        dataModalList.add(likeDataModal)

        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        val dataList3 = ArrayList<data>()
        dataList3.add(data(2, "Lahore5", "Dubai", "19500"))
        likeDataModal = LikeDataModal("Karachi", "", dataList3)
        dataModalList.add(likeDataModal)

        //trending fragment

        binding.tabIncluder.relativeFLight.setOnClickListener {
            with(binding) {
                with(tabIncluder) {
                    relativeFLight.setBackgroundResource(R.drawable.background_tab)
                    relativeHotel.setBackgroundResource(0)
                    relativeTour.setBackgroundResource(0)
                    relativeDiscover.setBackgroundResource(0)
                    relativeBlog.setBackgroundResource(0)

                    flightTv.visible()
                    hotelTv.gone()
                    tourTv.gone()
                    discoverTv.gone()
                    blogTv.gone()

                }
            }
            fragmentManager = requireActivity().supportFragmentManager
            transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragContainer, TrendingFlightfragment())
            transaction.commit()
        }
        binding.tabIncluder.relativeHotel.setOnClickListener {
            with(binding) {
                with(tabIncluder) {
                    relativeFLight.setBackgroundResource(0)
                    relativeHotel.setBackgroundResource(R.drawable.background_tab)
                    relativeTour.setBackgroundResource(0)
                    relativeDiscover.setBackgroundResource(0)
                    relativeBlog.setBackgroundResource(0)

                    flightTv.gone()
                    hotelTv.visible()
                    tourTv.gone()
                    discoverTv.gone()
                    blogTv.gone()
                }
            }
            fragmentManager = requireActivity().supportFragmentManager
            transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragContainer, TrendingHotelFragment())
            transaction.commit()
        }
        binding.tabIncluder.relativeTour.setOnClickListener {
            with(binding) {
                with(tabIncluder) {
                    relativeFLight.setBackgroundResource(0)
                    relativeHotel.setBackgroundResource(0)
                    relativeTour.setBackgroundResource(R.drawable.background_tab)
                    relativeDiscover.setBackgroundResource(0)
                    relativeBlog.setBackgroundResource(0)

                    flightTv.gone()
                    hotelTv.gone()
                    tourTv.visible()
                    discoverTv.gone()
                    blogTv.gone()
                }
            }
            fragmentManager = requireActivity().supportFragmentManager
            transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragContainer, TrendingTourFragment())
            transaction.commit()

        }
        binding.tabIncluder.relativeDiscover.setOnClickListener {
            with(binding.tabIncluder) {
                relativeFLight.setBackgroundResource(0)
                relativeHotel.setBackgroundResource(0)
                relativeTour.setBackgroundResource(0)
                relativeDiscover.setBackgroundResource(R.drawable.background_tab)
                relativeBlog.setBackgroundResource(0)

                flightTv.gone()
                hotelTv.gone()
                tourTv.gone()
                discoverTv.visible()
                blogTv.gone()
            }
            fragmentManager = requireActivity().supportFragmentManager
            transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragContainer, TrendingDiscoverFragment())
            transaction.commit()

        }
        binding.tabIncluder.relativeBlog.setOnClickListener {
            with(binding) {
                with(tabIncluder) {
                    relativeFLight.setBackgroundResource(0)
                    relativeHotel.setBackgroundResource(0)
                    relativeTour.setBackgroundResource(0)
                    relativeDiscover.setBackgroundResource(0)
                    relativeBlog.setBackgroundResource(R.drawable.background_tab)

                    flightTv.gone()
                    hotelTv.gone()
                    tourTv.gone()
                    discoverTv.gone()
                    blogTv.visible()
                }
            }
            fragmentManager = requireActivity().supportFragmentManager
            transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragContainer, TrendingBlogsFragment())
            transaction.commit()

        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                //image slider
                binding.imageSlider.load(BuildConfig.slide_banner)
                val url = URL(BuildConfig.slide_banner)
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                when {
                    cacheStore.getCacheFile("slider") == null -> {
//                        cacheStore.saveCacheFile("slider", image)
                        binding.imageSlider.load(image)
                    }
                    else -> {
                        val count = image.byteCount
                        val image2 = cacheStore.getCacheFile("slider")
                        val count2 = image2.byteCount
                        if (count == count2) {
                            binding.imageSlider.load(cacheStore.getCacheFile("slider"))
                        } else {
                            cacheStore.saveCacheFile("slider", image)
                            binding.imageSlider.load(cacheStore.getCacheFile("slider"))
                        }
                    }
                }
            } catch (e: IOException) {
            }
        }
        showDialog()
        //discover pakistan list initializer
        viewModel.hotelCityList.observe(viewLifecycleOwner, {
            with(mCityList) {
                clear()
                it?.let { it1->
                    addAll(it1)
                    cityAdapter.notifyDataSetChanged()
                }
            }
        })
        binding.discoverEt.setOnClickListener {
            dialog.window!!.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)
            dialog.show()
            dialog.window!!.attributes = lp
        }
        //special offers recyclerview
        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.special_offer_recycle)
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mOffersAdapter
        }
        //location videos recyclerview
//        binding.videosView.apply {
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            adapter = mVideosAdapter
//        }
//        mVideoList.add(VideoModal("Islamabad city", "0b-GCpyPEtE", "Hunza city view"))
//
//        mVideosAdapter.notifyDataSetChanged()
        //mosafir kahani video
//        binding.youtubeKahaniView.apply {
//            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//                override fun onReady(youTubePlayer: YouTubePlayer) {
//                    val defaultPlayerUiController =
//                        DefaultPlayerUiController(binding.youtubeKahaniView, youTubePlayer)
//                    binding.youtubeKahaniView.setCustomPlayerUi(defaultPlayerUiController.rootView)
//                    val videoId = "34sz-hAIFdw"
//                    youTubePlayer.loadVideo(videoId, 0f)
//                }
//            })
//            addFullScreenListener(object : YouTubePlayerFullScreenListener {
//                override fun onYouTubePlayerEnterFullScreen() {
//                    val intent = Intent(context, VideoFullScreen::class.java)
//                    intent.putExtra("path", "34sz-hAIFdw")
//                    context.startActivity(intent)
//                }
//
//                override fun onYouTubePlayerExitFullScreen() {
//                }
//            })
//        }

        //special offers
        lifecycleScope.launch {
            viewModel.offers.observe(viewLifecycleOwner, {
                with(mOffersList) {
                    clear()
                    it?.let { it1 -> addAll(it1) }
                    mOffersAdapter.notifyDataSetChanged()
                }
                setTitle(getString(R.string.app_name))
            })
        }

        viewModel.isLoading.observe(viewLifecycleOwner, {

        })
        viewModel.error.observe(viewLifecycleOwner, {
            requireContext().toast(it)
        })
        //trip, hotel and flight buttons implementation
        binding.planTrip.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, PlanTourFragment())
            transaction.commit()
        }
        binding.bookHotel.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, BookHotelFragment())
            transaction.commit()
        }
        binding.bookFlight.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, BookFlightFragment())
            transaction.commit()
        }

        return binding.root
    }

    @SuppressLint("FragmentLiveDataObserve", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setTitle(title: String) {
        tvTitle?.text = getString(R.string.app_name)
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutom_dialog_location)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
        val location = dialog.findViewById<EditText>(R.id.location)
        val title = dialog.findViewById<TextView>(R.id.title)
        title.text = "Discover Pakistan"
        location.hint = "Discover Pakistan"
        location.performClick()
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cityAdapter
        }
        val cancel = dialog.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        location.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s!!.isEmpty() -> {
                        cityAdapter = CityListAdapter("", mCityList, this@HomeFragment)
                        recyclerView.adapter = cityAdapter
                    }
                    else -> {
                        mCityList2.clear()
                        cityAdapter.notifyDataSetChanged()
                        cityAdapter = CityListAdapter(
                            s.toString().lowercase(Locale.getDefault()),
                            mCityList2,
                            this@HomeFragment
                        )
                        recyclerView.adapter = cityAdapter
                        viewModel.getSelectedLocation("%${s}%")
                        viewModel.searchCityList.observe(viewLifecycleOwner, {
                            with(mCityList2) {
                                clear()
                                it?.let { it1 -> addAll(it1) }
                                cityAdapter.notifyDataSetChanged()
                            }
                        })

                    }

                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun allWebViewYehnHere() {
        val webview = binding.webView
        webview.loadUrl("https://www.mosafir.pk/native_view")
        val settings: WebSettings = webview.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setAppCacheEnabled(true)
        settings.allowContentAccess = true
        webview.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webview.addJavascriptInterface(
            AnalyticsWebInterface(requireContext()), AnalyticsWebInterface.TAG
        )
        webview.addJavascriptInterface(
            DataFromJS(requireContext()), DataFromJS.TAG
        )

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (!url.equals("https://www.mosafir.pk/native_view") || !url.equals("https://mosafir.pk/native_view")) {
                    requireContext().toast(url!!)
                    val intent = Intent(requireActivity(), WebViewActivity::class.java)
                    intent.putExtra(
                        "url",
                        url
                    )
                    startActivity(intent)
                }
                return true
            }
        }
    }

    override fun selected(city: String?, id: String?) {
        id?.let { viewModel.putRecentLocation(it) }
        cityAdapter = CityListAdapter("", mCityList, this@HomeFragment)
        val intent = Intent(requireActivity(), WebViewActivity::class.java)
        intent.putExtra(
            "url",
            "https://mosafir.pk/mobile/website/discover?discover_pakistan=$city"
        )
        startActivity(intent)
        dialog.dismiss()
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
    }

    override fun onPurchaseHistoryRestored() {}

    override fun onBillingError(errorCode: Int, error: Throwable?) {
    }

    override fun onBillingInitialized() {
    }
}