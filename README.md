[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/GcPZVHXO)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-7f7980b617ed060a017424585567c406b6ee15c891e84e1186181d67ecf80aa0.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=13791277)
# CS214 Spring2024 - PA3 - Accounting for Users

## Motivation

In this assignment, you will extend Assignment #2 to account for variations in
how people rank songs. In particular, (1) not every users ranks every song,
and (2) some users like everything they hear, while others like nothing.
We need to account for these variations by renormalizing scores by user and
deleting uncooperative users before ranking songs. In terms of pedagogy,
the goal is to make you think about how data is organized in memory.
Whereas in Assignment #2 you had essentially linear data indexed by song,
now you have 2D data that can be indexed by either song or user.
As always, it is possible to implement this assignment in ways that skirt
the pedagogical goals. But why would you do this? First, you have chosen to
take this class, I assume you want to learn the material. Second,
although avoiding 2D data indexing might seem clever now, you know that
future assignments will build on this one. Less work now might translate
into more work later.

## Task

In Assignment #2, you ranked songs. In Assignment #3, we extend it to adjust
for individual differences in the number of songs ranked and ranking scales
among users. The input to your program does not change much. Your program will
take three arguments: the names of two input files and one output file. The
first input file is still a text file of song titles, with one song title per
line. The second file still contains the ratings for each song. There is
a difference in this second file, however. We now assume that the first value
in a row is the ranking by user #1, while the second value is the ranking by
user #2, and so on. As a result, every row should now have the same number of
values (otherwise, it is an error). We adopt ‘0’ as a signal value that says a
particular user did not rate a particular song. Note that ‘0’ is not a valid
rank, and therefore it should not be included in any numeric calculations. It
is simply a placeholder for unranked songs.

Your first task is to remove data
from uncooperative users. An uncooperative user is one who either didn't rank
any songs (so that every song has the signal value ‘0’), or who gave the same
ranking to every song that they bothered to rank. You should remove all data
associated with uncooperative users. For example, if a user gave every song
a ‘5’, those values should not be included in a song’s mean or standard
deviation.

Your second task is to normalize for variations in standards between the
remaining, cooperative users. To do this, you first compute the mean rank and
standard deviation of ranks for every user. Then for every song x and user y,
you compute the normalized rank by taking the ranking of song x by user y,
subtracting the mean rank for user y, and dividing it by the standard deviation
of ranks for user y. The result is a (floating point) ranking that reflects how
much more (or less) user y liked song x in comparison to other songs.

Your final task is to write an output file with one line per song. Each line
should contain the name of a song, followed by the normalized mean rank of that
song and the normalized standard deviation of the ranks of the song. As
in previous assignments, if the normalized mean or normalized standard
deviation is undefined, you should print the word UNDEFINED for its value.
Be careful about the signal value. ‘0’ is not a ranking, it is the absence
of a ranking. If there are five songs and the ranks given by a single users to
the 5 songs are are 3, 3, 0, 2, and 4, then the user’s mean is 3.0, not 2.4.
Similarly, do not normalize the signal value, and do not include signal
values when computing a song’s normalized mean or standard deviation

## Submitting your work

Your submission must be in the main branch of your `GitHub` repository.

## Grading your home work


We will pull your code from you repository. It must contain, at a minimum two
`Java` files. One **must** be named `CS_214_Project.java`. We will run your program by
starting it with that class name. The second file **must** be named
`CS_214_Project_Tester.java`. This will be used to run your `JUnit` tests. All future
assignments **must** contain these two files, although they may contain
different code as needed by the particulars of that assignment.

~~~~
public class CS_214_Project {
  public static void main (String[] args) {
    // your code goes here
    // You may just call the main of some other class you wrote.
  }
}
~~~~

~~~~
import org.junit.Test;

// other imports as needed

public CS_214_Project_Tester {
  @Test
  public void test1_OrWhateverNameMakesSense() {
    // code for your first test
  }

  @Test
  public void test2_OrWhateverNameMakesSense() {
    // code for your second test
  }
}
~~~~
Part of your grade for this assignment will be based on your test coverage. After successfully running `gradle test` your test coverage will be available for display at `/build/jacocoHtml/index.html`. You must have a minimum of 50% coverage to get full points for this portion of your grade.

## Polices

All work you submit must be your own. You may not submit code written by a
peer, a former student, or anyone else. You may not copy or buy code from the
web. The department academic integrity policies apply.

You may not submit your program late. To receive credit, it must be submitted by the due date (with a 7 day extension). The exception is an unforeseeable emergency, for example a medical crisis or a death in the immediate family. If an unforeseeable emergency arises, talk to the instructor.
