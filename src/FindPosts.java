import java.io.*;
import java.util.ArrayList;

//This class is used to find a post in a text file and then print it out
public class FindPosts {
    String id;
    String time;
    String author;
    String contents;
    ArrayList<post> posts = new ArrayList<>();

    FindPosts() {
        try {
            FileInputStream file = new FileInputStream("posts.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(file));

            String data;

            while ((data = br.readLine()) != null) {
                if (data.contains("SHA-256")) {
                    this.id = data;
                    data = br.readLine();
                    this.time = data;
                    data = br.readLine();
                    this.author = data;
                    data = br.readLine();
                    //Additional headers must come after author and before contents and start with a '#'
                    ArrayList<String> additionalHeaders = new ArrayList<>();

                    while(data.charAt(0) == '#') {
                        additionalHeaders.add(data);
                        data = br.readLine();
                    }
                    this.contents = data;
                    data = br.readLine();

                    ArrayList<String> body = new ArrayList<>();

                    while (data != null && !data.isEmpty() && !data.isBlank()) {
                        body.add(data);
                        data = br.readLine();
                    }

                    this.posts.add(new post(this.id, this.time, this.author, additionalHeaders, this.contents, body));
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<post> FilterPosts(String time, ArrayList<String> headers) {
        ArrayList<post> filteredPosts = new ArrayList<>();
        //Find all the posts that have the specified headers
        for(post checkPost: posts) {
            int count = 0;
            //would like to make this find the first int and then trim it
            String trimTime = checkPost.time.substring(checkPost.time.indexOf(":") + 2);
            if (Long.parseLong(trimTime) >= Long.parseLong(time)) {
                for(String header: headers) {
                    if (checkPost.additionalHeaders.contains(header)) {
                        count++;
                    }
                }
                //check if the post contains all the headers
                if (count == headers.size()) {
                    filteredPosts.add(checkPost);
                }
            }
        }
        return filteredPosts;
    }
    
    //fetch a single post by it's id
    
    public post getPost(String id) {
        for (post post: posts) {
            //get rid of the "Post-id:" label
            String postId = post.id.split(" ", 2)[1];
            if (postId.equals(id)) {
                return post;
            }
        } 
        return null;
    }
}