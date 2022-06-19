package ir.atriatech.customlibs.magicindicator.abs;


public interface IPagerNavigator {

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);


    void onAttachToMagicIndicator();


    void onDetachFromMagicIndicator();

    void notifyDataSetChanged();
}
