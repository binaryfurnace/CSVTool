package csvutils;
import java.io.*;
import java.util.*;
import screenutils.*;

@SuppressWarnings("unused")
public class HeaderOperation {
	protected ArrayList<String> recordHistory;
	protected File scriptFile;
	protected int cmd_flag;
	protected CSVFile tableA;
	protected int active_table;
	protected int active_column;
	
	public boolean debug_mode;
	
	protected int run_code;
	protected String post_cmd;
	
	public HeaderOperation() {
		debug_mode = false;
		recordHistory = new ArrayList<String>();
		tableA = new CSVFile("DEFAULT", ',');
		post_cmd = "";
		active_table = 0;
		active_column = 0;
		// run code is zero while running, negative is a fatal error
		// run code of 1 is a successful termination
		run_code = 0;
	}
	
	public void dbg(String out) {
		if(debug_mode) {
			System.out.println(out);
		}
	}
	
	public int execApp() {
		String inp;
		Scanner scan_in = new Scanner(System.in);
		while(run_code == 0) {
			// Main Application Loop
			ScreenUtils.clearScreen();
			System.out.println("Welcome to the Binary Furnace CSV Tool");
			if(!tableA.isReady()) {
				System.out.println("Import a table to get started");
			}
			System.out.println("\n\n\n");
			System.out.println(post_cmd);
			post_cmd = "";
			System.out.println("Enter a command:");

			inp = scan_in.nextLine();
			execOp(inp);
		}
		scan_in.close();
		return run_code;
	}
	
	protected void execOp(String op) {
		this.execOp(op, 0);
	}
	
	protected void execOp(String op, int exec) {
		cmd_flag = exec;
		active_table = -99;
		active_column = -99;
		
		// Break the op String into separate pieces
		// Format:
		// <table>.<column>.<command> <parameters>

		String ts[] = op.split("(\\.)(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		
		String op_table = "";
		String op_column = "";
		String op_command = "";
		ArrayList<String> op_parameters = new ArrayList<String>();
		
		if(ts.length >= 3) {
			
			if(debug_mode) {
				String disp = "[";
				disp += ts[0];
				for(int i = 1; i < ts.length; i++) {
					disp += ", ";
					disp += ts[i];
				}
				disp += "]";
				dbg("Split input: " + disp);
			}
			
			dbg(ts.length + " substrings.");
			
			op_table = ts[0];
			op_column = ts[1];
			
			ArrayList<String> tmpA = new ArrayList<String>(Arrays.asList(ts));
			
			ts = tmpA.get(2).split("(\\s)(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			op_command = ts[0];
			tmpA = new ArrayList<String>(Arrays.asList(ts));
			
			if(tmpA.size() > 1) {
				tmpA.remove(0);
				op_parameters = tmpA;
			}
		}
		else if(ts.length == 2) {
			
			if(debug_mode) {
				String disp = "[";
				disp += ts[0];
				for(int i = 1; i < ts.length; i++) {
					disp += ", ";
					disp += ts[i];
				}
				disp += "]";
				dbg("Split input: " + disp);
			}
			
			dbg(ts.length + " substrings.");
			
			try {
				// First parameter is a number
				int num = Integer.parseInt(ts[0]);
				op_column = ts[0];
				op_table = "NULL";
			} catch (NumberFormatException e) {
				// First parameter is NOT a number
				op_table = ts[0];
				op_column = "NULL";
			}
			ArrayList<String> tmpA = new ArrayList<String>(Arrays.asList(ts));
			tmpA.remove(0);
			
			ts = tmpA.get(0).split("(\\s)(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			op_command = ts[0];
			tmpA = new ArrayList<String>(Arrays.asList(ts));
			
			if(tmpA.size() > 1) {
				tmpA.remove(0);
				op_parameters = tmpA;
			}
		}
		else if(ts.length == 1) {
			
			if(debug_mode) {
				String disp = "[";
				disp += ts[0];
				for(int i = 1; i < ts.length; i++) {
					disp += ", ";
					disp += ts[i];
				}
				disp += "]";
				dbg("Split input: " + disp);
			}
			
			dbg(ts.length + " substrings.");
			
			op_column = "NULL";
			op_table = "NULL";
			
			ArrayList<String> tmpA = new ArrayList<String>(Arrays.asList(ts));
			
			ts = tmpA.get(0).split("(\\s)(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			op_command = ts[0];
			tmpA = new ArrayList<String>(Arrays.asList(ts));
			
			if(tmpA.size() > 1) {
				tmpA.remove(0);
				op_parameters = tmpA;
			}
		}
		
		if (debug_mode) {
			System.out.println("Table: " + op_table);
			System.out.println("Column: " + op_column);
			System.out.println("Command: " + op_command);
			System.out.println("Parameters: " + op_parameters);
		}
		
		if(op_table.contentEquals("a") || op_table.contentEquals("A")) {
			active_table = 0;
			dbg("Active table is being set to: " + active_table);
		}
		if(op_table.contentEquals("NULL")) {
			active_table = -1;
			dbg("Active table is being set to: " + active_table);
		}
		if (active_table == -99) {
			System.out.println("Error: table must be a letter, a or b");
			dbg("Active table is currently set to: " + active_table);
			return;
		}
		/* Everything is now overriden in the subclass, this is no longer needed
		else if (exec < 2) {
			System.out.println("Warning: single table operation is selected, changing to table a");
		}
		*/
		if (op_column.contentEquals("NULL")) active_column = -1;
		else {
			try {
				active_column = Integer.parseInt(op_column);
			} catch (NumberFormatException e) {
				post_cmd = "Error: column must be a number";
				return;
			}
		}
		if (active_column == -99) {
			post_cmd = "Error: something went wrong setting the active column";
			return;
		}
		
		switch(op_command) {
		case "exit":
			run_code = 1;
			break;
		case "load":
			loadScript(op_parameters);
			cmd_flag = (cmd_flag % 2 == 0) ? (cmd_flag + 1) : cmd_flag;
			break;
		case "shift":
			shiftScript(op_parameters);
			cmd_flag = (cmd_flag % 2 == 0) ? (cmd_flag + 1) : cmd_flag;
			break;
		case "delete":
			deleteScript(op_parameters);
			cmd_flag = (cmd_flag % 2 == 0) ? (cmd_flag + 1) : cmd_flag;
			break;
		case "insert":
			insertScript(op_parameters);
			cmd_flag = (cmd_flag % 2 == 0) ? (cmd_flag + 1) : cmd_flag;
			break;
		case "rename":
			renameScript(op_parameters);
			cmd_flag = (cmd_flag % 2 == 0) ? (cmd_flag + 1) : cmd_flag;
			break;
		case "export":
			exportScript(op_parameters);
			cmd_flag = (cmd_flag % 2 == 0) ? (cmd_flag + 1) : cmd_flag;
			break;
		case "import":
			importScript(op_parameters);
			cmd_flag = (cmd_flag % 2 == 0) ? (cmd_flag + 1) : cmd_flag;
			break;
		default:
			if(cmd_flag == 0) {
				System.out.println("That command is not recognized.");
			}
			break;
		}
	}
	
	// Script functions will be protected to prevent them from being called publicly
	// Subclasses should still be able to access these
	protected void loadScript(ArrayList<String> args) {
		if(args.size() < 1) {
			post_cmd = "Error: load requires at least 1 argument";
			return;
		}
		
		try {
			scriptFile = new File(args.get(0));
		} catch (NullPointerException e) {
			post_cmd = "Error: filename cannot be empty!";
			return;
		}
		
		FileReader reader;
		try {
			reader = new FileReader(scriptFile);
		} catch (FileNotFoundException e) {
			post_cmd = "Error: check file path, this file does not exist.";
			return;
		}
		
		recordHistory = new ArrayList<String>();
		
		char rd = '\0';
		int g = 0;
		String tmpstr = "";
		while(g != -1) {
			try {
				g = reader.read();
			} catch (IOException e) {
				post_cmd = "Error: " + e;
				return;
			}
			
			// Add the read character to the temporary String
			if(rd != '\n') {
				tmpstr += rd;
				continue;
			// When the delimiter is encountered, add the String to the array
			// Then, reset the temporary string
			} else {
				recordHistory.add(tmpstr);
				tmpstr = "";
				continue;
			}
		}
		
		// We are done with the reader
		try {
			reader.close();
		} catch (IOException e) {
			post_cmd = "Error: " + e;
		}
		
		// Execute each line of the script
		for (int i = 0; i < recordHistory.size(); i++) {
			execOp(recordHistory.get(i));
			if(post_cmd != "") return;
		}
		if (debug_mode) {
			String method = new Object() {} 
            .getClass() 
            .getEnclosingMethod() 
            .getName(); 
			System.out.println(method + " finished running with post_cmd: \n" + post_cmd);
			System.out.println("Press \"ENTER\" to continue...");
	        try {
	            int read = System.in.read(new byte[2]);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	protected void shiftScript(ArrayList<String> args) {
		if(args.size() < 1) {
			post_cmd = "Error: column.shift requires at least 1 argument";
			return;
		}
		if (debug_mode) {
			String method = new Object() {} 
            .getClass() 
            .getEnclosingMethod() 
            .getName(); 
			System.out.println(method + " finished running with post_cmd: \n" + post_cmd);
			System.out.println("Press \"ENTER\" to continue...");
	        try {
	            int read = System.in.read(new byte[2]);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	protected void deleteScript(ArrayList<String> args) {
		if (debug_mode) {
			String method = new Object() {} 
            .getClass() 
            .getEnclosingMethod() 
            .getName(); 
			System.out.println(method + " finished running with post_cmd: \n" + post_cmd);
			System.out.println("Press \"ENTER\" to continue...");
	        try {
	            int read = System.in.read(new byte[2]);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	protected void insertScript(ArrayList<String> args) {
		if(args.size() < 1) {
			post_cmd = "Error: column.insert requires at least 1 argument";
			return;
		}
		if (debug_mode) {
			String method = new Object() {} 
            .getClass() 
            .getEnclosingMethod() 
            .getName(); 
			System.out.println(method + " finished running with post_cmd: \n" + post_cmd);
			System.out.println("Press \"ENTER\" to continue...");
	        try {
	            int read = System.in.read(new byte[2]);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	protected void renameScript(ArrayList<String> args) {
		if(args.size() < 1) {
			post_cmd = "Error: column.rename requires at least 1 argument";
			return;
		}
		if (debug_mode) {
			String method = new Object() {} 
            .getClass() 
            .getEnclosingMethod() 
            .getName(); 
			System.out.println(method + " finished running with post_cmd: \n" + post_cmd);
			System.out.println("Press \"ENTER\" to continue...");
	        try {
	            int read = System.in.read(new byte[2]);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	protected void exportScript(ArrayList<String> args) {
		if(args.size() < 1) {
			post_cmd = "Error: table.export requires at least 1 argument";
			return;
		}
		if (debug_mode) {
			String method = new Object() {} 
            .getClass() 
            .getEnclosingMethod() 
            .getName(); 
			System.out.println(method + " finished running with post_cmd: \n" + post_cmd);
			System.out.println("Press \"ENTER\" to continue...");
	        try {
	            int read = System.in.read(new byte[2]);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	protected void importScript(ArrayList<String> args) {
		if(args.size() < 1) {
			post_cmd = "Error: table.import requires at least 1 argument";
			return;
		}
		if (debug_mode) {
			String method = new Object() {} 
            .getClass() 
            .getEnclosingMethod() 
            .getName(); 
			System.out.println(method + " finished running with post_cmd: \n" + post_cmd);
			System.out.println("Press \"ENTER\" to continue...");
	        try {
	            int read = System.in.read(new byte[2]);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
}
