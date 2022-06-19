package ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.abs;

public interface IPagerTitleView {

    void onSelected(int index, int totalCount);


    void onDeselected(int index, int totalCount);

    /**
     *
     *
     * @param leavePercent , 0.0f - 1.0f
     * @param leftToRight
     */
    void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight);

    /**
     *
     *
     * @param enterPercent , 0.0f - 1.0f
     * @param leftToRight  nothing
     */
    void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight);
}
