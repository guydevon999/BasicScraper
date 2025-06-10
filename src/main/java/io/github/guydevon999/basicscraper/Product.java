package io.github.guydevon999.basicscraper;

/*
Product class to represent a single unit of what we want to scrape.
In this case a book.
 */

public class Product {
    private String url;
    private String image;
    private String name;
    private String price;
    private String rating;
    private String availability;

    public Product() {
        this.url = "";
        this.image = "";
        this.name = "";
        this.price = "";
        this.rating = "";
        this.availability = "";
    }

    // Getters and setters for the fields
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    // ToString method separates values with commas for CSV
    @Override
    public String toString() {
        return escapeCsv(name) + "," +
                escapeCsv(price) + "," +
                escapeCsv(rating) + "," +
                escapeCsv(availability) + "," +
                escapeCsv(image) + "," +
                escapeCsv(url);
    }


    // Helper method to ensure correct CSV file format
    private String escapeCsv(String input) {
        if (input == null) {
            return "";
        }
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }

    // Helper method to generate CSV header(column names)
    public static String getCsvHeader() {
        return "Title,Price,Rating,Availability,Image URL,Product URL";
    }
}