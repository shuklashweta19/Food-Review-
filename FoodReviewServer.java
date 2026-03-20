import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.DefaultServlet;

class Food {
    String name;
    int rating;
    String review;

    public Food(String name, int rating, String review) {
        this.name = name;
        this.rating = rating;
        this.review = review;
    }

    public void showReview() {
        System.out.println(name + " has a rating of " + rating + "/5.");
        System.out.println("Review: " + review);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static Food fromJson(String json) {
        return new Gson().fromJson(json, Food.class);
    }
}

class Pizza extends Food {
    public Pizza(int rating, String review) {
        super("Pizza", rating, review);
    }

    @Override
    public void showReview() {
        System.out.println("Pizza Review:");
        super.showReview();
    }
}

class Burger extends Food {
    public Burger(int rating, String review) {
        super("Burger", rating, review);
    }

    @Override
    public void showReview() {
        System.out.println("\nBurger Review:");
        super.showReview();
    }
}

class Sushi extends Food {
    public Sushi(int rating, String review) {
        super("Sushi", rating, review);
    }

    @Override
    public void showReview() {
        System.out.println("\nSushi Review:");
        super.showReview();
    }
}

@WebServlet(name = "FoodServlet", urlPatterns = {"/api/*"})
class FoodServlet extends HttpServlet {
    private List<Food> foods = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        Gson gson = new Gson();

        if ("/reviews".equals(path)) {
            gson.toJson(foods, resp.getWriter());
        } else if ("/stats".equals(path)) {
            if (foods.isEmpty()) {
                resp.getWriter().write("{}");
                return;
            }
            int total = foods.stream().mapToInt(f -> f.rating).sum();
            double avg = total / (double) foods.size();
            Food highest = foods.stream().max((a,b) -> Integer.compare(a.rating, b.rating)).orElse(null);
            Map<String, Object> stats = new HashMap<>();
            stats.put("avgRating", avg);
            stats.put("highestRated", highest != null ? highest.name : null);
            gson.toJson(stats, resp.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString();
        JsonObject json = new Gson().fromJson(body, JsonObject.class);
        String type = json.get("type").getAsString();
        String name = json.get("name").getAsString();
        int rating = json.get("rating").getAsInt();
        String review = json.get("review").getAsString();

        Food food;
        switch (type.toLowerCase()) {
            case "pizza":
                food = new Pizza(rating, review);
                break;
            case "burger":
                food = new Burger(rating, review);
                break;
            case "sushi":
                food = new Sushi(rating, review);
                break;
            default:
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"Invalid type\"}");
                return;
        }
        food.name = name; // Override name if needed
        foods.add(food);
        resp.getWriter().write("{\"success\":true}");
    }
}

import java.util.HashMap;
import com.google.gson.JsonObject;

// Server main class
public class FoodReviewServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase(".");  // Serve from project root (for web/)
        context.addServlet(DefaultServlet.class.getName(), "/");
        ServletHolder servletHolder = context.addServlet(FoodServlet.class.getName(), "/api/*");
        server.setHandler(context);
        System.out.println("Server started at http://localhost:8080");
        server.start();
        server.join();
    }
}
