package com.hbtl.ui.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hbtl.models.AppAreaInfo;
import com.hbtl.beans.CommonMyHsAreaInfo;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;
import com.hbtl.ui.app.activity.AreaSerialPickerActivity;
//import com.coam.ui.logistics.adapter.PipelineHistoryBaseAdapter;
import com.hbtl.view.ToastHelper;
import com.hbtl.view.ToastHelper.ToastType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonSearchAreaLineListActivity extends BaseActivity implements OnClickListener {
    private static final int CHOOSE_START_PLACE = 0x1;
    private static final int CHOOSE_END_PLACE = 0x2;
    public static final int CHOOSE_PLACE_RESULTCODE = 0x3;
    private ArrayList<CommonMyHsAreaInfo> myHsAreaList;
    private CommonMyHsAreaInfo mMyHsArea;

    @BindView(R.id.historyTitle_TV) TextView historyTitle_TV;
    @BindView(R.id.searchStartAreaOne_LL) View searchStartAreaOne_LL;
    @BindView(R.id.searchEndAreaOne_LL) View searchEndAreaOne_LL;
    @BindView(R.id.searchStartAreaOne_TV) TextView searchStartAreaOne_TV;
    @BindView(R.id.searchEndAreaOne_TV) TextView searchEndAreaOne_TV;
    @BindView(R.id.confirm_Btn) Button confirm_Btn;
    @BindView(R.id.searchHistory_LV) ListView searchHistory_LV;

    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_search_area_line_list);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        try {
            // 搜索列表
            myHsAreaList = getIntent().getParcelableArrayListExtra("myHsAreaList");
            // 当前默认显示搜索区域
            mMyHsArea = getIntent().getParcelableExtra("myHsArea");

            appNav_ToolBar.setTitle("搜索专线");
            setSupportActionBar(appNav_ToolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

            appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            if (myHsAreaList != null) {
                historyTitle_TV.setVisibility(View.VISIBLE);
                ///mCommonMyHsArea = myHsAreaList.get(0);
                searchStartAreaOne_TV.setText(mMyHsArea.startAreaOne);
                searchEndAreaOne_TV.setText(mMyHsArea.endAreaOne);
                searchHistory_LV.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        CommonMyHsAreaInfo myHsArea = myHsAreaList.get(position);
                        intent.putExtra("myHsArea", myHsArea);
                        setResult(CHOOSE_PLACE_RESULTCODE, intent);
                        finish();
                    }
                });
            }

            confirm_Btn.setOnClickListener(this);
            searchStartAreaOne_LL.setOnClickListener(this);
            searchEndAreaOne_LL.setOnClickListener(this);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.searchStartAreaOne_LL:
                choosePlace(CHOOSE_START_PLACE);
                break;
            case R.id.searchEndAreaOne_LL:
                choosePlace(CHOOSE_END_PLACE);
                break;
            case R.id.confirm_Btn:
                if (mMyHsArea.startAreaSerial == null) {
                    ToastHelper.makeText(CommonSearchAreaLineListActivity.this, "请选择起始地", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                    return;
                }
                if (mMyHsArea.endAreaSerial == null) {
                    ToastHelper.makeText(CommonSearchAreaLineListActivity.this, "请选择目的地", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("myHsArea", mMyHsArea);
                setResult(CHOOSE_PLACE_RESULTCODE, intent);
                finish();
                break;

            default:
                break;
        }
    }

    private void choosePlace(int requestCode) {
        Intent intent = new Intent(CommonSearchAreaLineListActivity.this, AreaSerialPickerActivity.class);
        intent.putExtra("pickAreaLevel", "districtLevel");
        intent.putExtra("showChinese", true);
        if (requestCode == CHOOSE_START_PLACE) {
            intent.putExtra("areaSerial", mMyHsArea.startAreaSerial);
        } else if (requestCode == CHOOSE_END_PLACE) {
            intent.putExtra("areaSerial", mMyHsArea.endAreaSerial);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AreaSerialPickerActivity.AREA_CHOOSE_RESULTCODE) {
            AppAreaInfo pickAreaInfo = data.getParcelableExtra("pickAreaInfo");
            switch (requestCode) {
                case CHOOSE_START_PLACE:
                    searchStartAreaOne_TV.setText(pickAreaInfo.areaOne);
                    mMyHsArea.startAreaOne = pickAreaInfo.areaOne;
                    mMyHsArea.startAreaSerial = pickAreaInfo.areaSerial;
                    break;
                case CHOOSE_END_PLACE:
                    searchEndAreaOne_TV.setText(pickAreaInfo.areaOne);
                    mMyHsArea.endAreaOne = pickAreaInfo.areaOne;
                    mMyHsArea.endAreaSerial = pickAreaInfo.areaSerial;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void inNewIntent(@NotNull Intent intent) {
        // TODO ...
    }

    @Override
    protected void onNwUpdateEvent(@NotNull NetworkType networkType) {
        // TODO ...
    }
}
