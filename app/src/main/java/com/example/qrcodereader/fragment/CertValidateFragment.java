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
import java.net.URL;
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
import java.util.Objects;
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
    private TrustManagerFactory tmFactory;

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

                    if (url.contains("http://"))
                        url = url.replace("http://", "https://");

                    HttpsURLConnection connection = null;
                    String hostName = null;

                    try {

                        hostName = new URL(url).getHost();

                        connection = (HttpsURLConnection) new URL(url).openConnection();
                        connection.connect();

                        certificates = connection.getServerCertificates();

                        //ToDo: Fix if Server returns ROOT
                        intermediate = (X509Certificate) certificates[certificates.length - 1];

                        certificateChain.add(((X509Certificate) certificates[0])
                                .getSubjectX500Principal().getName());

                        //ToDo: Verify HostName, Expiration Date
                        ((X509Certificate) certificates[0]).checkValidity();

                        tmFactory = TrustManagerFactory
                                .getInstance(TrustManagerFactory
                                        .getDefaultAlgorithm());
                        tmFactory.init((KeyStore) null);


                        for (int i = 0; i < certificates.length - 1; i++) {

                            certificates[i].verify(certificates[i + 1].getPublicKey());
                            certificateChain
                                    .add(((X509Certificate) certificates[i + 1])
                                    .getSubjectX500Principal().getName());
                        }
                    }
                    catch (CertificateException |
                            InvalidKeyException |
                            NoSuchAlgorithmException |
                            NoSuchProviderException |
                            SignatureException |
                            KeyStoreException ex) {

                        ex.printStackTrace();
                        validity = false;
                    }
                    //ToDo: Get Certificate Chain (http://site1.ru/)
                    catch (SSLPeerUnverifiedException ex) {

                        ex.printStackTrace();
                        validity = false;
                        certificateChain
                                .add(Objects
                                        .requireNonNull(ex
                                                .getMessage())
                                        .substring(ex.getMessage()
                                                .indexOf("CN")));
                        parsePrincipal();
                        chainList.add(new CertChainRepository(certificateChain, validity));
                        break;
                    }
                    catch (ArrayIndexOutOfBoundsException |
                            ClassCastException |
                            IOException ex) {

                        ex.printStackTrace();
                        validity = false;
                        certificateChain.add("Unvalid Certificate " + hostName);
                        chainList.add(new CertChainRepository(certificateChain, validity));
                        break;
                    }
                    finally {

                        if (connection != null)
                            connection.disconnect();
                    }

                    getRootCertificate();

                    chainList.add(new CertChainRepository(certificateChain, validity));
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

        X509TrustManager x509Tm;

        TrustManager[] trustManagers = tmFactory.getTrustManagers();
        x509Tm = (X509TrustManager) trustManagers[0];
        X509Certificate[] issuers = x509Tm.getAcceptedIssuers();

        for (X509Certificate issuer : issuers) {

            try {
                intermediate.verify(issuer.getPublicKey());
                certificateChain.add(issuer.getSubjectX500Principal().getName());
                break;
            }
            catch (NoSuchAlgorithmException |
                    CertificateException |
                    InvalidKeyException |
                    SignatureException |
                    NoSuchProviderException  ex) {

                ex.printStackTrace();
            }

            //ToDo: Sometimes https://www.dubai.com/ throws IllegalBlockSizeException
            catch (Exception ex) {

                //javax.crypto.IllegalBlockSizeException:
                // error:04000073:RSA routines:OPENSSL_internal:DATA_TOO_LARGE_FOR_MODULUS
            }
        }
        parsePrincipal();
    }

    private void parsePrincipal() {

        String regex = "(?:^|,\\s?)(?:CN=(?<val>\"(?:[^\"]|\"\")+\"|[^,]+))";

        Pattern p = Pattern.compile(regex);

        for (int i = 0; i < certificateChain.size(); i++) {

            Matcher m = p.matcher(certificateChain.get(i));

            while(m.find()) {

                certificateChain.set(i, m.group(1));
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putSerializable("chainList", chainList);
    }
}