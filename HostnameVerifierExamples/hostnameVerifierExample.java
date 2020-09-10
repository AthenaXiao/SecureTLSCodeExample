//custom a hostname verifier
HostnameVerifier hostnameVerifier = new HostnameVerifier() {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        HostnameVerifier hv =
            HttpsURLConnection.getDefaultHostnameVerifier();
        return hv.verify("our.example.com", session);
    }
};

// tell the URLConnection to use our HostnameVerifier
URL url = new URL("https://our.example.org/");
HttpsURLConnection conn =
    (HttpsURLConnection)url.openConnection();
conn.setHostnameVerifier(hostnameVerifier);
InputStream in = conn.getInputStream();
copyInputStreamToOutputStream(in, System.out);
