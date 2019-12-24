package ic.system.bash


fun nohup(command: String): String {
	return "nohup bash -c " + singleQuote(command) + " &> /dev/null &"
}