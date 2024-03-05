import java.util.Vector;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
//import java.util.Scanner;

public class CS_214_Project {
    public static int numberOfSongs;
    public static int numberOfUsers;
    public static Vector<Song> songs = new Vector<Song>();
    public static int[][] arrRatings;           //arrRatings store all the ratings, be it 0
    //public static ArrayList<Vector<Integer>> vectorRatings;     //it stores non zero (cooperative user) ratings
    public static double[] userMean;
    public static double[] userStdDev;
    public static double[][] normalizedRanks;

    public static void main(String[] args) {
        // you may add code here or refer to the main of another class
        // YourClass.main(args);
        // check if filename is provided as command-line argument
        if(args.length != 3) {
            System.err.println("Error: 3 arguments needed.");
            return;
        }
        // store the filename from the command-line argument
        String songsFilename = args[0];
        String ratingsFilename = args[1];
        String outputFilename = args[2];
        // read the song titles from file
        try {
            loadSongsFromFile(songsFilename);
        } catch (IOException e) {
            System.err.println("Error: Reading song's file");
            return;
        }

        try {
            loadRatingsFromFile(ratingsFilename);
        } catch (IOException e) {
            System.err.println("Error: reading rating file");
            return;
        }
        removeUncooperativeUsers();     //It will populate vectorRatings
        userMean = new double[numberOfUsers];
        userStdDev = new double[numberOfUsers];
        normalizedRanks = new double[numberOfSongs][numberOfUsers];
        for (int songNum = 0; songNum < numberOfSongs; ++songNum)
        {
            for (int userNum = 0; userNum < numberOfUsers; ++userNum)
            {
                normalizedRanks[songNum][userNum] = Integer.MAX_VALUE;
            }
        }
        calculateUserMean();
        calculateUserStdDev();
        calculateNormalizedRatings();
        calculateMeanAndStdDev();
        outputToFile(outputFilename);
        System.out.println("Stopped");
    }    
    
    public static int getRating(int songNum, int userNum)
    {
        return songs.get(songNum).getRatings().get(userNum);
    }
      
    public static double calculateNormalizedMean(int songNum, int numOfRatings)
    {
        double sumNormalRank = 0;
        for (int userNum = 0; userNum < numberOfUsers; ++userNum)
        {
        	int rating = getRating(songNum, userNum);
        	if (rating != 0)
            {
        		sumNormalRank += normalizedRanks[songNum][userNum];
            }
        }
        return sumNormalRank/numOfRatings;
    }
    
    public static double calculateNormalizedStdDev(int songNum, double mean, int numOfRatings)
    {
        double sumSqDiff = 0;
        for (int userNum = 0; userNum < numberOfUsers; ++userNum){
            double normalRank = normalizedRanks[songNum][userNum]; 
            if (normalRank != Integer.MAX_VALUE)
            {
                sumSqDiff += Math.pow(normalRank - mean, 2);
            }
        }
        // sum square diff divided by number of numbers minus 1
        double variance = sumSqDiff / (numOfRatings - 1);
        // return the square root of variance to get std dev will bessel's correction
        return Math.sqrt(variance); 
    }
    
    public static void calculateNormalizedRatings()
    {
        double normalRank;
    
        int numOfRatings;
        for (int songNum = 0; songNum < numberOfSongs; ++songNum)
        {
            numOfRatings = 0;
            for (int userNum = 0; userNum < numberOfUsers; ++userNum)
            {
                int rating = getRating(songNum, userNum);
                if (rating != 0)
                {
                    ++numOfRatings;
                    normalRank = (rating - userMean[userNum])/userStdDev[userNum];
                    normalizedRanks[songNum][userNum] = normalRank;
                }
            }
            double normalMean = calculateNormalizedMean(songNum, numOfRatings);
            songs.get(songNum).setNormalizedMean(normalMean);
            if (Double.isNaN(normalMean))
            {
            	songs.get(songNum).setNormalizedStdDev(Double.NaN);
            	return;
            }
            double normalStdDev = calculateNormalizedStdDev(songNum, normalMean, numOfRatings);
            songs.get(songNum).setNormalizedStdDev(normalStdDev);
        }
    }
    
    public static void calculateUserMean(){
        double sumOfRatings;
        int numOfRatings;
        for (int userNum = 0; userNum < numberOfUsers; ++userNum)
        {
            sumOfRatings = 0;
            numOfRatings = 0;
            for (int songNum = 0; songNum < numberOfSongs; ++songNum)
            {
                int rating = getRating(songNum, userNum);
                if (rating != 0)
                {
                    sumOfRatings += rating;
                    ++numOfRatings;
                }
            }
            userMean[userNum] = sumOfRatings/numOfRatings;
        }
    }
      
    public static void calculateUserStdDev(){
        int numOfRatings;
        for (int userNum = 0; userNum < numberOfUsers; ++userNum){    
            double sumSqDiff = 0;
            numOfRatings = 0;
            double mean = userMean[userNum];
            // sum of the squares of the difference of each number with the mean
            for (int songNum = 0; songNum < numberOfSongs; ++songNum){
                int rating = getRating(songNum, userNum);
                if (rating != 0)
                {
                    sumSqDiff += Math.pow(rating - mean, 2);
                    ++numOfRatings;
                }
            }
            // sum square diff divided by number of numbers minus 1
            double variance = sumSqDiff / (numOfRatings - 1);
            // return the square root of variance to get std dev will bessel's correction
            userStdDev[userNum] = Math.sqrt(variance); 
        }
    }

    public static void AddCooperativeUserToSongs(int userNum){
        for (int songNum = 0; songNum < numberOfSongs; ++songNum)
        {
            int rating = arrRatings[songNum][userNum];
            songs.get(songNum).addRating(rating);
        }
    }
    
    public static int songWithNonZeroRating(int userNum)
    {
    	for (int songNum = 0; songNum < numberOfSongs; ++songNum)
    	{
        	if (arrRatings[songNum][userNum] != 0)
        	{
        		return songNum;
        	}   		
    	}   
    	return -1;
    }

    public static void removeUncooperativeUsers(){
    	int tempNumOfUsers = numberOfUsers;
    	int nonZeroRatingSongNum;
    	int nonZeroRating;
        for (int userNum = 0; userNum < tempNumOfUsers; ++userNum)
        {        		
        	nonZeroRatingSongNum = songWithNonZeroRating(userNum);
        	nonZeroRating = arrRatings[nonZeroRatingSongNum][userNum];
            boolean uncooperative = true;
            for (int songNum = nonZeroRatingSongNum + 1; songNum < numberOfSongs; ++songNum)
            {
            	if (arrRatings[songNum][userNum] != 0)
                {
            		if (nonZeroRating != arrRatings[songNum][userNum])
                    {
                        uncooperative = false;        //not an uncooperative user
                        break;
                    }
                }
            }
            if (!uncooperative)       // not uncooperative
            {
                AddCooperativeUserToSongs(userNum);	//0 2 3 4 5
                
            }
            else
            {
                --numberOfUsers;
            }
        }
    }

    public static void loadSongsFromFile(String songsFilename) throws IOException
    {
        FileReader fileReader = null;
        BufferedReader songsReader = null;
        String inputFilePath = "input_files/" + songsFilename;
        fileReader = new FileReader(inputFilePath);            
        songsReader = new BufferedReader(fileReader);
        String songTitle;
        while((songTitle = songsReader.readLine()) != null)
              {
                  Song song = new Song();
                  song.setSongTitle(songTitle);
                  songs.add(song);
                  ++numberOfSongs;
              }
        songsReader.close();
    }

    public static int[] convertTokensToRatings(String[] tokens){
        int[] oneSongRatings = new int[tokens.length];
        for(int i = 0; i < tokens.length; ++i) {
            try {
                // if we can parseInt, number found and readable
                int rating = Integer.parseInt(tokens[i]);
                
                if (rating < 0 || rating > 5)
                {
                    System.err.println("Error: Invalid range of rating");
                    return null;                                
                }
                oneSongRatings[i] = rating;
            }
            // if a non-number is read in, instead of crashing, handle the issue here
            catch (NumberFormatException e) {
                System.err.println("Error: Invalid number format in file");
                return null;
            }
        }
        return oneSongRatings;
    }

    // read the numbers from file into an array
    /**
     * @param filename
     * @return
     */
    public static void loadRatingsFromFile(String filename) throws IOException{
        int counter = 0;
        String tokens[];
        // string to hold the line read in
        String line;
        // count the number of valid entries in file
        // open the file for reading
        FileReader fileReader = null;
        BufferedReader reader = null;
        String inputFilePath = "input_files/" + filename;
        fileReader = new FileReader(inputFilePath);            
        try {
            reader = new BufferedReader(fileReader);
        } catch (Exception e) {
            reader.close();
        }
        // read first line to get the number of ratings per song
        if((line = reader.readLine()) == null)
        {
            return;
        }
        tokens = line.trim().split("\\s+");
        numberOfUsers = tokens.length;
        arrRatings = new int[numberOfSongs][numberOfUsers];
        arrRatings[counter++] = convertTokensToRatings(tokens);
        
        // read each line one by one until the end of file
        while((line = reader.readLine()) != null) {
            // check if line is empty after removing leading and trailing whitespaces
            if(!line.trim().isEmpty()) {
                ++counter;
                if (counter > numberOfSongs)
                {
                    System.err.println("Error: More number of ratings than songs");
                    System.exit(0);
                }
                // tokenize the line
                // whether the line has single or more numbers, we split at whitespaces and extract each number
                // \\s+ is a regular expression used to represent whitespaces of any numberOfSongs(tabs, spaces, newlines)
                tokens = line.trim().split("\\s+");
                if (tokens.length != numberOfUsers)
                {
                	System.err.println("Error: Ratings file is inconsistent");
                	System.exit(0);
                }
                // consider 1 number at a time
                arrRatings[counter-1] = convertTokensToRatings(tokens);                    
                //songs.get(counter-1).setRatings(arrRatings);
            }
        }
        if (counter < numberOfSongs)
        {
            System.err.println("Error: Less number of ratings than songs");
            return ;
        }
        reader.close();
    }
    

    private static double calculateStdDev(Integer[] numbers, double mean) {

        // find the sum square diff
        double sumSqDiff = 0;
        // sum of the squares of the difference of each number with the mean
        for(int n : numbers) {
            sumSqDiff += Math.pow(n - mean, 2);
        }
        // sum square diff divided by number of numbers minus 1
        double variance = sumSqDiff / (numbers.length - 1);
        // return the square root of variance to get std dev will bessel's correction
        return Math.sqrt(variance);
    }

    private static double calculateMean(Integer[] numbers) {
        // calculate the average
        double sum = 0;
        double avg = 0;
        // find the sum of all numbers in array
        for(double n : numbers) {
            sum += n;
        }
        // divide the total sum by number of elements in the array
        avg = sum / numbers.length;
        // return the result
        return avg;
    }
    private static void calculateMeanAndStdDev()
    {
        for (Song song: songs)
        {
            Vector<Integer> ratingsVector = new Vector<Integer>(song.getRatings());
            int arraySize = ratingsVector.size();
            Integer[] ratingsArray = new Integer[arraySize];
            ratingsVector.toArray(ratingsArray);
            double mean = calculateMean(ratingsArray);
            song.setMean(mean);
            double stdDev = calculateStdDev(ratingsArray , mean);
            song.setStdDev(stdDev);
        }
    }
    
    public static void outputToFile(String filename)
    {
        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter("output_files/" + filename));
        }
        catch (IOException e)
        {
            System.err.println("Error: Cannot write to output file");
        }
        for (Song song: songs)
        {
            String songData;
            songData = song.getSongTitle();
            double mean;
            double stdDev;
            mean = song.getNormalizedMean();
            
            if (Double.isNaN(mean))
            {
            	songData += " UNDEFINED";
            	
            }
            else
            {
                DecimalFormat df = new DecimalFormat("#.#########");
            	songData += " " + df.format(mean);
            }
            stdDev = song.getNormalizedStdDev();
            if (Double.isNaN(stdDev)){
                songData += " UNDEFINED";
            }
            else
            {  
            	DecimalFormat df = new DecimalFormat("#.#########");
                songData += " " +df.format(stdDev);
            }
            songData += "\n";
            
            try {
                writer.write(songData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            writer.close();    
        }
        catch (IOException e)
        {
            System.err.println("Error: Closing output file");
        }
        
    }

    public void divide(double d, double e) {
        throw new UnsupportedOperationException("Unimplemented method 'divide'");
    }


    public Double calculateAverage(ArrayList<Double> numbers) {
        throw new UnsupportedOperationException("Unimplemented method 'calculateAverage'");
    }
}
