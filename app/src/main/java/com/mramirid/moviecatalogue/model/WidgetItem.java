package com.mramirid.moviecatalogue.model;

import android.graphics.Bitmap;

public class WidgetItem {

	private String name;
	private Bitmap imgPoster;

	public WidgetItem(String name, Bitmap imgPoster) {
		this.name = name;
		this.imgPoster = imgPoster;
	}

	public String getName() {
		return name;
	}

	public Bitmap getImgPoster() {
		return imgPoster;
	}
}
