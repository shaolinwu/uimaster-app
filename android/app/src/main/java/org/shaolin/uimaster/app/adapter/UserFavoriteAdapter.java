package org.shaolin.uimaster.app.adapter;


import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.ListBaseAdapter;
import org.shaolin.uimaster.app.bean.Favorite;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserFavoriteAdapter extends ListBaseAdapter<Favorite> {
	
	static class ViewHolder {
		
		@InjectView(R.id.tv_favorite_title) TextView title;
		
		public ViewHolder(View view) {
			ButterKnife.inject(this,view);
		}
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_favorite, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Favorite favorite = (Favorite) mDatas.get(position);
		
		vh.title.setText(favorite.getTitle());
		return convertView;
	}

}
