package com.mramirid.moviecatalogue.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	private final List<Fragment> fragmentsList = new ArrayList<>();
	private final List<String> fragmentTitleList = new ArrayList<>();

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@NonNull
	@Override
	public Fragment getItem(int position) {
		return fragmentsList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentsList.size();
	}

	public void addFragment(Fragment fragment, String title) {
		fragmentsList.add(fragment);
		fragmentTitleList.add(title);
	}

	@Override
	public int getItemPosition(@NonNull Object object) {
		return POSITION_NONE;
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return fragmentTitleList.get(position);
	}
}
