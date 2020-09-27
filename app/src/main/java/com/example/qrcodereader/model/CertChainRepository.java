package com.example.qrcodereader.model;

import java.io.Serializable;
import java.util.ArrayList;


public class CertChainRepository implements Serializable {

    private ArrayList<String> certChains;

    public CertChainRepository(ArrayList<String> certChains) {

        this.certChains = certChains;
    }

    public ArrayList<String> getCertChains() {

        return certChains;
    }
}
