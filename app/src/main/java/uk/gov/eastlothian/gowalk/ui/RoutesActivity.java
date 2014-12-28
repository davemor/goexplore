package uk.gov.eastlothian.gowalk.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import uk.gov.eastlothian.gowalk.R;

public class RoutesActivity extends MainMenuActivity implements ActionBar.TabListener{

    // used by the tabs
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        // this adapter returns a fragment for each section of the page
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // set the view pager to use the adapter
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set up the action bar so it shows the tabs
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // bar.setDisplayShowTitleEnabled(false);
        ActionBar.Tab areasTab = getActionBar().newTab()
                .setText(R.string.title_routes_list_tab)
                .setTabListener(this);
        bar.addTab(areasTab);
        ActionBar.Tab mapTab = getActionBar().newTab()
                .setText(R.string.title_routes_map_tab)
                .setTabListener(this);
        bar.addTab(mapTab);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
