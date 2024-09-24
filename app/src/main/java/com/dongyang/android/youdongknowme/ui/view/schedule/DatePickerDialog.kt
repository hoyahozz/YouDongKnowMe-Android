package com.dongyang.android.youdongknowme.ui.view.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.activityViewModels
import com.dongyang.android.youdongknowme.R
import com.dongyang.android.youdongknowme.databinding.DialogDatepickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.threeten.bp.LocalDate

class DatePickerDialog(
    private val year: Int,
    private val month: Int,
    private val listener: ScheduleClickListener
) : BottomSheetDialogFragment() {

    private var _binding: DialogDatepickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = DialogDatepickerBinding.inflate(inflater, container, false)
        val view = binding.root

        // 현재 연도
        val currentYear = LocalDate.now().year

        // 순환 막기
        binding.numberpickerDialogDatepickerYear.wrapSelectorWheel = false
        binding.numberpickerDialogDatepickerMonth.wrapSelectorWheel = false

        // editText 설정 막기
        binding.numberpickerDialogDatepickerYear.descendantFocusability =
            NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.numberpickerDialogDatepickerMonth.descendantFocusability =
            NumberPicker.FOCUS_BLOCK_DESCENDANTS

        // 연도 최소/최대값 설정 및 출력 방식 설정
        with(binding.numberpickerDialogDatepickerYear) {
            minValue = currentYear - 1
            maxValue = currentYear + 1
            displayedValues =
                ((minValue..maxValue).map { "$it${getString(R.string.calendar_year)}" }
                    .toTypedArray())
        }

        // 월 최소/최대값 설정 및 출력 방식 설정
        with(binding.numberpickerDialogDatepickerMonth) {
            minValue = 1
            maxValue = if (year == currentYear + 1) 2 else 12

            // 연도 선택에 따른 월의 최대값 동적 설정
            binding.numberpickerDialogDatepickerYear.setOnValueChangedListener { _, _, newYear ->
                maxValue = if (newYear == currentYear + 1) 2 else 12
            }

            displayedValues =
                ((minValue..maxValue).map { "$it${getString(R.string.calendar_month)}" }
                    .toTypedArray())
        }

        // 초기 설정
        binding.numberpickerDialogDatepickerYear.value = year
        binding.numberpickerDialogDatepickerMonth.value = month

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.tvDialogPermissionComplete.setOnClickListener {
            val year = binding.numberpickerDialogDatepickerYear.value
            val month = binding.numberpickerDialogDatepickerMonth.value
            val date = CalendarDay.from(year, month, 1)

            listener.buttonClick(date = date)

            dismiss()
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
    }
}