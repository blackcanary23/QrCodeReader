package com.example.qrcodereader.model;

import java.io.Serializable;
import java.util.ArrayList;


public class CertChainRepository implements Serializable {

    private ArrayList<String> certChains;
    private boolean validity;

    public CertChainRepository(ArrayList<String> certChains, boolean validity) {

        this.certChains = certChains;
        this.validity = validity;
    }

    public ArrayList<String> getCertChains() {

        return certChains;
    }

    public boolean getValidity() {

        return validity;
    }
}
