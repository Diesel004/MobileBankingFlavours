package com.trustbank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.trustbank.Model.FAQModel;
import com.trustbank.R;

import java.util.HashMap;
import java.util.List;


public class FAQExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    private HashMap<String, List<FAQModel>> _listDataChild;     // child data in format of header title, child title
    TextView lblListHeader;
    private FAQModel faqModel;

    public FAQExpandableListAdapter(Context context, List<String> listDataHeader,
                                    HashMap<String, List<FAQModel>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        faqModel = (FAQModel) getChild(groupPosition, childPosition);
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_expandable, null);
        }
        TextView answer_tv = (TextView) convertView.findViewById(R.id.answer_tv);
        answer_tv.setText(faqModel.getAnswers());
        answer_tv.setText(faqModel.getAnswers());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        try {
            String headerTitle = (String) getGroup(groupPosition);
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            lblListHeader = convertView.findViewById(R.id.lblListHeader);
            lblListHeader.setText(headerTitle);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}