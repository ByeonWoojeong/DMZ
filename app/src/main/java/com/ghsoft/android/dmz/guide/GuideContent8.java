package com.ghsoft.android.dmz.guide;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghsoft.android.dmz.R;

/**
 * Created by User on 2017-09-01.
 */

public class GuideContent8 extends android.support.v4.app.Fragment {
    View.OnClickListener onClickListener;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    static CheckBox check;

    public GuideContent8() {
    }

    @SuppressLint("ValidFragment")
    public GuideContent8(View.OnClickListener onClickListener, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.onClickListener = onClickListener;
        this.checkedChangeListener = checkedChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_8, container, false);
        LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);

        int color = ContextCompat.getColor(getActivity(), R.color.text_red);

        TextView guide8_text = (TextView) view.findViewById(R.id.guide8_text);
        String string = "스폿과 로드 페이지에서는\n내용을 편안히 들을 수 있는\n도슨트도 제공합니다.";
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        builder.setSpan(new ForegroundColorSpan(color), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), 4, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), 30, 33, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        guide8_text.append(builder);

        Button startBtn = (Button) view.findViewById(R.id.startBtn);


        try {
            GradientDrawable bgShape = (GradientDrawable) startBtn.getBackground();
            int start_color = ContextCompat.getColor(getActivity(), R.color.guide_start_color);
            bgShape.setColor(start_color);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startBtn.setOnClickListener(onClickListener);

        check = (CheckBox) view.findViewById(R.id.check);
        check.setOnCheckedChangeListener(checkedChangeListener);
        return view;
    }

    public Drawable GetDrawable(int drawableResId, int color) {
        Drawable drawable = getResources().getDrawable(drawableResId);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
