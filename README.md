# CSVTool
Project I made for my Java programming class course project. 

About
This utility is designed for anyone working with tabular data in .csv or .tsv format. The CSV Tool modifies headers (or creates them if non-existent), appends data to existing tables, and reformats tables for export into other software.

Settings for any of these functions can be saved and loaded again for use on a different set of tables. This allows frequent reformatting of tables to be done quickly without needing an SQL server to do simple data cleanup.

Though this utility was originally designed for use on a political campaign, businesses and organizations of all types can make use of it. Anyone can benefit from this tool not only by saving time, but also from making these tasks simpler and easier to execute for those unfamiliar with SQL or some of the more complex functions of programs like Calc or Excel.

The CSV Tool is useful in any environment where you may be passing tabular data between different pieces of software. Much of the time, each of these applications require an imported table to be formatted perfectly. Newer applications can be more flexible in this regard, but that doesn't solve the issue if legacy software is anywhere in the tool-chain. This CSV Tool provides a workaround to the lackluster import options of many programs.

Who is this for?
All businesses usually deal with some form of data. Rather than tailor this program to a single target, the functionality is very flexible and allows anyone to make use of it. Use cases are too numerous to list, but here are a few:

• Inventory tracking between POS and analytics
• Migrating payroll data
• Migrating contact lists between software

Many types of businesses can benefit from using this tool. Restaurants, retail, fitness, logistics, government, healthcare, and repair facilities all deal with data in a way that could be easier with the CSV Tool.

Pseudocode:
Program will open with a welcome message.
If no table is loaded into tableA, the program will display a prompt to import one.
If there is a table loaded into tableB, but not tableA, a warning will be displayed prompting the user to import a primary table.
A prompt toward the bottom will ask the user to enter a command and wait for the string to be entered.

Main loop will take user input and pass it to the script parser.
Input will be checked for what command to run and execute that method.
	If any of these methods fail, the error will be displayed on screen.
	If any of these are fatal errors, the main  loop will break and the error code will be displayed.
  
User input format:
<table>.<column>.<command> <parameters>

The <table> is either the letter 'a' or 'b'. Any other character will display a warning. Capital letters are acceptable. Some commands do not need a table to be indicated and will ignore it even if it is present. If a command requires a table and it is omitted, that command will fail. The 'a' table is the primary table and the 'b' table is the secondary table. Anywhere in the documentation where an 'A' or a 'B' is used, either one will work.

The <column> is an integer number that is displayed before the listed header labels. Same rules apply to columns and tables. Some commands do not require this be entered, but some do. Warnings will be displayed when invalid input is entered.

The <command> is a string without any spaces. If the entered command matches a valid function then it will be called and any following arguments passed to it. If no valid command matches the input, a warning will be displayed.

The <parameters> are separated from the rest of the input by a space and multiple parameters can be input by separating them with more spaces. These are passed to the command methods. There are no maximum limits, but most commands require a minimum amount of parameters. If the parameter list or any specific element is invalid, the command method will fail and display an error or warning.

Note: Any filenames passed must be enclosed in double quotes.
Note: Text delimiters are used in regex format. Comma ',' and tab '\t' are the most common.

Commands:
Load Script
load script_filename
This command will read a text file and execute a pre-made script. Each command is executed without any user input until the end of the script is reached or an error is encountered.

Import Table
A.import table_filename character_delimiter
This command will import a file and read it as a tabular file using the specified delimiter. If the delimiter is omitted, then the standard comma delimiter will be used by default.

Export Table
B.export table_filename character_delimiter
This command will export a file as a tabular file using the specified delimiter. If the delimiter is omitted, then the standard comma delimiter will be used by default.

Append Table
A.append table
This command will append the contents of the table in the parameters to the end of the table on the left side of the command. If the headers do not match on these files, a warning will be displayed and the user will be asked to either continue or cancel the operation. If the operation is continued, then extra columns in the right side table will not be written. If the right side table has fewer columns then the remaining columns will have no value in the resulting table.
Note: this does not export anything. This simply modifies the left side table within the application. To save this table, the user must use the Export Table command.

Shift Column
A.8.shift direction amount
This command will move an entire column either left or right by a specified number of spaces. The direction uses the letter 'r' for right and 'l' for left. The amount is an integer number. If the column would go beyond the beginning or the end of the width of the table, then that column will just be set to the first or last position. If the amount is omitted, then the column will be shifted by 1.
Note: Extremely large values can be used to make certain that a column will be set to the beginning or end of the table.

Delete Column
B.5.delete
This command will simply delete a column from the table. There are no parameters for this command.

Insert Column
A.9.insert direction label
This command will insert a new column into the table to either the left or right of the operating column with the specified label. The direction uses the letter 'r' for right and 'l' for left. If the label is omitted then the column header will have no value in that column.

Rename Column
A.0.rename label
This command will simply rename a column from the table. The new label is specified in the parameters.

Exit
exit
This command will exit the application.
