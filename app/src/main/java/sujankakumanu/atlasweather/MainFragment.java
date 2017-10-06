package sujankakumanu.atlasweather;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    CurrWeatherDown downloader = new CurrWeatherDown();
    RecyclerView cards;
    View extraView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("onCreateView", "Entered the onCreateView method");

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        cards = (RecyclerView) view.findViewById(R.id.cardList);

        downloader.loadPage(this.getContext(), cards);
        extraView=view;

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setEnabled(true);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab:
                cards = (RecyclerView) extraView.findViewById(R.id.cardList);
                downloader.loadPage(this.getContext(), cards);

        }
    }


}

