package com.bigkoo.pickerviewdemo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.bigkoo.pickerview.configure.PickerOptions
import com.bigkoo.pickerview.listener.OnTimeSelectRangeListener
import com.bigkoo.pickerview.utils.LunarCalendar
import com.bigkoo.pickerview.view.TimePickerViewRange
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @createTime 2022/4/22 16:22
 * @author 崔兴旺  1607009565@qq.com
 * @description 选择日期范围的activity
 */
class SelectDateRangeActivity : AppCompatActivity() {
    private val FORMAT1 = "yyyy-MM-dd HH:mm:ss";
    private val FORMAT2 = "yyyy-MM-dd"
    private var beginFollowDate: Long? = null
    private var endFollowDate: Long? = null

    private val btnSelectDateRange by lazy {
        findViewById<Button>(R.id.btnSelectDateRange)
    }
    private val tvResult by lazy {
        findViewById<TextView>(R.id.tvResult)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_date_range)

        btnSelectDateRange.setOnClickListener {
            showDateRangePickerView(
                this,
                "请选择时间段",
                Color.parseColor("#3E7FEE"),
                Color.parseColor("#232833"),
                leftSelectTime = beginFollowDate ?: 0,
                rightSelectTime = endFollowDate ?: 0,
            ) { beginDate: Date, endDate: Date ->
                beginFollowDate = beginDate.time
                endFollowDate = endDate.time
                tvResult.text = "${parseLongToString(beginDate.time, FORMAT2)} 至 ${parseLongToString(endDate.time, FORMAT2)}"
                tvResult
            }
        }
    }

    private fun showDateRangePickerView(
        context: Context,
        title: String,
        @ColorInt operationColor: Int,
        @ColorInt titleColor: Int,
        leftSelectTime: Long = 0L,//时间戳单位:毫秒
        rightSelectTime: Long = 0L,//时间戳单位:毫秒
        mStartDate: Long = 0L,//时间戳单位:毫秒
        mEndDate: Long = 0L,//时间戳单位:毫秒
        onTimeSelectRangeListener: OnTimeSelectRangeListener,
    ) {
        val leftSelectCalendar = Calendar.getInstance()
        leftSelectCalendar.timeInMillis = if (leftSelectTime == 0L) {
            if (mEndDate != 0L) {
                mEndDate
            } else {
                System.currentTimeMillis()
            }
        } else {
            leftSelectTime
        }

        val rightSelectCalendar = Calendar.getInstance()
        rightSelectCalendar.timeInMillis = if (rightSelectTime == 0L) {
            if (mEndDate != 0L) {
                mEndDate
            } else {
                System.currentTimeMillis()
            }
        } else {
            rightSelectTime
        }

        val beginPickerOptions = createPickerOptions(title, context, operationColor, titleColor, mStartDate, mEndDate)
        beginPickerOptions.date = leftSelectCalendar

        val endPickerOptions = createPickerOptions(title, context, operationColor, titleColor, mStartDate, mEndDate)
        endPickerOptions.date = rightSelectCalendar

        val timePickerView = TimePickerViewRange(beginPickerOptions, endPickerOptions)
        timePickerView.onTimeSelectRangeListener = onTimeSelectRangeListener
        val params = FrameLayout.LayoutParams(
            (getScreenWidth() / 3f).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM
        )
        timePickerView.dialogContainerLayout.layoutParams = params
        timePickerView.show()
    }

    private fun createPickerOptions(
        title: String,
        context: Context,
        operationColor: Int,
        titleColor: Int,
        mStartDate: Long,
        mEndDate: Long
    ): PickerOptions {
        val pickerOptions = PickerOptions(PickerOptions.TYPE_PICKER_TIME_RANGE)
        pickerOptions.textContentCancel = "取消"
        pickerOptions.textContentConfirm = "完成"
        pickerOptions.textContentTitle = title
        pickerOptions.context = context
        pickerOptions.textColorCancel = operationColor
        pickerOptions.textColorConfirm = operationColor
        pickerOptions.textColorTitle = titleColor
        pickerOptions.textSizeSubmitCancel = 18
        pickerOptions.textSizeTitle = 18
        if (mStartDate > 0) {
            pickerOptions.startDate = Calendar.getInstance().apply {
                timeInMillis = mStartDate
            }
        } else {
            pickerOptions.startDate = Calendar.getInstance().apply {
                timeInMillis = parseStringToMillis(
                    "${LunarCalendar.MIN_YEAR}-01-01 00:00:01",
                    FORMAT1
                )
            }
        }
        if (mEndDate > 0) {
            pickerOptions.endDate = Calendar.getInstance().apply {
                timeInMillis = mEndDate
            }
        } else {
            pickerOptions.endDate = Calendar.getInstance().apply {
                timeInMillis = parseStringToMillis(
                    "${LunarCalendar.MAX_YEAR}-21-31 23:59:59",
                    FORMAT1
                )
            }
        }
        pickerOptions.isDialog = true
        pickerOptions.bgColorTitle = Color.parseColor("#EEF4FF")
        return pickerOptions
    }

    private fun getScreenWidth(): Int {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    private fun parseLongToString(time: Long, formater: String?): String? {
        if (time == 0L) {
            return ""
        }
        val format = SimpleDateFormat(formater)
        return format.format(Date(time))
    }

    /**
     * 根据字符串时间格式转换系统时间戳
     *
     * @param strTime
     * @return modify by zhang
     */
    private fun parseStringToMillis(strTime: String?, formatStr: String?): Long {
        val format = SimpleDateFormat(formatStr)
        try {
            return if (strTime != null && !strTime.isEmpty()) {
                val d = format.parse(strTime)
                d.time
            } else {
                0
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }
}