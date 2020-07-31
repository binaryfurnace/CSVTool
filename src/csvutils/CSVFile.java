package csvutils;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * CSVFile.java
 * This class will load and read the table.
 * The header row is read and split into the header[] String ArrayList.
 * ArrayList is used here because we do not know how many columns the file
 * 		will have.
 */

public class CSVFile {
	private File source_file;
	private FileReader reader;
	private ArrayList<String> header;
	public char delim;
	public boolean ready;
	public ArrayList<ArrayList<String>> table;
	
	public CSVFile(String fname, char delimiter) {
		header = new ArrayList<String>();
		delim = delimiter;
		ready = loadFile(fname);
		
		table = new ArrayList<ArrayList<String> >();
		
		// Only read table header if the file was valid
		if(ready) readHeader();
	}
	
	public void setDelimiter(char d) {
		delim = d;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public ArrayList<String> getHeader() {
		return header;
	}
	
	public boolean loadFile(String fname) {
		// DEFAULT indicates no table has been imported
		if(fname == "DEFAULT")
			return false;
		// Set the input file, fails if empty
		try {
			source_file = new File(fname);
		} catch (NullPointerException e) {
			System.out.println("Error: filename cannot be empty!");
			return false;
		}
		
		// Setup the reader, fails if the file doesn't exist
		try {
			reader = new FileReader(source_file);
		} catch (FileNotFoundException e) {
			System.out.println("Error: check file path, this file does not exist.");
			return false;
		}

		// succeeds if the file passed all checks
		return true;
	}
	
	public void readHeader() {
		// Double check that the file is valid
		if(!ready) {
			System.out.println("Error: source file is not valid!");
			return;
		}
		
		char rd = '\0';
		String tmpstr = "";
		// Read until first newline to get only the table header
		while(rd != '\n') {
			try {
				rd = (char)reader.read();
			} catch (IOException e) {
				System.out.println("Error: " + e);
				return;
			}
			
			// Add the read character to the temporary String
			if(rd != delim) {
				tmpstr += rd;
				continue;
			// When the delimiter is encountered, add the String to the header
			// Then, reset the temporary string
			} else {
				header.add(tmpstr);
				tmpstr = "";
				continue;
			}
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}
	
	public void loadTable(String fname) {
		if(!ready) {
			System.out.println("Error: source file is not valid!");
			return;
		}
		
		Path fileName = Path.of(fname);
         
        String filein = "";
		try {
			filein = Files.readString(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String rows[] = filein.split("\\n");
		for(int i = 0; i < rows.length; i++) {
			String cols[] = rows[i].split("(" + delim + ")(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			ArrayList<String> aList = new ArrayList<String>();
			for(int j = 0; j < cols.length; j++) {
				aList.add(cols[j]);
			}
			table.add(aList);
		}
		
		/*
		try {
			reader = new FileReader(source_file);
		} catch (FileNotFoundException e) {
			System.out.println("Error: check file path, this file does not exist.");
			return;
		}
		String line = "";
		int iread = 0;
		char rd = '\0';
		while(iread != -1) {
			try {
				iread = reader.read();
			} catch (IOException e) {
				System.out.println("Error: " + e);
				return;
			}
			rd = (char)iread;
			System.out.print(rd);
			
			if(rd != '\n') {
				line += rd;
			} else {
				String row[] = line.split("(" + delim + ")(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				ArrayList<String> aList = new ArrayList<String>();
				for(int i = 0; i < row.length; i++) {
					aList.add(row[i]);
				}
				table.add(aList);
			}
		}
		*/
	}
}
