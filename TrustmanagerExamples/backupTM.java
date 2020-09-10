import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class backupTM{


    public class SecDevTM implements X509TrustManager {
        // a default trust manager
        private X509TrustManager defaultTM;
        // a trust manager for special requirements
        private X509TrustManager backupTM;
        public SecDevTM() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
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
            return defaultTM.getAcceptedIssuers();
        }

        private X509TrustManager getDefaultTrustManager() throws KeyStoreException {
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

        private X509TrustManager getTrustManagerForCERTs(String filename) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
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
            TrustManager tms [] = tmf.getTrustManagers();
            //look for an instance of X509TrustManager
            for (int i = 0; i < tms.length; i++) {
                if (tms[i] instanceof X509TrustManager) {
                    return (X509TrustManager) tms[i];
                    
                }
            }

        }
    }

    public static void main(String [] args) throws Exception{



        SSLContext ctx = SSLContext.getInstance("TLS");
        // specify to use the custom trust manager
        ctx.init(null, new TrustManager[] {new SecDevTM()}, null);
        SSLSocketFactory factory = ctx.getSocketFactory()
        URL url = new URL("https://our.example.com");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(factory);
        InputStream in = conn.getInputStream();


    }
}