package strutter.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ResponseWrapper extends HttpServletResponseWrapper
{
	private StringWriter writer = null;

	public ResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	public PrintWriter getWriter() throws java.io.IOException {
		if (writer != null)
			throw new IllegalStateException("repeated getWriter() call");
		if (stream != null)
			throw new IllegalStateException("getOutputStream() was called first");

		writer = new StringWriter(2048);
		return new PrintWriter(writer);
	}

	private ByteArrayOutputStream stream;

	public ServletOutputStream getOutputStream() throws java.io.IOException {
		if (writer != null) {
			throw new IllegalStateException("getWriter() was called first");
		}
		if (stream != null) {
			throw new IllegalStateException("repeated getOutputStream() call");
		}
		stream = new ByteArrayOutputStream(16 * 1024);

		return new ServletOutputStream() {
			public void write(int i) throws java.io.IOException {
				stream.write(i);
			}
		};
	}

	public String toString(String encoding) throws Exception {
		if (writer != null)
			return writer.toString();
		if (stream != null)
			return stream.toString(encoding);
		return null;
	}

	
	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		//super.flushBuffer();
	}
	
	public void close() {
		try {
			if (writer != null)
				writer.close();
			if (stream != null)
				stream.close();
		} catch (Exception e) {
		}
	}
}
