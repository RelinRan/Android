package com.android.app.dialog;

/**
 * Author: Relin
 * Describe: Item选择监听
 * Date:2020/7/6 22:40
 */
public interface OnItemSelectListener {

    /**
     * Item选择
     *
     * @param content  内容
     * @param position 位置
     */
    void onItemSelect(String content, int position);

}
