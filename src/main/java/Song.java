import java.util.Vector;
class Song
{
    private String songTitle;
    private Vector<Integer> ratings = new Vector<Integer>();
    private double mean;
    private double stdDev;
    private double normalizedMean;
    private double normalizedStdDev;

    public void addRating(int rating)
    {
        ratings.add(rating);
    }
    public String getSongTitle(){
        return songTitle;
    }
    public Vector<Integer> getRatings(){
        return ratings;
    }
    public double getMean(){
        return mean;
    }
    public double getStdDev(){
        return stdDev;
    }
    public void setSongTitle(String newSongTitle)
    {
        songTitle = newSongTitle;
    }
    public void setRatings(Vector<Integer> songRatings)
    {
        ratings = new Vector<Integer>(songRatings);
    }
    public void setMean(double ratingsMean)
    {
        mean = ratingsMean;
    }
    public void setStdDev(double ratingsStdDev)
    {
        stdDev = ratingsStdDev;
    }
    public double getNormalizedMean(){
      return normalizedMean;
    }
    public double getNormalizedStdDev(){
      return normalizedStdDev;
    }
    public void setNormalizedMean(double normalMean){
      normalizedMean = normalMean;
    }
    public void setNormalizedStdDev(double normalStdDev){
      normalizedStdDev = normalStdDev;      
    }
}
