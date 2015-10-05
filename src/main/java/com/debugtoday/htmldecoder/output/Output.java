package com.debugtoday.htmldecoder.output;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;

/**
 * This interface includes only one method-<i>export()</i>.<br>
 * Under most circumstances, it accepts the object it requires to format with theme resource and export.<br>
 * So, often a type cast is needed when entering <i>export()</i>.<br>
 * If more resources, i.g. configurations, are needed to do the job, it's recommended to import with constructor. 
 * @author zydecx
 *
 */
public interface Output {
	
	String export(Object object) throws GeneralException;
	
	String DONE = "done";
}
