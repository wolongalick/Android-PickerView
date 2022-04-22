package com.bigkoo.pickerview.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.ISelectTimeCallback;
import com.bigkoo.pickerview.listener.OnTimeSelectRangeListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间选择器
 * Created by Sai on 15/11/22.
 * Updated by XiaoSong on 2017-2-22.
 */
public class TimePickerViewRange extends BasePickerView implements View.OnClickListener {

    private static final String TAG = "TimePickerViewRange";
    private WheelTimeRange beginWheelTimeRange; //自定义控件
    private WheelTimeRange endWheelTimeRange; //自定义控件
    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    public OnTimeSelectRangeListener onTimeSelectRangeListener;

    public TimePickerViewRange(PickerOptions pickerOptions, PickerOptions pickerOptions2) {
        super(pickerOptions.context);
        mPickerOptions = pickerOptions;
        mPickerOptions2 = pickerOptions2;
        initView(pickerOptions.context);
    }

    private void initView(Context context) {
        setDialogOutSideCancelable();
        initViews();
        initAnim();

        if (mPickerOptions.customListener == null) {
            LayoutInflater.from(context).inflate(R.layout.pickerview_time_range, contentContainer);

            //顶部标题
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            RelativeLayout rv_top_bar = (RelativeLayout) findViewById(R.id.rv_topbar);

            //确定和取消按钮
            Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
            Button btnCancel = (Button) findViewById(R.id.btnCancel);

            btnSubmit.setTag(TAG_SUBMIT);
            btnCancel.setTag(TAG_CANCEL);

            btnSubmit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            //设置文字
            btnSubmit.setText(TextUtils.isEmpty(mPickerOptions.textContentConfirm) ? context.getResources().getString(R.string.pickerview_submit) : mPickerOptions.textContentConfirm);
            btnCancel.setText(TextUtils.isEmpty(mPickerOptions.textContentCancel) ? context.getResources().getString(R.string.pickerview_cancel) : mPickerOptions.textContentCancel);
            tvTitle.setText(TextUtils.isEmpty(mPickerOptions.textContentTitle) ? "" : mPickerOptions.textContentTitle);//默认为空

            //设置color
            btnSubmit.setTextColor(mPickerOptions.textColorConfirm);
            btnCancel.setTextColor(mPickerOptions.textColorCancel);
            tvTitle.setTextColor(mPickerOptions.textColorTitle);
            rv_top_bar.setBackgroundColor(mPickerOptions.bgColorTitle);

            //设置文字大小
            btnSubmit.setTextSize(mPickerOptions.textSizeSubmitCancel);
            btnCancel.setTextSize(mPickerOptions.textSizeSubmitCancel);
            tvTitle.setTextSize(mPickerOptions.textSizeTitle);

        } else {
            mPickerOptions.customListener.customLayout(LayoutInflater.from(context).inflate(mPickerOptions.layoutRes, contentContainer));
        }
        // 时间转轮 自定义控件
        YearMonthDayView beginDate = (YearMonthDayView) findViewById(R.id.beginDate);
        YearMonthDayView endDate = (YearMonthDayView) findViewById(R.id.endDate);

        beginDate.setBackgroundColor(mPickerOptions.bgColorWheel);
        endDate.setBackgroundColor(mPickerOptions.bgColorWheel);

        initWheelTime(beginDate, endDate);
    }

    private void initWheelTime(YearMonthDayView beginDate, YearMonthDayView endDate) {
        beginWheelTimeRange = new WheelTimeRange(beginDate, mPickerOptions.type, mPickerOptions.textGravity, mPickerOptions.textSizeContent);
        endWheelTimeRange = new WheelTimeRange(endDate, mPickerOptions2.type, mPickerOptions2.textGravity, mPickerOptions2.textSizeContent);
        if (mPickerOptions.timeSelectChangeListener != null) {
            beginWheelTimeRange.setSelectChangeCallback(new ISelectTimeCallback() {
                @Override
                public void onTimeSelectChanged() {
                    try {
                        Date date = WheelTime.dateFormat.parse(beginWheelTimeRange.getTime());
                        mPickerOptions.timeSelectChangeListener.onTimeSelectChanged(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        beginWheelTimeRange.setLunarMode(mPickerOptions.isLunarCalendar);
        endWheelTimeRange.setLunarMode(mPickerOptions2.isLunarCalendar);

        if (mPickerOptions.startYear != 0 && mPickerOptions.endYear != 0
                && mPickerOptions.startYear <= mPickerOptions.endYear) {
            setRange(beginWheelTimeRange, mPickerOptions);
        }

        if (mPickerOptions2.startYear != 0 && mPickerOptions2.endYear != 0
                && mPickerOptions2.startYear <= mPickerOptions2.endYear) {
            setRange(endWheelTimeRange, mPickerOptions2);
        }

        preSetRangDate(beginWheelTimeRange, mPickerOptions);
        preSetRangDate(endWheelTimeRange, mPickerOptions2);

        setTime(beginWheelTimeRange, mPickerOptions);
        setTime(endWheelTimeRange, mPickerOptions2);

        setWheelUI(beginWheelTimeRange, mPickerOptions);
        setWheelUI(endWheelTimeRange, mPickerOptions2);
    }

    private void preSetRangDate(WheelTimeRange wheelTimeRange, PickerOptions pickerOptions) {
        //若手动设置了时间范围限制
        if (pickerOptions.startDate != null && pickerOptions.endDate != null) {
            if (pickerOptions.startDate.getTimeInMillis() > pickerOptions.endDate.getTimeInMillis()) {
                throw new IllegalArgumentException("startDate can't be later than endDate");
            } else {
                setRangDate(wheelTimeRange, pickerOptions);
            }
        } else if (pickerOptions.startDate != null) {
            if (pickerOptions.startDate.get(Calendar.YEAR) < 1900) {
                throw new IllegalArgumentException("The startDate can not as early as 1900");
            } else {
                setRangDate(wheelTimeRange, pickerOptions);
            }
        } else if (pickerOptions.endDate != null) {
            if (pickerOptions.endDate.get(Calendar.YEAR) > 2100) {
                throw new IllegalArgumentException("The endDate should not be later than 2100");
            } else {
                setRangDate(wheelTimeRange, pickerOptions);
            }
        } else {//没有设置时间范围限制，则会使用默认范围。
            setRangDate(wheelTimeRange, pickerOptions);
        }
    }

    private void setWheelUI(WheelTimeRange wheelTimeRange, PickerOptions pickerOptions) {
        wheelTimeRange.setLabels(pickerOptions.label_year, pickerOptions.label_month, pickerOptions.label_day
                , pickerOptions.label_hours, pickerOptions.label_minutes, pickerOptions.label_seconds);
        wheelTimeRange.setTextXOffset(pickerOptions.x_offset_year, pickerOptions.x_offset_month, pickerOptions.x_offset_day,
                pickerOptions.x_offset_hours, pickerOptions.x_offset_minutes, pickerOptions.x_offset_seconds);
        wheelTimeRange.setItemsVisible(pickerOptions.itemsVisibleCount);
        wheelTimeRange.setAlphaGradient(pickerOptions.isAlphaGradient);
        setOutSideCancelable(pickerOptions.cancelable);
        wheelTimeRange.setCyclic(pickerOptions.cyclic);
        wheelTimeRange.setDividerColor(pickerOptions.dividerColor);
        wheelTimeRange.setDividerType(pickerOptions.dividerType);
        wheelTimeRange.setLineSpacingMultiplier(pickerOptions.lineSpacingMultiplier);
        wheelTimeRange.setTextColorOut(pickerOptions.textColorOut);
        wheelTimeRange.setTextColorCenter(pickerOptions.textColorCenter);
        wheelTimeRange.isCenterLabel(pickerOptions.isCenterLabel);
    }


    /**
     * 设置默认时间
     */
    public void setDate(Calendar date) {
        mPickerOptions.date = date;
        mPickerOptions2.date = date;
        setTime(beginWheelTimeRange, mPickerOptions);
        setTime(endWheelTimeRange, mPickerOptions2);
    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void setRange(WheelTimeRange wheelTimeRange, PickerOptions pickerOptions) {
        wheelTimeRange.setStartYear(pickerOptions.startYear);
        wheelTimeRange.setEndYear(pickerOptions.endYear);

    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void setRangDate(WheelTimeRange wheelTimeRange, PickerOptions pickerOptions) {
        wheelTimeRange.setRangDate(pickerOptions.startDate, pickerOptions.endDate);
        initDefaultSelectedDate(pickerOptions);
    }


    private void initDefaultSelectedDate(PickerOptions mPickerOptions) {
        if (mPickerOptions == null) {
            return;
        }
        //如果手动设置了时间范围
        if (mPickerOptions.startDate != null && mPickerOptions.endDate != null) {
            //若默认时间未设置，或者设置的默认时间越界了，则设置默认选中时间为开始时间。
            if (mPickerOptions.date == null || mPickerOptions.date.getTimeInMillis() < mPickerOptions.startDate.getTimeInMillis()
                    || mPickerOptions.date.getTimeInMillis() > mPickerOptions.endDate.getTimeInMillis()) {
                mPickerOptions.date = mPickerOptions.startDate;
            }
        } else if (mPickerOptions.startDate != null) {
            //没有设置默认选中时间,那就拿开始时间当默认时间
            mPickerOptions.date = mPickerOptions.startDate;
        } else if (mPickerOptions.endDate != null) {
            mPickerOptions.date = mPickerOptions.endDate;
        }
    }

    /**
     * 设置选中时间,默认选中当前时间
     */
    private void setTime(WheelTimeRange wheelTimeRange, PickerOptions mPickerOptions) {
        int year, month, day, hours, minute, seconds;
        Calendar calendar = Calendar.getInstance();

        if (mPickerOptions.date == null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
        } else {
            year = mPickerOptions.date.get(Calendar.YEAR);
            month = mPickerOptions.date.get(Calendar.MONTH);
            day = mPickerOptions.date.get(Calendar.DAY_OF_MONTH);
            hours = mPickerOptions.date.get(Calendar.HOUR_OF_DAY);
            minute = mPickerOptions.date.get(Calendar.MINUTE);
            seconds = mPickerOptions.date.get(Calendar.SECOND);
        }

        wheelTimeRange.setPicker(year, month, day, hours, minute, seconds);
    }


    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_SUBMIT)) {
            if(returnData()){
                dismiss();
            }
        } else if (tag.equals(TAG_CANCEL)) {
            if (mPickerOptions.cancelListener != null) {
                mPickerOptions.cancelListener.onClick(v);
            }
            dismiss();
        }

    }

    public boolean returnData() {
        if (onTimeSelectRangeListener != null) {
            try {
                String beginTime = beginWheelTimeRange.getTime();
                String endTime = endWheelTimeRange.getTime();

                if(beginTime.compareTo(endTime)>0){
                    Toast.makeText(beginWheelTimeRange.getView().getContext(),"开始时间不能晚于结束时间",Toast.LENGTH_SHORT).show();
                    return false;
                }


                endTime += " 23:59:59";

                Log.i(TAG, "开始时间:" + beginTime);
                Log.i(TAG, "结束时间:" + endTime);
                Date beginDate = WheelTime.dateFormatDate.parse(beginTime);
                Date endDate = WheelTime.dateFormat.parse(endTime);
                onTimeSelectRangeListener.onTimeSelect(beginDate, endDate);
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 动态设置标题
     *
     * @param text 标题文本内容
     */
    public void setTitleText(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }

    /**
     * 目前暂时只支持设置1900 - 2100年
     *
     * @param lunar 农历的开关
     */
    public void setLunarCalendar(boolean lunar) {
        try {
            int year, month, day, hours, minute, seconds;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(WheelTime.dateFormat.parse(beginWheelTimeRange.getTime()));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);

            beginWheelTimeRange.setLunarMode(lunar);
            beginWheelTimeRange.setLabels(mPickerOptions.label_year, mPickerOptions.label_month, mPickerOptions.label_day,
                    mPickerOptions.label_hours, mPickerOptions.label_minutes, mPickerOptions.label_seconds);
            beginWheelTimeRange.setPicker(year, month, day, hours, minute, seconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isLunarCalendar() {
        return beginWheelTimeRange.isLunarMode();
    }


    @Override
    public boolean isDialog() {
        return mPickerOptions.isDialog;
    }
}
