package com.example.qrcodereader.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.qrcodereader.R;
import com.example.qrcodereader.adapter.CertValidateAdapter;
import com.example.qrcodereader.model.CertChainRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class CertValidateFragment extends Fragment {

    private ArrayList<CertChainRepository> chainList = new ArrayList<>();
    private ArrayList<String> certificateChain = new ArrayList<>();
    private Certificate[] certificates = new Certificate[0];
    private X509Certificate intermediate = null;
    private CertValidateAdapter cvAdapter;
    private boolean validity = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            chainList = (ArrayList<CertChainRepository>) savedInstanceState
                    .getSerializable("chainList");
        else {
            Bundle bundle = getArguments();
            assert bundle != null;

            ArrayList<String> urlList = (ArrayList<String>) bundle.getSerializable("urlList");

            validateCertificateChain(urlList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cert_validate_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.cv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cvAdapter = new CertValidateAdapter(chainList);
        recyclerView.setAdapter(cvAdapter);

        return view;
    }

    private void validateCertificateChain(final ArrayList<String> urlList) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                chainList.clear();

                for (String url : urlList) {

                    certificateChain.clear();

                    HttpsURLConnection connection = null;

                    try {

                        //url = new URL("https://site2.ru/cgi/users");

                        connection = (HttpsURLConnection) new URL(url).openConnection();
                        connection.connect();
                        certificates = connection.getServerCertificates();
                        //System.out.println(certificates.length + "LENGTH" + certificates[0]);
                        intermediate = (X509Certificate) certificates[certificates.length - 1];

                        certificateChain.add(((X509Certificate) certificates[0])
                                .getSubjectX500Principal().getName());

                        ((X509Certificate) certificates[0]).checkValidity();

                        for (int i = 0; i < certificates.length - 1; i++) {

                            certificates[i].verify(certificates[i + 1].getPublicKey());
                            //certificateChain.add(certificates[i]);
                            //certificateChain.add(certificates[i++]);
                            //System.out.println("Hello" + ((X509Certificate) certificates[i]).getSubjectX500Principal().getName() + " " + ((X509Certificate) certificates[i + 1]).getSubjectX500Principal().getName());
                            //certificateChain.add(((X509Certificate) certificates[i]).getSubjectX500Principal().getName());
                            certificateChain
                                    .add(((X509Certificate) certificates[i + 1])
                                    .getSubjectX500Principal().getName());
                            //break;
                        }
                    }
                    catch (CertificateException |
                            InvalidKeyException |
                            NoSuchAlgorithmException |
                            NoSuchProviderException |
                            SignatureException |
                            IOException |
                            ArrayIndexOutOfBoundsException ex) {

                        ex.printStackTrace();
                        validity = false;
                    }

                    finally {

                        if (connection != null)
                            connection.disconnect();
                    }

                    //
                    /*for (int i = 0; i < certificates.length - 1; i++) {

                        try {

                            assert intermediate != null;
                            certificates[i].verify(certificates[i + 1].getPublicKey());
                            //certificateChain.add(certificates[i]);
                            //certificateChain.add(certificates[i++]);
                            //System.out.println("Hello" + ((X509Certificate) certificates[i]).getSubjectX500Principal().getName() + " " + ((X509Certificate) certificates[i + 1]).getSubjectX500Principal().getName());
                            //certificateChain.add(((X509Certificate) certificates[i]).getSubjectX500Principal().getName());
                            certificateChain.add(((X509Certificate) certificates[i + 1]).getSubjectX500Principal().getName());
                            break;
                        }
                        catch (CertificateException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException ex) {

                            ex.printStackTrace();
                        }
                    }*/
                    //


                    //System.out.println("VALIDITY");

                    if (validity) {
                        getRootCertificate();
                        chainList.add(new CertChainRepository(certificateChain));
                    }
                }

                    if(getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            cvAdapter.notifyDataSetChanged();
                        }
                    });

            }
        }).start();
    }

    private void getRootCertificate() {

        TrustManagerFactory tmFactory;
        X509TrustManager x509Tm;

        try {

            tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmFactory.init((KeyStore) null);

            TrustManager[] trustManagers = tmFactory.getTrustManagers();
            x509Tm = (X509TrustManager) trustManagers[0];
            X509Certificate[] issuers = x509Tm.getAcceptedIssuers();

            for (X509Certificate issuer : issuers) {

                intermediate.verify(issuer.getPublicKey());
                //System.out.println(issuer.getSubjectX500Principal().getName() + "ISSUER");
                certificateChain.add(issuer.getSubjectX500Principal().getName());
                //System.out.println(certificateChain.size() + "SIZE");
                break;
            }
        }
        catch (NoSuchAlgorithmException |
                KeyStoreException |
                CertificateException |
                InvalidKeyException |
                SignatureException |
                NoSuchProviderException ex) {

            ex.printStackTrace();
        }

        /*TrustManager[] trustManagers = tmFactory.getTrustManagers();
        x509Tm = (X509TrustManager) trustManagers[0];
        X509Certificate[] issuers = x509Tm.getAcceptedIssuers();

        for (X509Certificate issuer : issuers) {

            try {

                assert intermediate != null;
                intermediate.verify(issuer.getPublicKey());
                //System.out.println(issuer.getSubjectX500Principal().getName() + "ISSUER");
                certificateChain.add(issuer.getSubjectX500Principal().getName());
                //System.out.println(certificateChain.size() + "SIZE");
                break;
            }
            catch (CertificateException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException ex) {

                ex.printStackTrace();
            }
        }*/

        parsePrincipal();
    }

    private void parsePrincipal() {

        String regex = "(?:^|,\\s?)(?:CN=(?<val>\"(?:[^\"]|\"\")+\"|[^,]+))"; //

        Pattern p = Pattern.compile(regex);

        for (int i = 0; i < certificateChain.size(); i++) {

            Matcher m = p.matcher(certificateChain.get(i));

            while(m.find()) {

                certificateChain.set(i, m.group(1));
            }
        }
        //System.out.println(certificateChain.size() + "SIZE");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putSerializable("chainList", chainList);
    }
}