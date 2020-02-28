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

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * @author Jeff Butler
 */
public class InsertElementGenerator extends AbstractXmlElementGenerator {

    private boolean isSimple;

    public InsertElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    /**
     * modified
     * insert
     * -去除parameterType
     * -添加换行
     * -自动生成创建时间
     * -忽略modifier
     * -忽略modified_time
     * -替换able参数
     */
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getInsertStatementId())); //$NON-NLS-1$
        //添加parameterType
//        FullyQualifiedJavaType parameterType;
//        if (isSimple) {
//            parameterType = new FullyQualifiedJavaType(
//                    introspectedTable.getBaseRecordType());
//        } else {
//            parameterType = introspectedTable.getRules()
//                    .calculateAllFieldsClass();
//        }
//
//        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
//                parameterType.getFullyQualifiedName()));

        context.getCommentGenerator().addComment(answer);
        //添加自动主键策略
        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = introspectedTable
                    .getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute(
                            "useGeneratedKeys", "true")); //$NON-NLS-1$ //$NON-NLS-2$
                    answer.addAttribute(new Attribute(
                            "keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
                    answer.addAttribute(new Attribute(
                            "keyColumn", introspectedColumn.getActualColumnName())); //$NON-NLS-1$
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        answer.addElement(new TextElement("insert into "));//新增
        answer.addElement(new TextElement(getTableStr() + "("));//新增:替换table

        StringBuilder insertClause = new StringBuilder();//fields
//        insertClause.append("insert into ");
//        insertClause.append(introspectedTable
//                .getFullyQualifiedTableNameAtRuntime());
//        insertClause.append(" (");

        StringBuilder valuesClause = new StringBuilder();//values()
//        valuesClause.append("values (");

        List<String> valuesClauses = new ArrayList<>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());

        //获取属性值
        String createTime = context.getProperty(PropertyRegistry.COMMENT_CREATE_TIME);
        String ignore = context.getProperty(PropertyRegistry.INSERT_IGNORE);
        String modifiedTime = context.getProperty(PropertyRegistry.COMMENT_MODIFIED_TIME);
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);

            //忽略
            String actualColumnName = introspectedColumn.getActualColumnName();
            if ((ignore != null && ignore.equalsIgnoreCase(actualColumnName))) {
                continue;
            }

            insertClause.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            //create_time，modified_time
            if ((createTime != null && createTime.equalsIgnoreCase(actualColumnName)) || modifiedTime != null && modifiedTime.equalsIgnoreCase(actualColumnName)) {//新增
                valuesClause.append("now()");
            } else {
                valuesClause.append(MyBatis3FormattingUtilities
                        .getParameterClause(introspectedColumn));
            }
            if (i + 1 < columns.size()) {
                insertClause.append(", ");
                valuesClause.append(", ");
            }
            insertClause.append("\n        ");//新增换行
            valuesClause.append("\n        ");//新增换行

//            if (valuesClause.length() > 80) {//长度大于80换行
//                answer.addElement(new TextElement(insertClause.toString()));
//                insertClause.setLength(0);
//                OutputUtilities.xmlIndent(insertClause, 1);
//
//                valuesClauses.add(valuesClause.toString());
//                valuesClause.setLength(0);
//                OutputUtilities.xmlIndent(valuesClause, 1);
//            }
        }

        insertClause.append(')');
        answer.addElement(new TextElement(insertClause.toString()));

        answer.addElement(new TextElement("values ("));//新增

        valuesClause.append(')');
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
