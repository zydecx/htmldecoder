package com.debugtoday.htmldecoder.decoder.md;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.html.MetaDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;

public class PandocDecoder implements MarkdownDecoder {

	@Override
	public String decode(File file, MarkdownDecoderArg arg) throws GeneralException {
		// TODO Auto-generated method stub
		ConfigurationWrapper conf = arg.getConf();
		
		MarkdownWrapper wrapper = decodeMarkdownWrapperFromFile(file);
		String cmd = conf.getConf(Configuration.MARKDOWN_INTERPRETER_PANDOC) + " -f markdown_github -t html";
		
		Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			throw new GeneralException("fail to decode markdown file [" + file.getAbsolutePath() + "]", e);
		}
		
		PrintWriter pw;
		try {
			pw = new PrintWriter(new OutputStreamWriter(p.getOutputStream(), "UTF-8"));
			pw.write(wrapper.getContent());
			pw.close();
		} catch (UnsupportedEncodingException e) {
			throw new GeneralException("fail to decode markdown file [" + file.getAbsolutePath() + "]", e);
		}

		StringBuilder sb = new StringBuilder();
		try (
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
				BufferedReader brError = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));
				) {
			String inLine;
			while ((inLine = br.readLine()) != null) {
				sb.append(inLine).append("\n");
			}
			StringBuilder sbError = new StringBuilder();
			while ((inLine = brError.readLine()) != null) {
				sbError.append(inLine).append("\n");
			}
			
			if (sbError.length() > 0) {
				throw new IOException(sbError.toString());
			}
		} catch (IOException e) {
			throw new GeneralException("fail to decode markdown file [" + file.getAbsolutePath() + "]", e);
		}
		
		p.destroy();
		
		return formatFullText(sb.toString(), wrapper.getMetas());
	}
	
	private String formatFullText(String contentFullText, Map<String, String> metas) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>").append("\n")
			.append("<head>").append("\n");
		for (Entry<String, String> entry : metas.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("title")) {
				sb.append("<title>")
					.append(entry.getValue())
					.append("</title>")
					.append("\n");
			} else {
				sb.append("<meta name=\"")
					.append(MetaDecoder.formatMetaName(entry.getKey()))
					.append("\" content=\"")
					.append(entry.getValue())
					.append("\">")
					.append("\n");
			}
		}
		sb.append("</head>").append("\n")
			.append("<body>").append("\n")
			.append(contentFullText).append("\n")
			.append("</body>").append("\n")
			.append("</html>").append("\n");
		
		return sb.toString();
	}
	
	/**
	 * decode MarkdownWrapper from file
	 * @param file
	 * @return
	 * @throws GeneralException
	 */
	private MarkdownWrapper decodeMarkdownWrapperFromFile(File file) throws GeneralException {
		StringBuilder sb = new StringBuilder();
		try (
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				) {
			String inLine;
			while ((inLine = br.readLine()) != null) {
				sb.append(inLine).append("\n");
			}
		} catch (IOException e) {
			throw new GeneralException("fail to read markdown file [" + file.getAbsolutePath() + "]");
		}
		
		MarkdownWrapper wrapper = decodeMarkdownWrapperFromString(sb.toString());
		
		return wrapper;
	}
	
	/**
	 * @param s
	 * @return
	 */
	private MarkdownWrapper decodeMarkdownWrapperFromString(String s) {
		Pattern p = Pattern.compile("\\\\\\\\\\w+\\\\\\\\.*\n");
		
		Map<String, String> metas = new HashMap<>();
		Matcher m = p.matcher(s);
		int start, end = 0;
		while (m.find()) {
			start = m.start();
			end = m.end();
			int separator = s.indexOf("\\\\", start + 2);
			metas.put(s.substring(start + 2, separator), (separator + 2) >= end ? "" : s.substring(separator + 2, end).trim());
		}
		
		MarkdownWrapper wrapper = new MarkdownWrapper(metas, s.substring(end));	// require meta info at the start place of an article.
		
		return wrapper;
	}
	
	private class MarkdownWrapper {
		private Map<String, String> metas;
		private String content;

		public MarkdownWrapper(Map<String, String> metas, String content) {
			this.metas = metas;
			this.content = content;
		}
		
		public Map<String, String> getMetas() {
			return metas;
		}

		public void setMetas(Map<String, String> metas) {
			this.metas = metas;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

}
