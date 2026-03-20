 
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

public class FoodReview {
    public static void main(String[] args) {
        
        Food pizza = new Pizza(5, "Delicious and cheesy!");
        Food burger = new Burger(4, "Tasty but a bit greasy.");

        pizza.showReview();
        burger.showReview();
    }
}