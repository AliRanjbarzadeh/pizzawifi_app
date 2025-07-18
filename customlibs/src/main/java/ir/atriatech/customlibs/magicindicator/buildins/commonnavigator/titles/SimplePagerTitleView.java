package ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.titles;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;

import ir.atriatech.customlibs.magicindicator.buildins.UIUtil;
import ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView;


public class SimplePagerTitleView extends AppCompatTextView implements IMeasurablePagerTitleView {
	protected int mSelectedColor;
	protected int mNormalColor;

	public SimplePagerTitleView(Context context) {
		super(context, null);
		init(context);
	}

	private void init(Context context) {
		setGravity(Gravity.CENTER);
		getPaint().setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/app_font.ttf"));
		int padding = UIUtil.dip2px(context, 10);
		setPadding(padding, 0, padding, 0);
		setSingleLine();
		setEllipsize(TextUtils.TruncateAt.END);
//        setTextSize(12f);
	}

	@Override
	public void onSelected(int index, int totalCount) {
		setTextColor(mSelectedColor);
	}

	@Override
	public void onDeselected(int index, int totalCount) {
		setTextColor(mNormalColor);
	}

	@Override
	public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
	}

	@Override
	public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
	}

	@Override
	public int getContentLeft() {
		Rect bound = new Rect();
		String longestString = "";
		if (getText().toString().contains("\n")) {
			String[] brokenStrings = getText().toString().split("\\n");
			for (String each : brokenStrings) {
				if (each.length() > longestString.length()) longestString = each;
			}
		} else {
			longestString = getText().toString();
		}
		getPaint().getTextBounds(longestString, 0, longestString.length(), bound);

		int contentWidth = bound.width();
		return getLeft() + getWidth() / 2 - contentWidth / 2;
	}

	@Override
	public int getContentTop() {
		Paint.FontMetrics metrics = getPaint().getFontMetrics();
		float contentHeight = metrics.bottom - metrics.top;
		return (int) (getHeight() / 2 - contentHeight / 2);
	}

	@Override
	public int getContentRight() {
		Rect bound = new Rect();
		String longestString = "";
		if (getText().toString().contains("\n")) {
			String[] brokenStrings = getText().toString().split("\\n");
			for (String each : brokenStrings) {
				if (each.length() > longestString.length()) longestString = each;
			}
		} else {
			longestString = getText().toString();
		}
		getPaint().getTextBounds(longestString, 0, longestString.length(), bound);
//        getPaint().setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/app_font.ttf"));

		int contentWidth = bound.width();
		return getLeft() + getWidth() / 2 + contentWidth / 2;
	}

	@Override
	public int getContentBottom() {
		Paint.FontMetrics metrics = getPaint().getFontMetrics();
		getPaint().setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/app_font.ttf"));

		float contentHeight = metrics.bottom - metrics.top;
		return (int) (getHeight() / 2 + contentHeight / 2);
	}

	public int getSelectedColor() {
		return mSelectedColor;
	}

	public void setSelectedColor(int selectedColor) {
		mSelectedColor = selectedColor;
	}

	public int getNormalColor() {
		return mNormalColor;
	}

	public void setNormalColor(int normalColor) {
		mNormalColor = normalColor;
	}
}
