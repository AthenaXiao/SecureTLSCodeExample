import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.*;

public class secureSSLSocket2{



    public static void main(String [] args) throws Exception{



        // create a SSLSocket
        SocketFactory sf = SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) sf.createSocket("our.example.com", 443);
        //set algorithm as HTTPS to enable hostnameverifier
        SSLParameters sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("HTTPS");
        socket.setSSLParameters(sslParams);

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