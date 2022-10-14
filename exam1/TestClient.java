package exam1;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 该类用于测试代理服务器。更改自exec2的Client类。
 *
 * @author wben, Yang Yongze
 */

public class TestClient {

	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(
			System.in));
	static PrintWriter screen = new PrintWriter(System.out, true);

	public static void main(String[] args) throws Exception {
		try {
			HttpClient myClient = new HttpClient();

			if (args.length != 1) {
				System.err.println("Usage: Client <server>");
				System.exit(0);
			}
			myClient.connect(args[0]);
			screen.println(args[0] + " is listening to your request:");
			String request = keyboard.readLine();

			if (request.startsWith("GET")) {
				myClient.processGetRequest(request);
			} else {
				screen.println("Bad request! \n");
				myClient.close();
				return;
			}

			/*
			  Get the headers and display them.
			 */
			screen.println("Header: \n");
			screen.print(myClient.getHeader() + "\n");
			screen.flush();

			if (request.startsWith("GET")) {
				screen.println();
				screen.print("Enter the name of the file to save: ");
				screen.flush();
				String filename = keyboard.readLine();
				FileOutputStream outfile = new FileOutputStream(filename);

				String response = myClient.getResponse();
				outfile.write(response.getBytes(StandardCharsets.ISO_8859_1));
				outfile.flush();
				outfile.close();
			}

			myClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试客户端的HTTP Client内部类。
	 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
	 *
	 * @author wben
	 */

	public static class HttpClient {


		private static int buffer_size = 8192;
		private byte[] buffer;
		Socket socket = null;
		private static final int PORT = 8000;
		BufferedOutputStream ostream = null;
		BufferedInputStream istream = null;

		private StringBuffer header = null;
		private StringBuffer response = null;
		static private String CRLF = "\r\n";

		public HttpClient() {
			buffer = new byte[buffer_size];
			header = new StringBuffer();
			response = new StringBuffer();
		}

		/**
		 * <em>connect</em> connects to the input host on the default http port --
		 * port 80. This function opens the socket and creates the input and output
		 * streams used for communication.
		 */
		public void connect(String host) throws Exception {

			socket = new Socket(host, PORT);
			ostream = new BufferedOutputStream(socket.getOutputStream());
			istream = new BufferedInputStream(socket.getInputStream());
		}

		/**
		 * <em>processGetRequest</em> process the input GET request.
		 */
		public void processGetRequest(String request) throws Exception {
			/*
			  Send the request to the server.
			 */
			request += CRLF + CRLF;
			buffer = request.getBytes();
			ostream.write(buffer, 0, request.length());
			ostream.flush();

			/*
			  waiting for the response.
			 */
			processResponse();
		}

		/**
		 * <em>processResponse</em> process the server response.
		 *
		 */
		public void processResponse() throws Exception {
			int last = 0, c = 0;
			/*
			  Process the header and add it to the header StringBuffer.
			 */
			boolean inHeader = true; // loop control
			while (inHeader && ((c = istream.read()) != -1)) {
				switch (c) {
				case '\r':
					break;
				case '\n':
					if (c == last) {
						inHeader = false;
						break;
					}
					last = c;
					header.append("\n");
					break;
				default:
					last = c;
					header.append((char) c);
				}
			}

			/*
			  Read the contents and add it to the response StringBuffer.
			 */
			while (istream.read(buffer) != -1) {
				response.append(new String(buffer, StandardCharsets.ISO_8859_1));
			}
		}

		/**
		 * Get the response header.
		 */
		public String getHeader() {
			return header.toString();
		}

		/**
		 * Get the server's response.
		 */
		public String getResponse() {
			return response.toString();
		}

		/**
		 * Close all open connections -- sockets and streams.
		 */
		public void close() throws Exception {
			socket.close();
			istream.close();
			ostream.close();
		}
	}
}
