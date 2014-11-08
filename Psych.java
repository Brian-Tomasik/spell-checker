/******************************************************************************
Speller.java
Brian Tomasik
Nov. 2006

This class has a main method that performs spell checking on the file
in its command-line argument.

The main method reads in words from two dictionary files into a 
TreeSet<String>. It asks the user whether the results should be printed or 
written to a file. It then runs through the command-line file, checking to
see that all of its words are in the TreeSet<String>. When a word isn't in
the TreeSet<String>, the main method generates and spell-checks all of the
words that could be generated from the mispelled word by adding just one
letter. Valid possibilities are outputted along with the original misspelled
word and its line number.

This program assumes that the user enters the input files in the correct
order: the first and second files should be dictionaries, while the third
should be the file to spell-check.
******************************************************************************/

import java.util.*;
import java.io.*;

public class Speller
{
    static Scanner userInput = new Scanner(System.in);
    final static String MAIN_DICT = "american-english";
    final static String SUPP_DICT = "supplemental-dict.txt";
    final static String PRINT = "p";
    final static String WRITE_TO_FILE = "w";

    public static void main (String[] args) throws IOException
    {
	Scanner file1 = new Scanner(new File(args[0]));
	Scanner file2 = new Scanner(new File(args[1]));
	TreeSet<String> dictionary = makeDictionary(file1, file2);
	
	Scanner lineReader = new Scanner(new File(args[2]));
	ArrayList<Word> misspelledWords 
	    = getMisspelledWords(lineReader, dictionary);

	String outputMode = askUserOutputMode();
	if(outputMode.equalsIgnoreCase(PRINT))
	    {
		printErrorResults(dictionary, misspelledWords);
	    }
	if(outputMode.equalsIgnoreCase(WRITE_TO_FILE))
	    {
		writeErrorResults(dictionary, misspelledWords);
	    }
    }
	
    /**
       makeDictionary: This method creates a TreeSet<String> object to serve
       as the dictionary. It then reads in the words from the two dictionary
       files and enters them into the TreeSet<String>.
       @param file1 the Scanner for the first dictionary file
       @param file2 the Scanner for the second dictionary file
       @return the TreeSet<String> that will act as the dictionary
    */
    public static TreeSet<String> makeDictionary(Scanner file1, Scanner file2)
    {
	TreeSet<String> dictionary = new TreeSet<String>();
	int counter = 0;

	file1.useDelimiter("[^a-zA-Z0-9']+");
	while(file1.hasNext())
	    {
		dictionary.add(file1.next().toLowerCase());
	    }

	file2.useDelimiter("[^a-zA-Z0-9']+");
	while(file2.hasNext())
	    {
		dictionary.add(file2.next().toLowerCase());
	    }

	return dictionary;
    }

    /**
       askUserOutputMode: This method asks the user what output mode (print
       or write to file) to use. It checks that the user's selection is a valid
       one and then returns the user's selection.
       @return a String representing the user's selection
    */
    public static String askUserOutputMode()
    {
	String mode;
	System.out.println("This spell checker outputs misspelled words in " +
			   "your file."); 
	System.out.println("Shall the results be printed out (enter \"" +
			   PRINT + "\") or written to a file (enter \"" +
			   WRITE_TO_FILE + "\")?");
	mode = userInput.next();

	while(!mode.equalsIgnoreCase(PRINT) &&
	      !mode.equalsIgnoreCase(WRITE_TO_FILE))
	    {
		System.out.println("Hey! You have to enter one of these " +
				   "options: " + PRINT + " or " +
				   WRITE_TO_FILE + ". Try again:");
		mode = userInput.next();
	    }

	return mode;
    }

    /**
       getMisspelledWords: This method takes a Scanner that reads in lines
       from text and a TreeSet<String> for the dictionary. It creates a new
       Scanner that reads in from each individual line. If the words that
       the new scanner reads in aren't in the dictionary, they are converted
       into a Word object that's stored in an ArrayList<Word> of misspelled
       words, which is returned at the end.
       @param lineReader a Scanner that reads in lines from the text
       @param dictionary a TreeSet<String> that acts as a dictionary
       @return the ArrayList<Word> of misspelled words
    */
    public static ArrayList<Word> getMisspelledWords(Scanner lineReader,
							  TreeSet<String> 
							  dictionary)
    {
	ArrayList<Word> misspelledWords = new ArrayList<Word>();

	lineReader.useDelimiter("[^a-zA-Z0-9']+");
	Scanner wordReader;
	int lineCount = 0;
	String thisLine, word;

	while(lineReader.hasNext())
	    {
		lineCount ++;
		thisLine = lineReader.nextLine();
		wordReader = new Scanner(thisLine);
		wordReader.useDelimiter("[^a-zA-Z0-9']+");
		
		while(wordReader.hasNext())
		    {
			word = wordReader.next().toLowerCase();
			if(!dictionary.contains(word))
			    {
				misspelledWords.add
				    (new Word(word, lineCount));
			    }
		    }
	    }

	return misspelledWords;
    }

    /**
       writeErrorResults: This method takes a TreeSet<String> for the
       dictionary of words and an ArrayList<Word> containing the misspelled
       words. The method asks the user for the name of the output
       file and then writes out the misspelled word and line number to that 
       file. It then calls findReplacements to get a String of 
       possible replacements. If the replacement String isn't null, it's
       written to the file as well.
       @param dictionary the TreeSet<String> of words
       @param word the misspelled word
       @param lineCount the line on which the misspelled word occurred
       @param outputFileName the name of the output file to which to write
    */
    public static void writeErrorResults(TreeSet<String> dictionary, 
					 ArrayList<Word> misspelledWords)
	{
	    System.out.println("Name the output file: ");
	    String fileName = userInput.next();
	    PrintWriter fileOut = null;

	    try
		{
		    fileOut = new PrintWriter(new FileWriter(fileName));

		    Iterator<Word> itr = misspelledWords.listIterator();
		    Word currWord;
		    String replacements;
		    while(itr.hasNext())
			{
			    currWord = itr.next();
			    fileOut.println
				("---------------------------------");
			    fileOut.println
				("Word: " + currWord.getText());
			    fileOut.println("Line: " + currWord.getLine());
			    replacements = findReplacements
				(dictionary, currWord.getText());
			    
			    if(replacements != null)
				{
				    fileOut.println("Possible replacements:");
				    fileOut.print(replacements);
				}
			    fileOut.println
				("---------------------------------\n");
			}
		} catch(IOException e) { e.printStackTrace(); }
	    
		finally
		    {
			if(fileOut != null)
			    {
				fileOut.close();
			    }
		    }
	}

    /**
       printErrorResults: This method takes a TreeSet<String> for the
       dictionary of words and an ArrayList<Word> containing misspelled 
       words. The method prints out the misspelled words and
       line number and then calls findReplacements to get a String of 
       possible replacements. If the replacement String isn't null, it's
       printed out.
       @param dictionary the TreeSet<String> of words
       @param misspelledWords an ArrayList<Word> of the misspelled words
    */
    public static void printErrorResults(TreeSet<String> dictionary, 
					 ArrayList<Word> misspelledWords)
	{
	    Iterator<Word> itr = misspelledWords.listIterator();
	    Word currWord;
	    String replacements;
	    while(itr.hasNext())
		{
		    currWord = itr.next();
		    System.out.println("\n---------------------------------");
		    System.out.println("Word: " + currWord.getText());
		    System.out.println("Line: " + currWord.getLine());
		    replacements 
			= findReplacements(dictionary, currWord.getText());
		    
		    if(replacements != "")
			{
			    System.out.println
				("Here are possible replacements:");
			    System.out.print(replacements);
			}
		    System.out.println("---------------------------------");
		}
	}

    /**
       findReplacements: This method takes a TreeSet<String> as a dictionary
       and a String as a word. It tries adding each possible new letter to each
       possible location in the misspelled word. If any of those new words
       are in the dictionary, that new word is concatenated to a String that
       will ultimately be returned.
       @param dictionary the TreeSet<String> of words
       @param word the misspelled word
       @return a String consisting of a concatenation of possible replacements
    */
    public static String findReplacements(TreeSet<String> dictionary, 
					  String word)
	{
	    String replacements = "";
	    String leftHalf, rightHalf, newWord;
	    int deleteAtThisIndex, insertBeforeThisIndex;
	    char index;
	    TreeSet<String> alreadyDoneNewWords = new TreeSet<String>();
	    /* The above TreeSet<String> will hold words that the spell checker
	     suggests as replacements. By keeping track of what has already
	    been suggested, the method can make sure not to output the
	    same recommended word twice. For instance, the word 
	    "mispelled" would ordinarily result in two of the same suggested
	    replacements: "misspelled" (where the additional "s" is added to 
	    different locations.) */
	    
	    // First, we'll look for words to make by subtracting one letter
	    // from the misspelled word.
	    for(deleteAtThisIndex = 0; deleteAtThisIndex < word.length();
		deleteAtThisIndex ++)
		{
		    if(deleteAtThisIndex == 0)
			{
			    leftHalf = "";
			    rightHalf = word;
			}
		    else
			{
			    leftHalf = word.substring(0, deleteAtThisIndex);
			    rightHalf = word.substring(deleteAtThisIndex+1,
						       word.length());
			}

		    newWord = "";
		    newWord = newWord.concat(leftHalf);
		    newWord = newWord.concat(rightHalf);
		    if(dictionary.contains(newWord) &&
		       !alreadyDoneNewWords.contains(newWord))
			{
			    replacements = replacements.concat(newWord + "\n");
			    alreadyDoneNewWords.add(newWord);
			}
		}

	    // The rest of this method looks for words to make by adding a 
	    // new letter to the misspelled word.
	    for(insertBeforeThisIndex = 0; 
		insertBeforeThisIndex <= word.length();
		insertBeforeThisIndex ++)
		{
		    if(insertBeforeThisIndex == word.length())
			{
			    leftHalf = word;
			    rightHalf = "";
			}
		    else
			{
			    leftHalf = word.substring(0,insertBeforeThisIndex);
			    rightHalf = word.substring(insertBeforeThisIndex,
						       word.length());
			}
		    
		    for(index = 'a'; index <= 'z'; index ++)
			{
			    newWord = "";
			    newWord = newWord.concat(leftHalf);
			    newWord = newWord.concat("" + index + "");
			    newWord = newWord.concat(rightHalf);
			    
			    if(dictionary.contains(newWord) &&
			       !alreadyDoneNewWords.contains(newWord))
				{
				    replacements 
					= replacements.concat(newWord + "\n");
				    alreadyDoneNewWords.add(newWord);
				}
			}
		}

	    return replacements;
	}
}
