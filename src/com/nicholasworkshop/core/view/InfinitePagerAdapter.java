package com.nicholasworkshop.core.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

public class InfinitePagerAdapter extends PagerAdapter implements OnPageChangeListener
{
    private int mCurrent = 0;
    private ViewPager mViewPager;
    private OnInfinitePageChangeListener mListener;
    private FrameLayout[] mFrames = new FrameLayout[3];
    private Vector<View> mViews = new Vector<View>();

    /**
     * Constructor of a pager adapter.
     *
     * @param pager The instance of parent InfiniteViewPager.
     */
    public InfinitePagerAdapter(ViewPager pager)
    {
        // store the instance of view pager
        mViewPager = pager;
        mViewPager.setOnPageChangeListener(this);

        // get context from pager
        Context context = mViewPager.getContext();
        assert context != null;

        // ensure that pager only has 3 views
        mViewPager.removeAllViews();
        for (int i = 0; i < 3; i++) {

            // add loading message to each frame
            TextView view = new TextView(context);
            view.setText("loading");
            view.setTextSize(64);
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

            // create frame
            mFrames[i] = new FrameLayout(context);
            mFrames[i].addView(view);
            pager.addView(mFrames[i]);
        }
    }

    public void setViews(Vector<View> views)
    {
        mViews = views;
        shiftViews();
    }

    public void setOnInfinitePageChangeListener(OnInfinitePageChangeListener listener)
    {
        mListener = listener;
    }

    private void shiftViews()
    {
        // remove all views from frames first
        // to prevent views from having multiple parents
        for (int i = 0; i < 3; i++) {
            mFrames[i].removeAllViews();
        }

        // fill views into frames
        int count = mViews.size();
        for (int i = 0; i < 3; i++) {

            // get the view
            int index = (mCurrent + i - 1 + count) % count;
            View view = mViews.get(index);
            ViewParent parent = view.getParent();

            if (parent != null) {
                // if view has parent, fill it with generated dummy
                ImageView dummy = generateDummy(view);
                mFrames[i].addView(dummy);
            } else {
                // else just add the view
                mFrames[i].addView(view);
            }
        }

        // reset the current position to center
        mViewPager.setCurrentItem(1, false);
    }

    private ImageView generateDummy(View view)
    {
        // get context from view pager
        Context context = mViewPager.getContext();
        assert context != null;

        // build bitmap
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        // create dummy image from bitmap generated
        assert bitmap != null;
        ImageView dummy = new ImageView(context);
        dummy.setImageBitmap(bitmap);
        return dummy;
    }

    // ===================================================
    // PagerAdapter
    // ===================================================

    /**
     * Always return 3.
     *
     * @return Number of view saved.
     */
    @Override
    public int getCount()
    {
        return 3;
    }

    /**
     * Draw frames to the display.
     * <p/>
     * //     * @param container
     * //     * @param position
     * //     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        return mFrames[position];
    }

    /**
     * Remove frames from display.
     * <p/>
     * //     * @param container
     * //     * @param position
     * //     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        View view = (View) object;
        container.removeView(view);
    }

    /**
     * Check if the view equals the one in saved views.
     * <p/>
     * //     * @param view
     * //     * @param object
     * //     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    // ===================================================
    // OnPageChangeListener
    // ===================================================

    /**
     * Whenever a page is selected (scrolled to), updated the current direction.
     */
    @Override
    public void onPageSelected(int position)
    {
        // calculate the direction
        // left if equals -1
        // right if equals 1
        int direction = position - 1;

        // calculate current position
        int count = mViews.size();
        mCurrent = (mCurrent + direction + count) % count;

        // callback function
        if (mListener != null) mListener.onPageChanged(mCurrent);
    }

    /**
     * //     * @param position
     * //     * @param positionOffset
     * //     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    /**
     * Handle the events when pager is scrolling or not.
     */
    @Override
    public void onPageScrollStateChanged(int state)
    {
        if (state == ViewPager.SCROLL_STATE_IDLE) shiftViews();
    }
}
