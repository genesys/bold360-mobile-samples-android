package com.sdk.samples.topics

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spanned
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.nanorep.convesationui.structure.UiConfigurations
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.elements.IncomingElementModel
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.convesationui.structure.providers.OutgoingElementUIProvider
import com.nanorep.convesationui.structure.setStyleConfig
import com.nanorep.convesationui.utils.assetsFileUri
import com.nanorep.convesationui.views.DrawableConfig
import com.nanorep.convesationui.views.adapters.BubbleContentUIAdapter
import com.nanorep.convesationui.views.chatelement.BubbleContentAdapter
import com.nanorep.convesationui.views.chatelement.ViewsLayoutParams
import com.nanorep.nanoengine.model.configuration.StyleConfig
import com.nanorep.nanoengine.model.configuration.TimestampStyle
import com.nanorep.sdkcore.model.StatusOk
import com.nanorep.sdkcore.utils.getTypeface
import com.nanorep.sdkcore.utils.px
import com.sdk.samples.R
import com.sdk.samples.databinding.BubbleOutgoingDemoBinding
import java.util.Date

open class CustomizedUI : BotChat() {

    override fun getChatBuilder(): ChatController.Builder? {

        return super.getChatBuilder()?.chatUIProvider(
            UIProviderFactory.create(
                this, intent?.getStringExtra("type") ?: ConfigOption.OVERRIDE.name
            )
        )
    }

}

private class UIProviderFactory {

    companion object {

        @JvmStatic
        fun create(context: Context, customUIOption: String?): ChatUIProvider {

            return when (customUIOption) {
                ConfigOption.ARTICLE_CONFIG.name -> configureArticle(context)
                ConfigOption.CONFIGURE.name -> configuring(context)
                else -> overriding(context)
            }
        }

        private fun configureArticle(context: Context) = ChatUIProvider(context).apply {

            articleUIProvider.articleUIConfig?.apply {

                background =
                    ContextCompat.getDrawable(context, R.drawable.genesys_back) ?: ColorDrawable(Color.LTGRAY)

                closeUIConfig?.apply {

                    val sidesMargin = 2.px
                    setMargin(sidesMargin, 8.px, sidesMargin, 10.px)

                    val closePadding = 4.px
                    setPadding(closePadding, closePadding, closePadding, closePadding)

                    position = UiConfigurations.Alignment.AlignCenterHorizontal
                    drawable = DrawableConfig(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.outline_cancel_white_24
                        )
                    ).apply {
                        compoundDrawablesPadding = 2.px
                    }
                }

                setContentPadding(0, 40.px, 0, 0)

                val pagePadding = 2.px
                setPadding(pagePadding, pagePadding, pagePadding, pagePadding)

                title.apply {
                    background = ColorDrawable(Color.YELLOW)
                    font = StyleConfig(14.px, Color.BLUE, context.getTypeface("fonts/great_vibes.otf"))
                }

                body.apply {
                    background = Color.GRAY
                    setFont( "great_vibes", assetsFileUri("fonts/great_vibes.otf"),
                        StyleConfig(8.px, Color.WHITE))
                }
            }
        }

        private fun configuring(context: Context) = ChatUIProvider(context).apply {

            chatElementsUIProvider.incomingUIProvider.apply {

                // Customize the general default SDK UI
                configure = { adapter: BubbleContentUIAdapter ->

                    adapter.apply {
                        setTextStyle(StyleConfig(14, Color.RED, Typeface.SANS_SERIF))
                        setStatusbarAlignment(UiConfigurations.Alignment.AlignStart)
                        setStatusbarComponentsAlignment(UiConfigurations.StatusbarAlignment.AlignLTR)
                        setBackground(ColorDrawable(Color.GRAY))

                        setAvatar(ContextCompat.getDrawable(context, R.drawable.mic_icon))
                        val margins = 2.px
                        setAvatarMargins(margins, margins, margins, margins)
                    }
                }

                // Dynamic Customization of the default SDK's UI (customization in real time according to the data of the element)
                customize = { adapter: BubbleContentUIAdapter, element: IncomingElementModel? ->

                    element?.run {

                        if (elemScope.isLive) {
                            adapter.apply {
                                setAvatar(ContextCompat.getDrawable(context, R.drawable.speaker_on))
                                setTextStyle(StyleConfig(10, Color.WHITE))
                                setBackground(ColorDrawable(Color.RED))
                            }
                        }
                    }

                    adapter
                }
            }
        }

        private fun overriding(context: Context) = ChatUIProvider(context).apply {

            chatElementsUIProvider.outgoingUIProvider.apply {

                overrideFactory =

                    object : OutgoingElementUIProvider.OutgoingFactory {

                        override fun createOutgoing(context: Context): BubbleContentAdapter {
                            return OverrideContentAdapter(context)
                        }
                    }
            }
        }
    }
}

/**
 * A demo for custom BubbleContentAdapter implementation
 */
private class OverrideContentAdapter(context: Context) : LinearLayout(context), BubbleContentAdapter {

    val binding = BubbleOutgoingDemoBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        this.orientation = VERTICAL

        if (layoutParams == null) {
            layoutParams = ViewsLayoutParams.getBubbleDefaultLayoutParams()
        }
    }

    override fun setTime(time: Long) {
        binding.customTimestamp.text = DateFormat.format("MM/dd/yyyy", Date(time)).toString()
    }

    override fun enableTimestampView(enable: Boolean) {
        binding.customTimestamp.visibility = if (enable) View.VISIBLE else View.GONE
    }

    override fun setText(text: Spanned, onLinkPress: ((url: String) -> Unit)?) {
        binding.localBubbleText.setBackgroundColor(Color.RED)
        binding.localBubbleText.text = text
    }

    override fun setLinkTextColor(color: Int) {
        binding.localBubbleText.setLinkTextColor(color)
    }

    override fun setTextPadding(left: Int, top: Int, right: Int, bottom: Int) {
        binding.localBubbleText.setPadding(left, top, right, bottom)
    }

    override fun setBackground(background: Drawable?) {
        binding.localBubbleText.background = background
    }

    override fun setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        (layoutParams as? MarginLayoutParams)?.apply {
            this.setMargins(left, top, right, bottom)
        }
    }

    override fun setBubblePadding(left: Int, top: Int, right: Int, bottom: Int) {
        setPadding(left, top, right, bottom)
    }

    override fun setTextStyle(styleConfig: StyleConfig) {
        binding.localBubbleText.setStyleConfig(styleConfig)
    }

    override fun setStatus(status: Int, statusText: String?) {
        binding.customStatus.text = if (status == StatusOk) "received" else "waiting"
    }

    override fun setDefaultStyle(styleConfig: StyleConfig, timestampStyle: TimestampStyle) {}

    override fun setTextAlignment(hAlignment: Int, vAlignment: Int) {}

    override fun setTextMargins(left: Int, top: Int, right: Int, bottom: Int) {}

    override fun enableStatusView(enable: Boolean) {}

    override fun setStatusViewTextStyle(statusStyle: StyleConfig) {}

    override fun setTimestampStyle(timestampStyle: TimestampStyle) {}

    override fun setStatusMargins(left: Int, top: Int, right: Int, bottom: Int) {}

}