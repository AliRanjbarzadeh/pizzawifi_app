package ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.titles;

import android.content.Context;
import android.view.View;

import ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;


public class DummyPagerTitleView extends View implements IPagerTitleView {

    public DummyPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onSelected(int index, int totalCount) {
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
    }
}
