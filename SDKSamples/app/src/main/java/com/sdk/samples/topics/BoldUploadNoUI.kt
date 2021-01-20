package com.sdk.samples.topics

//
//
//class BoldUploadNoUI : SampleActivity(), BoldChatListener {
//
//    override val account: BoldAccount
//    get() = ( getAccount_old() as BoldAccount ).apply { skipPrechat() }
//
//    private val uploader by lazy {
//        BoldLiveUploader()
//    }
//
//    override val onChatLoaded: (fragment: Fragment) -> Unit
//        get() = { /* We don't present the chat fragment at this Sample*/ }
//
//    override val containerId: Int
//        get() = R.id.upload_view
//
//    private var boldChat: BoldChat? = null
//
//    override var chatType = ChatType.Live
//
//   /* override fun getAccount_old(): Account {
//        return account
//    }*/
//
//    override fun startChat(savedInstanceState: Bundle?) {
//        topic_title.text = topicTitle
//
//        createChat()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_upload_no_ui)
//
//        setSupportActionBar(findViewById(R.id.sample_toolbar))
//    }
//
//    private fun createChat() {
//
//        boldChat = BoldChat().apply {
//
//            visitorInfo = account.info
//            wListener = this@BoldUploadNoUI.weakRef()
//
//            try {
//                prepare(
//                    this@BoldUploadNoUI, account.apiKey,
//                    sessionParams = mapOf(
//                        SessionParam.IncludeBranding to "true",
//                        SessionParam.IncludeLayeredBranding to "true",
//                        SessionParam.IncludeChatWindowSettings to "true"
//                    )
//                )
//
//            } catch (ex: IllegalStateException) {
//                Log.e("BoldUploadNoUI", "failed to create bold session")
//                finish()
//            }
//
//        }.start()
//
//    }
//
//    override fun chatEnded(formData: PostChatData?) {
//
//        if (supportFragmentManager.backStackEntryCount == 0) {
//            finish()
//        }
//    }
//
//    override fun chatUnavailable(formData: UnavailabilityData?) {
//        super.chatUnavailable(formData)
//
//        if (!isFinishing) {
//            runMain {
//                toast(this, "Chat unavailable")
//            }
//        }
//    }
//
//    override fun chatCreated(formData: PreChatData?) {
//        boldChat?.start()
//    }
//
//    override fun visitorInfoUpdated() {
//        runMain {
//            progress_bar.visibility = View.INVISIBLE
//            take_a_picture.visibility = View.VISIBLE
//            upload_default_image.visibility = View.VISIBLE
//        }
//
//        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray) {
//
//        requestCode.takeIf { it == CAMERA_PERMISSION_REQUEST_CODE &&  grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED }?.run {
//            take_a_picture.setOnClickListener {
//                startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE)
//            }
//        } ?: kotlin.run {
//            take_a_picture.isEnabled = false
//        }
//
//        upload_default_image.setOnClickListener {
//            (ContextCompat.getDrawable(this, R.drawable.sample_image) as? BitmapDrawable)?.bitmap?.run { uploadBitmap(this) }
//        }
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        (data?.extras?.get("data") as? Bitmap)?.takeIf { requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK }?.run {
//                uploadBitmap(this)
//        }
//    }
//
//    private fun uploadBitmap(bitmap: Bitmap) {
//
//        val progressController = ProgressController(upload_container)
//
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//
//        val fileUploadInfo = FileUploadInfo().apply {
//            content = stream.toByteArray()
//            name = "Test"
//            type = FileType.Picture
//        }
//
//        uploader.upload(
//            fileUploadInfo,
//            boldChat?.sessionInfo(),
//
//            { currentProgress ->
//
//                currentProgress.takeIf { it == 100 }?.run {
//                    progressController.updateProgress(this)
//                } ?: kotlin.run {
//                    progressController.updateText("Upload Completed")
//                }
//            }
//
//        ) { uploadResult: UploadResult? ->
//
//            uploadResult?.run {
//
//                error?.run {
//                    Log.e(TAG, toString())
//
//                } ?: kotlin.run {
//
//                    progressController.updateView(false)
//
//                    Log.i(TAG, "${data?.toString()} uploaded")
//                }
//
//            } ?: kotlin.run {
//                Log.e(TAG, "null response")
//            }
//        }
//    }
//
//    internal class ProgressController(private val container: ViewGroup){
//
//        private val progressBar: ProgressBar?
//        private val uploadTitle: TextView?
//
//        private val context: Context = container.context
//
//        init {
//
//            progressBar = (LayoutInflater.from(context).inflate(R.layout.upload_progress, container, false) as? ProgressBar)?.apply {
//                val progressDrawable: Drawable = progressDrawable.mutate()
//
//                progressDrawable.setColorFilter(
//                    Color.BLUE,
//                    PorterDuff.Mode.SRC_IN
//                )
//                setProgressDrawable(progressDrawable)
//            }
//
//            uploadTitle = TextView(context).apply {
//                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
//                text = context.getString(R.string.upload_in_progress)
//                gravity = Gravity.CENTER_HORIZONTAL
//            }
//
//            updateView(true)
//
//        }
//
//        fun updateView(show: Boolean) {
//
//            runMain {
//
//                if (show) {
//                    container.addView(uploadTitle)
//                    container.addView(progressBar)
//                } else {
//                    container.removeView(uploadTitle)
//                    container.removeView(progressBar)
//                }
//            }
//        }
//
//        fun updateProgress(progress: Int) {
//            runMain {
//                progressBar?.progress = progress
//            }
//        }
//
//        fun updateText(text: String) {
//            runMain {
//                uploadTitle?.text = text
//            }
//        }
//    }
//
//    companion object {
//        const val TAG = "NoUIUploadSample"
//
//        const val CAMERA_REQUEST_CODE = 99
//        const val CAMERA_PERMISSION_REQUEST_CODE = 100
//    }
//}
//
