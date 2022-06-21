import java.util.ArrayList;
//class for how posts are structured
public class post {
    String id;
    String time;
    String author;
    ArrayList<String> additionalHeaders;
    String contents;
    ArrayList<String> body;

    post(String id, String time, String author, ArrayList<String> additionalHeaders, String contents, ArrayList<String> body) {
        this.id = id;
        this.time = time;
        this.author = author;
        this.additionalHeaders = additionalHeaders;
        this.contents = contents;
        this.body = body;
    }
}

