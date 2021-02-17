package com.digiwin.util;

import java.util.regex.Pattern;

/**
 * 單號格式器<br>
 *
 * @author Bomin
 */
public class DocumentIdFormatter {

    /**
     * 單據編號格式化<br>
     * 例:SW20200101<br>
     *
     * @return
     */
    public String format(DocumentIdSetting setting) throws Exception {

        StringBuilder result = new StringBuilder();
        String strYear = String.valueOf(setting.getYear());
        String strMonth = formatMonth(setting.getMonth());
        String strDay = formatDay(setting.getDay());

        result.append(setting.getPrefix()).append(strYear).append(strMonth).append(strDay);

        return result.toString();
    }

    /**
     * 單據編號組成<br>
     * 例:SW202001010001<br>
     *
     * @param group    prefix+年月日8位組合
     * @param serialId 流水號
     * @return
     */
    public String format(String group, long serialId, int serialIdLength) throws Exception {

        StringBuilder result = new StringBuilder();
        String strSerialId = formatSerialId(serialIdLength, serialId);

        result.append(group + strSerialId);

        return result.toString();
    }

    /**
     * 取出單據號碼中的流水號<br>
     * 例:SW202001010001，回傳1<br>
     * 例:SW202001010324，回傳324<br>
     *
     * @param documentId 單據編號
     * @param group      prefix+年月日8位組合
     * @return
     * @throws Exception
     */
    public int fetchSerialNo(String documentId, String group, int serialIdLength) throws Exception {
        String strSerialNo = documentId.substring(group.length());

        Pattern pattern = Pattern.compile("[0-9]*");
        boolean isInteger = pattern.matcher(strSerialNo).matches();

        if (!isInteger) strSerialNo = formatSerialId(serialIdLength, 0);

        return Integer.parseInt(strSerialNo);
    }

    /**
     * 月格式編碼<br>
     * 例:10回傳10, 9回傳09<br>
     *
     * @param month
     * @return month
     */
    private String formatMonth(int month) throws Exception {
        return String.format("%02d", month);
    }

    /**
     * 日格式編碼<br>
     * 例:10回傳10, 9回傳09<br>
     *
     * @param day
     * @return day
     */
    private String formatDay(int day) throws Exception {
        return String.format("%02d", day);
    }

    /**
     * 流水號編碼(固定6碼)<br>
     * 例:1回傳000001，23回傳000023，337回傳000337<br>
     *
     * @param serialId
     * @return
     */
    private String formatSerialId(int serialIdLength, long serialId) throws Exception {
        return String.format("%0" + serialIdLength + "d", serialId);
    }
}
