package com.salesforce.sld.system.file;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.sld.foundation.exception.SecurityControlException;

public class UnzipTraversalDefenseTest {

	private ZipFileValidator zipValidator = new ZipFileValidator();
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void allowedUnzip() throws SecurityControlException, IOException {
		File legal = new File(getClass().getResource("/zipFiles/legalZipFile.zip").getPath());

		zipValidator.validateUnzipPath(new ZipFile(legal));
	}

	@Test
	public void allowedUnzipFile() throws SecurityControlException, IOException {
		File legal = new File(getClass().getResource("/zipFiles/legalZipFile.zip").getPath());

		zipValidator.validateUnzipPath(legal);
	}

	@Test
	public void illegalUnzip() throws IOException, SecurityControlException {
		exception.expect(SecurityControlException.class);

		File illegal = new File(getClass().getResource("/zipFiles/illegalZipFile.zip").getPath());
		zipValidator.validateUnzipPath(new ZipFile(illegal));
	}

	@Test
	public void illegalUnzipFile() throws IOException, SecurityControlException {
		exception.expect(SecurityControlException.class);

		File illegal = new File(getClass().getResource("/zipFiles/illegalZipFile.zip").getPath());
		zipValidator.validateUnzipPath(illegal);
	}

	@Test
	public void fakeUnzip() throws SecurityControlException, IOException {
		exception.expect(IOException.class);

		File fake = new File("this/doesnt/exist.zip");
		zipValidator.validateUnzipPath(new ZipFile(fake));
	}

	@Test
	public void fakeUnzipFile() throws SecurityControlException, IOException {
		exception.expect(IOException.class);

		File fake = new File("this/doesnt/exist.zip");
		zipValidator.validateUnzipPath(fake);
	}
}
