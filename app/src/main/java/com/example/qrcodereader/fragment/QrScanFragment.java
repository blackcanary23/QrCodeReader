package com.example.qrcodereader.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.qrcodereader.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;
import java.util.Objects;


public class QrScanFragment extends Fragment {

    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private OnReadButtonClicked mListener;
    private static final int PERMISSION_REQUEST_CODE = 1;


    public interface OnReadButtonClicked {

        void onReadButtonClicked(String rawData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.qr_scan, container, false);

        Objects.requireNonNull(getActivity())
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraView = view.findViewById(R.id.camera_view);
        Button readButton = view.findViewById(R.id.read);

        barcodeDetector =
                new BarcodeDetector.Builder(getActivity())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(Objects.requireNonNull(getActivity()), barcodeDetector)
                .setRequestedPreviewSize(300, 300)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                    return;
                }

                try {

                    cameraSource.start(cameraView.getHolder());
                }
                catch (IOException ex) {

                    ex.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                cameraSource.stop();
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                getRawData();
            }
        });

        return view;
    }

    public void getRawData() {

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                try {

                    //parseRawData(barcodes.valueAt(0).rawValue);
                    //parseRawData("https://yandex.ru, https://www.dubai.com/, http://site1.ru, http://site2.ru/cgi/users, https://one.site3.org?");
                    //barcodeInfo.setText(links.toString());
                    mListener.onReadButtonClicked(barcodes.valueAt(0).rawValue);
                }
                catch (ArrayIndexOutOfBoundsException ex) {

                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);

        try {

            mListener = (OnReadButtonClicked) context;
        }
        catch (ClassCastException ex) {

            throw new ClassCastException(context.toString()
                    + " must implement OnOnReadButtonClicked");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            try {

                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                cameraSource.start(cameraView.getHolder());
            }
            catch (IOException ex) {

                ex.printStackTrace();
            }
        }
        else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
    }
}