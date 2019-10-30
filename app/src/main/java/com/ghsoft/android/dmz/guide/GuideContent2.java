package com.ghsoft.android.dmz.guide;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghsoft.android.dmz.R;

/**
 * Created by User on 2017-09-01.
 */

public class GuideContent2 extends android.support.v4.app.Fragment {
    View.OnClickListener onClickListener;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    static CheckBox check;
    public GuideContent2() {
    }

    @SuppressLint("ValidFragment")
    public GuideContent2(View.OnClickListener onClickListener, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.onClickListener = onClickListener;
        this.checkedChangeListener = checkedChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_2, container, false);
        LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);

        int life_color = ContextCompat.getColor(getActivity(), R.color.life_color);
        int peace_color = ContextCompat.getColor(getActivity(), R.color.peace_color);
        int healing_color = ContextCompat.getColor(getActivity(), R.color.healing_color);

        TextView guide2_text = (TextView) view.findViewById(R.id.guide2_text);
        String string = "생명, 평화, 치유로\n연결된 ROAD 人 DMZ의\n추천 코스!";
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        builder.setSpan(new ForegroundColorSpan(life_color), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(peace_color), 4, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(healing_color), 8, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        guide2_text.append(builder);

        check = (CheckBox) view.findViewById(R.id.check);
        check.setOnCheckedChangeListener(checkedChangeListener);
        return view;
    }
}
