package com.example.student_app.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


import com.example.student_app.data.Taking_LectureData;
import com.example.student_app.utils.TypeConvertUtils;

import android.util.Log;



public class Taking_LectureDataXMLParser {
	public static final String				TAG											= "Taking_LectureDataXMLParser";
	
	public static final int					XMLTAG_NONE									= 0x0000;
	
	public static final int					XMLTAG_NODES								= 0x1000;
	public static final int					XMLTAG_NODE									= 0x1100;
	public static final int					XMLTAG_NODE_LECTURE_ID						= 0x1110;
	
	public static final int					XMLTAG_NODE_STUDENT_NUMBER					= 0x1130;
	
	
	
	private int								        m_nTagState									= XMLTAG_NONE;
	
	public ArrayList<Taking_LectureData>			m_listTaking_LectureData							= null;
	
	public Taking_LectureDataXMLParser() {
		
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
		
		Taking_LectureData taking_lectureData = null;
		m_listTaking_LectureData = new ArrayList<Taking_LectureData>();
		
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
					
					if (strTag.equals("node")) {
						taking_lectureData = new Taking_LectureData();
						m_nTagState = XMLTAG_NODE;
					}
					else if (strTag.equals("lecture_id"))
						m_nTagState = XMLTAG_NODE_LECTURE_ID;
					
					else if (strTag.equals("student_number"))
						m_nTagState = XMLTAG_NODE_STUDENT_NUMBER;
					
					
					else
						m_nTagState = XMLTAG_NONE;
					
				} else if(eventType == XmlPullParser.END_TAG) {
//					Log.d(TAG, "End tag "+xpp.getName());
					strTag = xpp.getName();
					
					if (strTag.equals("node")) 
					{
						m_listTaking_LectureData.add(taking_lectureData);
						taking_lectureData = null;
					}
				} else if(eventType == XmlPullParser.TEXT) {
//					Log.d(TAG, "Text "+xpp.getText());
					strValue = xpp.getText();
					
					switch (m_nTagState) {
					case XMLTAG_NODE_LECTURE_ID:
						taking_lectureData.dLectureId = strValue;
						break;
						
					
					case XMLTAG_NODE_STUDENT_NUMBER:
						taking_lectureData.dStudentNumber = strValue;
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
	
	public ArrayList<Taking_LectureData> getTaking_LectureDataList() {
		return m_listTaking_LectureData;
	}
}
