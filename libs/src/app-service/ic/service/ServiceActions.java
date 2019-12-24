package ic.service;


import ic.Distribution;
import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.throwables.InconsistentData;
import ic.throwables.WrongState;

import static ic.AppKt.getApp;
import static ic.SystemServices.startService;
import static ic.SystemServices.stopService;


@Degenerate public class ServiceActions { @Hide private ServiceActions() {}

	public static void start(String serviceName, String args) throws WrongState {
		startService(
			serviceName,
			Distribution.get().getLaunchDirectory().absolutePath + "/" + getApp().getPackageName() + " " + args
		);
	}

	public static void stop(String serviceName) throws WrongState {
		stopService(serviceName);
	}

	public static void restart(String serviceName, String args) {
		try {
			stop(serviceName);
		} catch (WrongState wrongState) {}
		try {
			start(serviceName, args);
		} catch (WrongState wrongState) { throw new InconsistentData(wrongState); }
	}

}
