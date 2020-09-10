// create a SSLSocket
SocketFactory sf = SSLSocketFactory.getDefault();
SSLSocket socket = (SSLSocket) sf.createSocket("our.example.com", 443);
//verify the hostname manually
HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
if (!hv.verify(socket.getSession().getPeerHost(), socket.getSession())) {
    throw new SSLHandshakeException("Hostname does not match!");
}

// ... use socket ...

// communication ends
socket.close();