package com.example.smart_attbook.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.example.smart_attbook.utils.TypeConvertUtils;

public class ResultXMLParser {
	public static final String				TAG											= "ResultXMLParser";
	
	public static final int					XMLTAG_NONE									= 0x0000;
	public static final int					XMLTAG_RESULT								= 0x1000;
	
	private int								m_nTagState									= XMLTAG_NONE;
	public int								m_nResult									= 0;
	
	public ResultXMLParser() {
		
	}
	
	public boolean Parse(String file) {
		boolean bResult = true;

		try {
			bResult = Parse(new BufferedInputStream(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			bResult = false;
			Log.e(TAG, e.toString());
		}
				
		return bResult;
	}
	
	public boolean Parse(ByteBuffer pData) {
		boolean bResult = true;
		
		bResult = Parse(TypeConvertUtils.ByteBufferToInputStream(pData));
		
		return bResult;
	}
	
	public boolean Parse(InputStream in) {
		boolean bResult = true;
		
		String strTag;
		String strValue;
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput(in, "utf-8");
			
			int eventType = xpp.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				
				if(eventType == XmlPullParser.START_DOCUMENT) { 
//					Log.d(TAG, "Start document"); 
				} else if(eventType == XmlPullParser.START_TAG) {
//					Log.d(TAG, "Start tag "+xpp.getName());
					strTag = xpp.getName();
					
					if (strTag.equals("result"))
						m_nTagState = XMLTAG_RESULT;
					else
						m_nTagState = XMLTAG_NONE;
					
				} else if(eventType == XmlPullParser.END_TAG) {
//					Log.d(TAG, "End tag "+xpp.getName());
					strTag = xpp.getName();
				} else if(eventType == XmlPullParser.TEXT) {
//					Log.d(TAG, "Text "+xpp.getText());
					strValue = xpp.getText();
					
					switch (m_nTagState) {
					case XMLTAG_RESULT:
						m_nResult = Integer.parseInt(strValue);
						break;
					
					default:
						break;
					}
					
					m_nTagState = XMLTAG_NONE;
				}
				eventType = xpp.next();
			}
//			Log.d(TAG, "End document");

		} catch (Exception e) {
			bResult = false;
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
		
		return bResult;
	}
	
	public int getResult() {
		return m_nResult;
	}
}
