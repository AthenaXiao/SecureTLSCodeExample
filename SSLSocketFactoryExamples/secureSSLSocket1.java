import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.*;

public class secureSSLSocket1{

    

    public static void main(String [] args) throws Exception{



        // create a SSLSocket
        SocketFactory sf = SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) sf.createSocket("our.example.com", 443);
        //verify the hostname manually
        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
        if (!hv.verify(socket.getSession().getPeerHost(), socket.getSession())) {
            throw new SSLHandshakeException("Hostname does not match!");
        }

        // ... use socket ...
        // Start handling application content
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
        // Write data
        printWriter.print("Hello Server!");
        printWriter.flush();


        // communication ends
        socket.close();



    }
}