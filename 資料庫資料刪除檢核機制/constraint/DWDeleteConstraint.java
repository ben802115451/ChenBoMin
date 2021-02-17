package com.digiwin.app.dao.constraint;

import com.digiwin.app.dao.DWDataRowSqlInfo;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.app.data.exceptions.DWDataDeleteConstraintException;
import com.digiwin.app.metadata.rdbms.DWRdbmsMetadata;
import com.digiwin.app.metadata.rdbms.DWRdbmsRelationshipAttribute;

import java.util.List;

/**
 * 2020/11/12 DWDao刪除檢核機制
 *
 * @author Bomin
 */
public class DWDeleteConstraint {

    public void check(DWDataRowSqlInfo sqlInfo, DWDataSetOperationOption option) throws Exception {
        check(sqlInfo.getRow(), option);
    }

    public void check(DWDataRow dataRow, DWDataSetOperationOption option) throws Exception {

        String tableName = dataRow.getDataTable().getName();
        DWReferenceInformation dwReferenceInformation = new DWReferenceInformation(tableName);

        String hintMessage = "";

        //檢查指定table是否開啟刪除檢核機制
        if (DWRdbmsMetadata.hasConstraint(tableName)) {

            //取得指定刪除內容之表名是否存在其他關係表
            List<DWRdbmsRelationshipAttribute> relationshipAttributeList = dwReferenceInformation.getRelationTableList();

            //存在關係表,進入檢核機制
            if (relationshipAttributeList.size() > 0) {

                for (DWRdbmsRelationshipAttribute relationshipAttribute : relationshipAttributeList) {

                    //取得指定刪除內容是否存在其他於關係表
                    DWDataTable referenceDataList = dwReferenceInformation.getReferenceDataList(relationshipAttribute, dataRow, option);

                    if (referenceDataList.getRows().size() > 0) {
                        hintMessage += DWConstraintFormatter.format(dwReferenceInformation.getTableDisplayName(relationshipAttribute.owner), dwReferenceInformation.getHintField(relationshipAttribute.owner), relationshipAttribute.getJoinColumns().keySet(), referenceDataList.getRows());
                    }
                }
                if (!hintMessage.isEmpty())
                    throw new DWDataDeleteConstraintException(hintMessage += "使用該筆資料,無法刪除!");
            }
        }
    }
}
