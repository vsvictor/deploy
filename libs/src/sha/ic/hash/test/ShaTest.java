package ic.hash.test;


import ic.test.Test;
import ic.text.CharOutput;

import java.nio.charset.Charset;

import static ic.hash.Sha.sha64;


public class ShaTest extends Test { @Override public void run(CharOutput charOutput) {

	final long sha = sha64("ilovejava".getBytes(Charset.forName("UTF-8")));

	assert sha == 7189826034930641078L : "\"ilovejava\" -> " + sha;

} }
