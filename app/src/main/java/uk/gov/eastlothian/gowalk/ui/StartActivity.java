package uk.gov.eastlothian.gowalk.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;
import uk.gov.eastlothian.gowalk.data.WalksDataLoader;
import uk.gov.eastlothian.gowalk.data.WalksDbHelper;

public class StartActivity extends MainMenuActivity {

    private static final String LOG_TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StartFragment())
                    .commit();
        }
        // set up some globals
        WalksDataLoader.initDatabase(this);

        Log.d(LOG_TAG, getDatabasePath(WalksDbHelper.DB_NAME).getAbsolutePath());
    }

    public void onViewRoutesClicked(View view) {
        Intent routesIntent = new Intent(this, RoutesActivity.class);
        startActivity(routesIntent);
    }

    public void onViewWildlifeGuideClicked(View view) {
        Intent routesIntent = new Intent(this, WildlifeGuideActivity.class);
        startActivity(routesIntent);
    }

    public void onViewLogBookClicked(View view) {
        Intent logbookIntent = new Intent(this, LogBookActivity.class);
        startActivity(logbookIntent);
    }

    public void onViewAboutClicked(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public static class StartFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_start, container, false);
            return rootView;
        }
    }
}
