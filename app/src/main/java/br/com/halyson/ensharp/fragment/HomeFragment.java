package br.com.halyson.ensharp.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.util.List;

import br.com.halyson.ensharp.R;
import br.com.halyson.ensharp.adapter.HomePagerAdapter;
import br.com.halyson.ensharp.fragment.api.BaseFragment;
import br.com.halyson.ensharp.interfaces.home.HomeView;
import br.com.halyson.ensharp.presenter.HomePresenterImpl;


public class HomeFragment extends BaseFragment implements HomeView {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private View mViewHome;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private HomePresenterImpl mHomePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewHome = inflater.inflate(R.layout.fragment_default, container, false);

        loadViewComponents();
        initPresenter();
        loadSectionsTabs();

        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Toast.makeText(getActivity(), mViewPager.getCurrentItem(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return mViewHome;
    }

    @Override
    public void onDestroy() {
        mHomePresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void loadViewComponents() {
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) mViewHome.findViewById(R.id.fragment_home_pager_sliding_tab);
        mViewPager = (ViewPager) mViewHome.findViewById(R.id.fragment_home_view_pager);
    }

    @Override
    public void initPresenter() {
        mHomePresenter = new HomePresenterImpl(this);
    }

    @Override
    public void loadSectionsTabs() {
        mHomePresenter.loadSectionsTabs();
    }

    @Override
    public void loadViewPager(List<String> listTitleTabs) {
        mViewPager.setAdapter(new HomePagerAdapter(listTitleTabs, getChildFragmentManager()));
    }

    @Override
    public void setColorTabs(int color) {
        mPagerSlidingTabStrip.setTextColor(mViewHome.getResources().getColor(R.color.text_white));
        // mPagerSlidingTabStrip.setTextColor(color);
    }

    @Override
    public void setDividerColorTabs(int color) {
        mPagerSlidingTabStrip.setDividerColor(mViewHome.getResources().getColor(R.color.text_black));
    }

    @Override
    public void setIndicatorColorTabs(int color) {
        mPagerSlidingTabStrip.setDividerColor(color);

    }

    @Override
    public void loadTabs() {
        mPagerSlidingTabStrip.setViewPager(mViewPager);
    }
}