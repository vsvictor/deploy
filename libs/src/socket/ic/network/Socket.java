package ic.network;


import org.jetbrains.annotations.NotNull;
import ic.interfaces.closeable.Closeable;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.throwables.IOException;


public class Socket implements Closeable {


	public final SocketAddress address;


	private final java.net.Socket javaSocket;


	@Override public void close() {
		try {
			javaSocket.close();
		} catch (java.io.IOException e) { throw new IOException.Runtime(e); }
	}


	public final ByteInput input;

	public final ByteOutput output;


	// Constructors:

	private Socket(@NotNull SocketAddress address, @NotNull java.net.Socket javaSocket) throws IOException {
		assert address 		!= null;
		assert javaSocket 	!= null;
		this.address = address;
		this.javaSocket = javaSocket;
		try {
			this.input = new ByteInput.FromInputStream(
				javaSocket.getInputStream()
			);
			this.output = new ByteOutput.FromOutputStream(
				javaSocket.getOutputStream()
			);
		} catch (java.io.IOException e) { throw new IOException(e); }
	}

	private static java.net.Socket createJavaSocket(SocketAddress address) throws IOException {
		try {
			return new java.net.Socket(address.host, address.port);
		} catch (java.io.IOException e) { throw new IOException(e); }
	}

	public Socket(SocketAddress address) throws IOException {
		this(address, createJavaSocket(address));
	}

	public Socket(java.net.Socket socket) throws IOException {
		this(
			new SocketAddress(
				socket.getInetAddress().getHostAddress(),
				socket.getPort()
			),
			socket
		);
	}


}
