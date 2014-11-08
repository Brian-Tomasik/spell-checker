README.txt
Brian Tomasik
Nov. 2006

Notes on the program:
This program does two extra features:
1. For each misspelled word, it checks for possible replacements that can be
formed by deleting a single letter.
2. It allows the user to write out the results to a file, not just print them
out to the screen.

The second feature required making the program significantly more complicated
that it would have otherwise been. If the program had only printed out the
results, it wouldn't have been necessary to create an ArrayList of Word objects
to store the misspelled words and their line numbers. Instead, the results
could have been printed out in the body of the getMisspelledWords method.
This wouldn't have worked well for writing to a file, however, because it
would have required opening and closing the file each time a new misspelled
word was found. In storing the misspelled words, I was able to write them
all out at the end.


Steps in the program:

1) Read in all of the words from the two dictionary files that were passed
as command-line arguments. (This is done in the makeDictionary method.)

Suppose there are W words to read in. Checking the while condition, 
reading in the next word, and making the word lower-case take a constant number
of steps per word, so the total amount work for that portion is a constant 
times W. However, those words also have to be inserted into the dictionary, 
a TreeSet<String>. We know from class that insertion into a tree takes a 
number of steps that's at most a constant times the height of the tree,
and for a relatively balanced tree with L nodes, the height is approximately
log_2(L). The actual number of nodes in the dictionary grows from 0 to W, but
assuming (conservatively) that the tree had W nodes the whole time, a single
insertion would take less than a constant times log_2(W) steps, and the
entire insertion process would take less than a constant times Wlog_2(W) steps.

Overall: This step is O(Wlog_2(W)).

2) Next the program creates a Scanner for the text file to be spell-checked.
An ArrayList<Word> is created to store the misspelled words and their line
numbers. For each word in the scanned text, we check to see that it's in the
dictionary; if not, we add it to the ArrayList<Word>.

Suppose we have N words in the scanned text, M of which are misspelled.
Scanning in each word, checking the while condition, and incrementing lineCount
all take a constant amount of time, so the total work from scanning is a 
constant times N. The more costly part comes from checking that each word
is in the dictionary. We know from class that a search in a relatively 
balanced BST with L nodes takes a constant times log_2(L) steps, so the entire
searching process takes less that a constant times Nlog_2(W) steps. In
addition, when we encounter a misspelled word, we have to create a new
Word object (constant number of steps per word, so constant times M steps
total) and add it to an ArrayList<Word> (as the ArrayList documentation
on the Java website notes, adding M elements to an ArrayList takes a constant 
times M steps).

Overall: The Nlog_2(W) term dominates, making this step O(Nlog_2(W)).

3) Ask user for an output format (the askUserOutputMode method).

These operations take only a constant amount of time. The cost is minimal 
(unless the user enters bad output a large number of times).

Overall: O(1).

4) Call the printErrorResults or writeErrorResults method.

With M things in the misspelledWords ArrayList, the while loop iterates M
times through. *Except for* the findReplacements method that is called, the
operations in each iteration of the while loop take a constant amount of time,
so the total cost is a constant times M.

The findReplacements method takes longer, however. In my version, it both
checks for words that could be formed by subtracting one letter from the
current word and adding one letter to the current word.

First consider the part of the method that subtracts one letter. The outer
for-loop iterates word.length() times. (Let the average word length be
A letters. Then the loop iterates A times on average.) Each time, a new
word to try is created (constant number of steps). We then check to see if the
word is in the dictionary (a constant times log_2(W) steps) and also check to
see if it's in the ArrayList of replacement words that have already been 
suggested (I'll assume this latter amount is small enough to neglect). If the 
tried word is in the dictionary, we concatenate its text to our output String 
(constant number of steps) and add it to the ArrayList of already completed
words (again, I'll assume this is neglibible, since that ArrayList is so 
small). The most expensive part of each for-loop iteration, then, is checking
the dictionary (constant times log_2(W)). Since the for-loop repeats A times,
this is a constant times Alog_2(W) steps.

The second half of the findReplacements method is similar. The main difference
is that, in addition to iterating through the first for-loop A times, we
iterate through a second one 26 times (to try each letter). Based on the same
reasoning as above, this amounts to roughly 26Alog_2(W) steps. Adding on the
extra Alog_2(W) from before, this becomes 27Alog_2(W) steps (or, less exactly,
a constant times Alog_2(W)).

Recall that these Alog_2(W) steps repeat each time the printErrorResults or
writeErrorResults for-loop iterates, so the total work from this part is
MAlog_2(W) steps.

Overall: O(MAlog_2(W)).

5) Adding the overall results from 1, 2, and 4 (3 is negligible), we get
O(Wlog_2(W) + Nlog_2(W) + MAlog_2(W)) = O((W+N+MA)log_2(W)).

For concreteness, I'll plug in some sample numbers. Suppose the total number
of steps were (W+N+MA)log_2(W). (There should actually be a constant there,
too, but I don't know exactly what it is.) Assume

W = 100,000
N = 1,000
M = 50
A = 6.

Then the total work would be (*very roughly*) 1,600,000 steps.