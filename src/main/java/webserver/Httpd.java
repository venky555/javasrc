import java.net.*;
import java.io.*;
import java.util.*;

/**
 * A very very simple Web server.
 *
 * NO SECURITY. ALMOST NO CONFIGURATION.
 * NO CGI. NO SERVLETS.
 *
 * This version is threaded. I/O is done in Handler.
 *
 * TODO
 *	Web Standard logfile formats.
 *	More property definitions...
 */
public class WebServer {
	/** The default port number */
	public static final int HTTP = 80;
	/** The server socket used to connect from clients */
	protected ServerSocket sock;
	/** A Properties, for loading configuration info */
	protected Properties wsp;
	/** A Properties, for loading mime types into */
	protected Properties mimeTypes;
	/** The root directory */
	protected String rootDir;

	public static void main(String argv[]) {
		System.out.println("DarwinSys JavaWeb Server 0.1 starting...");
		WebServer w = new WebServer();
		w.runServer();
		// NOTREACHED
	}

	WebServer() {
		super();
		// A ResourceBundle can't load from the same basename as your class,
		// but a simple Properties can.
		wsp=loadProps("httpd.properties");
		rootDir = wsp.getProperty("rootDir", ".");
		mimeTypes = loadProps(wsp.getProperty("mimeProperties", "mime.properties"));
		String portNumString = null;
		try {
			portNumString = wsp.getProperty("portNum");
			if (portNumString == null)
				sock = new ServerSocket(HTTP);
			else {
				int portNum = Integer.parseInt(portNumString);
				sock = new ServerSocket(portNum);
			}
		} catch(NumberFormatException e) {
			System.err.println("WebServer: \"" + portNumString +
				"\" not a valid number, unable to start server");
			System.exit(1);
		} catch(IOException e) {
			System.err.println("Network error " + e + "\n" +
				"Unable to start server");
			System.exit(1);
		}
	}

	/** Load the Properties. */
	protected Properties loadProps(String fname) {
		Properties sp = new Properties();

		try {
			// Create input file to load from.
			FileInputStream ifile = new FileInputStream(fname);

			sp.load(ifile);
		} catch (FileNotFoundException notFound) {
			System.err.println(notFound);
			System.exit(1);
		} catch (IOException badLoad) { 
			System.err.println(badLoad);
			System.exit(1);
		}
		return sp;
	}

	/** Run the main loop of the Server. */
	void runServer() {
		while (true) {
			try {
				Socket clntSock = sock.accept();
				new Handler(this, clntSock).start();
			} catch(IOException e) {
				System.err.println(e);
			}
		}
	}
}
