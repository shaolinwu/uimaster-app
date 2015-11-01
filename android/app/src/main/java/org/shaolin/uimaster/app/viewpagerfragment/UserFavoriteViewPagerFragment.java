package org.shaolin.uimaster.app.viewpagerfragment;


import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.adapter.ViewPageFragmentAdapter;
import org.shaolin.uimaster.app.base.BaseListFragment;
import org.shaolin.uimaster.app.base.BaseViewPagerFragment;
import org.shaolin.uimaster.app.bean.Favorite;
import org.shaolin.uimaster.app.fragment.UserFavoriteFragment;

import android.os.Bundle;
import android.view.View;

public class UserFavoriteViewPagerFragment extends BaseViewPagerFragment {
	
	public static UserFavoriteViewPagerFragment newInstance(){
		return new UserFavoriteViewPagerFragment();
	}
	
	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {

		String[] title = getResources().getStringArray(R.array.userfavorite);
//		adapter.addTab(title[0], "favorite_software", UserFavoriteFragment.class, getBundle(Favorite.CATALOG_SOFTWARE));
	}
	
	private Bundle getBundle(int favoriteType) {
		Bundle bundle = new Bundle();
		bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, favoriteType);
		return bundle;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void initView(View view) {

	}

	@Override
	public void initData() {

	}

}
