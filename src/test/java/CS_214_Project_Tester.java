
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CS_214_Project_Tester {

    @Test
    public void testReadSongNamesWithInvalidFile() {
        //Testing if readSongNames returns null for a non-existent file
        assertNull(CS_214_Project.readSongNames("nonexistent_file.txt"));
    }

    @Test
    public void testReadSongNamesWithEmptyLine() throws IOException {
        //Testing if readSongNames returns null when empty line is encountered in file
        Files.write(Paths.get("empty_line_test.txt"), List.of("Song1", "", "Song2"));
        assertNull(CS_214_Project.readSongNames("empty_line_test.txt"));
        Files.deleteIfExists(Paths.get("empty_line_test.txt"));
    }

    @Test
    public void testReadRatingsWithInvalidFile() {
        //Testing if readRatings returns null for non-existent file
        assertNull(CS_214_Project.readRatings("nonexistent_file.txt", 3));
    }

    @Test
    public void testReadRatingsWithInvalidRating() throws IOException {
        //Testing if readRatings return null when invalid rating format is found in the file
        Files.write(Paths.get("invalid_rating_test.txt"), List.of("1 2 3", "4 x 5"));
        assertNull(CS_214_Project.readRatings("invalid_rating_test.txt", 3));
        Files.deleteIfExists(Paths.get("invalid_rating_test.txt"));
    }

    @Test
    public void testRemoveUncooperativeUsers() {
        //Testing if uncooperative users are removed correctly
        List<List<Integer>> ratings = List.of(
            List.of(1, 2, 0, 4, 5), // Cooperative user
            List.of(0, 0, 0, 0, 0),  // Uncooperative user (all 0 ratings)
            List.of(3, 3, 3, 3, 3)   // Uncooperative user (all same rating)
        );
        List<List<Integer>> expected = List.of(List.of(1, 2, 0, 4, 5));
        assertEquals(expected, CS_214_Project.removeUncooperativeUsers(ratings));
    }

    @Test
    public void testCalculateUserMeans() {
        //Testing if user means are calculated correctly
        List<List<Integer>> ratings = List.of(
            List.of(1, 2, 0, 4, 5),
            List.of(3, 3, 3, 3, 3)
        );
        List<Double> expected = List.of(3.0, 3.0);
        assertEquals(expected, CS_214_Project.calculateUserMeans(ratings));
    }

    /*@Test
    public void testCalculateUserDeviations() {
            // Test data
            List<List<Integer>> ratings = List.of(
                List.of(1, 2, 0, 4, 5),
                List.of(3, 3, 3, 3, 3)
            );
            List<Double> userMeans = List.of(3.0, 3.0);
            List<Double> expected = List.of(1.5811, 0.0); // Expected values rounded to 4 decimal places
        
            // Calculate actual deviations
            List<Double> actual = CS_214_Project.calculateUserDeviations(ratings, userMeans);
        
            // Print expected and actual deviations
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actual);
        
            // Compare deviations
            assertEquals(expected.get(0), actual.get(0), 0.0001);
            assertEquals(expected.get(1), actual.get(1), 0.0001);
        }*/

        public static List<Double> calculateUserDeviations(List<List<Integer>> ratings, List<Double> userMeans) {
    List<Double> userDeviations = new ArrayList<>();
    for (int i = 0; i < ratings.size(); i++) {
        List<Integer> userRatings = ratings.get(i);
        double mean = userMeans.get(i);
        
        //Filter out '0' ratings
        List<Integer> filteredRatings = userRatings.stream()
                .filter(rating -> rating != 0)
                .collect(Collectors.toList());
        
        //Calculate deviation only if there are ratings to consider
        if (!filteredRatings.isEmpty()) {
            double deviation = Math.sqrt(filteredRatings.stream()
                    .mapToDouble(rating -> Math.pow(rating - mean, 2))
                    .average()
                    .orElse(Double.NaN));
            
            //Round deviation to 6 decimal places
            deviation = Math.round(deviation * 1e6) / 1e6;
            
            userDeviations.add(deviation);
        } else {
            userDeviations.add(Double.NaN); //No ratings to consider, deviation is undefined
        }
    }
    return userDeviations;
}

}



