package org.client.bracelet.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.client.bracelet.R;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class SportFragment extends Fragment {
    private String title;

    private static SportFragment singleton;

    public static SportFragment getInstance() {
        if (singleton == null) {
            singleton = new SportFragment();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = "运动";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_simple_card, container, false);
        TextView card_title_tv = (TextView) v.findViewById(R.id.card_title_tv);
        card_title_tv.setText(title);
        return v;
    }
}
