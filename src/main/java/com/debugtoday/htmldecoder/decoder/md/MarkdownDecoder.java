package com.debugtoday.htmldecoder.decoder.md;

import java.io.File;

import com.debugtoday.htmldecoder.exception.GeneralException;

public interface MarkdownDecoder {
	public String decode(File file, MarkdownDecoderArg arg) throws GeneralException;
}
