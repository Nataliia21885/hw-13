import Posts_Information.Post;
import Task_Information.Tasks;
import Users_Information.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class HttpClientUtil {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private static final String TEST_URI = "https://jsonplaceholder.typicode.com";
    private static final String USERS = "/users";
    private static final String POSTS = "/posts";
    private static final String TODOS = "/todos";
    private static final String COMMENTS = "/comments";

    public static String newUser(User user) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TEST_URI + USERS))
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(user)))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        return body;
    }

    public static String upgradeUser(int userId, User upgradeUser) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(upgradeUser);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s%s/%d", TEST_URI, USERS, userId)))
                .header("Content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static int deleteUser(User user) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()

                .uri(URI.create(String.format("%s%s/%d", TEST_URI, USERS, user.getId())))
                .header("Content-type", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static List<User> getAllUsers() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TEST_URI + USERS))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), new TypeToken<List<User>>() {
        }.getType());
    }

    public static User getUserById(int id) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()

                .uri(URI.create(String.format("%s%s/%d", TEST_URI, USERS, id)))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), User.class);
    }

    public static User getUserByUserName(String name) throws IOException, InterruptedException {
        List<User> allUsersList = getAllUsers();
        return allUsersList.stream().filter(user -> user.getName().equals(name)).findAny().orElse(new User());
    }

    public static String getAllCommentsToLastPostOfUser(User user) throws IOException, InterruptedException {
        Post lastPost = getLastPostOfUser(user);
        String fileName = "user -" + user.getId() + "- post -" + lastPost.getId() + "-comments.json";

        HttpRequest requestForComments = HttpRequest.newBuilder()

                .uri(URI.create(String.format("%s/%d%s", TEST_URI + POSTS, lastPost.getId(), COMMENTS)))
                .GET()
                .build();
        HttpResponse<Path> responseComments = CLIENT.send(
                requestForComments, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));
        return "Write comments to file" + responseComments.body();
    }

    private static Post getLastPostOfUser(User user) throws IOException, InterruptedException {
        HttpRequest requestForPosts = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s%s/%d/%s", TEST_URI, USERS, user.getId(), "posts")))
                .GET()
                .build();

        HttpResponse<String> responsePosts = CLIENT.send(requestForPosts, HttpResponse.BodyHandlers.ofString());
        List<Post> allUserPost = GSON.fromJson(responsePosts.body(), new TypeToken<List<Post>>() {
        }.getType());
        return Collections.max(allUserPost, Comparator.comparingInt(Post::getId));
    }

    public static List<Tasks> getListOfOpenTasksForUser(User user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s%s/%d%s", TEST_URI, USERS, user.getId(), TODOS)))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        List<Tasks> allTasks = GSON.fromJson(response.body(), new TypeToken<List<Tasks>>() {
        }.getType());
        return allTasks.stream().filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }

}
