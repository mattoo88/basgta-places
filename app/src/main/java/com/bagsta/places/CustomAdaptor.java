package com.bagsta.places;

import java.util.ArrayList;

import com.bagsta.places.R;

import android.content.Context;
import android.text.AndroidCharacter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdaptor extends BaseAdapter  {

	Context mContext;
	ArrayList<QueryObject> arrayList;
	LayoutInflater inflater;
	int lastViewPosition; 
	
	public CustomAdaptor(Context mContext, ArrayList<QueryObject> arrayList) {
		this.mContext = mContext;
		this.arrayList = arrayList;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		MainActivity.imageLoader = new ImageLoader(mContext);
	}
	
	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		ImageView imageView;
		TextView brandName;
		TextView offers;
		TextView categories;
		TextView distance;
		TextView neighbourhoodname;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item, parent, false);
			
			holder = new ViewHolder();
			holder.brandName = (TextView) convertView.findViewById(R.id.brandName);
			holder.offers = (TextView) convertView.findViewById(R.id.offers);
			holder.categories = (TextView) convertView.findViewById(R.id.categories);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.neighbourhoodname = (TextView) convertView.findViewById(R.id.neighbourhoodName);
			holder.imageView = (ImageView) convertView.findViewById(R.id.logoImage);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		QueryObject queryObject = arrayList.get(position);
		
		holder.brandName.setText(queryObject.getBrandName());
//		int offer = queryObject.getOffers();
		holder.offers.setText("1 Offer");
		holder.categories.setText(String.valueOf((queryObject.getCategoriesShortString())));
		holder.distance.setText(String.valueOf(queryObject.getDistance())+" m");
//		holder.neighbourhoodname.setText(queryObject.getNeighbourhoodName());
		MainActivity.imageLoader.displayBitmap(holder.imageView,queryObject.getIconURl(), false);
		
		if (position > lastViewPosition) {
			Animation animation = AnimationUtils.loadAnimation(mContext,
					R.anim.entry_from_bottom);
			convertView.setAnimation(animation);
		}
		lastViewPosition = position;
		return convertView;
	}

}
