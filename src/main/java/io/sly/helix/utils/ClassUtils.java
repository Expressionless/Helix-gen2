package io.sly.helix.utils;

import java.util.HashSet;
import java.util.Set;

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

	/**
	 * Get the class simple name of an object
	 * @param o
	 * @return
	 */
	public static String getClassName(Object o) {
		String str = o.getClass().getSimpleName();
		int index = str.indexOf('_');
		if(index != -1)
			str = str.substring(0, index);
		return str;
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
}
