package com.example.number7.smarthome;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentOne extends Fragment {

	ImageView ivIcon;
	TextView tvItemName;


	public static final String IMAGE_RESOURCE_ID = "iconResourceID";
	public static final String ITEM_NAME = "itemName";

	public FragmentOne() {

	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_contol, container,
				false);

		ivIcon = (ImageView) view.findViewById(R.id.frag1_icon);
		tvItemName = (TextView) view.findViewById(R.id.frag1_text);

		/*tvItemName.setText(getArguments().getString(ITEM_NAME));
		ivIcon.setImageDrawable(view.getResources().getDrawable(
				getArguments().getInt(IMAGE_RESOURCE_ID)));*/
		return view;

	}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

}
