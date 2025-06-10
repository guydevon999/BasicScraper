package io.github.guydevon999.basicscraper;


import java.io.FileWriter;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             BasicScraper scraper = new BasicScraper()) {

            // store scraped products from practice website in a List
            List<Product> products = scraper.scrapeProducts("https://books.toscrape.com/", 51);
            System.out.printf("Scraped %d products\n", products.size());

            // display sample product
            if (!products.isEmpty()) {
                System.out.println("\nSample product:\n" + products.get(0));
            }

            // write all products to CSV if requested
            System.out.print("Export to CSV? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.print("Filename (default: books.csv): ");
                String filename = scanner.nextLine();
                filename = filename.isEmpty() ? "books.csv" :
                        filename.endsWith(".csv") ? filename : filename + ".csv";

                try (FileWriter writer = new FileWriter(filename)) {
                    writer.write(Product.getCsvHeader() + "\n");
                    for (Product p : products) {
                        writer.write(p.toString() + "\n");
                    }
                    System.out.println("Saved to " + filename);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}