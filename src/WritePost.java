import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

//This class takes all the information that goes into creating a post and writes the post to a text file
//so that it is stored for later
public class WritePost {
    String id;
    String time;
    String author;
    String contents;

    //Function to create a sha-256 hash
    public static byte[] toSHA(String input) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHex(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        //add some zeroes
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }


   public void createPost(String time, String author, ArrayList<String> additionalHeaders, String contents, ArrayList<String> bodyList) {


        this.time = time;
        this.author = author;
        this.contents = contents;

        //put all the headers into one string for the sha encryption
        String headers = null;
        for (String words: additionalHeaders) {
            headers = headers + words;
        }


        //put all the contents of the body into one string for sha encryption
        String body = null;
        for (String words: bodyList) {
            body = body + words;
        }
        try {
            this.id = "SHA-256 " + toHex(toSHA(time + author + contents + headers + body));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }

       try {
            FileWriter myWriter = new FileWriter("posts.txt", true);
            //start on a new line
            myWriter.write("\n");
            //write the post id
            myWriter.write("Post-id: " + id + "\n");
            //write the time
            myWriter.write("Created: " + time + "\n");
            //write the author
            myWriter.write("Author: " + author + "\n");
            //all the additional headers
            for (String header: additionalHeaders) {
                myWriter.write(header + "\n");
            }
            //number of contents
            myWriter.write("Contents: " + contents + "\n");
            //the body of the post
            for (String bod: bodyList) {
                myWriter.write(bod + "\n");
            }

            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void savePost(String id, String time, String author, ArrayList<String> additionalHeaders, String contents, ArrayList<String> bodyList) {

        try {
            FileWriter myWriter = new FileWriter("posts.txt", true);
            //start on a new line
            myWriter.write("\n");
            //write the post id
            myWriter.write( id + "\n");
            //write the time
            myWriter.write( time + "\n");
            //write the author
            myWriter.write(author + "\n");
            //all the additional headers
            for (String header: additionalHeaders) {
                myWriter.write(header + "\n");
            }
            //number of contents
            myWriter.write( contents + "\n");
            //the body of the post
            for (String bod: bodyList) {
                myWriter.write(bod + "\n");
            }

            System.out.println("Post saved");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
