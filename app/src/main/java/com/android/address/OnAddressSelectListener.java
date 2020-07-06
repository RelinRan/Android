package com.android.address;

/**
 * @author RelinRan
 * @date 2019-09-10
 * @description 地址选择器监听
 */
public interface OnAddressSelectListener {

    /**
     * 地址选择
     *
     * @param province   省
     * @param city       市
     * @param district   区
     * @param provinceId 省ID
     * @param cityId     市ID
     * @param districtId 地区ID
     */
    void onAddressSelected(String province, String city, String district, String provinceId, String cityId, String districtId);

}
