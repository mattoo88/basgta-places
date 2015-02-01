package com.bagsta.places;

public class QueryObject implements Comparable<QueryObject> {

	private String name;
//	private String neighbourhoodName;
	private long latitude;
	private long longitude;
	private String iconURl;
	private float distance;
	private int offers;
	private String[] types;
	private String categoriesShortString;
	

	public QueryObject(String name,long latitude, long longitude, String iconURl, float distance, String[] types) {
		this.name = name;
//		this.neighbourhoodName = neighbourhoodName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.iconURl = iconURl;
		this.distance = distance;
//		this.offers = offers;
		this.types = types;
		
		categoriesShortString = getStringFromArray(types," • ", 4);
	}

	public void setBrandName(String brandName) {
		this.name = name;
	}

	public String getBrandName() {
		return name;
	}

//	public void setNeighbourhoodName(String neighbourhoodName) {
//		this.neighbourhoodName = neighbourhoodName;
//	}

//	public String getNeighbourhoodName() {
//		return neighbourhoodName;
//	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	public long getLatitude() {
		return latitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	public long getLongitude() {
		return longitude;
	}
	public void setLogoURl(String logoURl) {
		this.iconURl = logoURl;
	}

	public String getIconURl() {
		return iconURl;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getDistance() {
		return distance;
	}
	
//	public void setOffers(int offers) {
//		this.offers = offers;
//	}
//
//	public int getOffers() {
//		return offers;
//	}

	public void setCategories(String[] types) {
		this.types = types;
	}

	public String[] getTypes() {
		return types;
	}
	
	public void setCategoriesShortString(String categoriesShortString) {
		this.categoriesShortString = categoriesShortString;
	}

	public String getCategoriesShortString() {
		return categoriesShortString;
	}
	
	String getStringFromArray(String[] arr, String appendText, int appendCount) {
		StringBuilder builder = new StringBuilder();
		int len = appendCount < arr.length ? appendCount : arr.length;
		for (int i = 0; i < len; i++) {
			if (!arr[i].trim().equals("")) {
				builder.append(appendText);
				builder.append(arr[i]);
			}
		}
		return builder.toString();
	}
	
	@Override
	public int compareTo(QueryObject anotherQueryObject) {
		if (this.distance > anotherQueryObject.getDistance()) {
			return 1;
		} else if (this.distance == anotherQueryObject.getDistance()) {
			return 0;
		} else {
			return -1;
		}
	}
}
