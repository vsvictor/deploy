package ic


import ic.annotations.*
import ic.struct.list.JoinList
import ic.struct.list.List
import ic.text.JoinStrings
import ic.text.Text
import ic.throwables.CalledTwiceTwice
import ic.throwables.NotExists
import ic.util.reflect.getClassByNameOrThrow
import ic.util.reflect.instantiate


/**
 * App is an Action representing entry point for your program.
 * App can be created and executed only once.
 */
@Single
abstract class App(private val args: Text) : Service() {


	abstract val packageName: String


	private var includedPackageNamesValue: List<String>? = null

	val includedPackageNames : List<String> get() {
		synchronized (this) {
			if (includedPackageNamesValue == null) {
				includedPackageNamesValue = JoinList(
					getFullDependencies(Distribution.get(), packageName),
					List.Default(packageName)
				)
			}
			return includedPackageNamesValue!!
		}
	}


	override fun isReusable(): Boolean {
		return false
	}


	@CalledOnce
	protected abstract fun implementRun(args: Text)

	override fun implementStart() {

		Runtime.getRuntime().addShutdownHook(java.lang.Thread {

			if (this@App.isRunning) {
				this@App.stop()
			}

		})

		implementRun(args)

	}

	init {
		synchronized(App::class.java) {
			if (appValue != null) throw CalledTwiceTwice.Error("Tried to instantiate ic.App twice twice")
			appValue = this
		}
	}

	companion object {

		@JvmStatic @CallOnce fun main(argsSplit: Array<String>) {

			val argsIterator = JoinStrings(argsSplit, ' ').iterator

			var app: App; run {

				val mainClass = getMainClass()

				if (mainClass == App::class.java) {

					val appPackageName = argsIterator.readTill(' ').toString()
					val appArgs = argsIterator.read()

					val main = Package.getNotNull(appPackageName).main

					if (main == null) {
						println("Package $appPackageName doesn't have a 'main'")
						return
					}

					try {

						val appClass = getClassByNameOrThrow<App>(main)

						app = appClass.instantiate(
							List<Class<*>>(Text::class.java),
							List<Any?>(appArgs as Any)
						)

					} catch (notExists: NotExists) {

						app = object : NonJvmApp(appArgs) {
							override val packageName = appPackageName
							override fun getMain(): String {
								return main
							}
						}

					}

				} else {

					val args = argsIterator.read()
					app = mainClass.instantiate(
						List<Class<*>>(Text::class.java), List<Any?>(args)
					)

				}

			}

			app.start()

		}

	}


}


@Volatile
private var appValue: App? = null

val app : App get() = appValue!!
