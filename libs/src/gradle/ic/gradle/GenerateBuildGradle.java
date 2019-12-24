package ic.gradle;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.storage.Directory;
import ic.struct.collection.Collection;
import ic.text.Charset;
import ic.text.TextBuffer;


public @Degenerate class GenerateBuildGradle { @Hide private GenerateBuildGradle() {}


	public static void generateBuildGradle(

		@NotNull final Directory directory,

		@NotNull final Collection<String> srcPaths,
		@NotNull final Collection<String> binPaths,
		@NotNull final Collection<String> jarPaths,

		@NotNull final String mainClassName,

		@NotNull 	final String javaVersion,
		@Nullable 	final String kotlinVersion

	) {

		final TextBuffer buildGradle = new TextBuffer();

		buildGradle.writeLine("apply plugin: 'java'");

		if (kotlinVersion != null) {
			buildGradle.writeLine("apply plugin: 'kotlin'");
		}

		buildGradle.writeLine("apply plugin: 'application'");

		buildGradle.writeLine();

		buildGradle.writeLine("sourceSets {");

		buildGradle.put('\t');
		buildGradle.writeLine("main {");

		buildGradle.put('\t');
		buildGradle.put('\t');
		buildGradle.writeLine("java {");

		srcPaths.forEach(srcPath -> {

			buildGradle.put('\t');
			buildGradle.put('\t');
			buildGradle.put('\t');
			buildGradle.write("srcDir '");
			buildGradle.write(srcPath);
			buildGradle.write("'");
			buildGradle.writeLine();

		});

		buildGradle.put('\t');
		buildGradle.put('\t');
		buildGradle.writeLine("}");

		buildGradle.put('\t');
		buildGradle.writeLine("}");

		buildGradle.writeLine("}");

		//buildGradle.writeLine();

		//buildGradle.writeLine("mainClassName = '" + mainClassName + "'");

		buildGradle.writeLine();

		buildGradle.writeLine("sourceCompatibility = '" + javaVersion + "'");
		buildGradle.writeLine("targetCompatibility = '" + javaVersion + "'");

		buildGradle.writeLine();

		if (kotlinVersion != null) {

			buildGradle.writeLine("buildscript {");
			buildGradle.writeLine("\text.kotlin_version = '" + kotlinVersion + "'");
			buildGradle.writeLine("\trepositories {");
			buildGradle.writeLine("\t\tmavenCentral()");
			buildGradle.writeLine("\t}");
			buildGradle.writeLine("\tdependencies {");
			buildGradle.writeLine("\t\tclasspath \"org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version\"");
			buildGradle.writeLine("\t}");
			buildGradle.writeLine("}");
			buildGradle.writeLine();

			buildGradle.writeLine("repositories {");
			buildGradle.writeLine("\tmavenCentral()");
			buildGradle.writeLine("}");
			buildGradle.writeLine();

		}

		buildGradle.writeLine("dependencies {");

		if (kotlinVersion != null) {

			buildGradle.writeLine("\timplementation \"org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version\"");

		}

		binPaths.forEach(binPath -> {

			buildGradle.put('\t');
			buildGradle.write("compile files('");
			buildGradle.write(binPath);
			buildGradle.write("')");
			buildGradle.writeLine();

		});

		jarPaths.forEach(jarPath -> {

			buildGradle.put('\t');
			buildGradle.write("compile fileTree(dir: '");
			buildGradle.write(jarPath);
			buildGradle.write("', include: '*.jar')");
			buildGradle.writeLine();

		});

		buildGradle.writeLine("}");

		if (kotlinVersion != null) {

			buildGradle.writeLine();

			buildGradle.writeLine("compileKotlin {");
			buildGradle.writeLine("\tkotlinOptions {");
			buildGradle.writeLine("\t\tjvmTarget = \"1.8\"");
			buildGradle.writeLine("\t\tfreeCompilerArgs = ['-Xjvm-default=enable']");
			buildGradle.writeLine("\t}");
			buildGradle.writeLine("}");

			buildGradle.writeLine("compileTestKotlin {");
			buildGradle.writeLine("\tkotlinOptions {");
			buildGradle.writeLine("\t\tjvmTarget = \"1.8\"");
			buildGradle.writeLine("\t\tfreeCompilerArgs = ['-Xjvm-default=enable']");
			buildGradle.writeLine("\t}");
			buildGradle.writeLine("}");

		}

		directory.write("build.gradle", Charset.DEFAULT_UNIX.textToByteSequence(buildGradle));

	}


}
