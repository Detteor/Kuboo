package com.sethchhim.kuboo_client.ui.main.login.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.like.LikeButton
import com.like.OnLikeListener
import com.sethchhim.kuboo_client.BR
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.databinding.LoginItemBinding
import com.sethchhim.kuboo_client.ui.main.MainActivityImpl0_View
import com.sethchhim.kuboo_remote.model.Login
import kotlinx.android.synthetic.main.login_item.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick

class LoginAdapter(val mainActivity: MainActivityImpl0_View, val viewModel: ViewModel) : BaseQuickAdapter<Login, LoginAdapter.LoginHolder>(R.layout.login_item, viewModel.getLoginList()) {

    init {
        BaseApplication.appComponent.inject(this)
        setHasStableIds(true)
    }

    private lateinit var recyclerview: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerview = recyclerView
    }

    override fun convert(helper: LoginHolder, item: Login) {
        val binding = helper.binding
        binding.setVariable(BR.item, item)
        helper.itemView.login_item_likeButton.isLiked = viewModel.isActiveLogin(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        holder.itemView.onClick { holder.onItemSelected() }
        holder.itemView.onLongClick { holder.editItem() }
        holder.itemView.login_item_imageView.onClick { holder.editItem() }
        holder.itemView.login_item_likeButton.setOnLikeListener(getOnLikeListener(holder))

        holder.itemView.login_item_imageView.imageResource = when (Settings.APP_THEME) {
            0 -> R.drawable.ic_edit_black_24dp
            else -> R.drawable.ic_edit_white_24dp
        }
        return holder
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup?): View {
        val binding = DataBindingUtil.inflate<LoginItemBinding>(mainActivity.layoutInflater, layoutResId, parent, false)
        val view = binding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        return view
    }

    inner class LoginHolder(view: View) : BaseViewHolder(view) {
        val binding = itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as LoginItemBinding

        fun onItemSelected() {
            recyclerview.setLikedAt(adapterPosition)
            data[adapterPosition]?.setActive(itemView) ?: mainActivity.showToastError()
        }

        fun onLiked() {
            recyclerview.setLikedAt(adapterPosition)
            data[adapterPosition]?.setActive(itemView) ?: mainActivity.showToastError()
        }

        fun onUnLiked() {
            recyclerview.setLikedAt(adapterPosition)
            data[adapterPosition]?.setActive(itemView) ?: mainActivity.showToastError()
        }

        internal fun editItem() = data[adapterPosition]?.edit() ?: mainActivity.showToastError()
    }

    private fun Login.setActive(itemView: View) {
        viewModel.setActiveLogin(this@setActive)

        launch(UI) {
            itemView.isClickable = false
            delay(800)
            try {
                itemView.isClickable = true
            } catch (e: RuntimeException) { //ignore
            }
        }
    }

    private fun Login.edit() = mainActivity.showFragmentLoginEdit(login = this)

    private fun RecyclerView.setLikedAt(position: Int) {
        val size = data.size
        //unlike all buttons
        (0..size)
                .mapNotNull { findViewHolderForAdapterPosition(it) }
                .forEach { it.itemView.login_item_likeButton.isLiked = false }

        //like button at position
        (0..size)
                .mapNotNull { findViewHolderForAdapterPosition(it) }
                .forEach {
                    val likeButton = it.itemView.login_item_likeButton
                    if (it.adapterPosition == position) {
                        //workaround to getParser animation
                        likeButton.disable()
                        likeButton.performClick()
                        likeButton.enable(it as LoginHolder)
                    }
                }
    }

    private fun LikeButton.disable() {
        isSoundEffectsEnabled = false
        setOnLikeListener(null)
    }

    private fun LikeButton.enable(holder: LoginHolder) {
        isSoundEffectsEnabled = true
        setOnLikeListener(getOnLikeListener(holder))
    }

    private fun getOnLikeListener(holder: LoginHolder) = object : OnLikeListener {
        override fun liked(likeButton: LikeButton) {
            holder.onLiked()
        }

        override fun unLiked(likeButton: LikeButton) {
            holder.onUnLiked()
        }
    }

}





