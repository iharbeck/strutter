package strutter.filter;

import org.apache.log4j.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

/**
 * Error log class
 * For more information visit http://technology.amis.nl/blog/?p=2392
 *
 * @author Jeroen van Wilgenburg, AMIS Services BV
 * @version 0.1a - 9 September 2007
 *
 */
public class YUIFilterErrorReporter implements ErrorReporter {
    private static final Logger log = Logger.getLogger(YUIFilterErrorReporter.class);

    public void warning(String message, String sourceName,
                        int line, String lineSource, int lineOffset) {
        if (line < 0) {
            log.warn(message);
        } else {
            log.warn(line + ':' + lineOffset + ':' + message);
        }
    }

    public void error(String message, String sourceName,
                      int line, String lineSource, int lineOffset) {
        if (line < 0) {
            log.error(message);
        } else {
            log.error(line + ':' + lineOffset + ':' + message);
        }
    }

    public EvaluatorException runtimeError(String message, String sourceName,
                                           int line, String lineSource, int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);
        return new EvaluatorException(message);
    }
}
