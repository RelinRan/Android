package com.android.address;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.R;
import com.android.app.BaseApplication;
import com.android.app.dialog.Dialog;
import com.android.io.IOUtils;
import com.android.json.JsonParser;
import com.android.utils.ListUtils;
import com.android.utils.Screen;
import com.android.view.LoopView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RelinRan
 * @date 2019-09-10
 * @description 地址选择器<br                                                                                                                                                                                                                                                               />
 * 可以设置对应标题颜色、大小、分割线样式<br/>
 * 注意：在builder之后需要show方法显示。<br/>
 */
public class AddressSelector {

    /**
     * JSON - 城市key
     */
    public final String JSON_KEY_CITIES;

    /**
     * JSON - 地区key
     */
    public final String JSON_KEY_DISTRICTS;

    /**
     * JSON - 省份ID key
     */
    public final String JSON_KEY_PROVINCE_ID;

    /**
     * JSON - 城市ID key
     */
    public final String JSON_KEY_CITY_ID;

    /**
     * JSON - 地区ID key
     */
    public final String JSON_KEY_DISTRICT_ID;

    /**
     * JSON - 省份名称 key
     */
    public final String JSON_KEY_PROVINCE_NAME;

    /**
     * JSON - 区域名称 key
     */
    public final String JSON_KEY_DISTRICT_NAME;

    /**
     * JSON - 城市名称 key
     */
    public final String JSON_KEY_CITY_NAME;

    /**
     * 上下文
     */
    public final Context context;

    /**
     * Json文件名，放入main/assets文件夹里面
     */
    public final String jsonName;

    /**
     * 标题颜色
     */
    public final int titleBarBackgroundColor;

    /**
     * 标题字体颜色
     */
    public final int titleBarCancelTextColor;

    /**
     * 标题字体颜色
     */
    public final int titleBarConfirmTextColor;

    /**
     * 标题栏字体大小
     */
    public final int titleBarTextSize;

    /**
     * 分割线颜色
     */
    public final int dividerColor;

    /**
     * 选择颜色
     */
    public final int selectedColor;

    /**
     * 未选中颜色
     */
    public final int unselectedColor;

    /**
     * 字体大小
     */
    public final int textSize;

    /**
     * 背景是否半透明
     */
    public final boolean translucent;

    /**
     * 默认的省份
     */
    public final String province;
    public final String provinceId;

    /**
     * 默认的市
     */
    public final String city;

    /**
     * 城市id
     */
    public final String cityId;

    /**
     * 默认的区
     */
    public final String area;

    /**
     * 区域Id
     */
    public final String areaId;

    /**
     * 地址选择回调函数
     */
    public final OnAddressSelectListener listener;

    /**
     * 对话框对象
     */
    private Dialog dialog;

    /**
     * json数据
     */
    public static List<Map<String, String>> jsonData;

    /**
     * 城市数据
     */
    public List<Map<String, String>> cityData;

    /**
     * 区域数据
     */
    public List<Map<String, String>> areaData;

    /**
     * 省份数据
     */
    private List<String> provinceList;

    /**
     * 城市数据
     */
    private List<String> cityList;

    /**
     * 区域数据
     */
    private List<String> areaList;

    /**
     * 选中的省
     */
    private String selectProvince;

    /**
     * 选中省份ID
     */
    private String selectProvinceId;


    /**
     * 选中的市
     */
    private String selectCity;

    /**
     * 选中城市ID
     */
    private String selectCityId;

    /**
     * 选中的区域
     */
    private String selectArea;

    /**
     * 选中区域ID
     */
    private String selectAreaId;

    /**
     * 省可见性
     */
    private int provinceVisibility;

    /**
     * 区域可见性
     */
    private int cityVisibility;

    /**
     * 地区可见性
     */
    private int areaVisibility;

    /**
     * 构造函数
     *
     * @param builder 构造参数对象，提供选择器参数
     */
    public AddressSelector(Builder builder) {
        this.context = builder.context;
        this.jsonName = builder.jsonName;
        this.JSON_KEY_CITIES = builder.jsonKeyCities;
        this.JSON_KEY_DISTRICTS = builder.jsonKeyDistricts;
        this.JSON_KEY_PROVINCE_ID = builder.jsonKeyProvinceId;
        this.JSON_KEY_CITY_ID = builder.jsonKeyCityId;
        this.JSON_KEY_DISTRICT_ID = builder.jsonKeyDistrictId;
        this.JSON_KEY_PROVINCE_NAME = builder.jsonKeyProvinceName;
        this.JSON_KEY_CITY_NAME = builder.jsonKeyCityName;
        this.JSON_KEY_DISTRICT_NAME = builder.jsonKeyDistrictName;
        this.translucent = builder.translucent;
        this.titleBarBackgroundColor = builder.titleBarBackgroundColor;
        this.titleBarCancelTextColor = builder.titleBarCancelTextColor;
        this.titleBarConfirmTextColor = builder.titleBarConfirmTextColor;
        this.titleBarTextSize = builder.titleBarTextSize;
        this.dividerColor = builder.dividerColor;
        this.selectedColor = builder.selectedColor;
        this.unselectedColor = builder.unselectedColor;
        this.textSize = builder.textSize;
        this.province = builder.province;
        this.provinceId = builder.provinceId;
        this.city = builder.city;
        this.cityId = builder.cityId;
        this.area = builder.area;
        this.areaId = builder.areaId;
        this.listener = builder.listener;
        this.provinceVisibility = builder.provinceVisibility;
        this.cityVisibility = builder.cityVisibility;
        this.areaVisibility = builder.areaVisibility;
        createDialog();
        show();
    }

    /**
     * 地址选择器构造者
     */
    public static class Builder {

        /**
         * 上下文
         */
        private Context context;

        /**
         * 城市JSON key
         */
        private String jsonKeyCities = "children";
        /**
         * 地区JSON key
         */
        private String jsonKeyDistricts = "children";

        /**
         * 省JSON key id
         */
        private String jsonKeyProvinceId = "id";


        /**
         * 城市JSON key id
         */
        private String jsonKeyCityId = "id";

        /**
         * 地区JSON key id
         */
        private String jsonKeyDistrictId = "id";

        /**
         * 省JSON key name
         */
        private String jsonKeyProvinceName = "title";

        /**
         * 地区JSON key name
         */
        private String jsonKeyDistrictName = "title";

        /**
         * 城市JSON key name
         */
        private String jsonKeyCityName = "title";

        /**
         * JSON 字符串（一般是放入main/assets/address.json,利用IOUtils.readAssets(context,"address.json")方式获取内容）
         */
        private String jsonName;

        /**
         * 是否半透明，默认true
         */
        private boolean translucent = true;

        /**
         * 标题栏背景颜色，默认#F2F2F2
         */
        private int titleBarBackgroundColor = Color.parseColor("#F2F2F2");

        /**
         * 标题栏取消按钮颜色，默认#454545
         */
        private int titleBarCancelTextColor = Color.parseColor("#454545");

        /**
         * 标题栏去人按钮颜色，默认#0EB692
         */
        private int titleBarConfirmTextColor = Color.parseColor("#0EB692");

        /**
         * 标题栏大小，默认16
         */
        private int titleBarTextSize = 16;

        /**
         * 分割线颜色，默认#CDCDCD
         */
        private int dividerColor = Color.parseColor("#CDCDCD");

        /**
         * 选中颜色，默认#0EB692
         */
        private int selectedColor = Color.parseColor("#0EB692");

        /**
         * 未选中颜色，默认#AEAEAE
         */
        private int unselectedColor = Color.parseColor("#AEAEAE");

        /**
         * 字体大小
         */
        private int textSize = 16;

        /**
         * 省名称
         */
        private String province;

        /**
         * 城市名称
         */
        private String city;

        /**
         * 区域名称
         */
        private String area;

        /**
         * 省ID
         */
        private String provinceId;

        /**
         * 城市ID
         */
        private String cityId;

        /**
         * 区域ID
         */
        private String areaId;

        /**
         * 地址选中监听
         */
        private OnAddressSelectListener listener;

        /**
         * 省可见性
         */
        private int provinceVisibility;

        /**
         * 市可见性
         */
        private int cityVisibility;

        /**
         * 区域可见性
         */
        private int areaVisibility;

        /**
         * 地址构造器
         *
         * @param context 上下文
         */
        public Builder(Context context) {
            this.context = context;
            jsonData = JsonParser.parseJSONArray(IOUtils.readAssets(BaseApplication.app, "yunxiang.json"));
        }

        /**
         * 获取Json文件名称
         *
         * @return String
         */
        public String jsonName() {
            return jsonName;
        }

        /**
         * 设置JSON 文件名称
         *
         * @param jsonName JSON 文件名称 （一般是放入main/assets/address.json,利用IOUtils.readAssets(context,"address.json")方式获取内容）
         * @return Builder
         */
        public Builder jsonName(String jsonName) {
            this.jsonName = jsonName;
            jsonData = JsonParser.parseJSONArray(IOUtils.readAssets(BaseApplication.app, jsonName));
            return this;
        }

        /**
         * 获取城市JSON key name
         *
         * @return String
         */
        public String jsonKeyCities() {
            return jsonKeyCities;
        }

        /**
         * 设置城市JSON key name
         *
         * @return Builder
         */
        public Builder jsonKeyCities(String jsonKeyCities) {
            this.jsonKeyCities = jsonKeyCities;
            return this;
        }

        /**
         * 获取区域JSON key name
         *
         * @return String
         */
        public String jsonKeyDistricts() {
            return jsonKeyDistricts;
        }

        /**
         * 设置区域JSON key name
         *
         * @param jsonKeyDistricts 区域JSON key name
         * @return Builder
         */
        public Builder jsonKeyDistricts(String jsonKeyDistricts) {
            this.jsonKeyDistricts = jsonKeyDistricts;
            return this;
        }

        /**
         * 获取省JSON key id
         *
         * @return String
         */
        public String jsonKeyProvinceId() {
            return jsonKeyProvinceId;
        }

        /**
         * 设置省JSON key id
         *
         * @return String
         */
        public Builder jsonKeyProvinceId(String jsonKeyProvinceId) {
            this.jsonKeyProvinceId = jsonKeyProvinceId;
            return this;
        }

        /**
         * 获取城市JSON key id
         *
         * @return String
         */
        public String jsonKeyCityId() {
            return jsonKeyCityId;
        }

        /**
         * 设置城市JSON key id
         *
         * @return Builder
         */
        public Builder jsonKeyCityId(String jsonKeyCityId) {
            this.jsonKeyCityId = jsonKeyCityId;
            return this;
        }

        /**
         * 获取地区JSON key id
         *
         * @return String
         */
        public String jsonKeyDistrictId() {
            return jsonKeyDistrictId;
        }

        /**
         * 设置地区JSON key id
         *
         * @return Builder
         */
        public Builder jsonKeyDistrictId(String jsonKeyDistrictId) {
            this.jsonKeyDistrictId = jsonKeyDistrictId;
            return this;
        }

        /**
         * 获取省JSON key id
         *
         * @return String
         */
        public String jsonKeyProvinceName() {
            return jsonKeyProvinceName;
        }

        /**
         * 设置省JSON key id
         *
         * @return Builder
         */
        public Builder jsonKeyProvinceName(String jsonKeyProvinceName) {
            this.jsonKeyProvinceName = jsonKeyProvinceName;
            return this;
        }

        /**
         * 获取地区JSON key id
         *
         * @return String
         */
        public String jsonKeyDistrictName() {
            return jsonKeyDistrictName;
        }

        /**
         * 设置地区JSON key id
         *
         * @return Builder
         */
        public Builder jsonKeyDistrictName(String jsonKeyDistrictName) {
            this.jsonKeyDistrictName = jsonKeyDistrictName;
            return this;
        }

        /**
         * 获取城市JSON key name
         *
         * @return String
         */
        public String jsonKeyCityName() {
            return jsonKeyCityName;
        }

        /**
         * 设置城市JSON key name
         *
         * @return Builder
         */
        public Builder jsonKeyCityName(String jsonKeyCityName) {
            this.jsonKeyCityName = jsonKeyCityName;
            return this;
        }

        /**
         * 是否半透明背景
         *
         * @return boolean
         */
        public boolean isTranslucent() {
            return translucent;
        }

        /**
         * 设置背景是否半透明
         *
         * @param translucent 是否半透明
         * @return Builder
         */
        public Builder translucent(boolean translucent) {
            this.translucent = translucent;
            return this;
        }

        /**
         * 获取上下文对象
         *
         * @return 选择器构造者
         */
        public Context context() {
            return context;
        }

        /**
         * 获取标题栏背景颜色
         *
         * @return int
         */
        public int titleBarBackgroundColor() {
            return titleBarBackgroundColor;
        }

        /**
         * 设置标题栏背景颜色
         *
         * @param titleBarBackgroundColor 标题栏背景颜色，16进制
         * @return Builder
         */
        public Builder titleBarBackgroundColor(int titleBarBackgroundColor) {
            this.titleBarBackgroundColor = titleBarBackgroundColor;
            return this;
        }

        /**
         * 获取标题栏取消按钮文字颜色
         *
         * @return int
         */
        public int titleBarCancelTextColor() {
            return titleBarCancelTextColor;
        }

        /**
         * 设置标题栏取消按钮文字颜色
         *
         * @param titleBarCancelTextColor 标题栏取消按钮文字颜色，16进制
         * @return Builder
         */
        public Builder titleBarCancelTextColor(int titleBarCancelTextColor) {
            this.titleBarCancelTextColor = titleBarCancelTextColor;
            return this;
        }

        /**
         * 获取标题栏确认按钮文字颜色
         *
         * @return int
         */
        public int titleBarConfirmTextColor() {
            return titleBarConfirmTextColor;
        }

        /**
         * 设置标题栏确认按钮文字颜色，16进制
         *
         * @param titleBarConfirmTextColor 标题栏确认按钮文字颜色，16进制
         * @return Builder
         */
        public Builder titleBarConfirmTextColor(int titleBarConfirmTextColor) {
            this.titleBarConfirmTextColor = titleBarConfirmTextColor;
            return this;
        }

        /**
         * 获取标题栏文字大小
         *
         * @return int
         */
        public int titleBarTextSize() {
            return titleBarTextSize;
        }

        /**
         * 设置标题栏文字大小
         *
         * @param titleBarTextSize 标题栏文字大小（int）
         * @return Builder
         */
        public Builder titleBarTextSize(int titleBarTextSize) {
            this.titleBarTextSize = titleBarTextSize;
            return this;
        }

        /**
         * 获取分割线颜色
         *
         * @return int
         */
        public int dividerColor() {
            return dividerColor;
        }

        /**
         * 设置分割线颜色
         *
         * @param dividerColor 分割线颜色，16进制
         * @return Builder
         */
        public Builder dividerColor(int dividerColor) {
            this.dividerColor = dividerColor;
            return this;
        }

        /**
         * 获取选中文字颜色
         *
         * @return int
         */
        public int selectedColor() {
            return selectedColor;
        }

        /**
         * 设置选中文字颜色
         *
         * @param selectedColor 选中文字颜色，16进制
         * @return Builder
         */
        public Builder selectedColor(int selectedColor) {
            this.selectedColor = selectedColor;
            return this;
        }

        /**
         * 获取未选中颜色，16进制
         *
         * @return int
         */
        public int unselectedColor() {
            return unselectedColor;
        }

        /**
         * 设置未选中颜色，16进制
         *
         * @param unselectedColor 未选中颜色，16进制
         * @return Builder
         */
        public Builder unselectedColor(int unselectedColor) {
            this.unselectedColor = unselectedColor;
            return this;
        }

        /**
         * 获取文字大小
         *
         * @return int
         */
        public int textSize() {
            return textSize;
        }

        /**
         * 设置文字大小
         *
         * @param textSize 文字大小（int）
         * @return Builder
         */
        public Builder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        /**
         * 获取省名
         *
         * @return String
         */
        public String province() {
            return province;
        }

        /**
         * 设置省
         *
         * @param province 省名称
         * @return Builder
         */
        public Builder province(String province) {
            this.province = province;
            return this;
        }

        /**
         * 获取城市名称
         *
         * @return String
         */
        public String city() {
            return city;
        }

        /**
         * 设置城市名称
         *
         * @param city 城市名称
         * @return Builder
         */
        public Builder city(String city) {
            this.city = city;
            return this;
        }

        /**
         * 获取地区名字
         *
         * @return String
         */
        public String area() {
            return area;
        }

        /**
         * 设置地区名字
         *
         * @param area 地区名字
         * @return Builder
         */
        public Builder area(String area) {
            this.area = area;
            return this;
        }

        /**
         * 获取省名称
         *
         * @return String
         */
        public String provinceId() {
            return provinceId;
        }

        /**
         * 设置省名称
         *
         * @param provinceId 省名称
         * @return Builder
         */
        public Builder provinceId(String provinceId) {
            this.provinceId = provinceId;
            return this;
        }

        /**
         * 获取城市ID
         *
         * @return String
         */
        public String cityId() {
            return cityId;
        }

        /**
         * 设置城市ID
         *
         * @param cityId 城市ID
         * @return Builder
         */
        public Builder cityId(String cityId) {
            this.cityId = cityId;
            return this;
        }

        /**
         * 获取区域ID
         *
         * @return 区域ID
         */
        public String areaId() {
            return areaId;
        }

        /**
         * 设置区域ID
         *
         * @param areaId 区域ID
         * @return Builder
         */
        public Builder areaId(String areaId) {
            this.areaId = areaId;
            return this;
        }

        /**
         * 获取地址选择监听
         *
         * @return OnAddressSelectListener
         */
        public OnAddressSelectListener listener() {
            return listener;
        }

        /**
         * 设置地址选择监听
         *
         * @param listener
         * @return Builder
         */
        public Builder listener(OnAddressSelectListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 省可见性
         *
         * @return
         */
        public int provinceVisibility() {
            return provinceVisibility;
        }

        /**
         * 设置省可见性
         *
         * @param provinceVisibility
         * @return
         */
        public Builder provinceVisibility(int provinceVisibility) {
            this.provinceVisibility = provinceVisibility;
            return this;
        }

        /**
         * 市可见性
         *
         * @return
         */
        public int cityVisibility() {
            return cityVisibility;
        }

        /**
         * 设置市可见性
         *
         * @param cityVisibility
         */
        public Builder cityVisibility(int cityVisibility) {
            this.cityVisibility = cityVisibility;
            return this;
        }

        /**
         * 区域可见性
         *
         * @return
         */
        public int areaVisibility() {
            return areaVisibility;
        }

        /**
         * 设置区域可见性
         *
         * @param areaVisibility
         */
        public Builder areaVisibility(int areaVisibility) {
            this.areaVisibility = areaVisibility;
            return this;
        }

        /**
         * 创建地址选择器对象
         *
         * @return AddressSelector
         */
        public AddressSelector build() {
            return new AddressSelector(this);
        }
    }

    /**
     * 创建对话框
     */
    private void createDialog() {
        dialog = new Dialog.Builder(context)
                .width(Screen.width())
                .height(LinearLayout.LayoutParams.WRAP_CONTENT)
                .layoutResId(R.layout.android_dialog_address)
                .animResId(R.style.android_anim_bottom)
                .canceledOnTouchOutside(true)
                .cancelable(true)
                .themeResId(translucent ? R.style.Android_Theme_Dialog_Translucent_Background : R.style.Android_Theme_Dialog_Transparent_Background)
                .gravity(Gravity.BOTTOM)
                .build();
        LinearLayout ll_bar = dialog.contentView.findViewById(R.id.ll_bar);
        TextView tv_cancel = dialog.contentView.findViewById(R.id.tv_cancel);
        TextView tv_complete = dialog.contentView.findViewById(R.id.tv_complete);
        final LoopView lv_province = dialog.contentView.findViewById(R.id.lv_province);
        final LoopView lv_city = dialog.contentView.findViewById(R.id.lv_city);
        final LoopView lv_area = dialog.contentView.findViewById(R.id.lv_area);

        ll_bar.setBackgroundColor(titleBarBackgroundColor);
        tv_cancel.setTextColor(titleBarCancelTextColor);
        tv_complete.setTextColor(titleBarConfirmTextColor);
        tv_cancel.setTextSize(titleBarTextSize);
        tv_complete.setTextSize(titleBarTextSize);

        lv_province.setDividerColor(dividerColor);
        lv_city.setDividerColor(dividerColor);
        lv_area.setDividerColor(dividerColor);

        lv_province.setCenterTextColor(selectedColor);
        lv_city.setCenterTextColor(selectedColor);
        lv_area.setCenterTextColor(selectedColor);

        lv_province.setOuterTextColor(unselectedColor);
        lv_city.setOuterTextColor(unselectedColor);
        lv_area.setOuterTextColor(unselectedColor);

        lv_province.setTextSize(textSize);
        lv_city.setTextSize(textSize);
        lv_area.setTextSize(textSize);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (listener != null) {
                    int provincePosition = lv_province.getSelectedItem();
                    int cityPosition = lv_city.getSelectedItem();
                    int areaPosition = lv_area.getSelectedItem();
                    selectProvince = provinceList.get(provincePosition);
                    selectCity = cityList.get(cityPosition);
                    selectArea = areaList.get(areaPosition);
                    selectProvinceId = jsonData.get(provincePosition).get(JSON_KEY_PROVINCE_ID);
                    selectCityId = cityData.get(cityPosition).get(JSON_KEY_CITY_ID);
                    selectAreaId = areaData.get(areaPosition).get(JSON_KEY_DISTRICT_ID);
                    listener.onAddressSelected(selectProvince, selectCity, selectArea, selectProvinceId, selectCityId, selectAreaId);
                }
            }
        });
        //==============填充省数据=================
        provinceList = new ArrayList<>();
        int selectedProvincePosition = 0;
        for (int i = 0; i < ListUtils.getSize(jsonData); i++) {
            Map<String, String> item = jsonData.get(i);
            String provinceName = item.get(JSON_KEY_PROVINCE_NAME);
            String province_id = item.get(JSON_KEY_PROVINCE_ID);
            provinceList.add(provinceName);
            if (provinceName.equals(province) || province_id.equals(provinceId)) {
                selectedProvincePosition = i;
            }
        }
        cityData = JsonParser.parseJSONArray(jsonData.get(selectedProvincePosition).get(JSON_KEY_CITIES));
        selectProvince = provinceList.get(selectedProvincePosition);
        lv_province.setItems(provinceList);
        lv_province.setInitPosition(selectedProvincePosition);
        //===========填充市数据==========
        setCityData(lv_city);
        //===========填充区数据==========
        setAreaData(lv_area);
        //滑动监听
        lv_province.setOnItemSelectedListener(new LoopView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                cityData = JsonParser.parseJSONArray(jsonData.get(index).get(JSON_KEY_CITIES));
                setCityData(lv_city);
            }
        });
        lv_city.setOnItemSelectedListener(new LoopView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                areaData = JsonParser.parseJSONArray(cityData.get(index).get(JSON_KEY_DISTRICTS));
                setAreaData(lv_area);
            }
        });
        lv_province.setVisibility(provinceVisibility);
        lv_city.setVisibility(cityVisibility);
        lv_area.setVisibility(areaVisibility);
    }

    /**
     * 设置市数据
     *
     * @param lv_city 城市控件
     */
    private void setCityData(LoopView lv_city) {
        cityList = new ArrayList<>();
        int selectedCityPosition = 0;
        int citySize = ListUtils.getSize(cityData);
        for (int i = 0; i < citySize; i++) {
            Map<String, String> item = cityData.get(i);
            String cityName = item.get(JSON_KEY_CITY_NAME);
            String city_id = item.get(JSON_KEY_CITY_ID);
            cityList.add(cityName);
            if (cityName.equals(city) || city_id.equals(cityId)) {
                selectedCityPosition = i;
            }
        }
        areaData = JsonParser.parseJSONArray(cityData.get(selectedCityPosition).get(JSON_KEY_DISTRICTS));
        //如果只有一个，就不需要循环
        citySize = ListUtils.getSize(cityList);
        if (citySize == 1) {
            lv_city.setNotLoop();
        } else {
            lv_city.setLoop();
        }
        lv_city.setItems(cityList);
        selectCity = cityList.get(selectedCityPosition);
        lv_city.setInitPosition(selectedCityPosition);
    }

    /**
     * 设置区域数据
     *
     * @param lv_area 区域控件
     */
    private void setAreaData(LoopView lv_area) {
        areaList = new ArrayList<>();
        int selectedAreaPosition = 0;
        for (int i = 0; i < ListUtils.getSize(areaData); i++) {
            Map<String, String> item = areaData.get(i);
            String districtName = item.get(JSON_KEY_DISTRICT_NAME);
            String district_id = item.get(JSON_KEY_DISTRICT_ID);
            areaList.add(districtName);
            if (districtName.equals(area) || district_id.equals(areaId)) {
                selectedAreaPosition = i;
            }
        }
        //如果只有一个，就不需要循环
        int size = ListUtils.getSize(areaList);
        if (size == 1) {
            lv_area.setNotLoop();
        } else {
            lv_area.setLoop();
        }
        lv_area.setItems(areaList);
        if (ListUtils.getSize(areaList) > 0) {
            selectArea = areaList.get(selectedAreaPosition);
        }
        lv_area.setInitPosition(selectedAreaPosition);
    }

    /**
     * 显示
     */
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 消失
     */
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
