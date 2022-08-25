package io.sly.helix.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jboss.logging.Logger;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/**
 * Generic Class Utilities
 * @author Sly
 *
 */
public class ClassUtils {
	public static final Logger log = Logger.getLogger(ClassUtils.class.getCanonicalName());
	
	/**
	 * Get classes relative to /src/main/java + dir
	 * @param dir - Directory to search for classes (relative)
	 * @return A set containing all classes found (recursive)
	 */
	public static Set<Class<?>> getClasses() {
		return getClasses("io.sly");
	}

	public static Set<Class<?>> getClasses(String packageName) {
		log.info("Looking in: " + packageName);
		Set<Class<?>> classes = new HashSet<Class<?>>();

		try (ScanResult scanResult = new ClassGraph()
		.enableAnnotationInfo()
		.enableClassInfo()
		.acceptPackages(packageName)
		.scan()) { // Start the scan
			for (ClassInfo routeClassInfo : scanResult.getAllClasses()) {
				try {
					Class<?> clazzOf = Class.forName(routeClassInfo.getName());
					classes.add(clazzOf);
				} catch (ClassNotFoundException | SecurityException e) {
					log.error("Could not load class: " + routeClassInfo.getName());
				}
			}
		}

		log.info("Fetched " + classes.size() + " items");
		return classes;
	}

	public static String getFullyQualifiedName(String path) {
		
		String identifier = "src\\main\\java";
		int index = path.indexOf(identifier);

		// Remove everything before incl. "java/" and then remove ".java" from the end
		path = path.substring(index)
					.substring(identifier.length())
					.replaceAll("\\\\", ".");
		return path.substring(1, path.length() - 5);
	}
}
