package ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.abs;


import java.util.List;

import ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.model.PositionData;


public interface IPagerIndicator {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onPositionDataProvide(List<PositionData> dataList);
}
