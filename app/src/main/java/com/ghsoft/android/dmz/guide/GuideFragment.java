package com.ghsoft.android.dmz.guide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.ghsoft.android.dmz.R;

/**
 * Created by User on 2017-09-01.
 */

public class GuideFragment extends Fragment {
    ViewPager viewPager;
    private FragmentActivity myContext;
    View.OnClickListener onClickListener;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    SharedPreferences pref;
    SharedPreferences.Editor prefEd;

    public GuideFragment() {
    }

    @SuppressLint("ValidFragment")
    public GuideFragment(View.OnClickListener onClickListener, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.onClickListener = onClickListener;
        this.checkedChangeListener = checkedChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        FragmentManager fragManager = myContext.getSupportFragmentManager();
        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        prefEd = pref.edit();
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(8);
        viewPager.setAdapter(new PagerAdapter(fragManager));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                prefEd.putBoolean("GuideCheck", false);
                prefEd.commit();
                switch (position) {
                    case 0:
                        GuideContent1.check.setChecked(false);
                        break;
                    case 1:
                        GuideContent2.check.setChecked(false);
                        break;
                    case 2:
                        GuideContent3.check.setChecked(false);
                        break;
                    case 3:
                        GuideContent4.check.setChecked(false);
                        break;
                    case 4:
                        GuideContent5.check.setChecked(false);
                        break;
                    case 5:
                        GuideContent6.check.setChecked(false);
                        break;
                    case 6:
                        GuideContent7.check.setChecked(false);
                        break;
                    case 7:
                        GuideContent8.check.setChecked(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new GuideContent1(onClickListener, checkedChangeListener);
                case 1:
                    return new GuideContent2(onClickListener, checkedChangeListener);
                case 2:
                    return new GuideContent3(onClickListener, checkedChangeListener);
                case 3:
                    return new GuideContent4(onClickListener, checkedChangeListener);
                case 4:
                    return new GuideContent5(onClickListener, checkedChangeListener);
                case 5:
                    return new GuideContent6(onClickListener, checkedChangeListener);
                case 6:
                    return new GuideContent7(onClickListener, checkedChangeListener);
                case 7:
                    return new GuideContent8(onClickListener, checkedChangeListener);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    }


    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}
