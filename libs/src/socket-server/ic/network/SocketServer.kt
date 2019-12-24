package ic.network


import ic.annotations.Between
import ic.annotations.DoesNothing
import ic.annotations.ToOverride
import ic.Service
import ic.throwables.IOException

import java.net.ServerSocket
import java.net.SocketException

import ic.log.InstantCoffeeLogger.LOGGER
import ic.loop
import ic.parallel.doInParallel
import ic.throwables.Break.BREAK


abstract class SocketServer : Service() {


	/**
	 * @return TCP port to listen
	 */
	@Between(0, 65537)
	protected abstract val port: Int


	private var serverSocket: ServerSocket? = null


	@Throws(IOException::class)
	protected abstract fun handleConnection(connection: Socket)

	@ToOverride
	protected fun handleIOException(@Suppress("UNUSED_PARAMETER") ioException: IOException) {
	}

	@ToOverride
	protected fun handleError(error: Error) {
		error.printStackTrace()
		LOGGER.writeStackTrace(error)
	}

	@ToOverride
	protected fun handleRuntimeException(runtimeException: RuntimeException) {
		runtimeException.printStackTrace()
		LOGGER.writeStackTrace(runtimeException)
	}


	override fun isReusable() = false // TODO: Make reusable


	@ToOverride
	@Throws(java.io.IOException::class)
	protected open fun createServerSocket(port: Int): ServerSocket {
		return ServerSocket(port)
	}

	@ToOverride
	@DoesNothing
	@Throws(java.io.IOException::class)
	protected open fun onSocketAccept(socket: java.net.Socket) {
	}


	/**
	 * Start a server
	 */
	@Synchronized
	public override fun implementStart() {
		val port = port
		run {
			assert(port >= 0)
			assert(port < 65537)
		}
		try {
			serverSocket = createServerSocket(port)
		} catch (e: java.io.IOException) {
			throw IOException.Runtime(e)
		}

		doInParallel {
			loop {
				try {
					val socket = serverSocket!!.accept()
					doInParallel handlingConnection@{
						try {
							onSocketAccept(socket)
						} catch (ioException: java.io.IOException) {
							handleIOException(IOException(ioException))
							try {
								socket.close()
							} catch (e: java.io.IOException) {}
							return@handlingConnection
						}
						try {
							handleConnection(Socket(socket))
						} catch (ioException: IOException.Runtime) {
							handleIOException(IOException(ioException))
						} catch (ioException: IOException) {
							handleIOException(ioException)
						} catch (runtimeException: RuntimeException) {
							handleRuntimeException(runtimeException)
						} catch (error: Error) {
							handleError(error)
						}
					}
				} catch (socketClosed: SocketException) { throw BREAK
				} catch (ioException: java.io.IOException) {
					handleIOException(IOException(ioException))
				}
			}
		}

	}

	/**
	 * Stop a server.
	 */
	@Synchronized
	public override fun implementStop() {
		try {
			serverSocket!!.close()
		} catch (e: java.io.IOException) {
		}

	}


}
