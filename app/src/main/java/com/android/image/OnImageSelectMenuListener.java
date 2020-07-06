package com.android.image;

import java.util.List;

public interface OnImageSelectMenuListener {

    /**
     * 图片选择菜单
     *
     * @param list     菜单
     * @param position 菜单位置
     */
    void onImageSelectMenu(List<String> list, int position);

}
