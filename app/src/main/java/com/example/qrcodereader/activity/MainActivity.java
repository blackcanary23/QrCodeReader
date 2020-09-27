package com.example.qrcodereader.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import com.example.qrcodereader.R;
import com.example.qrcodereader.fragment.CertValidateFragment;
import com.example.qrcodereader.fragment.QrScanFragment;
import com.example.qrcodereader.fragment.UrlListFragment;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
                                                    QrScanFragment.OnReadButtonClicked,
                                                    UrlListFragment.OnValidateButtonClicked {

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
    public void onReadButtonClicked(String rawData) {

        UrlListFragment ulFrag = new UrlListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("rawData", rawData);
        ulFrag.setArguments(bundle);

        fMan.beginTransaction()
                .replace(R.id.container, ulFrag, "ulFrag")
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onValidateButtonClicked(ArrayList<String> urlList) {

        CertValidateFragment ulFrag = new CertValidateFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("urlList", urlList);
        ulFrag.setArguments(bundle);

        fMan.beginTransaction()
                .replace(R.id.container, ulFrag, "cvFrag")
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}