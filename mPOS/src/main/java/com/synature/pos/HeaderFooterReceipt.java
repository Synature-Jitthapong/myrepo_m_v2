package com.synature.pos;

public class HeaderFooterReceipt{
	private String TextInLine;
	private int LineType;
	private int LineOrder;
	
	public String getTextInLine() {
		return TextInLine;
	}
	public void setTextInLine(String textInLine) {
		TextInLine = textInLine;
	}
	public int getLineType() {
		return LineType;
	}
	public void setLineType(int lineType) {
		LineType = lineType;
	}
	public int getLineOrder() {
		return LineOrder;
	}
	public void setLineOrder(int lineOrder) {
		LineOrder = lineOrder;
	}
}
