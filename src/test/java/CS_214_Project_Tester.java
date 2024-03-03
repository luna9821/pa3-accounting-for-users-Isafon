
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CS_214_Project_Tester {


    private DataProcessing dataProcessing;
    private List<String> testRatings;

        @BeforeEach
        public void setUp() {

            testRatings = Arrays.asList(
                "5 3 0 4",   // Test ratings for song 1
                "4 0 2 5",   // Test ratings for song 2
                "3 4 5 0"    // Test ratings for song 3
            );
            Arrays.asList("Song A", "Song B", "Song C");
    
            dataProcessing = new DataProcessing(testRatings);
           
        }

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
            List.of(1, 2, 0, 4, 5), 
            List.of(0, 0, 0, 0, 0),  
            List.of(3, 3, 3, 3, 3)   
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


        @Test
        public void testBlankFiles() {
            assertThrows(IOException.class, () -> {
            CS_214_Project.readSongs("path/to/blank_songs_file.txt");
            });

            assertThrows(IOException.class, () -> {
            CS_214_Project.readRatings("path/to/blank_ratings_file.txt");
            });
        }   

        @Test
        public void testALotOfUsers() {
            List<String> songs;
            try {
                songs = CS_214_Project.readSongs("path/to/many_users_songs_file.txt");
                assertNotNull(songs);
                assertTrue(songs.size() > 50); // Assuming 'a lot' means more than 50

            } catch (IOException e) {
            
                e.printStackTrace();
            }
            List<List<Integer>> ratings;
            try {
                ratings = CS_214_Project.readRatings("path/to/many_users_ratings_file.txt");

                assertTrue(ratings.size() > 50); // Assuming 'a lot' means more than 50 users
                assertNotNull(ratings);
            } catch (IOException e) {
         
                e.printStackTrace();
            }
            
        }

        @Test
        public void testUsersWithSameRatings() {
            List<List<Integer>> ratings;
            try {
                ratings = CS_214_Project.readRatings("path/to/same_ratings_file.txt");

                boolean allRatingsSame = ratings.stream()
                .allMatch(rating -> Collections.frequency(rating, rating.get(0)) == rating.size());
                assertTrue(allRatingsSame);

            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }

        @Test
        public void testInvalidIntegerRatingsInRatingsFile() {
            List<List<Integer>> ratings;
            try {
                ratings = CS_214_Project.readRatings("path/to/invalid_ratings_file.txt");
                ratings.forEach(ratingList -> {
                    ratingList.forEach(rating -> {
                        assertTrue(rating >= 0 && rating <= 5); // Assuming valid ratings are between 0 and 5
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
           
        }
    
        @Test
        public void testReadSongs() throws IOException {
            String testSongsFile = "test_songs.txt";
            List<String> expectedSongs = Arrays.asList("Song1", "Song2", "Song3");
            writeTestDataToFile(testSongsFile, expectedSongs);
            List<String> actualSongs = CS_214_Project.readSongs(testSongsFile);
            assertEquals(expectedSongs, actualSongs);
        }
    
        @Test
        public void testReadRatings() throws IOException {
            String testRatingsFile = "test_ratings.txt";
            List<List<Integer>> expectedRatings = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 0),
                Arrays.asList(0, 0, 6)
            );

            writeTestRatingsToFile(testRatingsFile, expectedRatings);
    
            List<List<Integer>> actualRatings = CS_214_Project.readRatings(testRatingsFile);
            assertEquals(expectedRatings, actualRatings);
        }
    
        @Test
        public void testNormalizeRatings() {
            List<List<Integer>> ratings = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6)
            );
        
            // Ensure ratings is not null before passing to normalizeRatings
            assertNotNull(ratings, "Ratings list should not be null");
        
            List<List<Double>> normalizedRatings = CS_214_Project.normalizeRatings(ratings);
            
            // Check if normalizedRatings is not null
            assertNotNull(normalizedRatings, "Normalized ratings should not be null");
            assertFalse(normalizedRatings.isEmpty(), "Normalized ratings should not be empty");
        }

        @Test
        public void testParseRatings() {
            // Test the parsing of ratings
            List<List<Integer>> expected = Arrays.asList(
                Arrays.asList(5, 3, 0, 4),
                Arrays.asList(4, 0, 2, 5),
                Arrays.asList(3, 4, 5, 0)
            );
    
            assertNotNull(dataProcessing, "DataProcessing object should not be null");
            assertNotNull(dataProcessing.getProcessedRatings(), "Processed ratings should not be null");
            assertEquals(expected, dataProcessing.getProcessedRatings());
        }
    
        // Helper methods for test setup
        private void writeTestDataToFile(String filename, List<String> data) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (String line : data) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    
        private void writeTestRatingsToFile(String filename, List<List<Integer>> data) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (List<Integer> ratings : data) {
                    writer.write(ratings.stream().map(Object::toString).collect(Collectors.joining(" ")));
                    writer.newLine();
                }
            }
        }
    }



 
        /* Tests I absolutely know pass the gradle test if needed later:
        @Test
        public void testNormalizeRatings() {
            List<List<Integer>> ratings = Collections.singletonList(
                Arrays.asList(1, 2, 3)
            );
            List<List<Double>> expectedNormalizedRatings = Collections.singletonList(
                Arrays.asList(-1.0, 0.0, 1.0)
            );
    
            List<List<Double>> actualNormalizedRatings = CS_214_Project.normalizeRatings(ratings);
            assertEquals(expectedNormalizedRatings, actualNormalizedRatings);
        }*/