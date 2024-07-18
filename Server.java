import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Server {
	private int port;
	private ServerSocket serverSocket;
	public Map<String, Servlet> servletContainer = new HashMap<>();
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);

	public static void main(String[] args) {
		int port = 8080;
		Server server = new Server(port);
		server.addServlet("/", (req, res) -> res.send("Hello"));
		server.addServlet("/test", (req, res) -> res.send("Test"));
		server.start();
	}

	public Server(int port) {
		this.port = port;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
	}

	public void addServlet(String path, Servlet servlet) {
		this.servletContainer.put(path, servlet);
	}

	public void shutdown() {
		System.out.println("\nShutting down...");
		try {
			Thread.sleep(1000);
			this.threadPool.shutdown();
			this.serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(this.port);
			System.out.println("Listening on port " + this.port);
			while (serverSocket.isBound()) {
				try {
					Socket client = serverSocket.accept();
					threadPool.execute(() -> {
						try {
							handle(client);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				} catch (IOException ex) {
					
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public void handle(Socket client) throws IOException {
		Request req = new Request(client.getInputStream());
		Response res = new Response(client.getOutputStream());
		Servlet servlet = this.servletContainer.get(req.getPath());
		servlet.service(req, res);
	}
}

class Request {
	private String method;
	private String path;
	private Map<String, String> headers = new HashMap<>();

	public Request(InputStream in) throws IOException {
		parse(new BufferedReader(new InputStreamReader(in)));
	}

	private void parse(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		String[] requestLine = line.split(" ");
		this.method = requestLine[0];
		this.path = requestLine[1];

		while ((line = reader.readLine()).length() != 0) {
			if (line.contains(":")) {
				String[] tokens = line.split(":");
				headers.put(tokens[0], tokens[1]);
			}
		}
	}

	public String getMethod() {
		return this.method;
	}

	public String getPath() {
		return this.path;
	}
}

class Response {
	private PrintWriter out;
	
	public Response(OutputStream outputStream) throws IOException {
		this.out = new PrintWriter(outputStream, true);
	}

	public void send(String body) {
		out.println("HTTP/1.1 200 OK");
		out.println("Connection: close");
		out.println("Content-Length: " + body.length());
		out.println();
		out.println(body);
	}
}

/**
 * Servlet
 */
interface Servlet {
	void service(Request req, Response res);	
}

