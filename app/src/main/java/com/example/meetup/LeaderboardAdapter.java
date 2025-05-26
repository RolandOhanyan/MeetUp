package com.example.meetup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class LeaderboardAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> categoryTitles;
    private Map<String, List<String>> leaderboardData;

    public LeaderboardAdapter(Context context, List<String> categoryTitles, Map<String, List<String>> leaderboardData) {
        this.context = context;
        this.categoryTitles = categoryTitles;
        this.leaderboardData = leaderboardData;
    }

    @Override
    public int getGroupCount() {
        return categoryTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String category = categoryTitles.get(groupPosition);
        return leaderboardData.get(category).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categoryTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String category = categoryTitles.get(groupPosition);
        return leaderboardData.get(category).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String categoryTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(categoryTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String leaderboardEntry = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(leaderboardEntry);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return false; }
}
