package uk.gov.eastlothian.gowalk.ui;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.model.Wildlife;

/**
 * Created by davidmorrison on 28/12/14.
 */
public abstract class MainMenuActivity extends FragmentActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            } break;
            case R.id.action_home: {
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
            } break;
            case R.id.action_logbook: {
                Intent intent = new Intent(this, LogBookActivity.class);
                startActivity(intent);
            } break;
            case R.id.action_paths: {
                Intent intent = new Intent(this, RoutesActivity.class);
                startActivity(intent);
            } break;
            case R.id.action_wildlife: {
                Intent intent = new Intent(this, WildlifeGuideActivity.class);
                startActivity(intent);
            } break;
        }

        return super.onOptionsItemSelected(item);
    }
}
