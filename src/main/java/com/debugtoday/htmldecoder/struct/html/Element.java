package com.debugtoday.htmldecoder.struct.html;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;

import com.debugtoday.htmldecoder.struct.Document;

public class Element {
	private String tag;	// element tag, i.g. div
	private String fullText;	// element full text, include tag\attributes\content 
	private Document document;	// document that the element belongs to
	private Map<String, String> attributes;	// element attributes
	private int fileStartPos;	// start position of tag from the file
	private int endPosOffset;	// end position offset to fileStartPos
	private int contentStartPosOffset;	// start position offset of content to fileStartPos
	private int contentEndPosOffset;	// end position offset of content to fileStartPos
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getFullText() {
		return fullText;
	}
	public void setFullText(String fullText) {
		this.fullText = fullText;
	}
	public String getContentText() {
		return contentStartPosOffset < 0 ? "" : Matcher.quoteReplacement(this.fullText.substring(contentStartPosOffset, contentEndPosOffset + 1));
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public int getFileStartPos() {
		return fileStartPos;
	}
	public void setFileStartPos(int fileStartPos) {
		this.fileStartPos = fileStartPos;
	}
	public int getEndPosOffset() {
		return endPosOffset;
	}
	public void setEndPosOffset(int endPosOffset) {
		this.endPosOffset = endPosOffset;
	}
	public int getContentStartPosOffset() {
		return contentStartPosOffset;
	}
	public void setContentStartPosOffset(int contentStartPosOffset) {
		this.contentStartPosOffset = contentStartPosOffset;
	}
	public int getContentEndPosOffset() {
		return contentEndPosOffset;
	}
	public void setContentEndPosOffset(int contentEndPosOffset) {
		this.contentEndPosOffset = contentEndPosOffset;
	}
}
