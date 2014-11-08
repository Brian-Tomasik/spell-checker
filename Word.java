public class Word
{
    // Data fields
    private String text;
    private int line;

    // Constructor
    public Word(String t, int l)
    { text = t; line = l; }

    // Getters
    public String getText()
    {
	return text;
    }

    public int getLine()
    {
	return line;
    }

    // Setters
    public void setText(String newText)
    {
	text = newText;
    }

    public void setLine(int newLine)
    {
	line = newLine;
    }

    public String toString()
    {
	return "Word: " + text + "\nLine: " + line;
    }
}
