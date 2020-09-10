public class SecDevTM implements X509TrustManager {
    private X509TrustManager ourTM;
    public SecDevTM(){
        // load the new certificate from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = 
            new BufferedInputStream(new FileInputStream("special_trust.crt"));
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }
        // create a KeyStore containing the trusted Certificates
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        // create a new TrustManager that trusts our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        TrustManager tms [] = tmf.getTrustManagers()
        //look for an instance of X509TrustManager 
        for (int i = 0; i < tms.length; i++) {
            if (tms[i] instanceof X509TrustManager) {
                ourTM = (X509TrustManager) tms[i];
                return;
            }
        }
    }
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
    throws CertificateException {
        ourTM.checkClientTrusted(chain, authType);
    }
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) 
    throws CertificateException {
        ourTM.checkServerTrusted(chain, authType);
    }
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return ourTM.getAcceptedIssuers();
    }

}