import csvutils.*;

public class CSVTool {
	public static void main(String[] args) {
		
		boolean d = false;
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].contentEquals("-d")) {
				d = true;
			}
		}
		
		int ret_code = 0;
		AppendOperation App = new AppendOperation();
		
		App.debug_mode = d;
		ret_code = App.execApp();
		
		if(ret_code != 1) {
			System.out.println("CSV Tool terminated with error code " + ret_code);
		}
	}
}
