package uk.gov.eastlothian.gowalk.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import uk.gov.eastlothian.gowalk.R;

/**
 * Created by davidmorrison on 03/12/14.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    Context mContext;
    FragmentManager mFragmentManager;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new RoutesListFragment();
            case 1:
                return new RoutesMapFragment();
            default:
                throw new UnknownError("Unknown position."); // TODO: replace with proper error
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_routes_list_tab).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_routes_map_tab).toUpperCase(l);
        }
        return null;
    }

}
