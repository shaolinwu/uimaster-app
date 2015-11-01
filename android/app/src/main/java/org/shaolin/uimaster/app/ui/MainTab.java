package org.shaolin.uimaster.app.ui;


import android.os.Bundle;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.fragment.FunctionFragment;
import org.shaolin.uimaster.app.fragment.MyInformationFragment;

public enum MainTab {

	MAIN(0, R.string.main_tab_name_main, R.drawable.tab_icon_main, FunctionFragment.class, new Bundle()),

	SHOPPING_CART(1, R.string.main_tab_name_shopping, R.drawable.tab_icon_cart, FunctionFragment.class, new Bundle()),
//  QUICK(1, R.string.main_tab_name_quick, R.drawable.tab_icon_new,null),
	ORDER(2, R.string.main_tab_name_order, R.drawable.tab_icon_order, FunctionFragment.class, new Bundle()),

	ME(3, R.string.main_tab_name_my, R.drawable.tab_icon_me, MyInformationFragment.class, new Bundle());

	private int idx;
	private int resName;
	private int resIcon;
	private Class<?> clz;
	private Bundle bundle;

	private MainTab(int idx, int resName, int resIcon, Class<?> clz, Bundle bundle) {
		this.idx = idx;
		this.resName = resName;
		this.resIcon = resIcon;
		this.clz = clz;
		this.bundle = bundle;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getResName() {
		return resName;
	}

	public void setResName(int resName) {
		this.resName = resName;
	}

	public int getResIcon() {
		return resIcon;
	}

	public void setResIcon(int resIcon) {
		this.resIcon = resIcon;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle b) {
		this.bundle = b;
	}
}
