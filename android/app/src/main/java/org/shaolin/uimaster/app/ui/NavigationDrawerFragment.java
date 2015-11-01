package org.shaolin.uimaster.app.ui;


import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.shaolin.uimaster.app.api.remote.RService;
import org.shaolin.uimaster.app.base.BaseListFragment;
import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.bean.SimpleBackPage;
import org.shaolin.uimaster.app.util.UIHelper;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 侧滑菜单界面
 *
 * @author 
 * @created 2014年9月25日 下午6:00:05
 */
public class NavigationDrawerFragment extends BaseFragment implements
        OnClickListener {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;

    @InjectView(R.id.menu_item_setting)
    View mMenu_item_setting;

    @InjectView(R.id.menu_item_theme)
    View mMenu_item_theme;

    private View functionView;

    private final HashMap<Integer, String> functionMap = new HashMap<Integer, String>();

    public void setFunctionFragment(View view) {
        this.functionView = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState
                    .getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = inflater.inflate(R.layout.fragment_navigation_drawer,
                container, false);
        mDrawerListView.setOnClickListener(this);
        ButterKnife.inject(this, mDrawerListView);
        initView(mDrawerListView);
        initData();
        return mDrawerListView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.menu_item_setting:
                UIHelper.showSetting(getActivity());
                break;
            case R.id.menu_item_theme:
                switchTheme();
                break;
            default:
                if (!functionMap.containsKey(id)) {
                    break;
                }
                String link = functionMap.get(id);
                Bundle bundle = new Bundle();
                bundle.putInt("FunctionId", id);
                bundle.putInt("id", id);
                String[] items = link.split("&");
                bundle.putString("_chunkname", getValue(items, "_chunkname="));
                bundle.putString("_nodename", getValue(items, "_nodename="));
                bundle.putString("_page", getValue(items, "_page="));
                bundle.putString("_framename", getValue(items, "_framename="));
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FUNCTION, bundle);
                break;

        }
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawers();
            }
        }, 500);
    }

    private String getValue(String[] items, String name) {
        for(String i: items) {
            if(i.indexOf(name) != -1) {
                return i.substring(i.indexOf(name) + name.length());
            }
        }
        return "";
    }

    private void switchTheme() {
        if (AppContext.getNightModeSwitch()) {
            AppContext.setNightModeSwitch(false);
        } else {
            AppContext.setNightModeSwitch(true);
        }

        if (AppContext.getNightModeSwitch()) {
            getActivity().setTheme(R.style.AppBaseTheme_Night);
        } else {
            getActivity().setTheme(R.style.AppBaseTheme_Light);
        }

        getActivity().recreate();
    }

    @Override
    public void initView(final View view) {
        TextView night = (TextView) view.findViewById(R.id.tv_night);
        if (AppContext.getNightModeSwitch()) {
            night.setText("日间");
        } else {
            night.setText("夜间");
        }

        mMenu_item_setting.setOnClickListener(this);
        mMenu_item_theme.setOnClickListener(this);


        /**
         * <LinearLayout
         android:id="@+id/menu_item_quests"
         style="@style/MenuItemLayoutStyle" >

         <ImageView
         style="@style/MenuItemImageViewStyle"
         android:background="@drawable/drawer_menu_icon_quest_nor"
         android:contentDescription="@null" />

         <TextView
         style="@style/MenuItemTextViewStyle"
         android:gravity="center"
         android:text="@string/menu_quests" />
         </LinearLayout>

         <View
         style="@style/h_line" />
         */
        showWaitDialog(R.string.loading);
        final NavigationDrawerFragment othis = this;
        final FragmentActivity activity = this.getActivity();
        RService.getFunctionList(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBytes) {
                try {
                    String jsonStr = new String(responseBytes);
                    //System.out.println("jsonStr: " + jsonStr);
                    JSONArray array = new JSONArray(jsonStr);
                    LinearLayout root = (LinearLayout) view.findViewById(R.id.layout_root);

                    int length = array.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject item = array.getJSONObject(i);
                        int groupId = i;
                        int itemId = Integer.parseInt(item.getString("id"));

                        LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                        //ContextThemeWrapper theme = new ContextThemeWrapper(activity, R.style.MenuItemLayoutStyle);
                        LinearLayout layout = (LinearLayout)activity.getLayoutInflater().inflate(
                                R.layout.fragment_navigation_drawer_item, null);
                        layout.setId(item.getInt("id"));
                        layout.setOnClickListener(othis);
                        root.addView(layout, LP_FW);

                        ImageView imageView = (ImageView)activity.getLayoutInflater().inflate(
                                R.layout.fragment_navigation_drawer_item_icon, null);
                        //imageView.setBackgroundResource(R.drawable.drawer_menu_icon_opensoft_nor);

                        layout.addView(imageView);
                        // 接下来向layout中添加TextView
                        TextView textView = (TextView)activity.getLayoutInflater().inflate(
                                R.layout.fragment_navigation_drawer_item_text, null);
                        textView.setText(item.getString("text"));
                        //textView.setTextAppearance(activity, R.style.MenuItemTextViewStyle);
                        layout.addView(textView);

                        JSONArray children = item.getJSONArray("children");
                        if (children != null && children.length() > 0) {
                            int clength = children.length();
                            for (int j = 0; j < clength; j++) {
                                JSONObject citem = children.getJSONObject(j);
                                functionMap.put(citem.getInt("id"), citem.getJSONObject("a_attr").getString("href"));

                                LinearLayout.LayoutParams LP_FW1 = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                //activity.obtainStyledAttributes(R.style.MenuItemLayoutStyle);
                                LinearLayout sublayout = (LinearLayout)activity.getLayoutInflater().inflate(
                                        R.layout.fragment_navigation_drawer_item, null);
                                sublayout.setId(citem.getInt("id"));
                                sublayout.setOnClickListener(othis);
                                root.addView(sublayout, LP_FW1);

                                ImageView imageView1 = (ImageView)activity.getLayoutInflater().inflate(
                                        R.layout.fragment_navigation_drawer_item_icon, null);
                                //imageView1.setBackgroundResource(R.drawable.drawer_menu_icon_blog_nor);
                                sublayout.addView(imageView1);
                                // 接下来向layout中添加TextView
                                TextView textView1 = (TextView)activity.getLayoutInflater().inflate(
                                        R.layout.fragment_navigation_drawer_item_text, null);
                                textView1.setText("  " + citem.getString("text"));
                                //textView.setTextAppearance(activity, R.style.MenuItemTextViewStyle);
                                sublayout.addView(textView1);

                                View line = new View(new ContextThemeWrapper(activity, R.style.h_line));
                                root.addView(line);
                            }
                        } else {
                            functionMap.put(item.getInt("id"), item.getJSONObject("a_attr").getString("href"));

                            View line = new View(new ContextThemeWrapper(activity, R.style.h_line));
                            root.addView(line);
                        }

                    }
                } catch (Exception e) {
                    Log.w("", e.getMessage(), e);
                    showWaitDialog("Failed to load data: " + e.getMessage());
                }
                hideWaitDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                showWaitDialog("Failed to load data: " + arg3.getMessage());
            }
        });
    }

    @Override
    public void initData() {
    }

    private Bundle getBundle(int newType) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, newType);
        return bundle;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null
                && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation
     * drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                null, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActivity().invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void openDrawerMenu() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement NavigationDrawerCallbacks.");
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
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }
}
