import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private String hostname;
    private int port;
    Socket socketClient;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws UnknownHostException, IOException {
        System.out.println("Attempting to connect to " + hostname + ":" + port);
        socketClient = new Socket(hostname, port);
        System.out.println("Connection Established");
        //stupid comment
    }

    public String readResponse() throws IOException {
        String userInput;
        String msg = "";
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        System.out.println("Response from server:");
        while ((userInput = stdIn.readLine()) != null) {
            msg += userInput;
        }
        stdIn.close();
        return msg;
    }

    public void init(){
        //put something here
    }

    public static void main(String arg[]){
        //Creating a SocketClient object
        Client client = new Client ("localhost",9990);
        String ipOfServer1 = "";
        try {
            //trying to establish connection to the server
            client.connect();

            //if successful, read response from server
            ipOfServer1 = client.readResponse();
            System.out.println(ipOfServer1);

        } catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        }
    }
}
