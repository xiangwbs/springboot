/**
 *    Copyright 2006-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * modified
 * insertBatch
 *
 * @author xiangwb
 */
public class InsertBatchElementGenerator extends AbstractXmlElementGenerator {

    private boolean isSimple;

    public InsertBatchElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert");

        answer.addAttribute(new Attribute("id", introspectedTable.getInsertBatchStatementId()));
        context.getCommentGenerator().addComment(answer);

        answer.addElement(new TextElement("insert into "));
        answer.addElement(new TextElement(getTableStr() + "("));

        StringBuilder insertClause = new StringBuilder();//fields
        StringBuilder valuesClause = new StringBuilder("    ");//values

        //获取主键信息
        GeneratedKey gk = introspectedTable.getGeneratedKey();
        boolean hasPk = true;
        String id = null;
        String pkSqlStatement = null;
        if (gk != null) {
            id = gk.getColumn();
            pkSqlStatement = gk.getRuntimeSqlStatement();
        } else {
            hasPk = false;
        }
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());

        //获取属性值
        String createTime = context.getProperty(PropertyRegistry.COMMENT_CREATE_TIME);
        String ignore = context.getProperty(PropertyRegistry.INSERT_IGNORE);
        String modifiedTime = context.getProperty(PropertyRegistry.COMMENT_MODIFIED_TIME);
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);
            String actualColumnName = introspectedColumn.getActualColumnName();
            if ((ignore != null && ignore.equalsIgnoreCase(actualColumnName))) {//忽略
                continue;
            }
            if ((createTime != null && createTime.equalsIgnoreCase(actualColumnName)) || (modifiedTime != null && modifiedTime.equalsIgnoreCase(actualColumnName))) {
                valuesClause.append("now()");
            } else if (hasPk) {
                if (id.equals(actualColumnName)) {
                    valuesClause.append("(").append(pkSqlStatement).append(" as ").append(id).append(")");
                    hasPk = false;
                } else {
                    valuesClause.append(MyBatis3FormattingUtilities.getBatchParameterClause(introspectedColumn));
                }
            } else {
                valuesClause.append(MyBatis3FormattingUtilities.getBatchParameterClause(introspectedColumn));
            }
            insertClause.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            if (i + 1 < columns.size()) {
                insertClause.append(", ");
                valuesClause.append(", ");
            }
            insertClause.append("\n        ");//新增换行
            valuesClause.append("\n            ");//新增换行
        }

        insertClause.append(')');
        answer.addElement(new TextElement(insertClause.toString()));

        answer.addElement(new TextElement("values"));
        answer.addElement(new TextElement("<foreach collection=\"list\" item=\"obj\" separator=\",\">("));
        valuesClause.append(')');
        valuesClause.append("\n        ");
        valuesClause.append("</foreach>");

        List<String> valuesClauses = new ArrayList<>();
        valuesClauses.add(valuesClause.toString());
        for (String clause : valuesClauses) {
            answer.addElement(new TextElement(clause));
        }
        if (context.getPlugins().sqlMapInsertElementGenerated(answer,
                introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
