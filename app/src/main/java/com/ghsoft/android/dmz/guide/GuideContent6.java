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

public class GuideContent6 extends android.support.v4.app.Fragment {
    View.OnClickListener onClickListener;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    static CheckBox check;

    public GuideContent6() {
    }

    @SuppressLint("ValidFragment")
    public GuideContent6(View.OnClickListener onClickListener, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.onClickListener = onClickListener;
        this.checkedChangeListener = checkedChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_6, container, false);
        LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);

        int color = ContextCompat.getColor(getActivity(), R.color.text_red);

        TextView guide6_text = (TextView) view.findViewById(R.id.guide6_text);
        String string = "생명, 평화, 치유로 연결된\n로드 스토리텔링 페이지입니다.\n\n지도보기를 누르면\n로드에 포함된 스폿들을\n볼 수 있습니다.";
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        builder.setSpan(new ForegroundColorSpan(color), 16, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), 52, 54, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        guide6_text.append(builder);

        check = (CheckBox) view.findViewById(R.id.check);
        check.setOnCheckedChangeListener(checkedChangeListener);
        return view;
    }
}
