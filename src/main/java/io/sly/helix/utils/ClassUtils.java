package io.sly.helix.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

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

	public static Boolean inJarFile() {
		try {
			getJarFile();
		} catch(FileNotFoundException exception) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * For when running from a compiled jar file
	 * @return all distinct {@link Class}es in the compiled jar
	 * @throws IOException - if an IOException has occurred
	 */
	// TODO: REMOVE EXCEPTION FROM THIS
	public static Set<Class<?>> getClassesFromJarFile() throws IOException {
		Set<Class<?>> clazzes = new HashSet<>();
		String path;
		try {
			path = getJarFile();
		} catch(FileNotFoundException exception) {
			log.info("Attempted to get classes from Jar File, but not in a jar !");
			return null;
		}
		System.out.println("Running from: " + path);
		File file = new File(path);		
	    Set<String> classNames = new HashSet<>();
	    try (JarFile jarFile = new JarFile(file)) {
	        Enumeration<JarEntry> e = jarFile.entries();
	        while (e.hasMoreElements()) {
	            JarEntry jarEntry = e.nextElement();
	            if (jarEntry.getName().endsWith(".class")) {
	                String className = jarEntry.getName()
	                  .replace("\\", ".")
	                  .replace(".class", "");
	                classNames.add(className);
	                Class<?> clazz = Class.forName(className);
	                clazzes.add(clazz);
	                System.out.println(clazz.getCanonicalName());
	            }
	        }
	        return clazzes;
	    } catch (ClassNotFoundException e1) {
	    	System.err.println("Could not find class: " + e1.getCause());
			e1.printStackTrace();
			return null;
		}
	}
	
	private static String getJarFile() throws FileNotFoundException {
	    String path = ClassUtils.class.getResource(ClassUtils.class.getSimpleName() + ".class").getFile();
	    if(path.startsWith("/")) {
	        throw new FileNotFoundException("This is not a jar file: \n" + path);
	    }
	    path = ClassLoader.getSystemClassLoader().getResource(path).getFile();
	    path = path.replaceAll("file:/", "");
	    return path.substring(0, path.indexOf('!'));
	}
	
	/**
	 * Get classes relative to /src/main/java + dir
	 * @param dir - Directory to search for classes (relative)
	 * @return A set containing all classes found (recursive)
	 */
	public static Set<Class<?>> getClasses() {
		return getClasses("io.sly");
	}

	public static Set<Class<?>> getClasses(String packageName) {
		
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
					log.severe("Could not load class: " + routeClassInfo.getName());
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
