package com.sdk.samples.topics

//
//open class CustomUIChat : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_basic)
//
//        setSupportActionBar(findViewById(R.id.sample_toolbar))
//
//        findViewById<TextView>(R.id.topic_title).text = intent.getStringExtra("title")
//
//        initOptionsView()
//
//    }
//
//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.left_in, R.anim.right_out);
//    }
//
//
//    private fun initOptionsView(){
//        findViewById<ProgressBar>(R.id.basic_loading).visibility = View.GONE
//        LayoutInflater.from(this).inflate(R.layout.custom_ui_options_layout, findViewById<FrameLayout>(R.id.basic_chat_view), true)
//        buttonSetup(configure_option, configure)
//        buttonSetup(override_option, override)
//    }
//
//    private fun buttonSetup(button: Button, @CustomUIOption customUIOption: String) {
//
//        button.run {
//
//            text = getString(R.string.cutomized_ui, customUIOption)
//
//            setOnClickListener {
//
//                startActivity(Intent("com.sdk.sample.action.CUSTOMIZED_UI_IMPLEMENTATION").apply {
//
//                    putExtra("title", text.toString())
//                    putExtra("type", customUIOption)
//                }.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
//
//                overridePendingTransition(R.anim.right_in, R.anim.left_out);
//            }
//        }
//    }
//
//}