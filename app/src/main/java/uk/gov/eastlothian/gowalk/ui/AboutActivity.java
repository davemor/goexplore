package uk.gov.eastlothian.gowalk.ui;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.gov.eastlothian.gowalk.R;

public class AboutActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new AboutFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class AboutFragment extends Fragment {

        public AboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about, container, false);

            TextView about = (TextView) rootView.findViewById(R.id.about_people);
            about.setMovementMethod(LinkMovementMethod.getInstance());

            // get the view
            ImageView imageView = (ImageView) rootView.findViewById(R.id.short_eared_owl);
            int width = this.getResources().getDisplayMetrics().widthPixels;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int height = (int) (0.74 * width); // TODO: It's magic!
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));

            return rootView;
        }
    }
}
