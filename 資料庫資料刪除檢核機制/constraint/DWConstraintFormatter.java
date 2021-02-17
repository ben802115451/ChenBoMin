package com.digiwin.app.dao.constraint;

import com.digiwin.app.data.DWDataRowCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 2020/11/12 DWDao刪除檢核機制訊息Formatter
 *
 * @author Bomin
 */
public class DWConstraintFormatter {

    //組合刪除檢核提示訊息
    protected static String format(String tableDisplayName, String hintField, Set<String> joinColumns, DWDataRowCollection primaryTableDetail) throws Exception {

        List<String> pkList = new ArrayList<>();
        for (String pk : joinColumns) {
            pkList.add((String) primaryTableDetail.get(0).get(pk));
        }
        Set<String> hintFieldList = primaryTableDetail.stream().map(item -> (String) item.get(hintField)).collect(Collectors.toSet());

        return String.format("%s中 : PK%s -> Hint_Field%s ,", tableDisplayName, pkList, hintFieldList);
    }
}
