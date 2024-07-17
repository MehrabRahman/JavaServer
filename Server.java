import java.io.*;
import java.net.*;

public class Server {
	private int port;
	private ServerSocket serverSocket;

	public static void main(String[] args) {
		int port = 8080;
		Server server = new Server(port);
		server.start();
	}

	public Server(int port) {
		this.port = port;
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(this.port);
			System.out.println("Listening on port " + this.port);
			while (serverSocket.isBound()) {
				Socket client = serverSocket.accept();
				handle(client);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public void handle(Socket client) throws IOException {
		Request req = new Request(client.getInputStream());
		Response res = new Response(client.getOutputStream());
		switch (req.getPath()) {
			case "/" -> res.send("Hello from JavaServer!");
			case "/test" -> res.send("You've found the test endpoint.");
			default -> res.send("That's not a supported endpoint... perhaps we should return a 404 HTTP Status code instead?");
		}
	}
}

class Request {
	private String method;
	private String path;

	public Request(InputStream in) throws IOException {
		parse(new BufferedReader(new InputStreamReader(in)));
	}

	private void parse(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		String[] requestLine = line.split(" ");
		this.method = requestLine[0];
		this.path = requestLine[1];
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

	public void send(String body) throws IOException {
		out.println("HTTP/1.1 200 OK");
		out.println("Connection: close");
		out.println("Content-Length: " + body.length());
		out.println();
		out.println(body);
	}
}

