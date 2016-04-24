package br.com.halyson.ensharp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import br.com.halyson.ensharp.R;
import br.com.halyson.ensharp.activity.IntroductionActivity;
import br.com.halyson.ensharp.activity.MyCctvActivity;
import br.com.halyson.ensharp.activity.SleepActivity;
import br.com.halyson.ensharp.activity.TestActivity;
import br.com.halyson.ensharp.adapter.DrawerMenuAdapter;
import br.com.halyson.ensharp.fragment.api.BaseFragment;
import br.com.halyson.ensharp.model.DrawerMenuBean;


/**
 * Fragment utilizado para o gerenciamento de interações para e apresentação do menu drawer.
 */
public class
        NavigationDrawerFragment extends BaseFragment {
    private static final String TAG = NavigationDrawerFragment.class.getSimpleName();

    /**
     * Lembra a posição do item selecionado.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Por as diretrizes de design, você deve mostrar o menu drawer até que o usuário expande ele manualmente.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Componente
     */
    private ActionBarDrawerToggle mDrawerToggle;
    public static DrawerLayout mDrawerLayout;

    public static View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public static int spsound_decibel;


    ///////
    private View mViewDrawer;

    public static ToggleButton sw_accident, sw_caution, sw_prevention, sw_myInfo, sw_cctv;
    public static Spinner sp_accident, sp_caution, sp_prevention_time, sp_prevention_decibel;

    TextView tv_setting, tv_accident, tv_accident_distance;
    TextView tv_caution, tv_caution_distance;
    TextView tv_prevention, tv_prevention_time, tv_prevention_decibel, tv_cctv;
    TextView tv_myInfo;
    Typeface myTypeface;

    ImageButton btn_test;
    UserException ue = new UserException();
    //Fragment2 maa=new Fragment2();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has
        // s demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewDrawer = inflater.inflate(R.layout.fragment_drawer_menu, container, false);

        sw_accident = (ToggleButton) mViewDrawer.findViewById(R.id.sw_accident);
        sw_caution = (ToggleButton) mViewDrawer.findViewById(R.id.sw_caution);
        sw_prevention = (ToggleButton) mViewDrawer.findViewById(R.id.sw_prevention);
        //sw_myInfo = (ToggleButton) mViewDrawer.findViewById(R.id.sw_myInfo);
        //sw_cctv = (ToggleButton) mViewDrawer.findViewById(R.id.sw_cctv);


        sp_accident = (Spinner) mViewDrawer.findViewById(R.id.sp_accident);
        sp_caution = (Spinner) mViewDrawer.findViewById(R.id.sp_caution);
        sp_prevention_time = (Spinner) mViewDrawer.findViewById(R.id.sp_prevention_time);
        sp_prevention_decibel = (Spinner) mViewDrawer.findViewById(R.id.sp_prevention_decibel);


        myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "addi.ttf");


        tv_setting = (TextView) mViewDrawer.findViewById(R.id.tv_setting);
        tv_accident = (TextView) mViewDrawer.findViewById(R.id.tv_accident);
        tv_accident_distance = (TextView) mViewDrawer.findViewById(R.id.tv_accident_distance);
        tv_caution = (TextView) mViewDrawer.findViewById(R.id.tv_caution);
        tv_caution_distance = (TextView) mViewDrawer.findViewById(R.id.tv_caution_distance);
        tv_prevention = (TextView) mViewDrawer.findViewById(R.id.tv_prevention);
        tv_prevention_time = (TextView) mViewDrawer.findViewById(R.id.tv_prevention_time);
        tv_prevention_decibel = (TextView) mViewDrawer.findViewById(R.id.tv_prevention_decibel);
        //tv_myInfo = (TextView) mViewDrawer.findViewById(R.id.tv_myInfo);
        //tv_cctv= (TextView) mViewDrawer.findViewById(R.id.tv_cctv);

        setFont();

        /*btn_test = (ImageButton) mViewDrawer.findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SetDecibelFlag();
                ue.SleepOnPopup = true;
                sendToSleepActivity(getActivity()); //화면으로 전달
            }
        });*/

        return mViewDrawer;
    }


    public void SetDecibelFlag() {
        spsound_decibel = Integer.parseInt(sp_prevention_decibel.getSelectedItem().toString().substring(0, 3));
    }

    private void sendToSleepActivity(Context context) {
        Intent intent = new Intent(context, TestActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

    public void setFont() {

        tv_setting.setTypeface(myTypeface);
        tv_accident.setTypeface(myTypeface);
        tv_accident_distance.setTypeface(myTypeface);

        tv_caution.setTypeface(myTypeface);
        tv_caution_distance.setTypeface(myTypeface);

        tv_prevention.setTypeface(myTypeface);
        tv_prevention_time.setTypeface(myTypeface);
        tv_prevention_decibel.setTypeface(myTypeface);

        //tv_myInfo.setTypeface(myTypeface);
        //tv_cctv.setTypeface(myTypeface);
    }

    /////    /////

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout != null) {
                if (isDrawerOpen()) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                } else {
                    mDrawerLayout.openDrawer(mFragmentContainerView);
                }
            } else {
                getActivity().finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.ic_drawer_menu_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.app_name,  /* "open drawer" description for accessibility */
                R.string.app_name  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            //    mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public static void OpenIt() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */

    }
}
