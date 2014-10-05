package com.crazyks.mt;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

public class Achievements extends ListActivity {
    private SimpleAdapter mAdapter = null;
    private ArrayList<HashMap<String, Object>> mItemList = null;
    private final String KEY_TITLE = "Title";
    private final String KEY_SUMMARY = "Summary";
    private final String KEY_STATUS = "Status";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initList();
        updateAchievements(Tmp.getLatestEgg());
    }
    
    private void initList() {
        mItemList = new ArrayList<HashMap<String, Object>>();
        mAdapter = new SimpleAdapter(Achievements.this, mItemList,
                R.layout.achv_item,
                new String[] { KEY_TITLE, KEY_SUMMARY, KEY_STATUS },
                new int[] { R.id.title, R.id.summary, R.id.status });
        getListView().setAdapter(mAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                if (position == mItemList.size() - 1) {
                    Egg egg = Tmp.getTmpEgg();
                    if (egg.FLAG_FOR_CLICK_AFTER_EMAIL_FOUND <= 0 && egg.FLAG_FOR_FIND_EMAIL > 0) {
                        egg.FLAG_FOR_CLICK_AFTER_EMAIL_FOUND++;
                        try {
                            Tmp.sync(Achievements.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        changeStatus(position, egg.isCurious() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
                    }
                }
            }
        });
    }

    private void addItem(int titleResId, int summaryResId, int statusResId) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_TITLE, getString(titleResId));
        map.put(KEY_SUMMARY, getString(summaryResId));
        map.put(KEY_STATUS, statusResId);
        mItemList.add(map);
    }
    
    private void changeStatus(int position,int statusResId) {
        if (position < 0 || position >= mItemList.size()) {
            return;
        }
        HashMap<String, Object> map = mItemList.get(position);
        map.put(KEY_STATUS, statusResId);
        mAdapter.notifyDataSetChanged();
    }
    
    private void updateAchievements(final Egg egg) {
        if (egg == null) {
            return;
        }
        mItemList.clear();
        addItem(R.string.well_palyed, R.string.well_palyed_summary, egg.isWellPlayed() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.wide_exp, R.string.wide_exp_summary, egg.isKnownMuch() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.loser, R.string.loser_summary, egg.isLoser() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.diy_life, R.string.diy_life_summary, egg.isDIYLife() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.beginner, R.string.beginner_summary, egg.isBeginner() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.practitioner, R.string.practitioner_summary, egg.isPractitioner() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.expert, R.string.expert_summary, egg.isExpert() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.sniper, R.string.sniper_summary, egg.isSniper() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.daily_practice, R.string.daily_practice_summary, egg.isDailyPractice() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        addItem(R.string.curiosity, R.string.curiosity_summary, egg.isCurious() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        mAdapter.notifyDataSetChanged();
    }
}
