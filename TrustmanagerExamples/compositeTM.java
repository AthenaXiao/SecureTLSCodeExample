public class SecDevTM implements X509TrustManager {
    // a list of trust managers supporting multiple key stores
    private final List<X509TrustManager> trustManagers;
    public CompositeX509TrustManager(List<X509TrustManager> trustManagers) {
        this.trustManagers = ImmutableList.copyOf(trustManagers);
    }
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        ImmutableList.Builder<X509Certificate> certificates = 
            ImmutableList.builder();
        for (X509TrustManager trustManager : trustManagers) {
            for (X509Certificate cert : trustManager.getAcceptedIssuers()) {
                certificates.add(cert);
            }
        }
        return Iterables.toArray(certificates.build(), X509Certificate.class);
    }
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) 
        throws CertificateException {
        for (X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkClientTrusted(chain, authType);
                return; // someone trusts them. success!
            } catch (CertificateException e) { }
        }
        throw new CertificateException(
            "None of the TrustManagers trust this certificate chain");
    }
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) 
        throws CertificateException {
        for (X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkServerTrusted(chain, authType);
                return; // someone trusts them. success!
            } catch (CertificateException e) { }
        }
        throw new CertificateException(
            "None of the TrustManagers trust this certificate chain");
    }
}