/*
 * Copyright 2008 IHE International (http://www.ihe.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.common.util;

import java.io.File;


/**
 *  <b>Class Description :  </b>UtilForJSF<br><br>
 * This class is a library, including methods used for the JSF pages, developed as Facelets methods..
 * 
 * IMPORTANT NOTE : all methods defined here have to be declared also within /Common-ui/src/META-INF/gazelle.taglib.xml file
 * 
 * 
 * 
 * All methods executed by JSF as Facelets are implemented in this class :
 * <li> Calculate the size of a list</li>
 * <li> Calculate the length of a String</li>
 * <li> Check if two strings are equals</li>
 * <li> etc...</li>
 *
 *
 * @class              	UtilForJSF.java
 * @package        	net.ihe.gazelle.common.util
 * @author       		Jean-Renan Chatel / INRIA Rennes IHE development Project
 * @see        >        	Jchatel@irisa.fr  -  http://www.ihe-europe.org
 * @version     			1.0 - July 15, 2008
 *
 */

public class UtilForJSF {


	 
    /**
     * Calculate the size of a ListDataModel object.
     * 
     * @param ListDataModel : List where size is looked for
     * @return int : Size of that List
     */
    public static int sizeList (org.jboss.seam.jsf.ListDataModel listData) {
        
    	if ( listData == null )
    	{
    		return 0;
    	} 
    	else 
    	{
    		return listData.getRowCount();
    	}
    }

    /**
     * Calculate the length of a String object.
     * 
     * @param String : String where length is looked for
     * @return int : Length of that String
     */
    public static int lengthString(String string) {
        
    	if (  string == null )
    	{
    		return 0;
    	} 
    	else
    	{
    		return string.length();
    	}
    }

    /**
     * This method checks if two strings objects are equals, and returns the boolean result.
     * 
     * @param String : String to be checked
     * @param String : String to be checked
     * @return Boolean : Checking results : Returns true if the 2 strings are equalsLength of that String
     */
    public static boolean equalsString(String string1, String string2) {
        
    	if (  string1.equals(string2) )
    	{
    		//log.info("equalsString returns TRUE");
    		return true;
    	} 
    	else
    	{
    		//log.info("equalsString returns false");
    		return false;
    	}
    }

    


    
    /**
     * This method checks if a string is null .
     * 
     * @param String : String to be checked
     * @return Boolean : Checking results : Returns true if the string is null 
     */
    public static boolean isNullString(String string1) {
        
    	
    	if (   ( string1.equals("") ) || ( string1 == null ) )
    	{
    		//log.info("isNullString returns TRUE");
    		return true;
    	} 
    	else
    	{
    		//log.info("isNullString returns false");
    		return false;
    	}
    }
    
    /**
     * This method checks to see if the given file exists
     * @param fileName: file that needs to be checked for existence
     * @return boolean: True/False
     */
    public static boolean ifFileExists(String fileName){
       boolean exists = false; 
       exists = (new File(fileName)).exists();
       if (exists){
          exists = true;
          //log.info("ifFileExists returned TRUE");
       }
       else {
          exists = false;
          //log.info("ifFileExists returned TRUE");
       }
       return exists;
    }





}
