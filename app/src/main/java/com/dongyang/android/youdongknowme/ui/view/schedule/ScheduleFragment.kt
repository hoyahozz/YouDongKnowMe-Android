package com.dongyang.android.youdongknowme.ui.view.schedule

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dongyang.android.youdongknowme.R
import com.dongyang.android.youdongknowme.databinding.FragmentScheduleBinding
import com.dongyang.android.youdongknowme.standard.base.BaseFragment
import com.dongyang.android.youdongknowme.ui.adapter.ScheduleAdapter
import com.dongyang.android.youdongknowme.ui.view.util.EventObserver
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDate

/* 학사 일정 화면 */
class ScheduleFragment : BaseFragment<FragmentScheduleBinding, ScheduleViewModel>(),
    ScheduleClickListener {

    override val layoutResourceId: Int = R.layout.fragment_schedule
    override val viewModel: ScheduleViewModel by viewModel()

    private lateinit var adapter: ScheduleAdapter

    private var _year = LocalDate.now().year.toString()
    private var year = _year

    private var _month = LocalDate.now().month.toString()
    private var month = _month

    override fun buttonClick(date: CalendarDay) {
        viewModel.setPickedDate(date)
    }

    // 연/월 방식으로 타이틀 처리
    fun setCalenderHeader() {
        binding.mvScheduleCalendar.setTitleFormatter { day ->
            val inputText: LocalDate = day.date
            val calendarHeaderElements = inputText.toString().split("-").toTypedArray()

            year = calendarHeaderElements[0]
            month = calendarHeaderElements[1]

            val calendarHeaderBuilder = StringBuilder()
            calendarHeaderBuilder.append(year)
                .append(getString(R.string.calendar_year))
                .append(" ")
                .append(month)
                .append(getString(R.string.calendar_month))
            calendarHeaderBuilder.toString()
        }
    }

    override fun initStartView() {

        binding.vm = viewModel

        viewModel.setPickedDate(binding.mvScheduleCalendar.currentDate)

        binding.mvScheduleCalendar.leftArrow.setTintList(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.blue300
                )
            )
        )
        binding.mvScheduleCalendar.rightArrow.setTintList(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.blue300
                )
            )
        )

        adapter = ScheduleAdapter()
        binding.rvScheduleList.apply {
            this.adapter = this@ScheduleFragment.adapter
            this.layoutManager = LinearLayoutManager(requireActivity())
            this.setHasFixedSize(true)
            this.addItemDecoration(DividerItemDecoration(requireActivity(), 1))
        }
    }

    override fun initDataBinding() {
        viewModel.errorState.observe(viewLifecycleOwner, EventObserver { resId ->
            showToast(getString(resId))
        })

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading()
            else dismissLoading()
        }

        viewModel.pickYear.observe(viewLifecycleOwner) {
            viewModel.getSchedules()
        }

        viewModel.pickMonth.observe(viewLifecycleOwner) {
            viewModel.getSchedules()
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            updateCalendarViewHeader(date)
        }

        viewModel.scheduleList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.scheduleErrorContainer.refresh.setOnClickListener {
            viewModel.getSchedules()
        }
    }

    private fun updateCalendarViewHeader(date: CalendarDay) {
        binding.mvScheduleCalendar.setCurrentDate(date)

        setCalenderHeader()
    }


    override fun initAfterBinding() {

        binding.mvScheduleCalendar.setOnMonthChangedListener { _, date ->
            viewModel.setPickedDate(date)
        }

        binding.mvScheduleCalendar.setOnTitleClickListener {
            val dialog = DatePickerDialog(
                year = year.toInt(),
                month = month.toInt(),
                listener = this@ScheduleFragment
            )
            dialog.show(requireActivity().supportFragmentManager, "CustomDialog")
        }

        // 최소 날짜, 최대 날짜 지정
        binding.mvScheduleCalendar.apply {
            this.state().edit().setMinimumDate(CalendarDay.from(2023, 1, 1))
                .setMaximumDate(CalendarDay.from(2025, 2, 28))
                .commit()
        }

        setCalenderHeader()
    }
}
