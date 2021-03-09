package com.sdk.samples.topics

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.utils.chatForm.defs.DataKeys
import com.integration.core.Department
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.SingleLiveData
import com.nanorep.convesationui.structure.controller.ChatAvailability
import com.nanorep.sdkcore.utils.Event
import com.nanorep.sdkcore.utils.snack
import com.sdk.samples.R
import com.sdk.samples.databinding.BoldAvailabilityBinding

class CheckAvailabilityViewModel : ViewModel() {

    var account: BoldAccount = BoldAccount()

    private var results = SingleLiveData<ChatAvailability.AvailabilityResult>()
    fun observeResults(
        owner: LifecycleOwner,
        observer: Observer<ChatAvailability.AvailabilityResult?>
    ) {
        results.observe(owner, observer)
    }

    fun onResults(results: ChatAvailability.AvailabilityResult) {
        this.results.value = results
    }

    private var refresh = SingleLiveData<Event?>()
    fun observeRefresh(owner: LifecycleOwner, observer: Observer<Event?>) {
        refresh.observe(owner, observer)
    }

    fun refresh(event: Event) {
        refresh.value = event
    }
}


class BoldAvailability : Fragment() {

    companion object {
        const val TAG = "AvailabilityFragment"
        fun newInstance() = BoldAvailability()
    }

    private val viewModel: CheckAvailabilityViewModel? by lazy {
        activity?.let { ViewModelProvider(it).get(CheckAvailabilityViewModel::class.java) }
    }

    private lateinit var binding: BoldAvailabilityBinding

    private var chipUncheckedIcon: Drawable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BoldAvailabilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val departmentAdapter = DepartmentAdapter()

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            resetChip()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.departmentsRecycler.apply {
            adapter = departmentAdapter

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.showDepartments.apply {
            setOnClickListener {
                if (isActivated) {
                    deactivateDepartments()
                } else {
                    activateDepartments()
                }
            }
        }

        chipUncheckedIcon = binding.availabilityStatus.closeIcon

        binding.availabilityStatus.apply {

            closeIcon = ContextCompat.getDrawable(context, R.drawable.chat_channel)

            setOnCloseIconClickListener { _ ->

                val callback = object : ChatAvailability.Callback {
                    override fun onComplete(result: ChatAvailability.AvailabilityResult) {
                        if (context == null || !isAdded) return // in case fragment was closed by receiving the response

                        binding.availabilityStatus.apply {
                            isSelected = result.isAvailable
                            if (isSelected) {
                                chipIcon = checkedIcon
                                text = getString(R.string.start_chat)
                            } else {
                                chipIcon = chipUncheckedIcon
                                text = getString(R.string.chat_with_agent)
                            }

                            tag = result
                        }

                        result.reason?.run {
                            binding.availabilityStatus.snack(
                                "chat is not available due to $this",
                                backgroundColor = Color.DKGRAY,
                                disableSwipes = false
                            )
                        }
                    }
                }

                val account = viewModel?.account

                if (account == null) {

                    Log.e(TAG, ">>> account is null!!")

                    callback.onComplete(
                        ChatAvailability.AvailabilityResult(
                            "",
                            isAvailable = false
                        )
                    )

                } else {

                    val departmentId = (binding.departmentsRecycler.adapter as DepartmentAdapter)
                        .takeIf { it.selectedDepartment > -1 }
                        ?.let { it.getItemId(it.selectedDepartment) }
                        ?: 0L

                    ChatAvailability.checkAvailability(account, departmentId, callback)
                }
            }

            setOnClickListener {
                (binding.availabilityStatus.tag as? ChatAvailability.AvailabilityResult)?.run {
                    /* !! live chat can start only with departments that were configured to the ChatWindow
                        in prechat form configurations. unless skip prechat was applied. */
                    viewModel?.onResults(this)
                }
            }

            viewModel?.observeRefresh(viewLifecycleOwner, Observer { event ->
                event?.run {
                    resetChip()
                }
            })
        }

    }

    private fun activateDepartments() {
        viewModel?.run {
            ChatAvailability.availableDepartments(
                account,
                object : ChatAvailability.DepartmentsCallback {

                    override fun onComplete(result: ChatAvailability.DepartmentsResult) {

                        result.data?.takeIf { it.isNotEmpty() }?.apply {
                            binding.instruction.visibility = View.GONE
                            binding.departmentsLayout.visibility = View.VISIBLE
                            binding.showDepartments.apply {
                                text = getString(R.string.hide_departments)
                                isActivated = true
                            }

                            departmentAdapter.setDepartments(this)
                        }
                    }

                })
        }
    }

    private fun deactivateDepartments() {
        binding.instruction.visibility = View.VISIBLE
        binding.departmentsLayout.visibility = View.GONE
        departmentAdapter.clearSelected()
        binding.showDepartments.apply {
            text = getString(R.string.show_departments)
            isActivated = false
        }
        viewModel?.account?.removeExtraData(DataKeys.Department)
        resetChip()
    }

    private fun resetChip() {
        Log.e(TAG, "Reset availability state")

        binding.availabilityStatus.performCloseIconClick()
       /* availability_status.apply {
            isSelected = false
            chipIcon = null
            text = getString(R.string.chat_with_agent)
        }*/
    }

    override fun onStop() {
        super.onStop()
        try {
            departmentAdapter.unregisterAdapterDataObserver(adapterDataObserver)
        } catch (ignored: IllegalStateException) { }
    }

    override fun onStart() {
        super.onStart()

        if (this::binding.isInitialized) {
            binding.departmentsRecycler.adapter?.registerAdapterDataObserver(adapterDataObserver)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.availabilityStatus.performCloseIconClick() // starts with the current availability state
    }

}


class DepartmentAdapter(private var departments: List<Department> = listOf()) :
    RecyclerView.Adapter<DepartmentViewHolder>() {

    var selectedDepartment: Int = -1
    private val selectionListener: (position: Int) -> Unit = { position ->
        selectedDepartment = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.deprtment_view, parent, false)
        return DepartmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        holder.bind(departments[position], position == selectedDepartment, selectionListener)
    }

    override fun getItemCount(): Int {
        return departments.size
    }

    override fun getItemId(position: Int): Long {
        return position.takeIf { (0..departments.size).contains(it) }?.let { departments[it].id }
            ?.toLong()
            ?: 0
    }

    fun setDepartments(departmentsList: List<Department>) {
        this.departments = listOf(Department("No department", "0")).plus(departmentsList)
        selectedDepartment = 0
        notifyDataSetChanged()
    }

    fun clearSelected() {
        val curr = selectedDepartment
        selectedDepartment = -1
        if (curr > -1) {
            notifyItemChanged(curr)
        }
    }

}

class DepartmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun bind(department: Department, isSelected: Boolean, onSelection: (Int) -> Unit) {
        val radio: AppCompatRadioButton = itemView.findViewById(R.id.department_radio)

        radio.text = department.name

        radio.isChecked = isSelected

        radio.setOnClickListener {
            onSelection(adapterPosition)
        }
    }
}

