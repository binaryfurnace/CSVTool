package screenutils;
import java.io.IOException;

public class ScreenUtils {
	public static void clearScreen() { 
		try {
			if( System.getProperty("os.name").contains("Windows") ) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				Runtime.getRuntime().exec("clear");
			}
		} catch (IOException ioe) {
			System.out.println("Error: " + ioe);
		} catch (InterruptedException ie) {
			System.out.println("Error: " + ie);
		}
	}
}
