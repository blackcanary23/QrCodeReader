package com.example.qrcodereader;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UrlListFragment extends Fragment {

    private ArrayList<String> urlList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            urlList = (ArrayList<String>) savedInstanceState
                    .getSerializable("urlList");
        }
        else {
            Toast.makeText(getActivity(), "Successful reading!", Toast.LENGTH_SHORT).show();
            Bundle bundle = getArguments();
            assert bundle != null;
            String rawData = (String) bundle.getSerializable("rawData");

            parseRawData(rawData);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.url_list, container, false);

        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_UNSPECIFIED);

        /*Bundle bundle = getArguments();
        assert bundle != null;
        String rawData = (String) bundle.getSerializable("rawData");

        parseRawData(rawData);*/

        if (urlList.size() != 0) {

            RecyclerView recyclerView = view.findViewById(R.id.qs_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            UrlListAdapter qsAdapter = new UrlListAdapter(urlList);
            recyclerView.setAdapter(qsAdapter);
        }

        return view;
    }

    private void parseRawData(String rawData) {

        String regex = "\\(?\\b(https://|www[.]|http://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        urlList.clear();

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(rawData);

        while(m.find()) {

            String urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {

                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            urlList.add(urlStr);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putSerializable("urlList", urlList);
    }
}