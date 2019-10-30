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

public class GuideContent5 extends android.support.v4.app.Fragment {
    View.OnClickListener onClickListener;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    static CheckBox check;

    public GuideContent5() {
    }

    @SuppressLint("ValidFragment")
    public GuideContent5(View.OnClickListener onClickListener, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.onClickListener = onClickListener;
        this.checkedChangeListener = checkedChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_5, container, false);
        LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);

        int color = ContextCompat.getColor(getActivity(), R.color.text_red);

        TextView guide5_text = (TextView) view.findViewById(R.id.guide5_text);
        String string = "공지, 앱 설정, GPS 알림\n등을 확인 및 수정\n할 수 있습니다.";
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        builder.setSpan(new ForegroundColorSpan(color), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), 4, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), 10, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        guide5_text.append(builder);

        check = (CheckBox) view.findViewById(R.id.check);
        check.setOnCheckedChangeListener(checkedChangeListener);
        return view;
    }
}
