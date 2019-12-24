package ic


import ic.app.CommandLineApp
import ic.cmd.Command
import ic.cmd.SelectorCommand
import ic.service.commands.*
import ic.service.commands.monitor.MonitorCommand
import ic.service.monitoring.monitor.MonitorService
import ic.stream.ByteSequence
import ic.struct.list.List
import ic.text.Text


abstract class ServiceApp (args: Text) : CommandLineApp(args) {


	protected abstract val status: Any?

	protected abstract fun getBackup(): ByteSequence?


	protected abstract fun initService(args: Text): Service

	protected abstract fun initMonitorService(): MonitorService?


	override fun initCommand(): Command {
		return object : SelectorCommand() {

			override fun getShellWelcome(): String? {
				return null
			}

			override fun getShellTitle(): String {
				return "Service app control shell:"
			}

			override fun getName(): String {
				return app.packageName
			}

			override fun getSyntax(): String {
				return app.packageName
			}

			override fun getDescription(): String {
				return "A service app"
			}

			override fun initChildren(): List<Command> {
				return List.Default(

					object : RunCommand() {
						override fun initService(args: Text): Service {
							return this@ServiceApp.initService(args)
						}

						override fun initMonitorService(): MonitorService? {
							return this@ServiceApp.initMonitorService()
						}

						override fun getStatus(): Any? {
							return this@ServiceApp.status
						}

						override fun getBackup(): ByteSequence? {
							return this@ServiceApp.getBackup()
						}
					},

					StartCommand(),
					StopCommand(),
					RestartCommand(),

					object : DeployCommand() {
						override fun initMonitorService(): MonitorService? {
							return this@ServiceApp.initMonitorService()
						}
					},

					object : MonitorCommand() {
						override fun initMonitorService(): MonitorService? {
							return this@ServiceApp.initMonitorService()
						}
					}

				)
			}

		}
	}


}


val tier: String? get() = Distribution.get().customData.get("tier")

val serviceName: String get() = tier + "." + app.packageName