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

public class GuideContent4 extends android.support.v4.app.Fragment {
    View.OnClickListener onClickListener;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    static CheckBox check;

    public GuideContent4() {
    }

    @SuppressLint("ValidFragment")
    public GuideContent4(View.OnClickListener onClickListener, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.onClickListener = onClickListener;
        this.checkedChangeListener = checkedChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_4, container, false);
        LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);

        int color = ContextCompat.getColor(getActivity(), R.color.text_red);

        TextView guide4_text = (TextView) view.findViewById(R.id.guide4_text);
        String string = "이름, 지역, 키워드를\n이용해 직접 검색도\n할 수 있습니다.";
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        builder.setSpan(new ForegroundColorSpan(color), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), 4, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), 8, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        guide4_text.append(builder);

        check = (CheckBox) view.findViewById(R.id.check);
        check.setOnCheckedChangeListener(checkedChangeListener);
        return view;
    }
}
