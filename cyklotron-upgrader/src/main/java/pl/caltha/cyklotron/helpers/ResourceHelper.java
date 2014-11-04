package pl.caltha.cyklotron.helpers;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.log4j.Logger;

public class ResourceHelper {

	static final Logger logger = Logger.getLogger(PostgresqlHelper.class);

	public ResourceHelper() {
	}

	/**
	 * Get resource file content
	 * 
	 * @param name
	 * @return
	 */
	public static String getResourceFileContent(String name) {
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			InputStream is = classLoader.getResourceAsStream(name);
			return read(is);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Converts InputStream to String
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String read(InputStream input) throws IOException {
		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
		return new String(output.toByteArray());
	}

	/**
	 * Delete dir implement
	 * 
	 * @param dir
	 */
	public static void deleteDir(Path dir) {
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					logger.debug("Deleting file: " + file);
					Files.delete(file);
					return CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					logger.debug("Deleting dir: " + dir);
					if (exc == null) {
						Files.delete(dir);
						return CONTINUE;
					} else {
						throw exc;
					}
				}
			});
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Create directory
	 * 
	 * @param dir
	 */
	public static void createDir(Path dir) {
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Copy dir implement
	 * 
	 * @param dir
	 */
	public static void copyDir(String source,String dest) {
		try {
			final Path sourcePath = Paths.get(source);
			if(sourcePath == null){
				throw new IOException("Folder " + source + " does not exist");
			}
			final Path destPath = Paths.get(dest);
			if(destPath == null){
				throw new IOException("Folder " + dest + " does not exist");
			}
			Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(final Path dir,
						final BasicFileAttributes attrs) throws IOException {
					Files.createDirectories(destPath.resolve(sourcePath
							.relativize(dir)));
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(final Path file,
						final BasicFileAttributes attrs) throws IOException {
					logger.debug("Copy file: "
							+ sourcePath.relativize(file).toString() + "to "
							+ destPath.toString());
					Files.copy(file,
							destPath.resolve(sourcePath.relativize(file)));
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Put content as ROOT.xml file
	 * 
	 * @param fileContent
	 */
	public static void saveFile(String fileContent, String path) {
		try {
			InputStream is = new ByteArrayInputStream(
					fileContent.getBytes(StandardCharsets.UTF_8));
			Path target = Paths.get(path);
			Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
			logger.debug("Content save as " + target.toString());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Delete single file
	 * 
	 * @param fileContent
	 */
	public static void deleteFile(String path) {
		try {
			Path target = Paths.get(path);
			Files.deleteIfExists(target);
			logger.debug("file " + target.toString() + " deleted.");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
