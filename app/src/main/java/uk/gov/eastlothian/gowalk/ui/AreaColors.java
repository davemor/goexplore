package uk.gov.eastlothian.gowalk.ui;

import android.content.Context;
import android.content.res.TypedArray;

import uk.gov.eastlothian.gowalk.R;

/**
 * Created by davidmorrison on 04/12/14.
 */
public class AreaColors {
    private static int[] colors = null;

    public static int[] getColors() {
        assert colors != null;
        return colors;
    }

    public static void setup(Context context) {
        if(colors == null) {
            TypedArray ta = context.getResources()
                .obtainTypedArray(R.array.route_colors);
            colors = new int[ta.length()];
            for (int i = 0; i < ta.length(); i++) {
                colors[i] = ta.getColor(i, 0);
            }
            ta.recycle();
        }
    }

    public static int getAreaColor(long id) {
        int[] colors = AreaColors.getColors();
        return colors[(int)id%colors.length];
    }
}
