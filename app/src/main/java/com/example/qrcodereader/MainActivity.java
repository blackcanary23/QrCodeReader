package com.example.qrcodereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity implements QrScanFragment.OnFragmentInteractionListener {

    private FragmentManager fMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fMan = getSupportFragmentManager();

        if (savedInstanceState == null) {

            QrScanFragment swFrag = new QrScanFragment();
            fMan.beginTransaction()
                    .add(R.id.container, swFrag, "qsFrag")
                    .commitAllowingStateLoss();
        }
    }


    @Override
    public void onFragmentInteraction(String rawData) {

        UrlListFragment ulFrag = new UrlListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("rawData", rawData);
        ulFrag.setArguments(bundle);

        fMan.beginTransaction()
                .replace(R.id.container, ulFrag, "ulFrag")
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}