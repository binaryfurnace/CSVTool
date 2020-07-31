package csvutils;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import screenutils.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class AppendOperation extends HeaderOperation {
	protected CSVFile tableB;
	
	public AppendOperation() {
		super();
		tableB = new CSVFile("DEFAULT", ',');
	}
	
	public int execApp() {
		String inp;
		Scanner scan_in = new Scanner(System.in);
		while(run_code == 0) {
			// Main Application Loop
			ScreenUtils.clearScreen();
			System.out.println("Welcome to the Binary Furnace CSV Tool");
			if(!tableA.isReady() & !tableB.isReady()) {
				System.out.println("Import a table to get started");
			} else if (!tableA.isReady() & tableB.isReady()) {
				System.out.println("Warning: Secondary table is loaded but there is no primary table!");
				System.out.println("Import a primary table to get started");
			}
			displayHeaders();
			System.out.println(post_cmd);
			post_cmd = "";
			System.out.println("Enter a command:");
			
			inp = scan_in.nextLine();
			execOp(inp);
		}
		scan_in.close();
		return run_code;
	}
	
	protected void displayHeaders() {
		System.out.println("TableA                                                  TableB\n");
		ArrayList<String> leftS = new ArrayList<String>();
		ArrayList<String> rightS = new ArrayList<String>();
		if(tableA.table.size() > 0) {
			for(int i = 0; i < tableA.table.get(0).size(); i++) {
				leftS.add(tableA.table.get(0).get(i));
			}
		}
		
		if(tableB.table.size() > 0) {
			for(int i = 0; i < tableB.table.get(0).size(); i++) {
				rightS.add(tableB.table.get(0).get(i));
			}
		}
		
		for(int i = 0; i < leftS.size() - 1 || i < rightS.size() - 1; i++) {
			System.out.print(i + ".\t");
			if(i < leftS.size()) {
				System.out.print(leftS.get(i));
				for(int ij = 0; ij < 48 - (leftS.get(i).length()); ij++) {
					System.out.print(" ");
				}
			}
			System.out.print(i + ".\t");
			if(i < rightS.size()) {
				System.out.print(rightS.get(i));
			}
			System.out.println();
		}
	}

	protected void execOp(String op) {
		//super.execOp(op, 2);
		cmd_flag = 2;
		// If a command successfully exectuted in the superclass, return
		if(cmd_flag == 3) {
			return;
		}
		
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
		if(op_table.contentEquals("b") || op_table.contentEquals("B")) {
			active_table = 1;
			dbg("Active table is being set to: " + active_table);
		}
		// Indicate there was no table selected.
		// Some commands will fail on this setting
		if(op_table.contentEquals("NULL")) {
			active_table = -1;
			dbg("Active table is being set to: " + active_table);
		}
		if (active_table == -99) {
			System.out.println("Error: table must be a letter, a or b");
			dbg("Active table is currently set to: " + active_table);
			return;
		}
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
		case "append":
			appendScript(op_parameters);
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
			if(cmd_flag == 2) {
				post_cmd = "That command is not recognized.";
			}
			break;
		}
	}
	
	// Script functions will be protected to prevent them from being called publicly
	// Subclasses should still be able to access these
	protected void appendScript(ArrayList<String> args) {
		if(args.size() < 1) {
			post_cmd = "Error: table.append requires at least 1 argument";
			return;
		}
		if(active_table < 0) {
			post_cmd = "Error: column.rename requires an active table";
			return;
		}
		
		CSVFile opTable;
		if(active_table == 0) {
			opTable = tableA;
		} else if (active_table == 1) {
			opTable = tableB;
		} else {
			post_cmd = "Error: no active table is set!";
			return;
		}
		
		CSVFile appTable;
		if(args.get(0).contains("a") || args.get(0).contains("A")) {
			appTable = tableA;
		} else if (args.get(0).contains("b") || args.get(0).contains("B")) {
			appTable = tableB;
		} else {
			post_cmd = "Error: unable to set the source table";
			return;
		}
		
		for (int i = 1; i < appTable.table.size(); i++) {
			opTable.table.add(appTable.table.get(i));
		}
		
		post_cmd = appTable.table.size() + " records appended to the target table.";
		
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
		
		if(active_table < 0) {
			post_cmd = "Error: column.rename requires an active table";
			return;
		}
		if(active_column < 0) {
			post_cmd = "Error: column.rename requires an active column";
			return;
		}
		
		CSVFile opTable;
		if(active_table == 0) {
			opTable = tableA;
		} else if (active_table == 1) {
			opTable = tableB;
		} else {
			post_cmd = "Error: no active table is set!";
			return;
		}
		
		int dir = args.get(0).contains("l") ? -1 : 1;
		
		int amt = 1;
		
		if(args.size() >= 2) {
			try {
				amt = Integer.parseInt(args.get(1));
			} catch (NumberFormatException e) {
				post_cmd = "Error: the amount must be an integer number";
				return;
			} 
		}
		
		int shift = active_column + (dir * amt);
		if(shift < 0) shift++;
		
		int count = opTable.table.size();
		for(int i = 0; i < count; i++) {
			String tmpMv = opTable.table.get(i).get(active_column);
			opTable.table.get(i).add(shift, tmpMv);
		}
		int remCol = active_column;
		if(dir < 0) remCol++;
		for(int i = 0; i < count; i++) {
			opTable.table.get(i).remove(remCol);
		}
		
		post_cmd = count + " records affected. Column " + active_column +
						" shifted to " + (active_column + (dir * amt)) + ".";
		
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
		if(active_table < 0) {
			post_cmd = "Error: column.rename requires an active table";
			return;
		}
		if(active_column < 0) {
			post_cmd = "Error: column.rename requires an active column";
			return;
		}
		
		CSVFile opTable;
		if(active_table == 0) {
			opTable = tableA;
		} else if (active_table == 1) {
			opTable = tableB;
		} else {
			post_cmd = "Error: no active table is set!";
			return;
		}
		
		int count = opTable.table.size();
		for(int i = 0; i < count; i++) {
			opTable.table.get(i).remove(active_column);
		}
		
		post_cmd = count + " records affected. Column " + active_column + " deleted.";
		
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
		
		if(active_table < 0) {
			post_cmd = "Error: column.rename requires an active table";
			return;
		}
		if(active_column < 0) {
			post_cmd = "Error: column.rename requires an active column";
			return;
		}
		
		CSVFile opTable;
		if(active_table == 0) {
			opTable = tableA;
		} else if (active_table == 1) {
			opTable = tableB;
		} else {
			post_cmd = "Error: no active table is set!";
			return;
		}
		
		int dir = args.get(0).contains("l") ? 0 : 1;
		opTable.table.get(0).add(active_column + dir, args.get(1));
		
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
		if(active_table < 0) {
			post_cmd = "Error: column.rename requires an active table";
			return;
		}
		if(active_column < 0) {
			post_cmd = "Error: column.rename requires an active column";
			return;
		}
		
		CSVFile opTable;
		if(active_table == 0) {
			opTable = tableA;
		} else if (active_table == 1) {
			opTable = tableB;
		} else {
			post_cmd = "Error: no active table is set!";
			return;
		}
		
		opTable.table.get(0).add(active_column, args.get(0));
		opTable.table.get(0).remove(active_column + 1);
		
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
		
		CSVFile opTable;
		if(active_table == 0) {
			opTable = tableA;
		} else if (active_table == 1) {
			opTable = tableB;
		} else {
			post_cmd = "Error: no active table is set!";
			return;
		}
		
		if(args.size() > 1) {
			opTable.setDelimiter(args.get(1).charAt(0));
		} else {
			opTable.setDelimiter(',');
		}
		String pathSplit[] = args.get(0).split("\\\"");
		Path filepath = Paths.get(pathSplit[1]);
		try {
			String bigFile = "";
			for(int i = 0; i < opTable.table.size(); i++) {
				for (int j = 0; j < opTable.table.get(i).size(); j++) {
					bigFile += opTable.table.get(i).get(j);
					if (j + 1 != opTable.table.get(i).size())
						bigFile += opTable.delim;
				}
				bigFile += "\n";
			}
			byte[] bigFileBytes = bigFile.getBytes();
			Path writePath = Files.write(filepath, bigFileBytes);
		} catch (Exception e) {
			e.printStackTrace();
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
		
		CSVFile opTable;
		if(active_table == 0) {
			opTable = tableA;
		} else if (active_table == 1) {
			opTable = tableB;
		} else {
			post_cmd = "Error: no active table is set!";
			return;
		}
		
		if(args.size() > 1) {
			opTable.setDelimiter(args.get(1).charAt(0));
		} else {
			opTable.setDelimiter(',');
		}
		
		String newstr = "";
		for(int i = 0; i < args.get(0).length(); i++) {
			if(args.get(0).charAt(i) != '"')
				newstr += args.get(0).charAt(i);
		}
		
		opTable.ready = opTable.loadFile(newstr);
		opTable.loadTable(newstr);
		
		if(active_table == 0 && debug_mode) {
			tableA = opTable;
			post_cmd = "TableA size: " + tableA.table.size() + "\nOpTable size: " + opTable.table.size();
		} else if (active_table == 1 && debug_mode) {
			tableB = opTable;
			post_cmd = "TableB size: " + tableB.table.size() + "\nOpTable size: " + opTable.table.size();
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
