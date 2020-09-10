public class SecDevTM implements X509TrustManager {
    // a default trust manager
    private X509TrustManager defaultTM;
    // a trust manager for special requirements
    private X509TrustManager ourTM;
    public SecDevTM() throws Exception {
        defaultTM = getDefaultTrustManager();
        backupTM = getTrustManagerForCERTs("trustedCerts");
    }
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
    throws CertificateException {
        try{
            defaultTM.checkClientTrusted(chain, authType);
        }catch (CertificateException e){
            backupTM.checkClientTrusted(chain,authType)
        }
    }
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) 
    throws CertificateException {
        try{
            defaultTM.checkServerTrusted(chain, authType);
        }catch (CertificateException e){
            backupTM.checkServerTrusted(chain,authType)
        }
    }
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return defualtTM.getAcceptedIssuers();
    }
    
    private X509TrustManager getDefaultTrustManager(){
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init((KeyStore) null);
        TrustManager[] tms = tmf.getTrustManagers();
        if (tms != null) {
            for (TrustManager tm : tms) {
                if (tm instanceof X509TrustManager) {
                    return (X509TrustManager) tm;
                }
            }
        }
    }

    private X509TrustManager getTrustManagerForCERTs(String filename){
        // load the new certificate from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = 
            new BufferedInputStream(new FileInputStream(filename));
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
                return ourTM;
            }
        }

    }
}