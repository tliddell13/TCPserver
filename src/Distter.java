import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;

public class Distter{


    //says the version of DISTTER being used
    private final static String version = "DISTTER/1.0";
    //Sets the username
    private static String user = null;
    //Get the posts that this implementation has
    private static FindPosts myPosts = new FindPosts();


    //when running DISTTER you set your username when you start it up
    Distter(String out) {
        this.user = out;
        System.out.println("Welcome " + this.user + " to " + version + " of DISTTER");
    }

    //This function checks whether or not a server is active on the port.
    public static boolean checkServer(int port) {
        boolean result = false;
        try {
            (new ServerSocket(port)).close();
            result = true;
        } catch (IOException e) {

        }
        return result;
    }
    public static void main(String[] args) {
        String user = args[0];
        new Distter(user);
        ServerSocket serverSocket = null;
        final Socket clientSocket ;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc=new Scanner(System.in);
        int port = 20111;
        boolean isServer;

        try {
            //If there is not a server this implementation becomes the server
            if (checkServer(port)) {
                serverSocket = new ServerSocket(port);
                //System.out.println("I am the server");
                isServer = true;
                clientSocket = serverSocket.accept();
            }
            //If a server already exists it becomes the client
            else {
                clientSocket = new Socket("localhost", port);
                isServer = false;
                //System.out.println("I am the client");
            }

            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));

            Thread sender= new Thread(new Runnable() {
                String msg; //variable that will contains the data written by the user
                boolean helloSent = false;
                @Override   // annotation to override the run method
                public void run() {
                    while(true){
                        msg = sc.nextLine();
                        switch (msg) {
                            case "HELLO?":
                                //if the user sends a hello add the required info to the hello
                                msg = msg + " " + version + " " + user;
                                //the hello must be sent first
                                helloSent = true;
                                out.println(msg);
                                out.flush();
                                break;
                            case "GOODBYE!":
                                out.close();
                                try {
                                    clientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                out.println(msg);
                                out.flush();
                        }
                    }
                }
            });
            sender.start();

            ServerSocket finalServerSocket = serverSocket;
            Thread receive= new Thread(new Runnable() {
                boolean helloReceived = false;
                String msg ;
                @Override
                public void run() {
                    try {
                        msg = in.readLine();
                        while(msg!=null){
                            System.out.println(msg);
                            String words[] = msg.split(" ",2);
                            if (words[0].equals("HELLO?")) {
                                helloReceived = true;
                            }
                            //can only respond once the initial hello is sent
                            if (helloReceived) {
                                switch (words[0]) {
                                    case "WHEN?":
                                        String response = "NOW " + Instant.now().getEpochSecond();
                                        out.println(response);
                                        out.flush();
                                        break;
                                    case "POSTS?":
                                        //this gets rid of the first word
                                        String split2[] = words[1].split(" ", 2);
                                        //this gets the time
                                        String time = split2[0];
                                        //Now get all the headers and put them into an array list
                                        String headerCount = split2[1];
                                        ArrayList<String> headersList = new ArrayList<>();
                                        int headCountInt = Integer.parseInt(headerCount);
                                        for (int x = 0; x < headCountInt; x++) {
                                            msg = in.readLine();
                                            headersList.add(msg);
                                        }
                                        //Now I can get the posts with these filters applied
                                        ArrayList<post> filteredPosts;
                                        filteredPosts = myPosts.FilterPosts(time, headersList);
                                        out.println("OPTIONS " + filteredPosts.size());
                                        out.flush();
                                        for (post posts : filteredPosts) {
                                            out.println(posts.id);
                                            out.flush();
                                        }
                                        break;
                                    case "FETCH?":
                                        String postId = words[1];
                                        post foundPost = myPosts.getPost(postId);
                                        if (foundPost == null) {
                                            out.println("SORRY");
                                        } else {
                                            //Print all the contents of the found post
                                            out.println("FOUND");
                                            out.println(foundPost.id);
                                            out.println(foundPost.time);
                                            out.println(foundPost.author);
                                            for (String header : foundPost.additionalHeaders) {
                                                out.println(header);
                                            }
                                            out.println(foundPost.contents);
                                            for (String body : foundPost.body) {
                                                out.println(body);
                                            }
                                        }
                                        out.flush();
                                        break;
                                    //If the other peer finds a post you requested
                                    case "FOUND":
                                        //Then get all the contents of the post and save it to the text file
                                        msg = in.readLine();
                                        String id = msg;
                                        System.out.println(msg);

                                        msg = in.readLine();
                                        String created = msg;
                                        System.out.println(msg);

                                        msg = in.readLine();
                                        String author = msg;
                                        System.out.println(msg);

                                        msg = in.readLine();
                                        ArrayList<String> newHeaders = new ArrayList<>();
                                        //get all the additional headers before the contents header
                                        while (!msg.split(" ", 2)[0].equals("Contents:")) {
                                            newHeaders.add(msg);
                                            System.out.println(msg);
                                            msg = in.readLine();
                                        }
                                        String contents = msg;
                                        System.out.println(msg);
                                        msg = in.readLine();
                                        ArrayList<String> body = new ArrayList<>();
                                        //then until there is a blank line it is the contents of the body
                                        //then until there is a blank line it is the contents of the body
                                        for (int x = 0; x < Integer.parseInt(contents.split(" ", 2)[1]); x ++) {
                                            msg = in.readLine();
                                            body.add(msg);
                                            System.out.println(msg + "\n");
                                        }

                                        WritePost.savePost(id, created, author, newHeaders, contents, body);
                                        break;
                                    default:
                                }
                            }
                            else {
                                //if a message is sent before hello let them know
                                out.println("Send 'HELLO?' message first.");
                            }
                            msg = in.readLine();
                        }

                        System.out.println("Client disconnected");

                        if (isServer) {
                            finalServerSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
