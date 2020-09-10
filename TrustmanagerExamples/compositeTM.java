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
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class compositeTM{


    public class SecDevTM implements X509TrustManager {
        // a list of trust managers supporting multiple key stores
        private final List<X509TrustManager> trustManagers;

        public SecDevTM(){
            List<X509TrustManager> tms = loadTMsFromKSs();
            new SecDevTM(tms);
        }

        public SecDevTM(List<X509TrustManager> trustManagers) {
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
        private X509TrustManager loadTMsFromKSs() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
            //read keystore filenames and password from configuration


            //create trust manager from keystore
            List<X509TrustManager> trusts = new ArrayList<>();

            for(String file: keyStorefiles) {
                // create a KeyStore containing the trusted Certificates
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(file, pass);
                keyStore.setCertificateEntry("ca", ca);
                // create a new TrustManager that trusts our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);
                TrustManager tms[] = tmf.getTrustManagers();
                //look for an instance of X509TrustManager
                for (int i = 0; i < tms.length; i++) {
                    if (tms[i] instanceof X509TrustManager) {
                        trusts.add((X509TrustManager) tms[i]);
                        break;

                    }
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