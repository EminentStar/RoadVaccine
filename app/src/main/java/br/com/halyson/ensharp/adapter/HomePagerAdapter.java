package br.com.halyson.ensharp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import br.com.halyson.ensharp.activity.HomeActivity;
import br.com.halyson.ensharp.fragment.DefaultFragment;
import br.com.halyson.ensharp.fragment.Fragment2;
import br.com.halyson.ensharp.fragment.Fragment3;

/**
 * Created by halyson on 18/12/14.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {
    private List<String> mListTitleTabs;

    public HomePagerAdapter(List<String> listTitleTabs, FragmentManager childFragmentManager) {
        super(childFragmentManager);
        this.mListTitleTabs = listTitleTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mListTitleTabs == null || mListTitleTabs.isEmpty()) {
            return "";
        }
        return mListTitleTabs.get(position);
    }

    @Override
    public int getCount() {
        if (mListTitleTabs == null || mListTitleTabs.isEmpty()) {
            return 0;
        }
        return mListTitleTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                // HomeActivity.btn_q.setClickable(true);
                // HomeActivity.btn_q.setEnabled(true);
                return Fragment2.newInstance();
            case 1:
                // HomeActivity.btn_q.setClickable(false);
                // HomeActivity.btn_q.setEnabled(false);
                return Fragment3.newInstance();
            default:
                // HomeActivity.btn_q.setClickable(true);
                // HomeActivity.btn_q.setEnabled(true);
                return DefaultFragment.newInstance();
        }
    }
}
