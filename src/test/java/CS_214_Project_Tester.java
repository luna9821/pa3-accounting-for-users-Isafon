

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.*;
    import java.util.*;
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
        public void testRemoveUncooperativeUsers() {
            List<List<Integer>> ratings = new ArrayList<>(Arrays.asList(
                Arrays.asList(5, 5, 5),
                Arrays.asList(1, 2, 3),
                Arrays.asList(0, 0, 0)
            ));
            List<List<Integer>> expectedRatings = Collections.singletonList(
                Arrays.asList(1, 2, 3)
            );
    
            CS_214_Project.removeUncooperativeUsers(ratings);
            assertEquals(expectedRatings, ratings);
        }
    
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