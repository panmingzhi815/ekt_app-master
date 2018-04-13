package com.hbtl.ui.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.hbtl.models.AppAreaInfo;
import com.hbtl.plugins.kankan.wheel.widget.OnWheelChangedListener;
import com.hbtl.plugins.kankan.wheel.widget.WheelView;
import com.hbtl.plugins.kankan.wheel.widget.adapters.ArrayWheelAdapter;
import com.hbtl.ekt.R;
import com.hbtl.utils.FileUtils;
import com.hbtl.view.ToastHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AreaSerialPickerActivity extends Activity implements OnClickListener, OnWheelChangedListener {
    public static List<AppAreaInfo.AreaProvinceInfo> mProvinceList;
    private String pickAreaLevel = "districtLevel"; // 显示层级 provinceLevel cityLevel districtLevel
    public static boolean showChinese = false; // 是否显示 中国 选项
    public static final int AREA_CHOOSE_RESULTCODE = 0x16;
    public static String countryCode = "000000";
    private Timer timer;
    private TimerTask timerTask;
    private long time = 5000;

    private AppAreaInfo pickAreaInfo = new AppAreaInfo();

    @BindView(R.id.areaPickerLayout_RL) RelativeLayout areaPickerLayout_RL;

    @BindView(R.id.provincePicker_WV) WheelView provincePicker_WV;
    @BindView(R.id.cityPicker_WV) WheelView cityPicker_WV;
    @BindView(R.id.districtPicker_WV) WheelView districtPicker_WV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_area_serial_picker_activity);
        ButterKnife.bind(this);

        setUpViews();
        setUpListener();
        setUpData();
    }

    private void setUpViews() {
        pickAreaLevel = getIntent().getStringExtra("pickAreaLevel");
        showChinese = getIntent().getBooleanExtra("showChinese", false);
//        mViewProvince = (WheelView) findViewById(R.id.id_province);
//        mViewCity = (WheelView) findViewById(R.id.id_city);
//        mViewDistrict = (WheelView) findViewById(R.id.id_district);
//        if (pickAreaLevel == "cityLevel") {
//            cityPicker_WV.setVisibility(View.VISIBLE);
//        } else {
//            cityPicker_WV.setVisibility(View.VISIBLE);
//            districtPicker_WV.setVisibility(View.VISIBLE);
//        }
        if (pickAreaLevel.equals("districtLevel")) {
            districtPicker_WV.setVisibility(View.VISIBLE);
        }
//        pickerLayout = findViewById(R.id.pickerLayout);
        provincePicker_WV.setDrawShadows(false);
        cityPicker_WV.setDrawShadows(false);
        districtPicker_WV.setDrawShadows(false);
    }

    private void setUpListener() {
        provincePicker_WV.addChangingListener(this);
        cityPicker_WV.addChangingListener(this);
        districtPicker_WV.addChangingListener(this);
        areaPickerLayout_RL.setOnClickListener(this);
    }

    private void setUpData() {
        initAreaData(this);
        provincePicker_WV.setViewAdapter(new ArrayWheelAdapter<String>(this, getProvinces()));
        provincePicker_WV.setVisibleItems(5);
        cityPicker_WV.setVisibleItems(5);
        districtPicker_WV.setVisibleItems(5);
        updateCities();
        updateAreas();

        setPickAreaInfo(getIntent().getStringExtra("areaSerial"));
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == provincePicker_WV) {
            updateCities();
        } else if (wheel == cityPicker_WV) {
            updateAreas();
        } else if (wheel == districtPicker_WV) {

        }

        // 定时执行区域选择操作...
        if (timer != null) {
            timer.cancel();
        }
        timerTask = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getPickAreaInfo();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, time);
    }

    private void updateAreas() {
        int provinceIndex = provincePicker_WV.getCurrentItem();
        int cityIndex = cityPicker_WV.getCurrentItem();
        String[] areas = getDistricts(provinceIndex, cityIndex);

        if (areas == null) {
            areas = new String[]{""};
        }
        districtPicker_WV.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        districtPicker_WV.setCurrentItem(0);
    }

    private void updateCities() {
        int provinceIndex = provincePicker_WV.getCurrentItem();
        String[] cities = getCities(provinceIndex);
        if (cities == null) {
            cities = new String[]{""};
        }
        cityPicker_WV.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        cityPicker_WV.setCurrentItem(0);
        updateAreas();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.areaPickerLayout_RL:
                getPickAreaInfo();
                break;
            default:
                break;
        }
    }

    private void getPickAreaInfo() {
        try {
            int provinceIndex = provincePicker_WV.getCurrentItem();
            int cityIndex = cityPicker_WV.getCurrentItem();
            int districtIndex = districtPicker_WV.getCurrentItem();

            String provinceName = mProvinceList.get(provinceIndex).getName();
            String provinceCode = mProvinceList.get(provinceIndex).getCode();
            String cityName = mProvinceList.get(provinceIndex).getCityList().get(cityIndex).getName();
            String cityCode = mProvinceList.get(provinceIndex).getCityList().get(cityIndex).getCode();
            String districtName = mProvinceList.get(provinceIndex).getCityList().get(cityIndex).getDistrictList().get(districtIndex).getName();
            String districtCode = mProvinceList.get(provinceIndex).getCityList().get(cityIndex).getDistrictList().get(districtIndex).getCode();

            String pickAreaSerial = null;
            String pickAreaName = null;
            switch (pickAreaLevel) {
                case "provinceLevel":
                    pickAreaInfo.showAreaLevel = "provinceLevel";
                    pickAreaInfo.areaOne = provinceName;
                    pickAreaInfo.areaLine = provinceName;
                    break;
                case "cityLevel":
                    if (provinceCode.equals(cityCode)) {
                        pickAreaSerial = provinceCode;
                        pickAreaName = provinceName;

                        pickAreaInfo.showAreaLevel = "provinceLevel";
                        pickAreaInfo.areaOne = provinceName;
                        pickAreaInfo.areaLine = provinceName;
                    } else {
                        pickAreaSerial = cityCode;
                        pickAreaName = cityName;

                        pickAreaInfo.showAreaLevel = "cityLevel";
                        pickAreaInfo.areaOne = cityName;
                        pickAreaInfo.areaLine = provinceName + "-" + cityName;
                    }
                    break;
                case "districtLevel":
                    if (provinceCode.equals(cityCode)) {
                        pickAreaSerial = provinceCode;
                        pickAreaName = provinceName;

                        pickAreaInfo.showAreaLevel = "provinceLevel";
                        pickAreaInfo.areaOne = provinceName;
                        pickAreaInfo.areaLine = provinceName;
                    } else {
                        if (cityCode.equals(districtCode)) {
                            pickAreaSerial = cityCode;
                            pickAreaName = provinceName + "-" + cityName;

                            pickAreaInfo.showAreaLevel = "cityLevel";
                            pickAreaInfo.areaOne = cityName;
                            pickAreaInfo.areaLine = provinceName + "-" + cityName;
                        } else {
                            pickAreaSerial = districtCode;
                            pickAreaName = provinceName + "-" + cityName + "-" + districtName;

                            pickAreaInfo.showAreaLevel = "districtLevel";
                            pickAreaInfo.areaOne = districtName;
                            pickAreaInfo.areaLine = provinceName + "-" + cityName + "-" + districtName;
                        }
                    }
                    break;
            }

            if (pickAreaSerial == null || pickAreaName == null) {
                ToastHelper.makeText(AreaSerialPickerActivity.this, "选择区域 [pickAreaSerial|pickAreaName] 为 null 错误", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();
                return;
            }

            pickAreaInfo.areaSerial = pickAreaSerial;
            pickAreaInfo.provinceName = provinceName;
            pickAreaInfo.cityName = cityName;
            pickAreaInfo.districtName = districtName;

            setResultBack(pickAreaInfo);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public synchronized static void initAreaData(Context context) {
        try {
            mProvinceList = new ArrayList<AppAreaInfo.AreaProvinceInfo>();
            String areaStr = FileUtils.readAssets(context, "runAreaSerial.json");
            JSONObject jsonObject = new JSONObject(areaStr);
            JSONObject proviceObject = jsonObject.getJSONObject("province");
            JSONObject cityObject = jsonObject.getJSONObject("city");
            JSONObject districtObject = jsonObject.getJSONObject("district");
            Iterator<?> iterator = proviceObject.keys();
            while (iterator.hasNext()) {
                String provinceCode = iterator.next().toString();
                if (countryCode.equals(provinceCode) && !showChinese) {
                    continue;
                }
                AppAreaInfo.AreaProvinceInfo provinceModel = new AppAreaInfo.AreaProvinceInfo();
                provinceModel.setCode(provinceCode);
                provinceModel.setName(proviceObject.getString(provinceModel.getCode()));
                List<AppAreaInfo.AreaCityInfo> cityList = new ArrayList<AppAreaInfo.AreaCityInfo>();
                JSONArray cityArray = cityObject.getJSONArray(provinceModel.getCode());
                for (int i = 0; i < cityArray.length(); i++) {
                    JSONArray cityInfoArray = cityArray.getJSONArray(i);
                    AppAreaInfo.AreaCityInfo cityModel = new AppAreaInfo.AreaCityInfo();
                    cityModel.setName(cityInfoArray.getString(0));
                    cityModel.setCode(cityInfoArray.getString(1));
                    List<AppAreaInfo.AreaDistrictInfo> districtList = new ArrayList<AppAreaInfo.AreaDistrictInfo>();
                    JSONArray districtArray = districtObject.getJSONArray(cityModel.getCode());
                    for (int j = 0; j < districtArray.length(); j++) {
                        JSONArray districtInfoArray = districtArray.getJSONArray(j);
                        AppAreaInfo.AreaDistrictInfo districtModel = new AppAreaInfo.AreaDistrictInfo();
                        districtModel.setName(districtInfoArray.getString(0));
                        districtModel.setCode(districtInfoArray.getString(1));
                        districtList.add(districtModel);
                    }
                    cityModel.setDistrictList(districtList);
                    cityList.add(cityModel);
                }
                provinceModel.setCityList(cityList);
                mProvinceList.add(provinceModel);
            }
            Collections.sort(mProvinceList);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private String[] getProvinces() {
        try {
            if (mProvinceList != null) {
                String[] provinces = new String[mProvinceList.size()];
                for (int i = 0; i < mProvinceList.size(); i++) {
                    provinces[i] = mProvinceList.get(i).getName();
                }
                return provinces;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    private String[] getCities(int provinceIndex) {
        try {
            if (mProvinceList != null && mProvinceList.size() > provinceIndex) {
                List<AppAreaInfo.AreaCityInfo> cityList = mProvinceList.get(provinceIndex).getCityList();
                String[] cities = new String[cityList.size()];
                for (int i = 0; i < cityList.size(); i++) {
                    cities[i] = cityList.get(i).getName();
                }
                return cities;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    private String[] getDistricts(int provinceIndex, int cityIndex) {
        try {
            if (mProvinceList != null && mProvinceList.size() > provinceIndex && mProvinceList.get(provinceIndex).getCityList() != null && mProvinceList.get(provinceIndex).getCityList().size() > cityIndex) {
                List<AppAreaInfo.AreaDistrictInfo> districtList = mProvinceList.get(provinceIndex).getCityList().get(cityIndex).getDistrictList();
                String[] districts = new String[districtList.size()];
                for (int i = 0; i < districtList.size(); i++) {
                    districts[i] = districtList.get(i).getName();
                }
                return districts;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    private void setResultBack(AppAreaInfo pickAreaInfo) {
        Intent intent = new Intent();
        intent.putExtra("pickAreaInfo", pickAreaInfo);
        setResult(AREA_CHOOSE_RESULTCODE, intent);
        finish();
    }

    private void setPickAreaInfo(String code) {
        try {
            if (code != null) {
                int provinceSel = 0, citySel = 0, districtSel = 0;
                for (int i = 0; i < mProvinceList.size(); i++) {
                    if (code.equals(mProvinceList.get(i).getCode())) {
                        provinceSel = i;
                    } else {
                        for (int j = 0; j < mProvinceList.get(i).getCityList().size(); j++) {
                            if (code.equals(mProvinceList.get(i).getCityList().get(j).getCode())) {
                                provinceSel = i;
                                citySel = j;
                            } else {
                                for (int k = 0; k < mProvinceList.get(i).getCityList().get(j)
                                        .getDistrictList().size(); k++) {
                                    if (code.equals(mProvinceList.get(i).getCityList().get(j)
                                            .getDistrictList().get(k).getCode())) {
                                        provinceSel = i;
                                        citySel = j;
                                        districtSel = k;
                                    }
                                }
                            }
                        }
                    }
                }
                provincePicker_WV.setCurrentItem(provinceSel);
                cityPicker_WV.setCurrentItem(citySel);
                districtPicker_WV.setCurrentItem(districtSel);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getAreaMap(Context context, String code) {
        try {
            showChinese = true;
            if (mProvinceList == null) {
                initAreaData(context);
            }
            HashMap<String, String> areaMap = new HashMap<String, String>();
            for (int i = 0; i < mProvinceList.size(); i++) {
                AppAreaInfo.AreaProvinceInfo provinceModel = mProvinceList.get(i);
                //同省
                if (code.substring(0, 2).equals(provinceModel.getCode().substring(0, 2))) {
                    if (code.equals(provinceModel.getCode())) {
                        areaMap.put(provinceModel.getCode(), provinceModel.getName());
                    } else {
                        for (int j = 0; j < provinceModel.getCityList().size(); j++) {
                            AppAreaInfo.AreaCityInfo cityModel = provinceModel.getCityList().get(j);
                            //同市
                            if (code.substring(2, 4).equals(cityModel.getCode().substring(2, 4))) {
                                if (code.equals(cityModel.getCode())) {
                                    areaMap.put(cityModel.getCode(), cityModel.getName());
                                } else {
                                    for (int k = 0; k < provinceModel.getCityList().get(j).getDistrictList().size(); k++) {
                                        AppAreaInfo.AreaDistrictInfo districtModel = provinceModel.getCityList().get(j).getDistrictList().get(k);
                                        if (code.equals(districtModel.getCode())) {
                                            areaMap.put(districtModel.getCode(), districtModel.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return areaMap;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> getMultiAreaMap(Context context, String[] codes) {
        HashMap<String, String> areaMap = new HashMap<String, String>();
        for (int i = 0; i < codes.length; i++) {
            areaMap.putAll(getAreaMap(context, codes[i]));
        }
        return areaMap;
    }
}
