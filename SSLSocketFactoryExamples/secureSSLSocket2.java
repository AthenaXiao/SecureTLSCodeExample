// create a SSLSocket
SocketFactory sf = SSLSocketFactory.getDefault();
SSLSocket socket = (SSLSocket) sf.createSocket("our.example.com", 443);

SSLParameters sslParams = new SSLParameters();
sslParams.setEndpointIdentificationAlgorithm("HTTPS");
socket.setSSLParameters(sslParams);
// ... use socket ...

// communication ends
socket.close();