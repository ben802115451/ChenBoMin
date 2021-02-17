package com.digiwin.app.dao.constraint;

import com.digiwin.app.dao.*;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.metadata.DWValueAttribute;
import com.digiwin.app.metadata.rdbms.DWRdbmsMetadata;
import com.digiwin.app.metadata.rdbms.DWRdbmsRelationshipAttribute;
import com.digiwin.app.module.spring.SpringContextUtils;

import java.util.Map.Entry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 2020/11/12 DWDao刪除檢核機制取得關係表信息
 *
 * @author Bomin
 */
public class DWReferenceInformation {

    private DWDao dao = SpringContextUtils.getBean("dw-dao");

    static final String PRIMARY_TABLE_NO = "PRIMARY_TABLE_NO";

    String tableName;

    public DWReferenceInformation(String tableName) {
        this.tableName = tableName;
    }

    //取得指定刪除內容之表名是否存在其他關係表
    public List<DWRdbmsRelationshipAttribute> getRelationTableList() {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        String sql = "SELECT * FROM dw_rdbms_relations WHERE  REFERENCE_TABLE_NO = ?  ";
        List<Map<String, Object>> referenceTables = dao.select(option, sql, tableName);

        List<DWRdbmsRelationshipAttribute> relationshipAttributeList = new ArrayList<>();

        for (Map<String, Object> referenceTable : referenceTables) {
            DWRdbmsMetadata tableMetadata = DWMetadataContainer.get((String) referenceTable.get(PRIMARY_TABLE_NO), DWRdbmsMetadata.class);
            Collection<DWRdbmsRelationshipAttribute> relationshipAttributes = tableMetadata.getAttributes(DWRdbmsRelationshipAttribute.class);
            relationshipAttributeList.addAll(relationshipAttributes.stream().filter(item -> item.getDetailTableName().equals(tableName)).collect(Collectors.toList()));
        }
        return relationshipAttributeList;
    }

    //    取得指定刪除內容是否存在其他於關係表
    protected DWDataTable getReferenceDataList(DWRdbmsRelationshipAttribute relationshipAttribute, DWDataRow dataRow, DWDataSetOperationOption option) {

        DWQueryInfo queryInfo = new DWQueryInfo();
        Map<String, String> joinColumns = relationshipAttribute.getJoinColumns();

        queryInfo.setTableName(relationshipAttribute.owner.getName());
        for (Entry<String, String> item : joinColumns.entrySet()) {
            queryInfo.addFieldInfo(item.getKey(), DWQueryValueOperator.Equals, (Object) dataRow.get(item.getValue()));
        }
        return dao.select(queryInfo, option).getTable(relationshipAttribute.owner.getName());
    }

    protected String getTableDisplayName(DWRdbmsMetadata tableMetadata) {

        DWValueAttribute table_displayName = (DWValueAttribute) tableMetadata.getTableDisplayName();

        return (String) table_displayName.getValue();
    }

    protected String getHintField(DWRdbmsMetadata tableMetadata) {

        return tableMetadata.getHintField();
    }
}
