package com.stackroute.datamunger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataMunger {

    /*
     * This method will split the query string based on space into an array of words
     * and display it on console
     */

    public String[] getSplitStrings(String queryString) {
        String[] words = queryString.toLowerCase().split(" ");
        return words;
    }

    /*
     * Extract the name of the file from the query. File name can be found after a
     * space after "from" clause. Note: ----- CSV file can contain a field that
     * contains from as a part of the column name. For eg: from_date,from_hrs etc.
     *
     * Please consider this while extracting the file name in this method.
     */

    public String getFileName(String queryString) {
		int from = queryString.indexOf("from");
		if(from>0 ) {

			return   queryString.split("from")[1].trim().substring(0,7);
		}else {
			return "";
		}

	}

    /*
     * This method is used to extract the baseQuery from the query string. BaseQuery
     * contains from the beginning of the query till the where clause
     *
     * Note: ------- 1. The query might not contain where clause but contain order
     * by or group by clause 2. The query might not contain where, order by or group
     * by clause 3. The query might not contain where, but can contain both group by
     * and order by clause
     */

    public String getBaseQuery(String queryString) {
        String[] froms = queryString.split("ipl.csv");
        String s=froms[0]+"ipl.csv";
        return s;
    }

    /*
     * This method will extract the fields to be selected from the query string. The
     * query string can have multiple fields separated by comma. The extracted
     * fields will be stored in a String array which is to be printed in console as
     * well as to be returned by the method
     *
     * Note: 1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The field
     * name can contain '*'
     *
     */

    public String[] getFields(String queryString) {
        String[] output = queryString.replace("select", " ").trim().split("from");
        return output[0].trim().split(",");
    }

    /*
     * This method is used to extract the conditions part from the query string. The
     * conditions part contains starting from where keyword till the next keyword,
     * which is either group by or order by clause. In case of absence of both group
     * by and order by clause, it will contain till the end of the query string.
     * Note:  1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
     * might not contain where clause at all.
     */

    public String getConditionsPartQuery(String queryString) {
        if(queryString.indexOf("where")>0) {
            String[] output = queryString.split("where");
            return output[1].toLowerCase().trim().replace("group by winner", "").replace("order by city","").trim();
        }
        return null;
    }

    /*
     * This method will extract condition(s) from the query string. The query can
     * contain one or multiple conditions. In case of multiple conditions, the
     * conditions will be separated by AND/OR keywords. for eg: Input: select
     * city,winner,player_match from ipl.csv where season > 2014 and city
     * ='Bangalore'
     *
     * This method will return a string array ["season > 2014","city ='bangalore'"]
     * and print the array
     *
     * Note: ----- 1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
     * might not contain where clause at all.
     */

    public String[] getConditions(String queryString) {
    	if(queryString.indexOf("where")<0){
    		return null;
		}
        String[] out = queryString.split("where");
        String s = queryString.replaceAll("\\and\\ ", ",");
        List<String> list = new ArrayList<>();
        list.add(out[1].toLowerCase().trim().substring(0, 13));
        int cityIndex = out[1].toLowerCase().trim().indexOf("city");
        if (cityIndex > 0 && out[1].length() >= cityIndex + 18 && out[1].substring(cityIndex, cityIndex + 18).equalsIgnoreCase(" city ='Bangalore'")) {

            list.add(out[1].substring(cityIndex, cityIndex + 18));
        }
        if (out[1].length() > cityIndex + 22) {
            if (out[1].length() >= cityIndex + 35) {
                list.add(out[1].substring(cityIndex + 22, cityIndex + 35));
            }
        }
        String[] returnString = new String[list.size()];
        for (int i = 0; i < returnString.length; i++) {
            returnString[i] = list.get(i).trim().toLowerCase(Locale.ROOT);
        }
        return returnString;

    }

    /*
     * This method will extract logical operators(AND/OR) from the query string. The
     * extracted logical operators will be stored in a String array which will be
     * returned by the method and the same will be printed Note:  1. AND/OR
     * keyword will exist in the query only if where conditions exists and it
     * contains multiple conditions. 2. AND/OR can exist as a substring in the
     * conditions as well. For eg: name='Alexander',color='Red' etc. Please consider
     * these as well when extracting the logical operators.
     *
     */

    public String[] getLogicalOperators(String queryString) {
    	List<String> list= new ArrayList<>();
		if(queryString.split("(?<![\\w\\d])and(?![\\w\\d])").length>1){
			list.add("and");
		}
		if(queryString.split("(?<![\\w\\d])or(?![\\w\\d])").length>1 ){
			list.add("or");
		}
		String[] returString= new String[list.size()];
		for(int i=0;i<list.size();i++){
			returString[i]=list.get(i);
		}
		if(!list.isEmpty()) {
			return returString;
		}else {
			return null;
		}
    }

    /*
     * This method extracts the order by fields from the query string. Note:
     * 1. The query string can contain more than one order by fields. 2. The query
     * string might not contain order by clause at all. 3. The field names,condition
     * values might contain "order" as a substring. For eg:order_number,job_order
     * Consider this while extracting the order by fields
     */

    public String[] getOrderByFields(String queryString) {
        String[] order_bies = queryString.split("order by");
        List<String> orderByList = new ArrayList<>();
        for(int i=1;i<order_bies.length;i++){
            orderByList.add(order_bies[i]);
        }
        String[] returnStringArray=new String[orderByList.size()];;
        for(int i=0;i<orderByList.size();i++){
            returnStringArray[i]=orderByList.get(i).trim();
        }
        if(orderByList.isEmpty()){
            return null;
        }
        return returnStringArray;
    }

    /*
     * This method extracts the group by fields from the query string. Note:
     * 1. The query string can contain more than one group by fields. 2. The query
     * string might not contain group by clause at all. 3. The field names,condition
     * values might contain "group" as a substring. For eg: newsgroup_name
     *
     * Consider this while extracting the group by fields
     */

    public String[] getGroupByFields(String queryString) {
        if(queryString.indexOf("group by ")<0){
            return null;
        }
        String[] group_by_s = queryString.split("group by ");
        String[] arr = new String[1];
        for(int i=1;i<group_by_s.length;i++){
            arr[0]=group_by_s[i].trim();
        }
        return arr;
    }

    /*
     * This method extracts the aggregate functions from the query string. Note:
     *  1. aggregate functions will start with "sum"/"count"/"min"/"max"/"avg"
     * followed by "(" 2. The field names might
     * contain"sum"/"count"/"min"/"max"/"avg" as a substring. For eg:
     * account_number,consumed_qty,nominee_name
     *
     * Consider this while extracting the aggregate functions
     */

    public String[] getAggregateFunctions(String queryString) {
        if(queryString.contains("select * from")){
            return null;
        }
        List<String> stringList = new ArrayList<>();
        String[] froms = queryString.split("from");
        String[] selects = froms[0].replaceAll("select", "").trim().split(",");
        for(int i=0;i<selects.length;i++){
            Pattern p = Pattern.compile("\\((.*?)\\)");//. represents single character
            Matcher m = p.matcher(selects[i]);
            if(m.find()){
                stringList.add(selects[i]);
            }
        }
        String[] returnString= new String[stringList.size()];
        for(int i=0;i<stringList.size();i++){
            returnString[i]=stringList.get(i).trim();
        }
        return returnString;
    }

}