package com.route.todoappc40gsunwed.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.WeekDayBinder
import com.route.todoappc40gsunwed.R
import com.route.todoappc40gsunwed.WeekDayViewContainer
import com.route.todoappc40gsunwed.adapter.TaskAdapter
import com.route.todoappc40gsunwed.clearTime
import com.route.todoappc40gsunwed.database.TaskDatabase
import com.route.todoappc40gsunwed.databinding.FragmentTodoListBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TodoListFragment : Fragment() {
    lateinit var binding: FragmentTodoListBinding
    lateinit var adapter: TaskAdapter
    private val EDIT_TASK_REQUEST_CODE = 1
    var selectedDate: LocalDate? = null
    lateinit var calendar: Calendar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        initWeekCalendarView()
        getTasksFromDatabase()

    }

    fun getTasksFromDatabase() {
        val tasks = TaskDatabase.getInstance(requireContext())
            .getTaskDao().getAllTasks()
        adapter = TaskAdapter(tasks.toMutableList())
        binding.tasksRecyclerView.adapter = adapter
    }

    fun initWeekCalendarView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bindWeekCalendarView()
            val currentDate = LocalDate.now()
            //getItemCount > 0

            val currentMonth = YearMonth.now()
            val startDate = currentMonth.minusMonths(100).atStartOfMonth() // Adjust as needed
            val endDate = currentMonth.plusMonths(100).atEndOfMonth() // Adjust as needed
            val firstDayOfWeek =
                firstDayOfWeekFromLocale(Locale.forLanguageTag("ar")) // Available from the library
            binding.weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
            binding.weekCalendarView.scrollToWeek(currentDate)
        }
    }

    fun bindWeekCalendarView() {
        binding.weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {

            override fun bind(container: WeekDayViewContainer, data: WeekDay) { // Wed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val blue = ResourcesCompat.getColor(resources, R.color.blue, null)
                    val black = ResourcesCompat.getColor(resources, R.color.black, null)
                    container.weekDayTextView.text = data.date.dayOfWeek.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    )
                    container.dayInMonthTextView.text = "${data.date.dayOfMonth}"
                    if (data.date == selectedDate) {
                        container.dayInMonthTextView.setTextColor(blue)
                        container.weekDayTextView.setTextColor(blue)
                    } else {
                        container.dayInMonthTextView.setTextColor(black)
                        container.weekDayTextView.setTextColor(black)
                    }
                    container.view.setOnClickListener {
                        Log.e("TAG", "bind:calendar Month ${calendar.get(Calendar.MONTH)}")
                        Log.e("TAG", "bind: Local Date Month ${data.date.month.value}")
                        selectedDate = data.date
                        binding.weekCalendarView.notifyWeekChanged(data)
                        val date = data.date
                        calendar.set(Calendar.YEAR, date.year)
                        calendar.set(Calendar.MONTH, date.month.value - 1)
                        calendar.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                        calendar.clearTime()
                        getTasksByDate(calendar.time)

                    }
                }
            }

            override fun create(view: View): WeekDayViewContainer {
                return WeekDayViewContainer(view)
            }

        }
        binding.weekCalendarView.weekScrollListener = { week: Week ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val month =
                    week.days.get(0).date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                binding.monthNameText.text = month
            }
        }
    }

    fun getTasksByDate(date: Date) {
        val tasks = TaskDatabase.getInstance(requireContext())
            .getTaskDao().getTasksByDate(date)
        adapter.updateList(tasks)
    }
}
