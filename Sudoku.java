import java.util.Scanner; 
import java.lang.String;
import java.util.Scanner;
import java.io.File;
import java.lang.Integer;
import java.util.Arrays;


/* Sudoku Game class.
*/
class Sudoku 
{ 
	/* Private class to hold user input values.
	*/
	private class UserInput
	{
		// the row value for this user input value
		int row;
		// the col value for this user input value
		int col;
		// the actualy value the user wants to add to the table
		String value;
		
	}

	///////////////////////////////////////////////////////
	// Instance Variables
	///////////////////////////////////////////////////////
	
	// this is an array of given numbers for the Sudoku grid
	int[] givenNums = null;
	// these are the indexes of those given numbers
	int[] givenIndexes = null;
	// this string holds the user's input for the difficulty level
	String userInputDifficulty = "";
	// this string holds the user's input in the format #,row,col
	String userInput = "";
	
	
	// these make the numbers change colors when checking the user's answers
	public static final String ANSI_RESET = "\u001B[0m"; 
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_RED = "\u001B[31m"; 
	
	// this tells us if there were any erros found while checking the grid
	boolean anyErrorsFound = false;
	
	// our Sukoku grid will be a 9 by 9 2D array
	String[][] sudokuGrid = new String[9][9];
	
	// this 2D array will be the same size as the sudoku puzzle
	// but contain either true or false depending on if 
	// there is a duplicate number in each specified spot 
	boolean[][] isThisADuplicate = new boolean[9][9];
	
	// this 2D array will be the same size as the sudoku puzzle
	// but contain either true or false depending on if 
	// there is an invalid character in each specific spot 
	boolean[][] isThisInvalid = new boolean[9][9];
	
	// this is an array of valid charaters for the Sudoku puzzle
	String[] validCharacters = {"1","2","3","4","5","6","7","8","9","*"};
	
	// universal scanner obj 
	Scanner s = new Scanner(System.in);
	
	// valid user input can not be longer than this length (#,r,c)
	final int maxInputLength = 5;


	///////////////////////////////////////////////////////
	// Methods
	///////////////////////////////////////////////////////
	

	/** Retrieve and read the configuration file to know the number of given
	* values in the sudoku puzzle as well as their indexes. Then store the values in
	* the givenNums array and store the indexes of these numbers in the 
	* givenIndexes array.
	*/
	public void readConfigFile(String path)
	{
		// make a new scanner obj
		Scanner configScanner = null;
		
		try 
		{
			// get the path to the config file
			File configFile = new File(path);
			// use the scanner to read in the configFile path 
			configScanner = new Scanner(configFile);
		}
		catch(Exception e)
		{
			// if there is an error in trying to open the 
			// config file, print that error
			System.err.println(e);
		}
		
		// the first number in the config file is the number of predetermined spaces
		int numOfGivens = configScanner.nextInt();
		
		// make a givenNumber's array of the appropriate size
		givenNums = new int[numOfGivens];
		
		// "*2" to account for the x and y pair
		givenIndexes = new int[numOfGivens * 2];
		
		// add the given #/row/col values to their respective arrays
		for(int i = 0; i < numOfGivens; i++)
		{
			
			// the first number in each row of the config file is the row
			int row = configScanner.nextInt();
			// the next number is the col
			int col = configScanner.nextInt();
			// the third value in each row is the given number
			int number = configScanner.nextInt();
			
			// put this given number in our array of given numbers
			givenNums[i] = number;
			// store the indexes for this number into our index array
			givenIndexes[i * 2] = row;
			givenIndexes[(i * 2) + 1] = col;
		}
	}
	
	/** First, fill the whole grid with *'s as place holders, then loop through
	* again and fill in all the predetermined spaces
	*/
	public void setGrid()
	{	
		// the current index of the next number to be set
		int currentIndex = 0;
		
		// pre fill the grid's spaces with *'s
		for(int x = 0; x < 9; x++)
		{
			for(int y = 0; y < 9; y++)
			{
				sudokuGrid[x][y] = "*";
			}
		}
		
		// loop through the grid and set the given numbers
		for(int i = 0; i < 9; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				// "/2" since we are accounting for x and y values
				if(currentIndex / 2 < this.givenNums.length)
				{
					// check if the coordinate we are on is one of a predetermined value
					if(i == this.givenIndexes[currentIndex] && 
					j == this.givenIndexes[currentIndex + 1])
					{
						// set the grid array with the given numbers
						// at their designated positions
						sudokuGrid[i][j] = Integer.toString(givenNums[currentIndex/2]);
						currentIndex += 2;
					}
				}
			}
		}		
	}
	
	/** Print the grid. If the number we are about to print is a duplicate, 
	* print it as the color cyan, if the number we are about to print is
	* invalid, color it red. In order to create the grid shape of the 
	* puzzel, add a vertical line character after every three colums and
	* add a horizontal line to separate every three rows
	*/
	public void printGrid()
	{
		// counter for when to add a horizontal line ("-")
		int counterH = 0;
		
		// print the grid
		for(int i = 0; i < 9; i++)
		{
			// counter for when to add a vertical line ("|") as well as add a new line
			int counterV = 0;
			
			for(int j = 0; j < 9; j++)
			{
				// print on a new line after every 9 characters
				if(counterV == 8)
				{
					// if there is a duplicate, print this in cyan
					if(this.isThisADuplicate[i][j] == true)
					{
						System.out.println(" " + ANSI_CYAN + this.sudokuGrid[i][j] + ANSI_RESET + " ");
					}
					// if there is an error, print this in red
					else if(this.isThisInvalid[i][j] == true)
					{
						System.out.println(" " + ANSI_RED + this.sudokuGrid[i][j] + ANSI_RESET + " ");
					}
					// regular new line 
					else
					{
						// spaces for padding
						System.out.println(" " + this.sudokuGrid[i][j] + " ");
					}
				}
				// we add an "|" after every 3rd character
				else if(counterV == 2 | counterV == 5)
				{
					// if there is a duplicate, print this in cyan
					if(this.isThisADuplicate[i][j] == true)
					{
						System.out.print(" " + ANSI_CYAN + this.sudokuGrid[i][j] + ANSI_RESET + " | ");
					}
					// if there is an error, print this in red
					else if(this.isThisInvalid[i][j] == true)
					{
						System.out.print(" " + ANSI_RED + this.sudokuGrid[i][j] + ANSI_RESET + " | ");
					}
					// regular 
					else
					{
						// spaces for padding
						System.out.print(" " + this.sudokuGrid[i][j] + " | ");
					}
				}
				// print out a number with no expected "|" or new line character needed
				else
				{
					// if there is a duplicate, print this in cyan
					if(this.isThisADuplicate[i][j] == true)
					{
						System.out.print(" " + ANSI_CYAN + this.sudokuGrid[i][j] + ANSI_RESET + " ");
					}
					// if there is an error, print this in red
					else if(this.isThisInvalid[i][j] == true)
					{
						System.out.print(" " + ANSI_RED + this.sudokuGrid[i][j] + ANSI_RESET + " ");
					}
					// regular 
					else
					{
						// spaces for padding
						System.out.print(" " + this.sudokuGrid[i][j] + " ");
					}
				}
				counterV++;
			}
			// print a horizontal divider after the 3rd and 6th row
			if(counterH == 2 | counterH == 5)
			{
				System.out.println("------------------------------");
			}
			counterH++;
		}
	}
	
	/** Save all the row values in an array, then loop through
	* that array and see if there are 
	* any duplicate numbers within the array. 
	*/
	public void testForRepeatsInRows(int row, int col)
	{
		// loop through all the values in this specific row
		// and save those values to an array
		String[] temps = new String[9];
		for(int i = 0; i < 9; i++)
		{
			temps[i] = this.sudokuGrid[row][i];
		}
		// these 2 strings will be comapred to eachother
		String s1 = "";
		String s2 = "";
		
		// now we must check for duplicates
		for(int j = 0; j < 9; j++)
		{
			// this is the value we are going to compare to 
			// the rest of the values
			s1 = temps[j];
			for(int x = (j + 1); x < 9; x++)
			{
				// get every other value in the array to 
				// compare to s1
				s2 = temps[x];
				//we found a duplicate
				if(s1.equals(s2))
				{
					// lable this as a duplicate
					this.isThisADuplicate[row][x] = true;
					// label s1 as a duplicate
					this.isThisADuplicate[row][j] =true;
					
					anyErrorsFound = true;
				}
			}	
		}
		
	}
	
	/** Save all the col values in an array, then loop through
	* that array and see if there are 
	* any duplicate numbers within the array 
	*/
	public void testForRepeatsInCols(int row, int col)
	{
		// loop through all the values in this specific row
		// and save those values to an array
		String[] temps = new String[9];
		for(int i = 0; i < 9; i++)
		{
			temps[i] = this.sudokuGrid[i][col];
		}
		// these 2 strings will be comapred to eachother
		String s1 = "";
		String s2 = "";
		
		for(int j = 0; j < 9; j++)
		{
			// this is the value we are going to compare to 
			// the rest of the values
			s1 = temps[j];
			for(int x = (j + 1); x < 9; x++)
			{
				// get every other value in the array to 
				// compare to s1
				s2 = temps[x];
				//we found a duplicate
				if(s1.equals(s2))
				{
					// lable this as a duplicate
					this.isThisADuplicate[x][col] = true;
					// label s1 as a duplicate
					this.isThisADuplicate[j][col] = true;
					
					anyErrorsFound = true;
				}
			}	
		}
		
	}
	
	/** Save all the row values in an array, then loop through
	* the array and see if there are 
	* any invalid numbers within the array 
	*/
	public void testForValidCharacters(int row, int col)
	{
		// loop through all the values in this specific row
		// and save those values to an array
		String[] temps = new String[9];
		for(int i = 0; i < 9; i++)
		{
			temps[i] = this.sudokuGrid[row][i];
		}
		//check for invalid characters
		for(int i = 0; i < 9; i++)
		{
			// if the number/symbol at this position is not in the valid charatcers array...
			if(!(Arrays.asList(validCharacters).contains(temps[i])))
			{
				//mark it as invalid
				this.isThisInvalid[row][i] = true;
				
				anyErrorsFound = true;
			}
		}
		
		
	}
	
	/** If you complete the puzzel, this congratulatory message
	* will appear :)
	*/
	public static void congratulationsMessage()
	{
		// fun fireworks display!
		System.out.println("                                   .''.       ");
		System.out.println("       .''.      .        *''*    :_\\/_:     . ");
		System.out.println("      :_\\/_:   _\\(/_  .:.*_\\/_*   : /\\ :  .'.:.'.");
		System.out.println("  .''.: /\\ :   ./)\\   ':'* /\\ * :  '..'.  -=:o:=-");
		System.out.println(" :_\\/_:'.:::.    ' *''*    * '.\\'/.' _\\(/_'.':'.'");
		System.out.println(" : /\\ : :::::     *_\\/_*     -= o =-  /)\\    '  *");
		System.out.println("  '..'  ':::'     * /\\ *     .'/.\\'.   '");
		System.out.println("      *            *..*         :");
		System.out.println("Congratulations, you solved the Sudoku puzzle!");
	}
	
	/** Print the opening message that asks you what difficulty puzzle
	* you want to play and retrieve yur answer. If you enter an invalid 
	* respsonse, you will be prompted again.
	*/
	public void printOpening()
	{
		// openning banner
        System.out.println("\n\t\t\tSuduko"); 
		System.out.println("|----------------------------------------------------| \n"); 
		// choose the difficulty of the game through user input
		System.out.println("Choose a Difficulty: Easy(e), Medium(m), Hard(h)");
		
		
		// keep asking for a difficulty unitl proper input is entered
		do
		{
			System.out.print("Difficulty: ");
			this.userInputDifficulty = s.nextLine();
		}while(!this.userInputDifficulty.equals("e") && !this.userInputDifficulty.equals("m") && !this.userInputDifficulty.equals("h") 
			&& !this.userInputDifficulty.equals("q"));
	}
	
	
	/** Assume there are no errors in the grid by
	* assigning each row col value to be flase
	*/
	public void resetErrorGrid()
	{
		// assume there are no errors
		// by setting this whole error checking 2D array to "no"'
		for(int i = 0; i < 9; i++)
		{
			for(int y = 0; y < 9; y++)
			{
				this.isThisADuplicate[i][y] = false;
				this.isThisInvalid[i][y] = false;
			}
		}
	}
	
	/** Get user input, if the input isnt in the right format, ask again
	*/
	public void getUserInput()
	{
		int counter = 0;
		
		// keep asking for user input if it isnt the right format
		do
		{
			if(counter > 0)
			{
				System.out.println(ANSI_RED + "Input Error!" + ANSI_RESET);
			}
			System.out.print("Set: ");
			this.userInput = s.nextLine();
			counter++;
		}while((this.userInput.length() != maxInputLength) && !(this.userInput.equals("q")) && !(this.userInput.equals("c")));
		
	}
	
	/** Assuming input is valid at this point, the format should be
	* number, row, col, so split the data into these respective variables
	*/
	public UserInput splitUserInput()
	{
		
		// since we know we will only have three inputs, we can use split
		String[] answerToken = this.userInput.split(",");
		
		// user input object we will be returning with all the following values
		UserInput u = new UserInput();
		
		// this will hold the value
		u.value = answerToken[0];
		// we will subtract 1 from the row/col values to make them zero based
		// the will hold the row value
		u.row = Integer.parseInt(answerToken[1]) - 1;
		// this will hold the col value
		u.col = Integer.parseInt(answerToken[2]) - 1;
		
		return u;
	}
	
	/** Check that the user input is valid, ex: #,row,col
	*/
	public void checkUserInputFormat()
	{
		// if the input is 5 characters long, check to make sure there are separated 
		// characters. We dont want an input like this: 22222
		while(this.userInput.charAt(1) != ',' && this.userInput.charAt(3) != ',')
		{
			System.out.println(ANSI_RED + "Input Error!" + ANSI_RESET);
			System.out.print("Set: ");
			this.userInput = s.nextLine();
			// once we get the next group of inputs, dont check again if they
			// are the check and quit commands
			if(this.userInput.equals("c") || this.userInput.equals("q"))
			{
				return;
			}
		}	
	}
	
	/////////////////////////////////////////////
	// Main
	/////////////////////////////////////////////
	
    public static void main(String args[]) 
    { 	
		// use this object to call all the methods
		Sudoku s = new Sudoku();
		
		// print the opening banner
		s.printOpening();
		
		// provide a space
		System.out.println();
		
		// Now we must read the config file to get all of our predetermined numbers 
		// and their indexes
		if(s.userInputDifficulty.equals("e"))
		{
			s.readConfigFile("easy.txt");
		}
		else if(s.userInputDifficulty.equals("m"))
		{
			s.readConfigFile("medium.txt");
		}
		else if(s.userInputDifficulty.equals("h"))
		{
			s.readConfigFile("hard.txt");
		}
		else if(s.userInputDifficulty.equals("q"))
		{
			return;
		}

		// set all the predetermined values in the grid array
		s.setGrid();
		
		//before we do anything, assume there are no errors
		// by setting this whole error checking grid
		s.resetErrorGrid();
		
		// print the grid
		s.printGrid();
		
		// while there are errors that were found in the grid
		do
		{
			// before each new iteration, reset the color patterns
			s.resetErrorGrid();
			
			// reset the boolean
			s.anyErrorsFound = false;
			
			// add a space in between the grid and the next text
			System.out.println();
			
			// these are the instructions 
			System.out.println("Enter your answer as: #, row, col");
			// easier for the user to use a 1 based system, we will subtract 1 from
			// each value after we split the string
			System.out.println("The top left corner starts at 1,1");
			System.out.println("Type \"c\" to check your work");
			System.out.println("Type \"q\" to quit");
			
			// get the user's input
			s.getUserInput();
			
			// if the user enters the 'q' as input, quit the game
			if(s.userInput.equals("q"))
			{
				return;
			}
			
			// while the user still wants to enter in numbers to the grid
			// (the user will enter a d to signal they want to check their answers)
			while(!(s.userInput.equals("c")))
			{
				// if we have 5 characters, make sure they have ,'s for when we parse
				s.checkUserInputFormat();
				
				// do these desired commands if entered
				if(s.userInput.equals("q"))
				{
					return;
				}
				if(s.userInput.equals("c"))
				{
					continue;
				}
				
				// split the user input in to a #/row/col format
				UserInput lastUserInput = s.splitUserInput();
				
				// add this new character to the bord
				s.sudokuGrid[lastUserInput.row][lastUserInput.col] = lastUserInput.value;
				
				// add a space
				System.out.println();
				
				//print the new bord
				s.printGrid();
				
				// get the next user input values
				s.getUserInput();
				
				// if the user enters the 'q' as input, quit the game
				if(s.userInput.equals("q"))
				{
					return;
				}
			}
			// since the user entered 'q' we must check the user's answer
			// test for row repeats
			for(int i = 0; i < 9; i++)
			{
				s.testForRepeatsInRows(i, 0);
				s.testForValidCharacters(i, 0);
			}
			for(int i = 0; i < 9; i++)
			{
				s.testForRepeatsInCols(0, i);
			}
			//space
			System.out.println();
			
			// print the new grid, with color changes if there were any errors
			s.printGrid();
		}while(s.anyErrorsFound);
		// if there are no errors found, print the congratulations message 
		congratulationsMessage();
    } 
} 