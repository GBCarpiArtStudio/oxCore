/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;

import org.gluu.site.ldap.persistence.util.ReflectHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.xdi.exception.PythonException;
import org.xdi.util.StringHelper;

/**
 * Provides operations with python module
 *
 * @author Yuriy Movchan Date: 08.21.2012
 */
@Scope(ScopeType.APPLICATION)
@Name("pythonService")
@AutoCreate
public class PythonService implements Serializable {

	private static final long serialVersionUID = 3398422090669045605L;

	@Logger
	private Log log;

	private PythonInterpreter pythonInterpreter;
	private boolean interpereterReady;

	/*
	 * Initialize singleton instance during startup
	 */
	public boolean initPythonInterpreter() {
		boolean result = false;

		if (isInitInterpreter()) {
	        try {
	    		PythonInterpreter.initialize(getPreProperties(), getPostProperties(), null);
	            this.pythonInterpreter = new PythonInterpreter();
	            
	            // Init output redirect for all new interpreters
	            this.pythonInterpreter.setOut(new PythonLoggerOutputStream(log, false));
	            this.pythonInterpreter.setErr(new PythonLoggerOutputStream(log, true));
	
	            result = true;
			} catch (PyException ex) {
				log.error("Failed to initialize PythonInterpreter correctly", ex);
			} catch (Exception ex) {
				log.error("Failed to initialize PythonInterpreter correctly", ex);
			}
		}

        this.interpereterReady = result;

        return result;
	}

	/**
	 * When application undeploy we need clean up pythonInterpreter
	 */
	@Destroy
	public void destroy() {
		log.debug("Destroying pythonInterpreter component");
		if (this.pythonInterpreter != null) {
			this.pythonInterpreter.cleanup();
		}
	}

	private Properties getPreProperties() {
		Properties props = System.getProperties();
		Properties clonedProps = (Properties) props.clone();
		clonedProps.setProperty("java.class.path", ".");
		clonedProps.setProperty("java.library.path", "");
		clonedProps.remove("javax.net.ssl.trustStore");
		clonedProps.remove("javax.net.ssl.trustStorePassword");

		return clonedProps;
	}

	private Properties getPostProperties() {
		Properties props = getPreProperties();

		String catalinaTmpFolder = System.getProperty("java.io.tmpdir") + File.separator + "python" + File.separator + "cachedir";
		props.setProperty("python.cachedir", catalinaTmpFolder);

		String pythonHome = System.getenv("PYTHON_HOME");
		if (StringHelper.isNotEmpty(pythonHome)) {
			props.setProperty("python.home", pythonHome);
		}
		
		// Register custom python modules
		String oxAuthPythonModulesPath = System.getProperty("catalina.home") + File.separator + "conf" + File.separator + "python";
		props.setProperty("python.path", oxAuthPythonModulesPath);

		return props;
	}

	private boolean isInitInterpreter() {
		String pythonHome = System.getenv("PYTHON_HOME");
		return StringHelper.isNotEmpty(pythonHome);
	}

	public <T> T loadPythonScript(String scriptName, String scriptPythonType, Class<T> scriptJavaType, PyObject[] constructorArgs) throws PythonException {
		if (!interpereterReady || StringHelper.isEmpty(scriptName)) {
			return null;
		}

    	PythonInterpreter currentPythonInterpreter = PythonInterpreter.threadLocalStateInterpreter(null);
        try {
        	currentPythonInterpreter.execfile(scriptName);
		} catch (Exception ex) {
			throw new PythonException(String.format("Failed to load python file '%s'", scriptName), ex);
		}

        return loadPythonScript(scriptPythonType, scriptJavaType, constructorArgs, currentPythonInterpreter);
	}

	public <T> T loadPythonScript(InputStream scriptFile, String scriptPythonType, Class<T> scriptJavaType, PyObject[] constructorArgs) throws PythonException {
		if (!interpereterReady || (scriptFile == null)) {
			return null;
		}

    	PythonInterpreter currentPythonInterpreter = PythonInterpreter.threadLocalStateInterpreter(null);
        try {
        	currentPythonInterpreter.execfile(scriptFile);
		} catch (Exception ex) {
			throw new PythonException(String.format("Failed to load python file '%s'", scriptFile), ex);
		}

        return loadPythonScript(scriptPythonType, scriptJavaType, constructorArgs, currentPythonInterpreter);
	}

	@SuppressWarnings("unchecked")
	private <T> T loadPythonScript(String scriptPythonType, Class<T> scriptJavaType, PyObject[] constructorArgs, PythonInterpreter interpreter)
			throws PythonException {
		PyObject scriptPythonTypeObject = interpreter.get(scriptPythonType);
        if (scriptPythonTypeObject == null) {
        	return null;
        }
        

        PyObject scriptPythonTypeClass;
        try {
        	scriptPythonTypeClass = scriptPythonTypeObject.__call__(constructorArgs);
		} catch (Exception ex) {
			throw new PythonException(String.format("Failed to initialize python class '%s'", scriptPythonType), ex);
		}

        Object scriptJavaClass = scriptPythonTypeClass.__tojava__(scriptJavaType);
        if (!ReflectHelper.assignableFrom(scriptJavaClass.getClass(), scriptJavaType)) {
        	return null;

        }

        return (T) scriptJavaClass;
	}

	/**
	 * Get pythonService instance
	 * @return PythonService instance
	 */
	public static PythonService instance() {
		return (PythonService) Component.getInstance(PythonService.class);
	}
	
	class PythonLoggerOutputStream extends OutputStream {

		private boolean error;
		private Log log;
		private StringBuffer buffer;

		private PythonLoggerOutputStream(Log log, boolean error) {
			this.error = error;
			this.log = log;
			this.buffer = new StringBuffer();
		}

		public void write(int b) throws IOException {
			if (((char) b == '\n') || ((char) b == '\r')) {
				flush();
			} else {
				buffer.append((char) b);
			}
		}

		public void flush() {
			if (buffer.length() > 0) {
				if (error) {
					this.log.error(buffer.toString());
				} else {
					this.log.info(buffer.toString());
				}
	
				this.buffer.setLength(0);
			}
		}
	}

}
