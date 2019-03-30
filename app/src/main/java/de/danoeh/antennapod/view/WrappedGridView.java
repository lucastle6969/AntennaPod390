package de.danoeh.antennapod.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;


/**
 * inspired by Kyle Ivey - https://stackoverflow.com/questions/20561663/gridview-show-according-to-actual-height-within-scroll-view
 * adapted from Neil Traft - https://stackoverflow.com/questions/4523609/grid-of-images-inside-scrollview/4536955#4536955
 * A gridview that doesn't scroll and automatically wraps to the height of its contents
 */
public class WrappedGridView extends GridView {
    public WrappedGridView(Context context) {
        super(context);
    }

    public WrappedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrappedGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate entire height by providing a very large height hint.
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
