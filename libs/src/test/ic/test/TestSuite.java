package ic.test;


import ic.text.CharOutput;


public class TestSuite extends Test {

	private final Test[] tests;

	@Override public void run(CharOutput log) {
		for (Test test : tests) test.run(log);
	}

	public TestSuite(Test... tests) {
		this.tests = tests;
	}

}
