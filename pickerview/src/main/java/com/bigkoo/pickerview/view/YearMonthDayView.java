package com.bigkoo.pickerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.R;

/**
 * @author 崔兴旺  1607009565@qq.com
 * @createTime 2022/4/13 11:53
 * @description
 */
public class YearMonthDayView extends LinearLayout {
    public YearMonthDayView(Context context) {
        this(context, null);
    }

    public YearMonthDayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YearMonthDayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_year_month_day, this);
        initViews();

    }

    private void initViews() {

    }


}