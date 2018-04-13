package com.hbtl.view;


import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbtl.ekt.R;

import timber.log.Timber;

//import com.coam.plugins.carbon.widget.RelativeLayout;

/**
 * Created by 亚飞 on 2015-11-20.
 */
public class ToolbarTabsView extends Toolbar implements View.OnClickListener {
    private LayoutInflater mInflater;
//    private LinearLayout rl;

    private int showTabTag;
    // footer view
    private LinearLayout mAppToolbarTab_LL;
    private RelativeLayout toolbar_left_tab_RL, toolbar_center_tab_RL, toolbar_right_tab_RL;
    //private String leftTag, centerTag, rightTag;
    private TextView leftTab_TV, centerTab_TV, rightTab_TV;
    private View leftTabDivider_V, centerTabDivider_V, rightTabDivider_V;

    private OnTabSwitchListener mOnTabSwitchListener;

    public ToolbarTabsView(Context context) {
        super(context);
    }

    public ToolbarTabsView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        Toolbar action = this; //get the actionbar
//        setDisplayShowCustomEnabled(true); //enable it to display a
//        // custom view in the action bar.
//        this.setCustomView(R.layout.search_bar);//add the custom view
//        this.setDisplayShowTitleEnabled(false); //hide the title
//
//        edtSeach = (EditText)this.getCustomView().findViewById(R.id.edtSearch); //the text editor
//
//        //this is a listener to do a search when the user clicks on search button
//        edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    doSearch();
//                    return true;
//                }
//                return false;
//            }
//        });

        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        getSupportActionBar().getThemedContext()

//        try {
//            Timber.i("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
//            rl = (LinearLayout) getRootView().findViewById(R.id.toolbar_linear_layout);
//            Timber.i("iiiiiiiiiiiiiiiiiiiiiii");
//            rl.setOnClickListener(this);
//            Timber.i("iiiiiiiiiiiiiiiiiiiiiii");
//        }catch (Exception e){
//            e.printStackTrace();
//            Timber.i("iiiiiiiiiiiiiiiiiiiiiii");
//        }


        // footer
        mAppToolbarTab_LL = (LinearLayout) mInflater.inflate(R.layout.app_toolbar_tab_layout, this, false);
        try {
//            Timber.i("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
            toolbar_left_tab_RL = (RelativeLayout) mAppToolbarTab_LL.findViewById(R.id.toolbar_left_tab_relative_layout);
            toolbar_left_tab_RL.setOnClickListener(this);
//            Timber.i("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
            toolbar_center_tab_RL = (RelativeLayout) mAppToolbarTab_LL.findViewById(R.id.toolbar_center_tab_relative_layout);
            toolbar_center_tab_RL.setOnClickListener(this);
//            Timber.i("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
            toolbar_right_tab_RL = (RelativeLayout) mAppToolbarTab_LL.findViewById(R.id.toolbar_right_tab_relative_layout);
            toolbar_right_tab_RL.setOnClickListener(this);
//            Timber.i("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }

        leftTab_TV = (TextView) toolbar_left_tab_RL.findViewById(R.id.toolbar_left_tab_text);
        centerTab_TV = (TextView) toolbar_center_tab_RL.findViewById(R.id.toolbar_center_tab_text);
        rightTab_TV = (TextView) toolbar_right_tab_RL.findViewById(R.id.toolbar_right_tab_text);

        leftTab_TV.setSelected(true);
        centerTab_TV.setSelected(false);
        rightTab_TV.setSelected(false);

        leftTabDivider_V = (View) toolbar_left_tab_RL.findViewById(R.id.toolbar_left_tab_divider);
        centerTabDivider_V = (View) toolbar_center_tab_RL.findViewById(R.id.toolbar_center_tab_divider);
        rightTabDivider_V = (View) toolbar_right_tab_RL.findViewById(R.id.toolbar_right_tab_divider);

        leftTabDivider_V.setVisibility(View.VISIBLE);
        centerTabDivider_V.setVisibility(View.GONE);
        rightTabDivider_V.setVisibility(View.GONE);
        showTabTag = toolbar_left_tab_RL.getId();


        //mLabLoadMore = (TextView) mFooterView.findViewById(R.id.no_more_textView);
        //mProgressBarLoadMore = (ProgressBar) mFooterView.findViewById(R.id.load_more_progressBar);

        addView(mAppToolbarTab_LL);
    }

    // 根据需要依次设定顶部 Tab 标签 Tag
    public void setLeftTag(String lTag) {
        leftTab_TV.setText(lTag);
        toolbar_left_tab_RL.setVisibility(View.VISIBLE);
    }

    public void setCenterTag(String cTag) {
        centerTab_TV.setText(cTag);
        toolbar_center_tab_RL.setVisibility(View.VISIBLE);
    }

    public void setRightTag(String rTag) {
        rightTab_TV.setText(rTag);
        toolbar_right_tab_RL.setVisibility(View.VISIBLE);
    }

    public void setSwitchTabListen(OnTabSwitchListener tabSwitchListener) {
        mOnTabSwitchListener = tabSwitchListener;
    }

    @Override
    public void onClick(View v) {
        // 判断是否与当前显示的Tab是相同的页面,防止重复刷新界面
        if (showTabTag == v.getId()) return;
        showTabTag = v.getId();

        leftTab_TV.setSelected(false);
        centerTab_TV.setSelected(false);
        rightTab_TV.setSelected(false);

        leftTabDivider_V.setVisibility(View.GONE);
        centerTabDivider_V.setVisibility(View.GONE);
        rightTabDivider_V.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.toolbar_left_tab_relative_layout:
                Timber.i("CCCCCCCCCCCCCCCCCCCCCCCC---toolbar_left_tab_relative_layout");
                leftTab_TV.setSelected(true);
                leftTabDivider_V.setVisibility(View.VISIBLE);
                mOnTabSwitchListener.onLeftTabSwitch();
                break;
            case R.id.toolbar_center_tab_relative_layout:
                Timber.i("CCCCCCCCCCCCCCCCCCCCCCCC---toolbar_center_tab_relative_layout");
                centerTab_TV.setSelected(true);
                centerTabDivider_V.setVisibility(View.VISIBLE);
                mOnTabSwitchListener.onCenterTabSwitch();
                break;
            case R.id.toolbar_right_tab_relative_layout:
                Timber.i("CCCCCCCCCCCCCCCCCCCCCCCC---toolbar_right_tab_relative_layout");
                rightTab_TV.setSelected(true);
                rightTabDivider_V.setVisibility(View.VISIBLE);
                mOnTabSwitchListener.onRightTabSwitch();
                break;
            default:
                break;
        }
        Timber.i("CCCCCCCCCCCCCCCCCCCCCCCC:" + v.getId());
    }


    /**
     * Interface definition for a callback to be invoked when list reaches the
     * last item (the user load more items in the list)
     */
    public interface OnTabSwitchListener {
        /**
         * Called when the list reaches the last item (the last item is visible
         * to the user)
         */
        public void onLeftTabSwitch();

        public void onCenterTabSwitch();

        public void onRightTabSwitch();
    }
}