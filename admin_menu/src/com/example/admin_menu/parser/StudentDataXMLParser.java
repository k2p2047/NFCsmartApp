package com.example.admin_menu.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.example.admin_menu.data.StudentData;
import com.example.admin_menu.utils.TypeConvertUtils;


public class StudentDataXMLParser {
	public static final String				TAG											= "BookDataXMLParser";
	
	public static final int					XMLTAG_NONE									= 0x0000;
	
	public static final int					XMLTAG_NODES								= 0x1000;
	public static final int					XMLTAG_NODE									= 0x1100;
	public static final int					XMLTAG_NODE_NUMBER							= 0x1110;
	public static final int					XMLTAG_NODE_NAME							= 0x1120;
	public static final int					XMLTAG_NODE_PHONE							= 0x1130;
	public static final int					XMLTAG_NODE_ADDRESS							= 0x1140;
	public static final int					XMLTAG_NODE_PICTURE							= 0x1150;
	
	private int								m_nTagState									= XMLTAG_NONE;
	
	public ArrayList<StudentData>			m_listStudentData							= null;
	
	public StudentDataXMLParser() {
		
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
		
		StudentData studentData = null;
		m_listStudentData = new ArrayList<StudentData>();
		
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
						studentData = new StudentData();
						m_nTagState = XMLTAG_NODE;
					}
					else if (strTag.equals("student_number"))
						m_nTagState = XMLTAG_NODE_NUMBER;
					else if (strTag.equals("name"))
						m_nTagState = XMLTAG_NODE_NAME;
					else if (strTag.equals("phone"))
						m_nTagState = XMLTAG_NODE_PHONE;
					else if (strTag.equals("address"))
						m_nTagState = XMLTAG_NODE_ADDRESS;
					else if (strTag.equals("picture"))
						m_nTagState = XMLTAG_NODE_PICTURE;
					else
						m_nTagState = XMLTAG_NONE;
					
				} else if(eventType == XmlPullParser.END_TAG) {
//					Log.d(TAG, "End tag "+xpp.getName());
					strTag = xpp.getName();
					
					if (strTag.equals("node")) 
					{
						m_listStudentData.add(studentData);
						studentData = null;
					}
				} else if(eventType == XmlPullParser.TEXT) {
//					Log.d(TAG, "Text "+xpp.getText());
					strValue = xpp.getText();
					
					switch (m_nTagState) {
					case XMLTAG_NODE_NUMBER:
						studentData.dStudentNumber = Double.parseDouble(strValue);
						break;
						
					case XMLTAG_NODE_NAME:
						studentData.strName = strValue;
						break;
						
					case XMLTAG_NODE_PHONE:
						studentData.strPhoneNumber = strValue;
						break;
						
					case XMLTAG_NODE_ADDRESS:
						studentData.strAddress = strValue;
						break;
						
					case XMLTAG_NODE_PICTURE:
						studentData.strPictureURL = strValue;
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
	
	public ArrayList<StudentData> getStudentDataList() {
		return m_listStudentData;
	}
}
